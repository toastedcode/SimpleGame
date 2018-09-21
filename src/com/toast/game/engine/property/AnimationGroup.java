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
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class AnimationGroup extends Property implements Updatable, Drawable
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
      }
      else if (animations.containsKey(animationId) == true)
      {
         currentAnimation = animations.get(animationId);
         currentAnimation.start(animationType, animationDirection);
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
   
   Map<String, Animation> animations = new HashMap<>();
   
   Animation currentAnimation = null;
   
   private Dimension dimension = new Dimension();
}
