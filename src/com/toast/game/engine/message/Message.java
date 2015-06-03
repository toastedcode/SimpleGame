package com.toast.game.engine.message;

import java.util.HashMap;

public class Message
{
   // **************************************************************************
   //                             Public Classes
   // **************************************************************************
   
   public static class Parameter
   {
      public Parameter(String id, Object parameter)
      {
         this.id = id;
         this.parameter = parameter;
      }
      
      public String getId()
      {
         return (id);
      }
      
      public Object getParameter()
      {
         return (parameter);
      }
      
      private String id;
      
      private Object parameter;
   }
   
   @SuppressWarnings("serial")
   public class Payload extends HashMap<String, Object>
   {
   };
   
   // **************************************************************************
   //                             Public Operations
   // **************************************************************************

   public Message(
      String messageId,
      String sourceId,
      String destinationId)
   {
      this.messageId = messageId;
      this.sourceId = sourceId;
      this.destinationId = destinationId;
      payload = new Payload();
   }
   
   public Message(
         String messageId,
         String sourceId,
         String destinationId,
         Parameter ... parameters)
   {
      this.messageId = messageId;
      this.sourceId = sourceId;
      this.destinationId = destinationId;
      
      payload = new Payload();
      for (Parameter parameter : parameters)
      {
         payload.put(parameter.getId(), parameter.getParameter());
      }
   }
   
   public String getMessageId()
   {
      return (messageId);
   }
   
   public String getSourceId()
   {
      return (sourceId);
   }
   
   public String getDestinationId()
   {
      return (destinationId);
   }
   
   public void addPayload(
      String name,
      Object value)
   {
      payload.put(name, value);
   }

   public Object getPayload(
      String name)
   {
      Object value = null;
      
      if (payload.containsKey(name) == false)
      {
               
      }
      else
      {
         value = payload.get(name);
      }
      
      return (value); 
   }
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
   
   private String messageId;
   
   private String sourceId;
   
   private String destinationId;
   
   private Payload payload;
}
