package com.toast.game.engine.collision;

import java.awt.Shape;

import com.toast.game.common.Vector2D;

public interface Collidable
{
   public boolean isCollisionEnabled();

   public Shape getCollisionShape();
   
   public void onCollision(Collision collision);
   
   public void onSeparation(Collidable collidable);
}
