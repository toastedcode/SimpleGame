package com.toast.game.engine.property;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.toast.game.common.Vector2D;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.resource.ImageResource;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlUtils;
import com.toast.xml.exception.XmlFormatException;

public class Image extends Property implements Drawable
{
   public Image(
      String id,
      BufferedImage bufferedImage)
   {
      super(id);
      this.bufferedImage = bufferedImage;
      scale = new Vector2D(1.0, 1.0);
   }
   
   public Image(
      String id,
      ImageResource resource)
   {
      super(id);
      this.resource = resource;
      this.bufferedImage = resource.getImage();
      scale = new Vector2D(1.0, 1.0);
   }
   
   public Image(XmlNode node) throws XmlFormatException
   {
      super(node);
      scale = new Vector2D(1.0, 1.0);
      deserializeThis(node);
   }
   
   public Property clone()
   {
      Image clone = new Image(getId(), bufferedImage);
      
      clone.scale = scale.clone();
      
      return (clone);
   }
   
   // **************************************************************************
   //                           Drawable interface
   
   @Override
   public void draw(Graphics graphics)
   {
      Point position = new Point(0, 0);
      
      Rectangle sourceRectangle = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      
      Rectangle destinationRectangle = new Rectangle(position,
                                                     new Dimension((int)(bufferedImage.getWidth() * scale.x), 
                                                                   (int)(bufferedImage.getHeight() * scale.y)));
      
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
      return (int)((double)bufferedImage.getWidth() * scale.x);
   }

   
   @Override
   public int getHeight()
   {
      return (int)((double)bufferedImage.getHeight() * scale.y);
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <image id="" resource="">
      <isVisible></isVisible>
   </image>
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
         propertyNode.setAttribute("resource",  resource.getId());
      }
      
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
            Color transparentColor = XmlUtils.getColor(node, "transparentRGB", null);
            
            if (transparentColor != null)
            {
               bufferedImage = resource.getImage(transparentColor);
            }
            else
            {
               bufferedImage = resource.getImage();
            }
         }
      }
      
      // scale
      if (node.hasChild("scale"))
      {
         scale = com.toast.game.common.XmlUtils.getVector(node.getChild("scale"));
      }
   }
   
   private ImageResource resource;
      
   private BufferedImage bufferedImage;
   
   // TODO: Write a Dimension2D.Double class.
   private Vector2D scale;
}
