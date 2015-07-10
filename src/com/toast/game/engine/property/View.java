package com.toast.game.engine.property;

import java.awt.geom.AffineTransform;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import com.toast.game.common.XmlUtils;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class View extends Property implements Updatable
{
   public View(
      String id,
      Rectangle worldBounds,         
      Rectangle screenBounds)
   {
      super(id);
      
      this.worldBounds = worldBounds;
      this.screenBounds = screenBounds;
   }
   
   public View(XmlNode node) throws XmlFormatException
   {
      super(node);
   }

   public Rectangle getScreenBounds()
   {
      return (screenBounds);
   }
   
   public Rectangle getWorldBounds()
   {
      return (worldBounds);
   }

   public Rectangle worldToScreen(
      Rectangle rectangle)
   {
      double scaleX = ((double)screenBounds.width / (double)worldBounds.width); 
      double scaleY = ((double)screenBounds.height / (double)worldBounds.height);
      
      Rectangle transformedRectangle = new Rectangle(rectangle);
      transformedRectangle.setLocation(worldToScreen(transformedRectangle.getLocation()));
      transformedRectangle.setSize((int)(rectangle.width * scaleX), (int)(rectangle.height * scaleY));
      
      return (transformedRectangle);
   }
      
   public Point worldToScreen(
      Point point)
   {
      double scaleX = ((double)screenBounds.width / (double)worldBounds.width); 
      double scaleY = ((double)screenBounds.height / (double)worldBounds.height);
      
      // Math notes:
      // We subtract the worldBounds x/y to transform to view coordinates.
      // We add screenBounds x/y and scale to transform to screen coordinates.
      Point transformedPoint = new Point((int)((point.x - worldBounds.x) * scaleX + screenBounds.x), 
                                         (int)((point.y - worldBounds.y) * scaleY + screenBounds.y));
      
      
      return (transformedPoint);
   }   
   
   public Shape worldToScreen(
      Shape shape)
   {
      double scaleX = ((double)screenBounds.width / (double)worldBounds.width); 
      double scaleY = ((double)screenBounds.height / (double)worldBounds.height);
      
      AffineTransform transform = new AffineTransform(IDENTITY_TRANSFORM);
      
      transform.translate(screenBounds.x, screenBounds.y);
      transform.scale(scaleX, scaleY);
      transform.translate((worldBounds.getX() * -1), (worldBounds.getY() * -1));
      
      return (transform.createTransformedShape(shape));
   }
      
   public Point screenToWorld(
      Point point)
   {
      double scaleX = ((double)worldBounds.width / (double)screenBounds.width); 
      double scaleY = ((double)worldBounds.height / (double)screenBounds.height);
      
      
      // Math notes:
      // We add the worldBounds x/y to transform to view coordinates.
      // We subtract screenBounds x/y and scale to transform to world coordinates.
      Point transformedPoint = new Point((int)((point.x - screenBounds.x) * scaleX + worldBounds.x), 
                                         (int)((point.y - screenBounds.y) * scaleY + worldBounds.y));
      
      return (transformedPoint);
   }
   
   /*
   // Returns the screenBounds as a Shape for clipping.
   public Shape getClip()
   {
      return (screenBounds);
   }
   
   public void setTrackingSpriteId(
      String initTrackingSpriteId)
   {
      trackingSpriteId = initTrackingSpriteId;
   }
   
   
   public void startTracking()
   {
      isTracking = true;
   }
    
   
   public void stopTracking()
   {
      isTracking = false;
   }
   */   
   
   // **************************************************************************
   //                           Updatable interface

   public void update(
      long elapsedTime)
   {
      /*
      if (isTracking == true)
      {
         trackSprite();
      }
      */
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <view id="">
      <worldBounds x="" y="" width="" height=""/>
      <screenBounds x="" y="" width="" height=""/>
   </view>
   */
   
   @Override
   public String getNodeName()
   {
      return("view");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);

      // worldBounds
      XmlNode childNode = propertyNode.appendChild("worldBounds");
      childNode.setAttribute("x",  worldBounds.getX());
      childNode.setAttribute("y",  worldBounds.getY());
      childNode.setAttribute("width",  worldBounds.getWidth());
      childNode.setAttribute("height",  worldBounds.getHeight());
      
      // screenBounds
      childNode = propertyNode.appendChild("screenBounds");
      childNode.setAttribute("x",  screenBounds.getX());
      childNode.setAttribute("y",  screenBounds.getY());
      childNode.setAttribute("width",  screenBounds.getWidth());
      childNode.setAttribute("height",  screenBounds.getHeight());
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      // worldBounds
      worldBounds = XmlUtils.getRectangle(node.getChild("worldBounds"));
      
      // screenBounds
      screenBounds = XmlUtils.getRectangle(node.getChild("screenBounds"));
   }
   
   // **************************************************************************
   //                            Private Operations
   // **************************************************************************
   
   /*
   private void trackSprite()
   {
      if (SpriteManager.spriteExists(trackingSpriteId) == false)
      {
         logger.log(Level.SEVERE, "Cannot find tracking Sprite [%s].", trackingSpriteId);
         stopTracking();
      }
      else
      {
         // Retrieve the position of the tracking Sprite.
         Point2D spritePosition = SpriteManager.getSprite(trackingSpriteId).getPosition();
         
         // Determine the new position of View, such that it is centered on the tracking Sprite.
         int xPos = (int)spritePosition.getX() - (int)(worldBounds.getWidth() / 2);
         int yPos = (int)spritePosition.getY() - (int)(worldBounds.getHeight() / 2);
         
         // Retrieve the world dimensions.
         Dimension worldDimensions = LevelManager.getInstance().getCurrentLevel().getWorldDimensions();
         
         // Determine the bounds of the View based on the world dimensions.
         int minX = 0;
         int minY = 0;
         int maxX = (int)worldDimensions.getWidth() - (int)worldBounds.getWidth();
         int maxY = (int)worldDimensions.getHeight() - (int)worldBounds.getHeight();
         
         // Restrict to the correct world boundaries.
         xPos = (xPos < minX) ? minX : xPos;
         yPos = (yPos < minY) ? minY : yPos;
         xPos = (xPos > maxX) ? maxX : xPos;
         yPos = (yPos > maxY) ? maxY : yPos;
              
         // Set the new position.
         worldBounds.setLocation(xPos, yPos);
      }
   }
   */
   
   // **************************************************************************
   //                            Private Attributes
   // **************************************************************************
   
   private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
   
   private Rectangle worldBounds = null;
   
   private Rectangle screenBounds = null;
   
   /*
   // The Sprite from which the View will determine it's position.
   private String trackingSpriteId;
   
   // A flag indicating if the View is currently tracking a Sprite.
   private boolean isTracking;
   */
}
