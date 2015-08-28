package com.toast.game.engine.property;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class CollisionShape extends Property
{
   public CollisionShape(String id)
   {
      super(id);
   }
   
   public CollisionShape(XmlNode node) throws XmlFormatException
   {
      super(node);
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

   private boolean isEnabled = true;
   
   private Shape shape = null;
}
