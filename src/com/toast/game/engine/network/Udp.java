package com.toast.game.engine.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlParseException;

public class Udp extends Thread
{
   public Udp()
   {
      listenPort = DEFAULT_LISTEN_PORT;
   }
      
   public void setup(int listenPort)
   {
      this.listenPort = listenPort;
   }
   
   public void start()
   {
      try
      {
         socket = new DatagramSocket(listenPort);
         
         System.out.format("Udp::start(): Listening on port %d\n", listenPort);
         
         isStarted = true;
      }
      catch (SocketException e)
      {
         System.out.format("Udp::start(): Failed to start server on port %d\n", listenPort);
      }
      
      super.start();
   }
   
   public void end()
   {
      isStarted = false;
   }
   
   public void sendData(InetAddress address, int port, String data)
   {
      if (socket != null)
      {
         byte[] sendBuffer = data.getBytes();
         
         DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
         
         try
         {
            socket.send(packet);
         }
         catch (IOException e)
         {
            System.out.format("Failed to send packet.");
         }
      }
   }
   
   public void run()
   {
      while (isStarted)
      {
         // Clear buffer before read.
         buffer = new byte[1024];

         DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
         
         try
         {
            socket.receive(packet);
            
            InetAddress address = packet.getAddress();
            
            int port = packet.getPort();
            
            packet = new DatagramPacket(buffer, buffer.length, address, port);
            
            String received = new String(packet.getData(), 0, packet.getLength());
            
            received = received.substring(0, received.indexOf(0));
            
            onData(address, port, received);
         }
         catch (IOException e)
         {
            
         }
      }

      if (socket != null)
      {
         socket.close();
      }
   }
   
   protected void finalize()
   {
      if (socket != null)
      {
         socket.close();
      }
   }
   
   protected void onData(InetAddress address, int port, String data)
   {
      try
      {
         XmlDocument document = new XmlDocument();
         
         document.parse(data);
         
         handleMessage(address, port, document.getRootNode());
         
      }
      catch (XmlParseException e)
      {
         System.out.format("Got packet from %s:%d: %s\n", address.toString(), port, data);
      }
   }
   
   protected void handleMessage(InetAddress address, int port, XmlNode message)
   {
      // Implement.
   }
   
   private static final int DEFAULT_LISTEN_PORT = 1975;
   
   private boolean isStarted = false;
   
   private int listenPort;
   
   private DatagramSocket socket;
   
   private byte[] buffer = new byte[1024];
}
