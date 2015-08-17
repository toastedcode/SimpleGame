package com.toast.game.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.toast.game.engine.actor.Camera;
import com.toast.game.engine.interfaces.Drawable;

public class Renderer
{
   private static class Layer
   {
      public Layer(
         int width,
         int height)
      {
         BACK_BUFFER = new BufferedImage(width, 
                                        height, 
                                        BufferedImage.TYPE_INT_RGB);
         
         GRAPHICS = BACK_BUFFER.createGraphics();
      }
      
      
      public BufferedImage getBackBuffer()
      {
         return (BACK_BUFFER);
      }
      
      
      public Graphics2D getGraphics()
      {
         return (GRAPHICS);
      }
      
      private final BufferedImage BACK_BUFFER;
      
      private final Graphics2D GRAPHICS;
   }
   
   // **************************************************************************
   //                           Public Operations
   // **************************************************************************
   
   public Renderer(
      int screenWidth,         
      int screenHeight,
      int numLayers)
   {
      screenDimension = new Dimension(screenWidth, screenHeight);
      
      for (int layer = 0; layer < numLayers; layer++)
      {
         layers.add(new Layer(screenWidth, screenHeight));
      }
   }
   
   public Dimension getScreenDimension()
   {
      return ((Dimension)screenDimension.clone());
   }
   
   
   public Rectangle getViewport()
   {
      return ((Rectangle)viewport.clone());
   }
   
   public void setViewport(Rectangle viewport)
   {
      this.viewport = viewport;
   }
   
   public void clear()
   {
      for (Layer layer : layers)
      {
         // Set transformation matrix to identity
         layer.getGraphics().setTransform(IDENTITY_TRANSFORM);

         // Erase the back-buffer.
         layer.getGraphics().setPaint(Color.BLACK);
         layer.getGraphics().fillRect(0, 
                                      0, 
                                      (int)screenDimension.getWidth(), 
                                      (int)screenDimension.getHeight());
      }
   }
   
   public void paint(
      Graphics graphics,
      ImageObserver observer)
   {
      // Copy each back-buffer to the front-buffer.
      for (Layer layer : layers)
      {
         graphics.drawImage(layer.getBackBuffer(), 0, 0, observer);      
      }
      
      // TODO: Remove
      // Draw framerate
      graphics.setColor(Color.WHITE);
      Font font = new Font("Verdana", 1, 14);
      graphics.setFont(font);
      graphics.drawString(Double.toString(Timing.getFrameRate()), 0,  15);
   }
      
   public void draw(
      Drawable drawable,
      AffineTransform transform,
      int layerIndex)
   {
      if ((layerIndex >= 0) &&
          (layerIndex < layers.size()))
      {
         Layer layer = layers.get(layerIndex);
         
         // Set transformation matrix to identity
         layer.getGraphics().setTransform(IDENTITY_TRANSFORM);
             
         // Viewport transform
         if (viewport != null)
         {
            AffineTransform viewportTransform = new AffineTransform();
            viewportTransform.translate(-viewport.getX(), -viewport.getY());
            viewportTransform.scale((viewport.getWidth() / screenDimension.getWidth()),
                                   (viewport.getHeight() / screenDimension.getHeight()));
            layer.getGraphics().transform(viewportTransform);
         }
      
         // Object transform
         layer.getGraphics().transform(transform);
         
         // Draw
         drawable.draw(layer.getGraphics());
      }
   }
  
   // **************************************************************************
   //                           Private Operations
   // **************************************************************************
   
   // **************************************************************************
   //                           Private Attributes
   // **************************************************************************
   
   // An identity transformation
   private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
   
   private Rectangle viewport;
  
   // The dimensions (height, width) of the screen.
   private Dimension screenDimension;
   
   private ArrayList<Layer> layers = new ArrayList<>();
}
