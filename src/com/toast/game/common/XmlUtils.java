package com.toast.game.common;

import java.awt.Rectangle;

import com.toast.xml.XmlNode;

public class XmlUtils
{
   /*
   public XmlNode serialize(Vector2D vector, XmlNode node)
   {
      node.appendChild("x", vector.x);
      node.appendChild("y", vector.y);
   }
   */
   
   public static Vector2D getVector(XmlNode node)
   {
      Vector2D vector = new Vector2D(node.getChild("x").getDoubleValue(), node.getChild("y").getDoubleValue());
      
      return (vector);
   }
   
   public static Rectangle getRectangle(XmlNode node)
   {
      Rectangle rectangle = new Rectangle(node.getChild("x").getIntValue(), 
                                          node.getChild("y").getIntValue(),
                                          node.getChild("width").getIntValue(),
                                          node.getChild("height").getIntValue());
      
      return (rectangle);
   }
}
