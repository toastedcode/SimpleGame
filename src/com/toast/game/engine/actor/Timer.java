package com.toast.game.engine.actor;

import com.toast.game.engine.Game;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.Messenger;

public class Timer extends Actor
{
   public Timer(
      String id,
      double duration,
      boolean isPeriodic,
      Message message)
   {
      super(id);
      
      this.duration = duration;
      this.isPeriodic = isPeriodic;
      this.message = message;
      
      stop();
   }
   
   public void start()
   {
      endTime = System.currentTimeMillis() + duration;
      setEnabled(true);
   }
   
   public void stop()
   {
      endTime = 0;
      setEnabled(false);
   }
   
   @Override
   public void update(
      long elapsedTime)
   {
      if (isEnabled() == true)
      {
         if (System.currentTimeMillis() >= endTime)
         {
            onTimeout();
            
            // Determine we should start this timer again.
            if (isPeriodic == true)
            {
               endTime = System.currentTimeMillis() + duration;
            }
            else
            {
               // TODO: Mark this for deletion, rather than destroying here.
               Game.cancelTimer(getId());
            }
         }
      }
   }
   
   // **************************************************************************
   //                             Protected
   // **************************************************************************
   
   protected void onTimeout()
   {
      Messenger.sendMessage(message);
   }
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************

   private double duration;
   
   private boolean isPeriodic;
   
   private Message message;
   
   private double endTime;
}
