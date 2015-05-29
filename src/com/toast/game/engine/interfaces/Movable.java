package com.toast.game.engine.interfaces;

public interface Movable
{
   public void moveBy(
         double deltaX,
         double deltaY);
      
   public void moveTo(
         double x,
         double y);
   
}
