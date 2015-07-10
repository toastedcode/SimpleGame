package com.toast.game.engine.property;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class State extends Property
{

   public State(String id)
   {
      super(id);
   }
   
   public State(String id, Object value)
   {
      super(id);
      
      setValue(value);
   }
   
   @Override
   public Property clone()
   {
      State clone = new State(getId());
      
      clone.setValue(value);
      
      return (clone);
   }
   
   public void setValue(Object value)
   {
      this.value = value;
   }
   
   public Object getValue()
   {
      return (value);
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <state id="" class = "" value=""/>
   */
   
   @Override
   public String getNodeName()
   {
      return("state");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);

      propertyNode.setAttribute("class",  value.getClass().getName());
      propertyNode.setAttribute("value", value.toString());
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      try
      {
         Class<?> propertyClass = Class.forName(node.getAttribute("class").getValue());
         
         value = (Object)propertyClass.newInstance();
         
         // TODO: How do we deserialize a generic object from a String?
         
      }
      catch (ClassNotFoundException | IllegalAccessException | InstantiationException e)
      {
         
      }
      
   }
   
   private Object value;
}
