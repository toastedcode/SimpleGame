package com.toast.game.engine.network;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

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
   
   public void start()
   {
      super.start();
      
      if (isStarted())
      {
          TimerTask registrationTask = new TimerTask()
          {
             public void run()
             {
                 sendRegistrationRequestMessage();
             }
          };
          
          registrationTimer = new Timer("registrationTimer");
         
          registrationTimer.scheduleAtFixedRate(registrationTask, 0, REGISTRATION_DELAY);
      }
   }
   
   public void sendData(String data)
   {
      sendData(sendAddress, sendPort, data);
   }
   
   public void register(String name)
   {
      sendRegistrationRequestMessage();
   }
   
   public boolean isRegistered()
   {
      return (isRegistered);
   }
   
   protected void handleMessage(InetAddress address, int port, XmlNode message)
   {
      try
      {
         String messageId = message.getAttribute("messageId").getValue();
         
         switch (messageId)
         {
            case "register_reply":
            {
               handleRegisterReplyMessage(address, port, message);
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
   
   private void handleRegisterReplyMessage(InetAddress address, int port, XmlNode message) throws XmlFormatException
   {
      boolean status = message.getChild("status").getBoolValue();
      
      if (status == true)
      {
         logger.log(Level.INFO, 
                    String.format("Client registered with server at %s:%d.", 
                                  address.toString(), 
                                  port));
         
         isRegistered = true;
         
         if (registrationTimer != null)
         {
            registrationTimer.cancel();
         }
      }
   }
   
   private void sendRegistrationRequestMessage()
   {
      XmlDocument document = new XmlDocument();
      document.createRootNode("message");
      document.getRootNode().setAttribute("messageId",  "register");
      
      sendData(sendAddress, sendPort, document.toString());
   }
   
   private final static Logger logger = Logger.getLogger(Client.class.getName());
   
   private static final long REGISTRATION_DELAY = 3000L;  // 3 seconds

   private InetAddress sendAddress;
   
   private int sendPort;
   
   private boolean isRegistered;
   
   private Timer registrationTimer;
}
