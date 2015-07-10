package com.toast.game.engine.property;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.resource.ImageResource;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Image extends Property implements Drawable
{
   public Image(
      String id,
      BufferedImage bufferedImage)
   {
      super(id);
      this.bufferedImage = bufferedImage;
   }
   
   public Image(
      String id,
      ImageResource resource)
   {
      super(id);
      this.resource = resource;
      this.bufferedImage = resource.getImage();
   }
   
   public Image(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public Property clone()
   {
      Image clone = new Image(getId(), bufferedImage);
      
      return (clone);
   }
   
   // **************************************************************************
   //                           Drawable interface
   
   @Override
   public void draw(Graphics graphics)
   {
      if (isVisible() == true)
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
      return (isVisible);
   }
   
   @Override
   public void setVisible(boolean isVisible)
   {
      this.isVisible = isVisible;
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <image id="walk">
      <image></image>
      <isVisible></isVisible>
   </animation>
   */
   
   @Override
   public String getNodeName()
   {
      return("image");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // resource
      if (resource != null)
      {
         propertyNode.setAttribute("src",  resource.getId());
      }

      // isVisible
      propertyNode.appendChild("isVisible", isVisible);
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // resource
      if (node.hasAttribute("resource"))
      {
         String resourceId = node.getAttribute("resource").getValue();
         
         resource = ImageResource.getResource(resourceId);
         
         if (resource != null)
         {
            bufferedImage = resource.getImage();
         }
      }
      
      // isVisible
      if (node.hasChild("isVisible"))
      {
         isVisible = node.getChild("isVisible").getBoolValue();
      }      
   }
   
   private ImageResource resource;
      
   private BufferedImage bufferedImage;
   
   private boolean isVisible = true;
}
