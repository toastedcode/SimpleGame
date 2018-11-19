package com.toast.game.engine.property;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Syncable;
import com.toast.game.engine.property.Animation;
import com.toast.game.engine.property.Animation.AnimationDirection;
import com.toast.game.engine.property.Animation.AnimationType;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class AnimationGroup extends Property implements Updatable, Drawable, Syncable
{
   // **************************************************************************
   //                                 Public
   // **************************************************************************
   
   public AnimationGroup(String id)
   {
      super(id);
   }
   
   public AnimationGroup(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
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
         
         changeSet.set(SyncableProperties.CURRENT_ANIMATION.ordinal());
      }
      else if (animations.containsKey(animationId) == true)
      {
         currentAnimation = animations.get(animationId);
         currentAnimation.start(animationType, animationDirection);
         
         changeSet.set(SyncableProperties.CURRENT_ANIMATION.ordinal());
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
      super.update(elapsedTime);
      
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
   public void draw(Graphics graphics)
   {
      if (currentAnimation != null)
      {
         currentAnimation.draw(graphics);
      }
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <animation id="walk">
      <image></image>
      <animationMap></animationMap>
      <frameRate></frameRate>
      <isVisible></isVisible>
   </animation>
   */
   
   @Override
   public String getNodeName()
   {
      return("animationGroup");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // animations
      for (Animation animation : animations.values())
      {
         animation.serialize(propertyNode);
      }

      // currrentAnimation
      if (currentAnimation != null)
      {
         XmlNode childNode = node.appendChild("currentAnimation"); 
         childNode.setAttribute("id", currentAnimation.getId());
         childNode.setAttribute("type", currentAnimation.getAnimationType());
         childNode.setAttribute("direction", currentAnimation.getAnimationDirection());
      }

      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
   // **************************************************************************
   //                           Syncable interface
   
   public boolean isChanged()
   {
      boolean isChanged = !changeSet.isEmpty();
      
      for (Animation animation : animations.values())
      {
         isChanged |= animation.isChanged();
      }
      
      return (isChanged);
   }
   
   public XmlNode syncTo(XmlNode node)
   {
      XmlNode animationGroupNode = super.syncTo(node);
      
      // currentAnimation
      if (changeSet.at(SyncableProperties.CURRENT_ANIMATION.ordinal()))
      {
         if (currentAnimation != null)
         {
            animationGroupNode.appendChild("currentAnimation").setAttribute("id", currentAnimation.getId());
         }
         else
         {
            animationGroupNode.appendChild("currentAnimation").setAttribute("id", "");
         }
      }
      
      for (Animation animation : animations.values())
      {
         if (animation.isChanged())
         {
            animation.syncTo(animationGroupNode);
         }
      }
      
      return (animationGroupNode);      
   }
   
   public void syncFrom(XmlNode node) throws XmlFormatException
   {
      // animationDirection
      if (node.hasChild("currentAnimation"))
      {
         String animationId = node.getChild("currentAnimation").getAttribute("id").getValue();
         
         if (animationId.isEmpty())
         {
            currentAnimation = null;
         }
         else
         {
            currentAnimation = animations.get(animationId);
         }
      }
      
      XmlNodeList animationNodes = node.getChildren("animation");
      
      for (XmlNode animationNode : animationNodes)
      {
         String animationId = animationNode.getAttribute("id").getValue();
         
         Animation animation = animations.get(animationId);
         
         if (animation != null)
         {
            animation.syncFrom(animationNode);
         }
      }
   }
   
   // **************************************************************************
   //                                Protected
   // **************************************************************************
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   public void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // animations
      XmlNodeList animationNodes = node.getChildren("animation");
      for (XmlNode animationNode : animationNodes)
      {
         addAnimation(new Animation(animationNode));
      }
      
      // currentAnimation
      if (node.hasChild("currentAnimation"))
      {
         XmlNode childNode = node.getChild("currentAnimation");
         
         String animationId = childNode.getAttribute("id").getValue();
         AnimationType animationType = AnimationType.valueOf(childNode.getAttribute("type").getValue());
         AnimationDirection animationDirection = AnimationDirection.valueOf(childNode.getAttribute("direction").getValue());
         
         setAnimation(animationId, animationType, animationDirection);
      }
   }
   
   private enum SyncableProperties
   {
      CURRENT_ANIMATION
   }
   
   private Map<String, Animation> animations = new HashMap<>();
   
   private Animation currentAnimation = null;
   
   private Dimension dimension = new Dimension();
}
