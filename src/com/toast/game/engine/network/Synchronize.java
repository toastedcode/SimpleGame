package com.toast.game.engine.network;

import com.toast.game.engine.Game;
import com.toast.game.engine.Scene;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class Synchronize
{
   public static void syncFrom(XmlNode node)
   {
      try
      {
         XmlNode scenesNode = node.getChild("scenes");
         
         XmlNodeList sceneNodes = scenesNode.getChildren("scene");
         
         for (XmlNode sceneNode : sceneNodes)
         {
            String sceneId = sceneNode.getAttribute("id").getValue();
            
            Scene scene = Game.getCurrentScene();
            
            if (scene.getId().equals(sceneId))
            {
               scene.syncFrom(sceneNode);
            }
         }
      }
      catch (XmlFormatException e)
      {
         
      }
   }
   
   public static void syncTo(Client client)
   {
      XmlDocument document = new XmlDocument();
      
      document.createRootNode("message");
      document.getRootNode().setAttribute("messageId",  "sync");
      
      syncTo(document.getRootNode());
      
      client.sendData(document.toString());
   }
   
   public static void syncTo(XmlNode node)
   {
      XmlNode synchNode = node.appendChild("sync");
      
      XmlNode scenesNode = synchNode.appendChild("scenes");
      
      Scene scene = Game.getCurrentScene();
      
      scene.syncTo(scenesNode);
   }
}
