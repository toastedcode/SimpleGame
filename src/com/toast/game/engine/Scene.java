package com.toast.game.engine;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import com.toast.game.engine.actor.Actor;

public class Scene
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
      for (Actor actor : actors.values())
      {
         actor.update(elapsedTime);
      }
   }
   
   public void draw(Renderer renderer)
   {
      drawList.build(actors.values(), new Rectangle(0, 0, 800, 600));
      drawList.draw(renderer);
   }
   
   public void add(Actor actor)
   {
      actors.put(actor.getId(), actor);
   }
   
   public void remove(Actor actor)
   {
      actors.remove(actor);
   }
   
   private String id;
   
   Map<String, Actor> actors = new HashMap<>();
   
   DrawList drawList = new DrawList();
}
