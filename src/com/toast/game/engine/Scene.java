package com.toast.game.engine;

import java.awt.Rectangle;

import com.toast.game.common.LockableMap;
import com.toast.game.engine.actor.Actor;
import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;

public class Scene implements Serializable
{
   public Scene(String id)
   {
      this.id = id;
   }
   
   public String getId()
   {
      return (id);
   }
   
   public void update(long elapsedTime)
   {
      actors.lock();
      
      for (Actor actor : actors.values())
      {
         actor.update(elapsedTime);
      }
      
      actors.unlock();
   }
   
   public void draw(Renderer renderer)
   {
      drawList.build(actors.values(), new Rectangle(0, 0, 800, 600));
      drawList.draw(renderer);
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
      // id
      id = node.getAttribute("id");
      
      //
      // actors
      //
      
      XmlNodeList actorNodes = node.getChildren("actor");
      
      for (int i = 0; i < actorNodes.getLength(); i++)
      {
         XmlNode actorNode = actorNodes.item(i);
         
         Actor actor = new Actor(actorNode.getAttribute("id"));
         actor.deserialize(actorNode);
         
         // TODO: This does not register the actor for messaging.
         add(actor);
      }
   }
   
   private String id;
   
   LockableMap<String, Actor> actors = new LockableMap<>();
   
   DrawList drawList = new DrawList();
}
