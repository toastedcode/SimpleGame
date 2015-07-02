package com.toast.game.engine.property;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;

public class Motor extends Property implements MessageHandler
{
   public enum Direction
   {
      NONE,
      UP,
      DOWN,
      LEFT,
      RIGHT
   }
   
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public Motor(String id)
   {
      super(id);
   }
   
   public Motor(XmlNode node)
   {
      super(node);
   }
   
   public void mapKey(int keyId, Direction direction)
   {
      if (direction == Direction.NONE)
      {
         keyMap.remove(keyId);
      }
      else
      {
         keyMap.put(keyId,  direction);
      }
   }
   
   // **************************************************************************
   //                        MessageHandler interface
   
   @Override
   public void handleMessage(Message message)
   {
      if (message.getMessageId() == "msgKEY_PRESSED")
      {
         KeyEvent event = (KeyEvent)message.getPayload("keyEvent");
         
         Direction direction = keyMap.get(event.getKeyCode());
         
         if (direction != null)
         {
            switch (direction)
            {
               case UP:
               {
                  up();
                  break;
               }
               
               case DOWN:
               {
                  down();
                  break;
               }
               
               case LEFT:
               {
                  left();
                  break;
               }
               
               case RIGHT:
               {
                  right();
                  break;
               }
               
               default:
               {
                  break;
               }
            }
         }
      }
      else if (message.getMessageId() == "msgKEY_RELEASED")
      {
        // TODO
      }
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <motor id="">
      <keyMap>
         <key keyId=""></key>
      </keyMap>
   </motor>
   */
   
   @Override
   public String getNodeName()
   {
      return("motor");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);

      //
      // keyMap
      //
      
      XmlNode keyMapNode = propertyNode.appendChild("keyMap"); 
      
      Iterator<Map.Entry<Integer, Motor.Direction>> it = keyMap.entrySet().iterator();
      
      while (it.hasNext() == true)
      {
         Map.Entry<Integer, Motor.Direction> pair = (Map.Entry<Integer, Motor.Direction>)it.next();
         
         XmlNode keyNode = keyMapNode.appendChild("key");
         keyNode.setAttribute("keyId", pair.getKey());
         keyNode.setValue(pair.getValue());
      }
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node)
   {
      super.deserialize(node);
      
      //
      // keyMap
      //
      
      XmlNodeList keyNodes = node.getChild("keyMap").getChildren("key");
      
      for (int i = 0; i < keyNodes.getLength(); i++)
      {
         XmlNode keyNode = keyNodes.item(i);
         
         keyMap.put(Integer.valueOf(keyNode.getAttribute("keyId")), Motor.Direction.valueOf(keyNode.getValue()));
      }
      
   }
   
   // **************************************************************************
   //                                 Protected
   // **************************************************************************
  
   protected void up()
   {
      getParent().moveBy(0, -10);      
   }
   
   protected void down()
   {
      getParent().moveBy(0, 10);
   }
   
   protected void left()
   {
      getParent().moveBy(-10, 0);
   }
   
   protected void right()
   {
      getParent().moveBy(10, 0);
   }
   
   Map<Integer, Direction> keyMap = new HashMap<>();
}
