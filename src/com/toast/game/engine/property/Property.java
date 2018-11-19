package com.toast.game.engine.property;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.interfaces.Syncable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

import jdk.nashorn.internal.runtime.regexp.joni.BitSet;

public class Property implements Updatable, Serializable, Syncable
{
   public static Property createProperty(XmlNode node)
   {
      Property property = null;
      
      if (propertyClasses.isEmpty())
      {
         registerPropertyClass("image", Image.class);
         registerPropertyClass("animation", Animation.class);
         registerPropertyClass("animationGroup", AnimationGroup.class);
         registerPropertyClass("motor", Motor.class);
         registerPropertyClass("mailbox", Mailbox.class);
         registerPropertyClass("script", Script.class);
         registerPropertyClass("keyMap", KeyMap.class);
         registerPropertyClass("text", Text.class);
         registerPropertyClass("collision", CollisionShape.class);
         registerPropertyClass("physics", Physics.class);
      }
      
      String propertyName = node.getName();
      
      try
      {
         if (propertyClasses.containsKey(propertyName))
         {
            Class<?> propertyClass = propertyClasses.get(propertyName);
            
            property = (Property)propertyClass.getConstructor(XmlNode.class).newInstance(node);
         }
      }
      catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e)
      {
         logger.log(Level.WARNING, 
                    String.format("Failed to create property [%s] from node: \n.",propertyName, node.toString()));
      }
      
      return (property);
   }
   
   public static void registerPropertyClass(String propertyName, Class<?> propertyClass)
   {
      propertyClasses.put(propertyName, propertyClass);
   }
   
   public Property(String id)
   {
      this.id = id;
   }
   
   public Property(XmlNode node) throws XmlFormatException
   {
      deserializeThis(node);
   }
   
   @Override
   public Property clone()
   {
      Property property = new Property(id);

      return (property);
   }
   
   public String getId()
   {
      return (id);
   }
   
   public Actor getParent()
   {
      return (parent);
   }
   
   public void setParent(Actor parent)
   {
      this.parent = parent;
   }
   
   // **************************************************************************
   //                             Updatable interface   
   
   public void update(
      long elapsedTime)
   {
      // Clear the change set.
      changeSet.clear();
   }
   
   // **************************************************************************
   //                         xml.Serializable interface
   
   /*
   <property id="">
   </property>
   */
   
   @Override
   public String getNodeName()
   {
      return("property");
   }

   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = node.appendChild(getNodeName());
      
      // id
      propertyNode.setAttribute("id",  id);
      
      return (propertyNode);
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
      XmlNode propertyNode = node.appendChild(getNodeName());
      
      // id
      propertyNode.setAttribute("id",  getId());
      
      return (propertyNode);
   }
   
   public void syncFrom(XmlNode node) throws XmlFormatException
   {
      // Implementation left to subclasses.
   }
   
   // **************************************************************************
   //                                Protected
   // **************************************************************************
   
   protected BitSet changeSet = new BitSet();
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      id = node.getAttribute("id").getValue();      
   }
   
   private final static Logger logger = Logger.getLogger(Property.class.getName());
      
   private static Map<String, Class<?>> propertyClasses = new HashMap<>();
   
   private String id;
   
   private Actor parent;
}
