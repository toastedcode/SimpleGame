package com.toast.game.engine.interfaces;

import com.toast.game.engine.Message;

public interface Mailable
{
   public void queueMessage(Message message);
}
