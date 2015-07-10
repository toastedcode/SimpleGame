package com.toast.game.engine.property;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toast.game.engine.actor.Actor;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Property implements Serializable
{
   public static Property createProperty(XmlNode node)
   {
      Property property = null;
      
      if (propertyClasses.isEmpty())
      {
         registerPropertyClass("image", Image.class);
         registerPropertyClass("motor", Motor.class);
         registerPropertyClass("mailbox", Mailbox.class);
         registerPropertyClass("script", Script.class);
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
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      id = node.getAttribute("id").getValue();      
   }
   
   private final static Logger logger = Logger.getLogger(Property.class.getName());
      
   private static Map<String, Class<?>> propertyClasses = new HashMap<>();
   
   private String id;
   
   private Actor parent;
}
