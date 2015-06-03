package com.toast.game.engine.interfaces;

import com.toast.game.engine.message.Message;

public interface Mailable
{
   public String getAddress();
   
   public void queueMessage(Message message);
}
