package com.toast.game.engine.collision;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.toast.game.common.Pair;

public class CollisionManager
{
   static public void register(Collidable collidable)
   {
      if (!collidables.contains(collidable))
      {
         collidables.add(collidable);
      }
   }
   
   static public void unregister(Collidable collidable)
   {
      collidables.remove(collidable);
   }
   
   static public void update(long elapsedTime)
   {
      // Get list of collisions.
      List<Collision> newCollisions = calculateCollisions();
      
      updateCollisions(newCollisions);
   }
   
   static public List<Collidable> checkIntersection(Point2D point)
   {
      List<Collidable> intersections = new ArrayList<Collidable>();
      
      for (Collidable collidable : collidables)
      {
         if (collidable.isCollisionEnabled() &&
             collidable.getCollisionShape().contains(point))
         {
            intersections.add(collidable);
         }
      }
      
      return (intersections);
   }
   
   static public boolean isCollided(Collidable collidableA, Collidable collidableB)
   {
      return (getCollision(collidableA, collidableB) != null);
   }
   
   static public Collision getCollision(Collidable collidableA, Collidable collidableB)
   {
      Collision collision = null;
      
      Map<Collidable, Collision> collisionMap = collisions.get(collidableA);
      if (collisionMap != null)
      {
         collision = collisionMap.get(collidableB);
      }
      
      return (collision);
   }
   
   static public Collection<Collision> getCollisions(Collidable collidable)
   {
      Collection<Collision> currentCollisions = new ArrayList<Collision>();
      
      Map<Collidable, Collision> collisionMap = collisions.get(collidable);
      if (collisionMap != null)
      {
         currentCollisions = collisionMap.values();
      }
      
      return (currentCollisions);
   }
   
   static private List<Collision> calculateCollisions()
   {
      List<Collision> newCollisions = broadPhase(collidables);
      
      newCollisions = narrowPhase(newCollisions);
      
      return (newCollisions);
   }

   
   static private void updateCollisions(List<Collision> newCollisions)
   {
      Set<Collision> currentCollisions = getCollisionSet();
      
      //
      // Separations
      //
      
      for (Collision collision : currentCollisions)
      {
         if (!newCollisions.contains(collision))
         {
            removeCollision(collision);
            
            collision.first().onSeparation(collision.second());
            collision.second().onSeparation(collision.first());
         }
      }
      
      //
      // New/updated collisions
      //
      
      /*
      for (Collision collision : newCollisions)
      {
         Collision currentCollison = getCollision(collision.first(), collision.second());
         
         boolean newCollision = (currentCollison == null);
         boolean updatedCollision = ((!newCollision) && (!collision.equals(currentCollison)));
         
         if (newCollision || updatedCollision)
         {
            addCollision(collision);

            collision.first().onCollision(collision);
            collision.second().onCollision(collision);
         }
      }
      */
      for (Collision collision : newCollisions)
      {
         Collision currentCollison = getCollision(collision.first(), collision.second());
         
         boolean newCollision = (currentCollison == null);
         boolean updatedCollision = ((!newCollision) && (!collision.equals(currentCollison)));
         
         if (newCollision || updatedCollision)
         {
            addCollision(collision);
         }

         collision.first().onCollision(collision);
         collision.second().onCollision(collision);
      }
   }
   /*
   static private void updateCollisions(List<Collision> newCollisions)
   {
      // Remember old collisions, temporarily.
      Set<Collision> previousCollisions = getCollisionSet();
      
      // Create a new collision map to populate.
      collisions = new HashMap<>();
      
      //
      // New/updated collisions
      //
      
      for (Collision collision : newCollisions)
      {
         Collision currentCollison = getCollision(collision.first(), collision.second());
         
         boolean newCollision = (currentCollison == null);
         boolean updatedCollision = ((!newCollision) && (!collision.equals(currentCollison)));
         
         if (newCollision || updatedCollision)
         {
            addCollision(collision);

            collision.first().onCollision(collision);
            collision.second().onCollision(collision);
         }
      }
      
      //
      // Separations
      //
      
      for (Collision collision : previousCollisions)
      {
         if (!isCollided(collision.first(), collision.second()))
         {
            collision.first().onSeparation(collision.second());
            collision.second().onSeparation(collision.first());
         }
      }
   }
   */
   
   static private List<Collision> broadPhase(List<Collidable> collidables)
   {
      // TODO:
      // Use quadtree to reduce comparisons.
      
      List<Collision> broadCollisions = new ArrayList<>();
      
      List<Pair<Collidable>> collidablePairs = getPairs(collidables);
      
      for (Pair<Collidable> pair : collidablePairs)
      {
         if (pair.first().isCollisionEnabled() && pair.second().isCollisionEnabled())
         {
            if (pair.first().getCollisionShape().getBounds2D().intersects(
                  pair.second().getCollisionShape().getBounds2D()))
            {
               Collision collision = new Collision(pair.first(), pair.second());
               broadCollisions.add(collision);
            }
         }
      }
      
      return (broadCollisions);
   }
   
   static private List<Collision> narrowPhase(List<Collision> broadcollisions)
   {
      List<Collision> narrowCollisions = new ArrayList<>();
      
      // TODO
      narrowCollisions = new ArrayList<Collision>(broadcollisions);
      
      return (narrowCollisions);
   }
   
   static private void addCollision(Collision collision)
   {
      addCollision(collision.first(), collision);
      addCollision(collision.second(), collision);
   }
   
   static private void addCollision(Collidable collidable, Collision collision)
   {
      // Create a interior Map, if necessary.
      if (collisions.get(collidable) == null)
      {
         collisions.put(collidable,  new HashMap<Collidable, Collision>());
      }
      
      collisions.get(collidable).put(collision.getOther(collidable), collision);
   }
   
   static private void removeCollision(Collision collision)
   {
      removeCollision(collision.first(), collision);
      removeCollision(collision.second(), collision);
   }
   
   static private void removeCollision(Collidable collidable, Collision collision)
   {
      // Create a interior Map, if necessary.
      if (collisions.get(collidable) != null)
      {
         collisions.get(collidable).remove(collision.getOther(collidable));
      }
   }
   
   static private Set<Collision> getCollisionSet()
   {
      Set<Collision> collisionSet = new HashSet<>();
      
      for (Map.Entry<Collidable, Map<Collidable, Collision>> entry : collisions.entrySet())
      {
         if (entry.getValue() != null)
         {
            for (Collision collision : entry.getValue().values())
            {
               collisionSet.add(collision);
            }
         }
      }
      
      return (collisionSet);
   }
   
   static private <T> List<Pair<T>> getPairs(List<T> list)
   {
      List<Pair<T>> pairs = new ArrayList<>();
      
      for (int i = 0; i < list.size(); i++)
      {
         for (int j = (i + 1); j < list.size(); j++)
         {
            pairs.add(new Pair<T>(list.get(i), list.get(j)));
         }
      }
      
      return (pairs);
   }
   
   static List<Collidable> collidables = new ArrayList<>();
   
   static Map<Collidable, Map<Collidable, Collision>> collisions = new HashMap<>();
}
