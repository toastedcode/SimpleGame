package com.toast.game.engine.actor;

import java.util.HashMap;
import java.util.Map;

import com.toast.game.common.Direction;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.property.AnimationGroup;
import com.toast.game.engine.property.Property;
import com.toast.game.engine.property.Animation;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Player extends Actor
{
   // **************************************************************************
   //                               Enumerations
   // **************************************************************************
   
   public enum Action
   {
      IDLE,
      WALK,
      RUN,
      CROUCH,
      JUMP,
      FIRE,
      DAMAGE,
      DIE
   }
   
   public enum Facing
   {
      UP,
      DOWN,
      LEFT,
      RIGHT,
   }
   
   // **************************************************************************
   //                                Public
   // **************************************************************************
   
   public Player(String id)
   {
      super(id);
   }
   
   /*
   public void setAnimation(String actionId, Direction direction, Animation animation)
   {
      // Validate parameters.
      if ((actionId == null) ||
          (actionId.equals("") == null) ||
          (direction == null))
      {
         throw (new IllegalArgumentException());
      }
      
      if (animations.containsKey(action) == false)
      {
         animations.put(action,  new HashMap<Direction, Animation>());
      }
      
      animations.get(action).put(direction,  animation);
   }
   
   public Animation getAnimation(Action action, Direction direction)
   {
      Animation animation = null;
      
      
      if (animations.containsKey(action) == false)
      {
         animation = animations.get(action).get(direction);
      }
      
      return (animation);
   }
   
   public void setCurrentAnimation(String animationId)
   {
      
   }
   
   public void setCurrentAnimation(Action action, Direction direction)
   {
      
   }
   */
   
   // **************************************************************************
   //                            Actor overrides
   
   // **************************************************************************
   //                         xml.Serializable interface
   
   /*
   <player id="id">
   </player>
   */
   
   public String getNodeName()
   {
      return("player");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode playerNode = super.serialize(node);
      
      return (playerNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
   }
   
   // **************************************************************************
   //                                Protected
   // **************************************************************************
   
   /*
   protected void idle()
   {
      System.out.println("Up");
   }
   
   protected void up()
   {
      System.out.println("Up");
   }
   
   protected void down()
   {
      
   }
   
   protected void left()
   {
      
   }
   
   protected void right()
   {
      
   }
   
   protected void jump()
   {
      
   }
   
   protected void fire()
   {
      
   }
   */
   
   // **************************************************************************
   //                                Private
   // **************************************************************************
   
   public void updateAnimation(Action action, Direction direction)
   {
      AnimationGroup animationGroup = (AnimationGroup)getProperty("playerAnimations");
      
      if (animationGroup != null)
      {
         
      }
   }

   private Map<Action, Map<Direction, Animation>> animations = new HashMap<>();
   
   private Action action = Action.IDLE;
   
   private Facing facing = Facing.RIGHT;
}
