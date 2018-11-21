package com.toast.game.engine.actor;

import com.toast.game.common.CoordinatesType;
import com.toast.game.common.Vector2D;
import com.toast.game.engine.Renderer;
import com.toast.game.engine.collision.Collidable;
import com.toast.game.engine.collision.Collision;
import com.toast.game.engine.collision.CollisionHandler;
import com.toast.game.engine.collision.CollisionManager;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Mailable;
import com.toast.game.engine.interfaces.Movable;
import com.toast.game.engine.interfaces.Syncable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.message.Messenger;
import com.toast.game.engine.property.Mailbox;
import com.toast.game.engine.property.Physics;
import com.toast.game.engine.property.Property;
import com.toast.game.engine.property.State;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.XmlUtils;
import com.toast.xml.exception.XmlFormatException;

import jdk.nashorn.internal.runtime.regexp.joni.BitSet;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Actor implements Updatable, Movable, Mailable, Serializable, Collidable, Syncable
{
   public static Actor createActor(XmlNode node)
   {
      Actor actor = null;
      
      Class<?> actorClass = null;
      
      try
      {
         switch (node.getName())
         {
            case "actor":
            {
               if (node.hasAttribute("class"))
               {
                  actorClass = Class.forName(node.getAttribute("class").getValue());  
               }
               else
               {
                  actorClass = Actor.class;
               }
               break;
            }
            
            case "character":
            {
               actorClass = Character.class;
               break;
            }
            
            case "camera":
            {
               actorClass = Camera.class;
               break;
            }
            
            default:
            {
               break;
            }
         }
         
         actor = (Actor)actorClass.getConstructor(XmlNode.class).newInstance(node);
      }
      catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException | XmlFormatException e)
      {
         logger.log(Level.WARNING, 
                    String.format("Failed to create actor [%s] from node: \n.", 
                    (actorClass != null) ? actorClass.getName() : "UNKNOWN",
                    node.toString()));
      }
      
      return (actor);
   }
   
   public Actor(String id)
   {
      this.id = id;
   }
   
   public Actor(XmlNode node) throws XmlFormatException
   {
      deserializeThis(node);
   }

   public Actor clone(String id)
   {
      Actor clone = new Actor(id);
      
      clone.setVisible(isVisible);
      clone.setEnabled(isEnabled);
      clone.setPosition(position);
      clone.setZOrder(zOrder);
      clone.setDimension(dimension);
      
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
   
   public void initialize()
   {
      // Nothing to do here.
   }
   
   public void draw(Renderer renderer)
   {
      AffineTransform transform = getTransform();
      
      for (Property property : properties.values())
      {
         // TODO: Make this more OO, please!
         if (property instanceof Drawable)
         {
            renderer.draw((Drawable)property, transform, getLayer(), getCoordinatesType());
         }
      }
   }
   
   public Point2D.Double getPosition()
   {
      return ((Point2D.Double)position.clone());
   }
   
   public void setPosition(Point2D.Double position)
   {
      this.position = (Point2D.Double)position.clone();
   }
   
   public void setPosition(double x, double y)
   {
      position.setLocation(x,  y);
   }
   
   public Vector2D getDelta()
   {
      return (delta);
   }
   
   public Point2D.Double getCenter()
   {
      Point2D.Double position = getPosition();
      
      return (new Point2D.Double((position.getX() + (getWidth() / 2)),
                                 (position.getY() + (getHeight() / 2))));
   }
   
   public void setCenter(Point2D.Double center)
   {
      setPosition((center.getX() - (getWidth() / 2)),
                  (center.getY() - (getHeight() / 2)));
   }
   
   public Point2D.Double getHead()
   {
      Point2D.Double position = getPosition();
      
      return (new Point2D.Double((position.getX() + (getWidth() / 2)), 
                                 position.getY()));
   }
   
   public Point2D.Double getFoot()
   {
      Point2D.Double position = getPosition();
      
      return (new Point2D.Double((position.getX() + (getWidth() / 2)),
                                 (position.getY() + getHeight())));
   }
   
   public void setFoot(Point2D.Double center)
   {
      setPosition((center.getX() - (getWidth() / 2)),
                  (center.getY() - getHeight()));
   }
      
   public int getZOrder()
   {
      int z = zOrder;
      
      if (yAsZOrder)
      {
         z = (int)getFoot().y;
      }
      
      return (z);
   }
   
   public void setZOrder(int zOrder)
   {
      this.zOrder = zOrder;
   }
   
   public CoordinatesType getCoordinatesType()
   {
      return (coordinatesType);
   }
   
   public void setCoordinatesType(CoordinatesType coordinatesType)
   {
      this.coordinatesType = coordinatesType;
   }
   
   public Dimension getDimension()
   {
      return ((Dimension)dimension.clone());
   }
   
   public int getWidth()
   {
      return ((int)dimension.getWidth());
   }
   
   public int getHeight()
   {
      return ((int)dimension.getHeight());
   }
   
   public void setDimension(Dimension dimension)
   {
      this.dimension = (Dimension)dimension.clone();
   }
   
   public void setDimension(int width, int height)
   {
      dimension.setSize(width, height);
   }
   
   public Rectangle2D.Double getBounds()
   {
      return (new Rectangle2D.Double(position.getX(), position.getY(), dimension.getWidth(), dimension.getHeight()));
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
   
   public int getLayer()
   {
      // TODO      
      return (0);
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
         setDimension((int)Math.max(getWidth(),  drawable.getWidth()),
                      (int)Math.max(getHeight(),  drawable.getHeight()));
      }
      
      // TODO: Make this more OO, please!
      if (property instanceof Physics)
      {
         addPhysics((Physics)property);
      }
      
      // TODO: Make this more OO, please!
      if (property instanceof Collidable)
      {
         addCollidable((Collidable)property);
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
   
   public Map<String, Property> getProperties()
   {
      return (properties);
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
   
   public Physics getPhysics()
   {
      return (physics);
   }
   
   // **************************************************************************
   //                           Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      if (isEnabled() == true)
      {
         // Clear the change set.
         changeSet.clear();
         
         // Clear delta.
         delta.x = 0;
         delta.y = 0;
         
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

   @Override
   public void moveBy(double deltaX, double deltaY)
   {
      position.setLocation(position.getX() + deltaX, position.getY() + deltaY);
      
      // Update delta. 
      delta.x += deltaX;
      delta.y += deltaY;
      
      // TODO: Temporary.
      changeSet.set(SyncableProperties.POSITION.ordinal());
   }

   @Override
   public void moveTo(double x, double y)
   {
      // Update delta.
      delta.x += (x - position.x);
      delta.y += (y - position.y);
      
      position.setLocation(x, y);
   }
   
   // **************************************************************************
   //                             Mailable interface

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
   //                         Collidable interface

   @Override
   public boolean isCollisionEnabled()
   {
      return (isCollisionEnabled);
   }

   @Override
   public Shape getCollisionShape()
   {
      Area collisionShape = new Area();
      
      // Combine all enabled Collidable property collision shapes.
      for (Property property : properties.values())
      {
         if ((property instanceof Collidable) &&
             ((Collidable)property).isCollisionEnabled())
         {
            Shape componentShape = ((Collidable)property).getCollisionShape();  
            
            collisionShape.add(new Area(componentShape));
         }
      }

      AffineTransform transform = getTransform();
      Shape transformedShape = transform.createTransformedShape(collisionShape);
      
      return (transformedShape);
   }
   
   @Override
   public void onCollision(Collision collision)
   {
      System.out.format("%s collided with %s\n", this.getId(), ((Actor)(collision.getOther(this))).getId());
      
      for (Property property : properties.values())
      {
         if (property instanceof CollisionHandler)
         {
            ((CollisionHandler)property).onCollision(collision);
         }
      }
   }
   
   @Override
   public void onSeparation(Collidable collided)
   {
      System.out.format("%s separated from %s\n", this.getId(), ((Actor)(collided)).getId());
      
      for (Property property : properties.values())
      {
         if (property instanceof CollisionHandler)
         {
            ((CollisionHandler)property).onSeparation(collided);
         }
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
      
      // coordinatesType
      if (coordinatesType != CoordinatesType.WORLD)
      {
         actorNode.appendChild("coordinatesType").setValue(coordinatesType);
      }
      
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
      deserializeThis(node);
   }
   
   // **************************************************************************
   //                           Syncable interface
   
   public boolean isChanged()
   {
      return (!changeSet.isEmpty());
   }
   
   public XmlNode syncTo(XmlNode node)
   {
      XmlNode actorNode = node.appendChild(getNodeName());
      
      // id
      actorNode.setAttribute("id",  id);
      
      // isVisible
      if (changeSet.at(SyncableProperties.IS_VISIBLE.ordinal()))
      {
         actorNode.appendChild("isVisible").setValue(Boolean.toString(isVisible));
      }
      
      // isEnabled
      if (changeSet.at(SyncableProperties.IS_ENABLED.ordinal()))
      {
         actorNode.appendChild("isEnabled").setValue(Boolean.toString(isEnabled));
      }
      
      // position
      if (changeSet.at(SyncableProperties.POSITION.ordinal()))
      {
         XmlNode childNode = actorNode.appendChild("position");
         childNode.setAttribute("x",  (int)position.getX());
         childNode.setAttribute("y",  (int)position.getY());
      }
      
      // zOrder
      if (changeSet.at(SyncableProperties.Z_ORDER.ordinal()))
      {
         actorNode.appendChild("zOrder").setValue(Integer.toString(zOrder));
      }
      
      // properties
      XmlNode childNode = actorNode.appendChild("properties");
      for (Property property : properties.values())
      {
         if (property.isChanged())
         {
            property.syncTo(childNode);
         }
      }
      
      return (actorNode);      
   }
   
   public void syncFrom(XmlNode node) throws XmlFormatException
   {
      // isVisible
      if (node.hasAttribute("isVisible"))
      {
         isVisible = XmlUtils.getBool(node, "isVisible", true);
      }
      
      // isEnabled
      if (node.hasAttribute("isVisible"))
      {
         isEnabled = XmlUtils.getBool(node, "isEnabled", true);
      }
      
      // position
      if (node.hasChild("position"))
      {
         XmlNode childNode = node.getChild("position");
         position.setLocation(childNode.getAttribute("x").getIntValue(), childNode.getAttribute("y").getIntValue());
      }
      
      // zOrder
      if (node.hasChild("zOrder"))
      {
         XmlNode childNode = node.getChild("zOrder");
         zOrder = XmlUtils.getInt(childNode, "z", 0);
      }
      
      // properties
      if (node.hasChild("properties"))
      {
         XmlNodeList childNodes = node.getChild("properties").getChildren();
         for (XmlNode childNode : childNodes)
         {
            String propertyId = childNode.getAttribute("id").getValue();
            
            Property property = getProperty(propertyId);
            if (property != null)
            {
               property.syncFrom(childNode);
            }
            else
            {
               logger.log(Level.WARNING, 
                          String.format("Could not sync property [%s] in actor [%s].", 
                                        propertyId, 
                                        getId()));
            }
         }
      }      
   }
   
   // **************************************************************************
   
   protected AffineTransform getTransform()
   {
      AffineTransform transform = new AffineTransform();
      transform.translate(getPosition().getX(), getPosition().getY());
      transform.scale(getScale().getWidth(), getScale().getHeight());
      
      return (transform);
   }
   
   private void addPhysics(Physics physics)
   {
      this.physics = physics;
   }
   
   private void addCollidable(Collidable collidable)
   {
      CollisionManager.register(this);
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
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
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
      if (node.hasChild("zOrder"))
      {
         XmlNode childNode = node.getChild("zOrder");
         yAsZOrder = XmlUtils.getBool(childNode, "yAsZOrder", false);
         zOrder = XmlUtils.getInt(childNode, "z", 0);
      }
      
      // coordinatesType
      if (node.hasChild("coordinatesType"))
      {
         coordinatesType = CoordinatesType.valueOf(node.getChild("coordinatesType").getValue());
      }
      
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
   
   private enum SyncableProperties
   {
      IS_VISIBLE,
      IS_ENABLED,
      POSITION,
      Z_ORDER
   }
   
   private final static Logger logger = Logger.getLogger(Property.class.getName());
   
   private String id;
   
   private boolean isVisible = true;
   
   private boolean isEnabled = true;
   
   private Point2D.Double position = new Point2D.Double(0, 0);
   
   private Vector2D delta = new Vector2D(0, 0);
   
   private int zOrder = 0;
   
   private boolean yAsZOrder = false;
   
   private CoordinatesType coordinatesType = CoordinatesType.WORLD;
   
   private Dimension dimension = new Dimension(0, 0);
   
   private Map<String, Property> properties = new HashMap<>();
   
   private Mailbox mailbox = null;
   
   private Physics physics = null;
   
   private boolean isCollisionEnabled = true;
   
   private BitSet changeSet = new BitSet();
}
