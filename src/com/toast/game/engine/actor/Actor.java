package com.toast.game.engine.actor;

import com.toast.game.engine.Renderer;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Mailable;
import com.toast.game.engine.interfaces.Movable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.property.Mailbox;
import com.toast.game.engine.property.Property;
import com.toast.game.engine.property.State;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class Actor implements Updatable, Movable, Mailable, Serializable
{
   public Actor(String id)
   {
      this.id = id;

      // Set up this actor to receive and process messages.
      mailbox = new Mailbox("mailbox");
      add(mailbox);
   }
   
   public Actor clone(String id)
   {
      Actor clone = new Actor(id);
      
      clone.setVisible(isVisible);
      clone.setEnabled(isEnabled);
      clone.setZOrder(zOrder);
      clone.setPosition(position);
      clone.setBounds(bounds);
      
      // Clone properties.
      for (Property property : properties.values())
      {
         clone.add(property.clone());
      }
      
      return (clone);
   }
   
   public String getId()
   {
      return (id);
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
   
   public void setPosition(Point2D.Double position)
   {
      this.position = (Point2D.Double)position.clone();
   }
   
   public boolean isVisible()
   {
      return (isVisible);
   }
   
   public void setVisible(boolean isVisible)
   {
      this.isVisible = isVisible;
   }
   
   public boolean isEnabled()
   {
      return (isEnabled);
   }
   
   public void setEnabled(boolean isEnabled)
   {
      this.isEnabled = isEnabled;
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
      this.bounds = (Rectangle2D.Double)bounds.clone();
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
      
      // TODO: Make this more OO, please!
      if (property instanceof MessageHandler)
      {
         mailbox.register((MessageHandler)property);
      }
   }
   
   public Property getProperty(String id)
   {
      return (properties.get(id));
   }
   
   public void removeProperty(Property property)
   {
      // TODO: Make this more OO, please!
      if (property instanceof MessageHandler)
      {
         mailbox.unregister((MessageHandler)property);
      }      
   }
   
   // **************************************************************************
   //                                 State
   // **************************************************************************
   
   public Object getState(String id)
   {
      Object value = null;
      
      Property property = getProperty(id);
      if ((property != null) &&
          (property instanceof State))
      {
         value = ((State)property).getValue();
      }
      
      return (value);
   }
   
   public Object setState(String id, Object value)
   {
      Property property = getProperty(id);
      if ((property != null) &&
          (property instanceof State))
      {
         ((State)property).setValue(value);
      }
      else
      {
         add(new State(id, value));
      }
      
      return (value);
   }
   
   // **************************************************************************
   //                           Updatable interface
   // **************************************************************************
   
   @Override
   public void update(long elapsedTime)
   {
      if (isEnabled() == true)
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
   }
   
   // **************************************************************************
   //                             Movable interface
   // **************************************************************************

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
   
   // **************************************************************************
   //                             Mailable interface
   // **************************************************************************

   @Override
   public String getAddress()
   {
      return (getId());
   }

   @Override
   public void queueMessage(Message message)
   {
      mailbox.queueMessage(message);
   }
   
   // **************************************************************************
   //                         xml.Serializable interface
  
   /*
   <actor id="id">
      <isVisible></isValue>
      <isEnabled></isEnabled>
      <position x="0" y="0"/>
      <zOrder>0</zOrder>
      <properties> ... </properties>
   </actor>
   */
   
   public String getNodeName()
   {
      return("actor");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode actorNode = node.appendChild(getNodeName());
      
      // id
      actorNode.setAttribute("id",  id);
      
      // isVisible
      actorNode.appendChild("isVisible").setValue(Boolean.toString(isVisible));
      
      // isEnabled
      actorNode.appendChild("isEnabled").setValue(Boolean.toString(isEnabled));
      
      // position
      
      // zOrder
      actorNode.appendChild("zOrder").setValue(Integer.toString(zOrder));
      
      // properties
      XmlNode childNode = actorNode.appendChild("properties");
      for (Property property : properties.values())
      {
         property.serialize(childNode);
      }
      
      return (actorNode);
   }

   @Override
   public void deserialize(XmlNode node)
   {
      // id
      id = node.getAttribute("id");
      
      // isVisible
      isVisible = node.getChild("isVisible").getBoolValue();
      
      // isEnabled
      isEnabled = node.getChild("isEnabled").getBoolValue();
      
      // position
      
      // zOrder
      zOrder = node.getChild("zOrder").getIntValue();
      
      // properties
      XmlNodeList childNodes = node.getChildren("property");
      for (int i = 0; i < childNodes.getLength(); i++)
      {
         XmlNode childNode = childNodes.item(i);
         
         add(new Property(childNode));
      }
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
   
   private boolean isEnabled = true;
   
   private int zOrder = 0;
   
   // Temp.
   private Point2D.Double position = new Point2D.Double(0, 0);
   
   private Map<String, Property> properties = new HashMap<>();
   
   private Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 0, 0);
   
   Mailbox mailbox;
}
