package com.toast.game.engine.property;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.message.Messenger;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;

public class KeyMap extends Property implements MessageHandler
{
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public KeyMap(String id)
   {
      super(id);
   }
   
   public KeyMap(XmlNode node)
   {
      super(node);
   }
   
   public void mapKey(int keyId, String messageId)
   {
      // Validate the parameters.
      if ((messageId == null) || (messageId.equals("")))
      {
         throw (new IllegalArgumentException());
      }
      
      keyMap.put(keyId,  messageId);
   }
   
   // **************************************************************************
   //                        MessageHandler interface
   
   @Override
   public void handleMessage(Message message)
   {
      switch (message.getMessageId())
      {
         case "msgKEY_PRESSED":
         {
            KeyEvent event = (KeyEvent)message.getPayload("keyEvent");
            onKeyPressed(event);
            break;
         }
         
         case "msgKEY_RELEASED":
         {
            KeyEvent event = (KeyEvent)message.getPayload("keyEvent");
            onKeyReleased(event);
            break;
         }
         
         default:
         {
            // Ignore all other messages.
            break;
         }
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
      return("keyMap");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);

      //
      // keyMap
      //

      Iterator<Map.Entry<Integer, String>> it = keyMap.entrySet().iterator();
      
      while (it.hasNext() == true)
      {
         Map.Entry<Integer, String> pair = (Map.Entry<Integer, String>)it.next();
         
         XmlNode keyNode = propertyNode.appendChild("key");
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
      
      XmlNodeList keyNodes = node.getChildren("key");
      
      for (int i = 0; i < keyNodes.getLength(); i++)
      {
         XmlNode keyNode = keyNodes.item(i);
         
         mapKey(Integer.valueOf(keyNode.getAttribute("keyId")), keyNode.getValue());
      }
   }
   
   // **************************************************************************
   //                                 Protected
   // **************************************************************************
   
   protected void onKeyPressed(KeyEvent event)
   {
      if (keyMap.containsKey(event.getKeyCode()) == true)
      {
         String messageId = keyMap.get(event.getKeyCode());
         Messenger.sendMessage(new Message(messageId, getId(), getParent().getId(), new Message.Parameter("isKeyPressed", true)));
      }
   }
   
   protected void onKeyReleased(KeyEvent event)
   {
      if (keyMap.containsKey(event.getKeyCode()) == true)
      {
         String messageId = keyMap.get(event.getKeyCode());
         Messenger.sendMessage(new Message(messageId, getId(), getParent().getId(), new Message.Parameter("isKePressed", false)));
      }
   }
   
   Map<Integer, String> keyMap = new HashMap<>();
}
