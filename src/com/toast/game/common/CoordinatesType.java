package com.toast.game.common;

public enum CoordinatesType
{
   WORLD,
   SCREEN,
   OFFSET;
   
   public String toString()
   {
      return (name().charAt(0) + name().substring(1).toLowerCase());
  }
}
