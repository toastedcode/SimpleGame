package com.toast.game.engine.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toast.game.engine.interfaces.Mailable;

public class Messenger
{
   public void register(Mailable mailable)
   {
      sendMailboxes.put(mailable.getAddress(), mailable);
   }
   
   public void register(Mailable mailable, String messageId)
   {
      if (broadcastMailboxes.containsKey(messageId) == false)
      {
         broadcastMailboxes.put(messageId, new ArrayList<Mailable>());
      }
      
      broadcastMailboxes.get(messageId).add(mailable);
   }
   
   public void sendMessage(Message message)
   {
      if (message.getDestinationId() != null)
      {
         //
         // Direct send.
         //
         
         Mailable recepient = sendMailboxes.get(message.getDestinationId());
         
         if (recepient != null)
         {
            recepient.queueMessage(message);
         }
      }
      else
      {
         // Broadcast.
         
         String messageId = message.getMessageId();
         
         if (broadcastMailboxes.containsKey(messageId) == true)
         {
            for (Mailable recepient : broadcastMailboxes.get(messageId))
            {
               recepient.queueMessage(message);
            }
         }
      }
   }
   
   Map<String, Mailable> sendMailboxes = new HashMap<>();
   
   Map<String, List<Mailable>> broadcastMailboxes = new HashMap<>();
}
