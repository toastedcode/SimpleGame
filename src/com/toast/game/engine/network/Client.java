package com.toast.game.engine.network;

import java.net.InetAddress;

import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Client extends Udp
{
   public void setup(InetAddress sendAddress, int listenPort, int sendPort)
   {
      this.sendAddress = sendAddress;
      this.sendPort = sendPort;
      
      super.setup(listenPort);
   }
   
   public void sendData(String data)
   {
      sendData(sendAddress, sendPort, data);
   }
   
   public void register(String name)
   {
      sendRegistrationRequestMessage();
   }
   
   protected void handleMessage(XmlNode message)
   {
      try
      {
         String messageId = message.getAttribute("messageId").getValue();
         
         System.out.format("Got message: %s\n",  message.getAttribute("messageId").getValue());
         
         switch (messageId)
         {
            case "register_reply":
            {
               // TODO
               break;
            }
            
            case "sync":
            {
               Synchronize.syncFrom(message.getChild("sync"));
               break;
            }
            
            default:
            {
               break;
            }
         }
      }
      catch (XmlFormatException e)
      {
         System.out.format("Failed to parse XML message.\n");
      }
   }
   
   private void sendRegistrationRequestMessage()
   {
      XmlDocument document = new XmlDocument();
      document.createRootNode("message");
      document.getRootNode().setAttribute("messageId",  "register");
      
      sendData(sendAddress, sendPort, document.toString());
   }

   private InetAddress sendAddress;
   
   private int sendPort;
}
