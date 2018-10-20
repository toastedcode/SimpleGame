package com.toast.game.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class XmlUtils
{
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public static Point getPoint(XmlNode node) throws XmlFormatException
   {
      Point point = new Point(node.getAttribute("x").getIntValue(), node.getAttribute("y").getIntValue());
      
      return (point);
   }
   
   public static Vector2D getVector(XmlNode node) throws XmlFormatException
   {
      Vector2D vector = new Vector2D(node.getAttribute("x").getDoubleValue(), node.getAttribute("y").getDoubleValue());
      
      return (vector);
   }
   
   public static Rectangle getRectangle(XmlNode node) throws XmlFormatException
   {
      Rectangle rectangle = new Rectangle(node.getAttribute("x").getIntValue(), 
                                          node.getAttribute("y").getIntValue(),
                                          node.getAttribute("width").getIntValue(),
                                          node.getAttribute("height").getIntValue());
      
      return (rectangle);
   }
   
   public static Dimension getDimension(XmlNode node) throws XmlFormatException
   {
      Dimension dimension = new Dimension(node.getAttribute("width").getIntValue(),
                                          node.getAttribute("height").getIntValue());
      
      return (dimension);
   }
   
   public static Font getFont(XmlNode node) throws XmlFormatException
   {
      String fontName = DEFAULT_FONT_NAME;
      int fontStyle = DEFAULT_FONT_STYLE;
      int fontSize = DEFAULT_FONT_SIZE;
      
      // fontName
      if (node.hasAttribute("fontName"))
      {
         fontName = node.getAttribute("fontName").getValue();
      }
      
      // fontStyle
      if (node.hasAttribute("fontStyle"))
      {
         fontStyle = getFontStyle(node);
      }
      
      // fontSize
      if (node.hasAttribute("fontSize"))
      {
         fontSize = node.getAttribute("fontSize").getIntValue();
      }

      Font font = new Font(fontName, fontStyle, fontSize);
      
      return (font);
   }
   
   /*
   public static Color getColor(XmlNode node) throws XmlFormatException
   {
      Color color;
      
      String colorString = node.getAttribute("color").getValue();

      try
      {
         Field field = Class.forName("java.awt.Color").getField(colorString);
         color = (Color)field.get(null);
      }
      catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
      {
         throw (new XmlFormatException(String.format("Color expected in node: \n%s", node)));
      }
      
      return (color);
   }
   */
      
   public static int getFontStyle(XmlNode node) throws XmlFormatException
   {
      int fontStyle = DEFAULT_FONT_STYLE;
      
      String styleString = node.getAttribute("fontStyle").getValue();
      
      if (styleString.toLowerCase().equals("plain"))
      {
         fontStyle = Font.PLAIN;
      }
      else if (styleString.toLowerCase().equals("italic"))
      {
         fontStyle = Font.ITALIC;
      }
      else if (styleString.toLowerCase().equals("bold"))
      {
         fontStyle = Font.BOLD;
      }
      else
      {
         throw (new XmlFormatException(String.format("Font style expected in node: \n%s", node)));
      }
      
      return (fontStyle);
   }
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private static String DEFAULT_FONT_NAME = "Verdana";
   
   private static int DEFAULT_FONT_STYLE = Font.PLAIN;
   
   private static int DEFAULT_FONT_SIZE = 10;
}
