package com.toast.game.engine.property;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.property.Animation;
import com.toast.game.engine.property.Animation.AnimationDirection;
import com.toast.game.engine.property.Animation.AnimationType;

public class AnimationGroup extends Property implements Updatable, Drawable
{
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public AnimationGroup(String id)
   {
      super(id);
   }
   
   public void addAnimation(Animation animation)
   {
      if (animation == null)
      {
         throw (new IllegalArgumentException());
      }

      animations.put(animation.getId(), animation);
      
      dimension.setSize(Math.max(animation.getWidth(), dimension.getWidth()),
                        Math.max(animation.getHeight(), dimension.getHeight()));
   }

   public void setAnimation(String animationId, AnimationType animationType, AnimationDirection animationDirection)
   {
      if ((animationId == null) ||
          (animationId.equals("")))
      {
         currentAnimation = null;
      }
      else if (animations.containsKey(animationId) == true)
      {
         currentAnimation = animations.get(animationId);
         currentAnimation.start(AnimationType.NORMAL, AnimationDirection.FORWARD);
      }
      else
      {
         // Invalid animation id.  Ignore.
      }      
   }
   
   // **************************************************************************
   //                          Updatable interface

   @Override
   public void update(long elapsedTime)
   {
      if (currentAnimation != null)
      {
         currentAnimation.update(elapsedTime);
      }
   }
   
   // **************************************************************************
   //                          Drawable interface

   @Override
   public int getWidth()
   {
      return ((int)dimension.getWidth());
   }

   @Override
   public int getHeight()
   {
      return ((int)dimension.getHeight());
   }

   @Override
   public boolean isVisible()
   {
      return (isVisible);
   }

   @Override
   public void setVisible(boolean isVisible)
   {
      this.isVisible = isVisible;
   }
   
   @Override
   public void draw(Graphics graphics)
   {
      if (currentAnimation != null)
      {
         currentAnimation.draw(graphics);
      }
   }
   
   // **************************************************************************
   //                                Protected
   // **************************************************************************
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   Map<String, Animation> animations = new HashMap<>();
   
   Animation currentAnimation = null;

   private boolean isVisible = true;
   
   private Dimension dimension = new Dimension();
}
