package com.toast.game.engine.property;

import com.toast.game.engine.actor.Actor;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;

public class Property implements Serializable
{
   public Property(String id)
   {
      this.id = id;
   }
   
   public Property(XmlNode node)
   {
      deserialize(node);
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
   public void deserialize(XmlNode node)
   {
      id = node.getAttribute("id");
   }
   
   private String id;
   
   private Actor parent;
}
