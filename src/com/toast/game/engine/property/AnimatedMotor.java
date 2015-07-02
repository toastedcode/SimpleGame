package com.toast.game.engine.property;

import java.util.HashMap;
import java.util.Map;

public class AnimatedMotor extends Motor
{
   public AnimatedMotor(String id)
   {
      super(id);
   }
   
   public void mapAnimation(Motor.Direction direction, Animation animation)
   {
      animations.put(direction,  animation);
   }
   
   // **************************************************************************
   //                                 Protected
   // **************************************************************************
  
   protected void up()
   {
      super.up();
      
      if (animations.containsKey(Direction.UP) == true)
      {
         
      }
   }
   
   protected void down()
   {
      super.up();
      
      if (animations.containsKey(Direction.DOWN) == true)
      {
         setAnimation(animations.get(Direction.DOWN));
      }
   }
   
   protected void left()
   {
      super.up();
      
      if (animations.containsKey(Direction.LEFT) == true)
      {
         
      }
   }
   
   protected void right()
   {
      super.up();
      
      if (animations.containsKey(Direction.RIGHT) == true)
      {
         
      }
   }
   
   private void setAnimation(Animation animation)
   {
      
   }

   Map<Motor.Direction, Animation> animations = new HashMap<>();
}
