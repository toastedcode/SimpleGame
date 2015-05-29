package com.toast.game.engine.property;

import java.util.ArrayList;
import java.util.List;

import com.toast.game.engine.Message;
import com.toast.game.engine.interfaces.Mailable;
import com.toast.game.engine.interfaces.Updatable;

public abstract class Mailbox implements Mailable, Updatable
{
   @Override
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
   
   public abstract void processMessage(Message message);
   
   private List<Message> messages = new ArrayList<>();
}
