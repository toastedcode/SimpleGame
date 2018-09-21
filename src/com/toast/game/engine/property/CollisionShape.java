package com.toast.game.engine.property;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.toast.game.common.XmlUtils;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class CollisionShape extends Property implements Drawable
{
   // **************************************************************************
   //                                Private
   // **************************************************************************
   
   public CollisionShape(String id)
   {
      super(id);
   }
   
   public CollisionShape(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public void setEnabled(boolean isEnabled)
   {
      this.isEnabled = isEnabled;
   }
   
   public boolean isEnabled()
   {
      return (isEnabled);
   }

   public Shape getShape()
   {
      return (shape);
   }

   public Rectangle2D.Double getBounds()
   {
      Rectangle2D bounds = shape.getBounds2D();
      return (new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
   }
   
   // **************************************************************************
   //                              Drawable interface
   
   @Override
   public void draw(Graphics graphics)
   {
      graphics.setColor(Color.WHITE);
      
      ((Graphics2D)graphics).draw(shape);
   }
   
   @Override
   public int getWidth()
   {
      return ((int)getBounds().getWidth());
   }

   @Override
   public int getHeight()
   {
      return ((int)getBounds().getHeight());
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <collision id="">
      <rectangle x="" y="" width="" height=""/>
   </animation>
   */
   
   @Override
   public String getNodeName()
   {
      return("collision");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);

      // TODO
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
  // **************************************************************************
  //                                Private
  // **************************************************************************
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      if (node.hasChild("rectangle"))
      {
         Rectangle rectangle = XmlUtils.getRectangle(node.getChild("rectangle"));
         
         shape = new Rectangle2D.Double(rectangle.getX(), 
                                        rectangle.getY(), 
                                        rectangle.getWidth(), 
                                        rectangle.getHeight());
      }      
   }

   private boolean isEnabled = true;
   
   private Shape shape = null;
}
