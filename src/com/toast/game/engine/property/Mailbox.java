package com.toast.game.engine.property;

import java.util.ArrayList;
import java.util.List;

import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;

public class Mailbox extends Property implements Updatable
{
   public Mailbox(String id)
   {
      super(id);
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
   
   private List<Message> messages = new ArrayList<>();
   
   private List<MessageHandler> messageHandlers = new ArrayList<>();
}
