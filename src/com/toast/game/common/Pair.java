package com.toast.game.common;

public class Pair<T>
{
   public Pair(T first, T second)
   {
      this.first = first;
      this.second = second;
   }
   
   public T first()
   {
      return (first);
   }
   
   public T second()
   {
      return (second);
   }
   
   private T first;
   
   private T second;
}