package com.toast.game.engine.actor;

import com.toast.game.engine.Game;
import com.toast.game.engine.property.Property;
import com.toast.game.engine.property.Script;

public class Generator extends Actor
{
   public Generator(String id)
   {
      super(id);
   }
   
   public void setFrequency(int frequency)
   {
      this.frequency = frequency;
   }
   
   public void setActor(Actor actor)
   {
      this.actor = actor;
   }
   
   @Override
   public void add(Property property)
   {
      super.add(property);
      
      if (property instanceof Script)
      {
         script = (Script)property;
      }
   }
   
   @Override
   public void update(long elapsedTime)
   {
      if (isEnabled() == true)
      {
         this.elapsedTime += elapsedTime;
         
         if (this.elapsedTime >= frequency)
         {
            generate();
            this.elapsedTime = 0;
         }
      }
   }
   
   public void generate()
   {
      Actor clone = actor.clone(getNextId());
      
      Game.add(clone);
      
      initializeClone(clone);
      
      generateCount++;
   }
   
   protected void initializeClone(Actor clone)
   {
      if (script != null)
      {
         script.evaluate("initializeClone", new Script.Variable("actor", this), new Script.Variable("clone", clone));
      }
   }
   
   private String getNextId()
   {
      return (actor.getId() + "_" + String.valueOf(generateCount)); 
   }
   
   private Script script;
   
   private Actor actor;
   
   private int generateCount;
   
   private double frequency;
   
   private double elapsedTime;
}
