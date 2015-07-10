package com.toast.game.engine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import com.toast.game.engine.actor.Actor;

@SuppressWarnings("serial")
public class DrawList extends ArrayList<Actor>
{
   public void build(Collection<Actor> actors, Rectangle bounds)
   {
      clear();
      
      for (Actor actor : actors)
      {
         if (actor.getBounds().intersects(bounds))
         {
            super.add(actor);
         }
      }
      
      sort(Z_ORDER_DESCENDING_COMPARATOR);
   }
   
   @Override
   public boolean add(Actor actor)
   {
      boolean returnStatus = super.add(actor);
      if (returnStatus == true)
      {
         sort(Z_ORDER_DESCENDING_COMPARATOR);
      }
      
      return (returnStatus);
   }
      
   public void draw(Renderer renderer)
   {
      for (Actor actor : this)
      {
         actor.draw(renderer);
      }
   }
      
   // **************************************************************************
   //                           Private Attributes
   // **************************************************************************   
   
   private static final Comparator<Actor> Z_ORDER_DESCENDING_COMPARATOR = new Comparator<Actor>()
   {
      public int compare(Actor actor1, Actor actor2)
      {
         return(((actor1.getZOrder() > actor2.getZOrder()) ? -1 :
                 (actor1.getZOrder() == actor2.getZOrder()) ? 0 : 1));
      }
   };
}
