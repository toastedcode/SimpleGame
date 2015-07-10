package com.toast.game.engine.property;

import java.util.ArrayList;
import java.util.List;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.message.Messenger;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class Mailbox extends Property implements Updatable
{
   // **************************************************************************
   //                                  Public
   // **************************************************************************
   
   public Mailbox(String id)
   {
      super(id);
   }
   
   public Mailbox(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public void queueMessage(Message message)
   {
      messages.add(message);
   }
   
   public void register(MessageHandler messageHandler)
   {
      if (messageHandlers.contains(messageHandler) == false)
      {
         messageHandlers.add(messageHandler);
      }
   }
   
   public void unregister(MessageHandler messageHandler)
   {
      messageHandlers.remove(messageHandler);
   }
   
   protected void processMessage(Message message)
   {
      for (MessageHandler messageHandler : messageHandlers)
      {
         messageHandler.handleMessage(message);
      }
   }
   
   // **************************************************************************
   //                            Actor overrides
   
   @Override
   public void setParent(Actor parent)
   {
      super.setParent(parent);
      
      registerForMessages();
   }
   
   // **************************************************************************
   //                           Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      List<Message> copy = new ArrayList<Message>(messages);
      messages.clear();
      
      for (Message message : copy)
      {
         processMessage(message);
      }
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   @Override
   public String getNodeName()
   {
      return("mailbox");
   }

   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // TODO.
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
   // **************************************************************************
   //                                  Private
   // **************************************************************************
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      XmlNodeList messageNodes = node.getChildren("message");
      for (XmlNode messageNode : messageNodes)
      {
         String messageId = messageNode.getAttribute("id").getValue();
         
         registerForMessages.add(messageId);
      }
      
      registerForMessages();
   }
   
   private void registerForMessages()
   {
      if (getParent() != null)
      {
         for (String messageId : registerForMessages)
         {
            Messenger.register(getParent(),  messageId);
         }
      }
   }
   
   private List<String> registerForMessages = new ArrayList<>();
   
   private List<Message> messages = new ArrayList<>();
   
   private List<MessageHandler> messageHandlers = new ArrayList<>();
}
