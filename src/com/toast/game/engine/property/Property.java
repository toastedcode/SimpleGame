package com.toast.game.engine.property;

import com.toast.game.engine.Actor;
import com.toast.xml.XmlNode;

public class Property
{
   public Property(String id)
   {
      this.id = id;
   }
   
   public Property(XmlNode node)
   {
      id = node.getAttribute("id");
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
   
   private final String id;
   
   private Actor parent;
}
