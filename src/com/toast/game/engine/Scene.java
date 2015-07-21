package com.toast.game.engine;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.toast.game.common.LockableMap;
import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.xml.Serializable;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;
import com.toast.xml.exception.XmlSerializeException;

public class Scene implements Updatable, Serializable
{
   public Scene(String id)
   {
      this.id = id;
   }
   
   public String getId()
   {
      return (id);
   }
   
   public Actor getActor(String id)
   {
      return (actors.get(id));
   }
   
   public void add(Actor actor)
   {
      actors.put(actor.getId(), actor);
   }
   
   public void remove(Actor actor)
   {
      actors.remove(actor.getId());
   }
  
   public void remove(String id)
   {
      actors.remove(id);
   }
   
   public void draw(Renderer renderer)
   {
      drawList.build(actors.values(), new Rectangle(0, 0, 800, 600));
      drawList.draw(renderer);
   }
   
   public void load(File file) throws IOException, XmlParseException, XmlFormatException
   {
      if (file == null)
      {
         throw (new IllegalArgumentException("Null file specified."));
      }
      
      if (file.exists() == false)
      {
         throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", file.toString())));
      }
      else if (file.isFile() == false)
      {
         throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", file.toString())));
      }
      
      XmlDocument document = new XmlDocument();
      
      document.load(file.getAbsolutePath());
      
      deserialize(document.getRootNode());
   }
   
   public void save(File file) throws IOException, XmlSerializeException
   {
      if (file == null)
      {
         throw (new IllegalArgumentException("Null file specified."));
      }
      
      XmlDocument document = new XmlDocument();
      
      XmlNode rootNode = document.createRootNode("game");
      serialize(rootNode);
     
      document.save(file.getAbsolutePath());
   }
   
   // **************************************************************************
   //                           Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      actors.lock();
      
      for (Actor actor : actors.values())
      {
         actor.update(elapsedTime);
      }
      
      actors.unlock();
   }
   
   // **************************************************************************
   //                        xml.Serializable interface

   @Override
   public String getNodeName()
   {
      return ("scene");
   }

   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode sceneNode = node.appendChild(getNodeName());
      
      // id
      sceneNode.setAttribute("id", getId());
      
      // actors
      for (Actor actor : actors.values())
      {
         actor.serialize(sceneNode);
      }
      
      return (sceneNode);
   }

   @Override
   public void deserialize(XmlNode node)
   {
      try
      {
         // id
         id = node.getAttribute("id").getValue();
         
         //
         // actors
         //
         
         XmlNodeList actorNodes = node.getChildren("actor");
         
         for (XmlNode actorNode : actorNodes)
         {
            Actor actor = new Actor(actorNode.getAttribute("id").getValue());
            actor.deserialize(actorNode);
            
            // TODO: This does not register the actor for messaging.
            add(actor);
         }
      }
      catch (XmlFormatException e)
      {
         // TODO:
         System.out.println(e.toString());
      }
   }
   
   private String id;
   
   LockableMap<String, Actor> actors = new LockableMap<>();
   
   DrawList drawList = new DrawList();
}
