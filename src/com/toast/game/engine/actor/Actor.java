package com.toast.game.engine.actor;

import com.toast.game.engine.Renderer;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Mailable;
import com.toast.game.engine.interfaces.Movable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.message.Messenger;
import com.toast.game.engine.property.Mailbox;
import com.toast.game.engine.property.Property;
import com.toast.game.engine.property.State;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.XmlUtils;
import com.toast.xml.exception.XmlFormatException;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Actor implements Updatable, Movable, Mailable, Serializable
{
   public static Actor createActor(XmlNode node)
   {
      Actor actor = null;
      
      // TODO: Implement factory.
      
      return (actor);
   }
   
   public Actor(String id)
   {
      this.id = id;
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
      if (property instanceof Mailbox)
      {
         addMailbox((Mailbox)property);
      }

      // TODO: Make this more OO, please!
      if ((mailbox != null) &&
          (property instanceof MessageHandler))
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
      if (mailbox != null)
      {
         mailbox.queueMessage(message);
      }
      else
      {
         logger.log(Level.WARNING, 
                    String.format("Actor [%s] is not configured to handle message [%s].", 
                                  getId(), 
                                  message.getMessageId()));
      }
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
      XmlNode childNode = actorNode.appendChild("position");
      childNode.setAttribute("x",  (int)position.getX());
      childNode.setAttribute("y",  (int)position.getY());
      
      // zOrder
      actorNode.appendChild("zOrder").setValue(Integer.toString(zOrder));
      
      // properties
      childNode = actorNode.appendChild("properties");
      for (Property property : properties.values())
      {
         property.serialize(childNode);
      }
      
      return (actorNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      // id
      id = node.getAttribute("id").getValue();
      
      // isVisible
      isVisible = XmlUtils.getBool(node, "isVisible", true);
      
      // isEnabled
      isEnabled = XmlUtils.getBool(node, "isEnabled", true);
      
      // position
      if (node.hasChild("position"))
      {
         XmlNode childNode = node.getChild("position");
         position.setLocation(childNode.getAttribute("x").getIntValue(), childNode.getAttribute("y").getIntValue());
      }
      
      // zOrder
      zOrder = XmlUtils.getInt(node, "isEnabled", 0);
      
      // properties
      if (node.hasChild("properties"))
      {
         XmlNodeList childNodes = node.getChild("properties").getChildren();
         for (XmlNode childNode : childNodes)
         {
            Property property = Property.createProperty(childNode);
            
            if (property != null)
            {
               add(property);
            }
         }
      }
   }
   
   protected AffineTransform getTransform()
   {
      AffineTransform transform = new AffineTransform();
      transform.translate(getPosition().getX(), getPosition().getY());
      transform.scale(getScale().getWidth(), getScale().getHeight());
      
      return (transform);
   }
   
   private void addMailbox(Mailbox mailbox)
   {
      this.mailbox = mailbox;
      
      // Register all existing message handlers with the mailbox.
      for (Property property : properties.values())
      {
         if (property instanceof MessageHandler)
         {
            mailbox.register((MessageHandler)property);
         }
      }
      
      // Register the actor to receive messages.
      Messenger.register(this);
   }
   
   private final static Logger logger = Logger.getLogger(Property.class.getName());
   
   private String id;
   
   private boolean isVisible = true;
   
   private boolean isEnabled = true;
   
   private int zOrder = 0;
   
   // Temp.
   private Point2D.Double position = new Point2D.Double(0, 0);
   
   private Map<String, Property> properties = new HashMap<>();
   
   private Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 0, 0);
   
   private Mailbox mailbox = null;
}
