package com.toast.game.engine.collision;

public interface CollisionHandler
{
   public void onCollision(Collision collision);
   
   public void onSeparation(Collidable collided);
}
