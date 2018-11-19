package com.toast.game.engine.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Server extends Udp
{
   public Collection<ClientInfo> getClients()
   {
      return (clients.values());
   }
   
   public void addListener(ServerListener listener)
   {
      listeners.add(listener);
   }
   
   public void broadcastData(String data)
   {
      for (ClientInfo client : clients.values())
      {
         sendData(client.address, client.port, data);
      }
   }
   
   protected void handleMessage(InetAddress address, int port, XmlNode message)
   {
      try
      {
         String messageId = message.getAttribute("messageId").getValue();
         
         switch (messageId)
         {
            case "register":
            {
               handleRegisterRequestMessage(address, port, message);
               break;
            }
            
            case "unregister":
            {
               handleUnregisterRequestMessage(address, port, message);
               break;
            }
            
            case "sync":
            {
               handleSyncMessage(address, port, message);
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
         logger.log(Level.INFO, 
                    String.format("Invald message format from %s:%d: %s", 
                                  address.toString(), 
                                  port,
                                  message.toString()));
      }
   }
   
   private void handleSyncMessage(InetAddress address, int port, XmlNode message) throws XmlFormatException
   {
      int clientId = new InetSocketAddress(address, port).hashCode();
      
      // Synchronize our own data.
      Synchronize.syncFrom(message.getChild("sync"));
      
      String serializedMessage =  message.toString();
      
      // Sync all registered clients.
      for (ClientInfo clientInfo : clients.values())
      {
         if (clientInfo.clientId != clientId)
         {
            sendData(clientInfo.address, clientInfo.port, serializedMessage);
         }
      }
   }
   
   private void handleRegisterRequestMessage(InetAddress address, int port, XmlNode message) throws XmlFormatException
   {
      int clientId = new InetSocketAddress(address, port).hashCode();
      
      if (!clients.containsKey(clientId))
      {
         logger.log(Level.INFO, 
                    String.format("New client registered with server from %s:%d.", 
                                  address.toString(), 
                                  port));
         
         String clientName = "";
         if (message.hasChild("name"))
         {
            clientName = message.getChild("clientName").getValue();  
         }
         
         ClientInfo clientInfo = new ClientInfo(clientId, address, port, clientName);
         
         clients.put(clientId,  clientInfo);
         
         sendRegistrationReplyMessage(clientInfo);
      }
   }
   
   private void handleUnregisterRequestMessage(InetAddress address, int port, XmlNode message) throws XmlFormatException
   {
      int clientId = new InetSocketAddress(address, port).hashCode();
      
      if (clients.containsKey(clientId))
      {
         clients.remove(clientId);
      }
   }
   
   private void sendRegistrationReplyMessage(ClientInfo clientInfo)
   {
      XmlDocument document = new XmlDocument();
      document.createRootNode("message");
      document.getRootNode().setAttribute("messageId",  "register_reply");
      document.getRootNode().appendChild("status", true);
      
      sendData(clientInfo.address, clientInfo.port, document.toString());
   }
   
   private final static Logger logger = Logger.getLogger(Server.class.getName());
   
   private Map<Integer, ClientInfo> clients = new HashMap<>();
   
   private Set<ServerListener> listeners = new HashSet<>();
}