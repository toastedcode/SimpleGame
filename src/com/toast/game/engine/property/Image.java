package com.toast.game.engine.property;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.toast.game.engine.interfaces.Drawable;

public class Image extends Property implements Drawable
{
   public Image(
      String id,
      BufferedImage bufferedImage)
   {
      super(id);
      this.bufferedImage = bufferedImage;
   }
   
   @Override
   public void draw(Graphics graphics)
   {
      Point position = new Point(0, 0);
      double scale = 1.0;
      
      Rectangle sourceRectangle = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      
      Rectangle destinationRectangle = new Rectangle(position,
                                                     new Dimension((int)(bufferedImage.getWidth() * scale), 
                                                                   (int)(bufferedImage.getHeight() * scale)));
      
      ((Graphics2D)graphics).drawImage(
         bufferedImage, 
         destinationRectangle.x, 
         destinationRectangle.y, 
         (destinationRectangle.x + destinationRectangle.width), 
         (destinationRectangle.y + destinationRectangle.height), 
         sourceRectangle.x, 
         sourceRectangle.y, 
         (sourceRectangle.x+ sourceRectangle.width), 
         (sourceRectangle.y + sourceRectangle.height), 
         null);  
   }
   
   @Override
   public int getWidth()
   {
      return (bufferedImage.getWidth());
   }

   
   @Override
   public int getHeight()
   {
      return (bufferedImage.getHeight());
   }
   

   @Override
   public boolean isVisible()
   {
      // TODO Auto-generated method stub
      return false;
   }   
   
   private final BufferedImage bufferedImage;
}
