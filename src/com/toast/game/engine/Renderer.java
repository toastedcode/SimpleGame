package com.toast.game.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Dimension;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
      screenDimensions = new Dimension(screenWidth, screenHeight);
      
      for (int layer = 0; layer < numLayers; layer++)
      {
         layers.add(new Layer(screenWidth, screenHeight));
      }
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
                                      (int)screenDimensions.getWidth(), 
                                      (int)screenDimensions.getHeight());
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
         
         // Transform
         layer.getGraphics().setTransform(transform);
         
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
  
   // The dimensions (height, width) of the screen.
   private Dimension screenDimensions;
   
   private ArrayList<Layer> layers = new ArrayList<>();
}
