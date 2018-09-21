package com.toast.game.engine.property;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import com.toast.game.common.XmlUtils;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Text extends Property implements Drawable
{
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public Text(String id)
   {
      super(id);
   }
   
   public Text(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public String getText()
   {
      return (text);
   }

   public void setText(String text)
   {
      this.text = text;
      recalculateDimension();
   }

   public Font getFont()
   {
      return (font);
   }

   public void setFont(Font font)
   {
      this.font = font;
      recalculateDimension();
   }

   public Color getColor()
   {
      return (color);
   }

   public void setColor(Color color)
   {
      this.color = color;
   }
   
   public int getParagraphWidth()
   {
      return (paragraphWidth);
   }
   
   public void setParagraphWidth(int paragraphWidth)
   {
      this.paragraphWidth = paragraphWidth;
   }
   
   // **************************************************************************
   //                             Drawable interface
   
   @Override
   public void draw(Graphics graphics)
   {
      graphics.setColor(color);
      graphics.setFont(font);
      graphics.drawString(text, 0, getHeight());
   }

   @Override
   public int getWidth()
   {
      return ((int)dimension.getWidth());
   }

   @Override
   public int getHeight()
   {
      return ((int)dimension.getHeight());
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <text id="" fontName="" fontStyle="" fontSize="" color="" width="">
   </text>
   */
   
   @Override
   public String getNodeName()
   {
      return("text");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // text
      node.setValue(text);
      
      // font
      node.setAttribute("fontName", font.getName());
      node.setAttribute("fontStyle", font.getStyle());
      node.setAttribute("fontSize", font.getSize());
      
      // color
      // TODO: Routine to serialize color.
      //node.setAttribute("color",  color.toString());
      
      // paragraphWidth
      if (paragraphWidth != 0)
      {
         node.setAttribute("width",  paragraphWidth);
      }
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // text
      setText(node.getValue());
      
      //
      // font
      //
      
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
         fontStyle = XmlUtils.getFontStyle(node);
      }
      
      // fontSize
      if (node.hasAttribute("fontSize"))
      {
         fontSize = node.getAttribute("fontSize").getIntValue();
      }
      
      setFont(new Font(fontName, fontStyle, fontSize));
      
      // color
      if (node.hasAttribute("color"))
      {
         color = XmlUtils.getColor(node);
      }
      
      // paragraphWidth
      if (node.hasAttribute("width"))
      {
         paragraphWidth = node.getAttribute("width").getIntValue();
      }
   }
   
   private void recalculateDimension()
   {
      // Won't work because the JPanel must be visible before getGraphics() returns anything.
      //FontMetrics fontMetrics = Game.getGamePanel().getGraphics().getFontMetrics(font);
      
      //dimension.setSize(fontMetrics.stringWidth(text),
      //                  fontMetrics.getHeight());
      
      dimension.setSize(50, 50);
   }
   
   private static String DEFAULT_FONT_NAME = "Verdana";
   
   private static int DEFAULT_FONT_STYLE = Font.PLAIN;
   
   private static int DEFAULT_FONT_SIZE = 10;
   
   private static Color DEFAULT_COLOR = Color.WHITE;
   
   private String text = "";
   
   private Font font = new  Font(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);
   
   private Color color = DEFAULT_COLOR;
   
   private int paragraphWidth = 0;
   
   Dimension dimension = new Dimension();
}
