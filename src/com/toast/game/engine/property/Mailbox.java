package com.toast.game.engine.property;

import java.util.ArrayList;
import java.util.List;

import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;

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
   
   public void processMessage(Message message)
   {
      
   }
   
   private List<Message> messages = new ArrayList<>();
}
