package com.toast.game.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LockableList<E> extends ArrayList<E>
{

   // **************************************************************************
   //                          Public Operations
   // **************************************************************************
   
   // Constructor
   public LockableList()
   {
      super();
      additionList = new ArrayList<E>();
      subtractionList = new ArrayList<Object>();
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

         // React to a call to clear() that occurred while we were locked.
         if (shouldClear == true)
         {
            super.clear();
            shouldClear = false;
         }
         
         // Process any additions or subtractions that occurred while we were locked.
         flushAdditionList();
         flushSubtractionList();
      }
   }
   
   
   public boolean isLocked()
   {
      return (lockDepth > 0);
   }   
   
   
   public boolean add(
      E element)
   {
      boolean returnStatus = false;
      
      if (isLocked() == true)
      {
         returnStatus = additionList.add(element);
      }
      else
      {
         returnStatus = super.add(element);
      }
      
      return (returnStatus);
   }
   
   
   public boolean remove(
      Object object)
   {
      boolean returnStatus = false;
      
      if (contains(object) == true)
      {
         if (isLocked() == true)
         {
            subtractionList.add(object);
         }
         else
         {
            super.remove(object);
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
      additionList.clear();
      subtractionList.clear();
   }
   
   // **************************************************************************
   //                          Private Operations
   // **************************************************************************   
   
   private void flushAdditionList()
   {
      if (isLocked() == false)
      {
         for (E element : additionList)
         {
            add(element);
         }
         
         additionList.clear();         
      }
      
   }   
   
   
   private void flushSubtractionList()
   {
      if (isLocked() == false)
      {
         for (Object object : subtractionList)
         {
            remove(object);
         }
         
         subtractionList.clear();         
      }
      
   }      
  
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
   
   // A list of objects to add into the list.
   // Note: This is necessary so that we can modify the list while iterating.
   private List<E> additionList;
   
   // A list of objects to remove from into the list.
   // Note: This is necessary so that we can modify the list while iterating.
   private List<Object> subtractionList;
   
   // A flag indicating if the list should be cleared, once it has been safely unlocked.
   // Note: This is necessary so we can clear the list while iterating.
   private boolean shouldClear;   
   
   // A counter indicating how many times the map has been recursively locked.
   // The list is considered "ulocked" when the counter is 0.
   private int lockDepth;
}