package com.toast.game.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class LockableMap<K, E> extends HashMap<K, E>
{

   // **************************************************************************
   //                          Public Operations
   // **************************************************************************
   
   // Constructor
   public LockableMap()
   {
      super();
      additions = new HashMap<K, E>();
      subtractions = new ArrayList<Object>();
      lockDepth = 0;
   }
   
   
   public void lock()
   {
      lockDepth++;
   }
   
   
   public void unlock()
   {
      if (isLocked() == true)
      {
         lockDepth--;
         
         if (lockDepth == 0)
         {
            // React to a call to clear() that occurred while we were locked.
            if (shouldClear == true)
            {
               super.clear();
               shouldClear = false;
            }         
   
            // Process any additions or subtractions that occurred while we were locked.
            flushAdditions();
            flushSubtractions();
         }
      }
   }
  
   
   public boolean isLocked()
   {
      return (lockDepth > 0);
   }
  
   
   public E put(
      K key,
      E value)
   {
      E returnStatus = null;
      
      if (isLocked() == true)
      {
         additions.put(key, value);
         returnStatus = value;
      }
      else
      {
         return super.put(key, value);
      }
      
      return (returnStatus);
   }
   
   
   public E remove(
      Object key)
   {
      E returnStatus = null;
      
      if (containsKey(key) == true)
      {
         if (isLocked() == true)
         {
            subtractions.add(key);
         }
         else
         {
            super.remove(key);
         }
      }
      
      return (returnStatus);
   }
   
   
   public void clear()
   {
      if (isLocked() == true)
      {
         shouldClear = true;
      }
      else
      {
         super.clear();
      }

      // Clear any pending additions/subtractions.
      additions.clear();
      subtractions.clear();
   }
   
   
   public Set<Map.Entry<K, E>> entrySet()
   {
      return (super.entrySet());
   }
   
   // **************************************************************************
   //                          Private Operations
   // **************************************************************************   
   
   private void flushAdditions()
   {
      if (isLocked() == false)
      {
         Iterator<Map.Entry<K, E>> it = additions.entrySet().iterator();
         
         while (it.hasNext())
         {
            Map.Entry<K, E> pair = it.next();
            put(pair.getKey(), pair.getValue());
        }
       
        additions.clear();         
      }
   }   
   
   
   private void flushSubtractions()
   {
      if (isLocked() == false)
      {
         for (Object key : subtractions)
         {
            remove(key);
         }
       
        subtractions.clear();         
      }      
   }      
  
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
   
   private Map<K, E> additions;

   private List<Object> subtractions;

   private boolean shouldClear;      

   private int lockDepth;
}