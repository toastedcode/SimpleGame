package com.toast.game.engine.collision;

import com.toast.game.common.Pair;

public class Collision extends Pair<Collidable>
{
   public Collision(Collidable first, Collidable second)
   {
      super(first, second);
   }
   
   public Collidable getOther(Collidable collidable)
   {
      Collidable other = null;
      
      if (collidable == first())
      {
         other = second();
      }
      else if (collidable == second())
      {
         other = first();
      }
      
      return (other);
   }
   
   public boolean contains(Collidable collidable)
   {
      return ((collidable == first()) ||
              (collidable == second()));
   }
   
   @Override
   public boolean equals(Object collision)
   {
      return ((collision != null) &&
              contains(((Collision)collision).first()) && 
              contains(((Collision)collision).second()));
   }
}
