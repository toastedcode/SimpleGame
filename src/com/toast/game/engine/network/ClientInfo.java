package com.toast.game.engine.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientInfo
{
   public ClientInfo(
      int clientId,
      InetAddress address,
      int port,
      String name)
   {
      this.clientId = clientId;
      this.address = address;
      this.port = port;
      this.name = name;
      
      lastContact = 0;
   }
   
   public static int getClientId(
      InetAddress address,
      int port)
   {
      return (new InetSocketAddress(address, port).hashCode());
   }
      
   public int clientId;
   
   public InetAddress address;
   
   public int port;
   
   public String name;
   
   public long lastContact;
}
