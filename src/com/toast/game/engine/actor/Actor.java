package com.toast.game.engine.actor;

import com.toast.game.engine.Renderer;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Movable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.property.Property;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class Actor implements Updatable, Movable
{
   public Actor(String id)
   {
      this.id = id;
   }
   
   public String getId()
   {
      return (id);
   }
   
   public void update(long elapsedTime)
   {
      for (Property property : properties.values())
      {
         // TODO: Make this more OO, please!
         if (property instanceof Updatable)
         {
            ((Updatable)property).update(elapsedTime);
         }
      }
   }
   
   public void draw(Renderer renderer)
   {
      AffineTransform transform = getTransform();
      
      for (Property property : properties.values())
      {
         // TODO: Make this more OO, please!
         if (property instanceof Drawable)
         {
            renderer.draw((Drawable)property, transform, getLayer());
         }
      }
   }
   
   public Point2D.Double getPosition()
   {
      return (position);
   }
   
   public boolean isVisible()
   {
      return (isVisible);
   }
   
   public void setVisible(boolean isVisible)
   {
      this.isVisible = isVisible;
   }
   
   public int getZOrder()
   {
      return (zOrder);
   }
   
   public void setZOrder(int zOrder)
   {
      this.zOrder = zOrder;
   }
   
   public int getLayer()
   {
      // TODO      
      return (0);
   }
   
   public Rectangle2D.Double getBounds()
   {
      return (bounds);
   }
   
   protected void setBounds(Rectangle2D.Double bounds)
   {
      this.bounds = bounds;
   }
   
   public Dimension getScale()
   {
      // TODO
      return (new  Dimension(1, 1));
   }
   
   public void add(Property property)
   {
      properties.put(property.getId(), property);
      property.setParent(this);
      
      // TODO: Make this more OO, please!
      if (property instanceof Drawable)
      {
         Drawable drawable = (Drawable)property;
         bounds.add(new Rectangle(0, 0, drawable.getWidth(), drawable.getHeight()));
      }
   }
   
   public void removeProperty(Property property)
   {
   }
   

   @Override
   public void moveBy(double deltaX, double deltaY)
   {
      position.setLocation(position.getX() + deltaX, position.getY() + deltaY);
   }

   @Override
   public void moveTo(double x, double y)
   {
      position.setLocation(x, y);
   }
   
   protected AffineTransform getTransform()
   {
      AffineTransform transform = new AffineTransform();
      transform.translate(getPosition().getX(), getPosition().getY());
      transform.scale(getScale().getWidth(), getScale().getHeight());
      
      return (transform);
   }
   
   private String id;
   
   private boolean isVisible = true;
   
   private int zOrder = 0;
   
   // Temp.
   private Point2D.Double position = new Point2D.Double(0, 0);
   
   private Map<String, Property> properties = new HashMap<>();
   
   private Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 0, 0);
}
