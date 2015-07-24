package com.toast.game.engine.actor;

import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Character extends Actor implements MessageHandler
{
   // **************************************************************************
   //                                Public
   // **************************************************************************
   
   public Character(String id)
   {
      super(id);
      
      /*
       * state - stateId, animationId, 
       */
      
      mailbox.register(this);
   }
   
   // **************************************************************************
   //                            Actor overrides
   
   // **************************************************************************
   //                         xml.Serializable interface
   
   /*
   <character id="id">
   </character>
   */
   
   public String getNodeName()
   {
      return("character");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode playerNode = super.serialize(node);
      
      return (playerNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
   }

   @Override
   public void handleMessage(Message message)
   {
      switch (message.getMessageId())
      {
         case "LEFT_BUTTON":
         case "RIGHT_BUTTON":
         case "UP_BUTTON":
         case "DOWN_BUTTON":
         {
            boolean isPressed = (Boolean)message.getPayload("isPressed");
            handleActionKey(message.getMessageId(), isPressed);
         }
      }
   }
   
   // **************************************************************************
   //                                Protected
   // **************************************************************************
   
   protected void handleActionKey(String action, boolean isPressed)
   {
      
   }
   
   
}
