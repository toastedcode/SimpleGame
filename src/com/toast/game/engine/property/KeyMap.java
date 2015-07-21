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
import com.toast.xml.exception.XmlFormatException;

public class KeyMap extends Property implements MessageHandler
{
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public KeyMap(String id)
   {
      super(id);
   }
   
   public KeyMap(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
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
   <keyMap id="">
      <key keyId="" messageId=""/>
   </keyMap>
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
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
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
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      //
      // keyMap
      //
      
      XmlNodeList keyNodes = node.getChildren("key");
      
      for (XmlNode keyNode : keyNodes)
      {
         if (keyNode.hasAttribute("keyChar"))
         {
            int keyId = KeyEvent.getExtendedKeyCodeForChar(keyNode.getAttribute("keyChar").getCharacterValue());
            mapKey(keyId, keyNode.getAttribute("messageId").getValue());
         }
         else
         {
            mapKey(keyNode.getAttribute("keyId").getIntValue(), keyNode.getAttribute("messageId").getValue());
         }
      }
   }
   
   private Map<Integer, String> keyMap = new HashMap<>();
}
