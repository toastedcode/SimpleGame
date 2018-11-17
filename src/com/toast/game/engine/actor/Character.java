package com.toast.game.engine.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.toast.game.engine.property.Animation.AnimationDirection;
import com.toast.game.engine.property.Animation.AnimationType;
import com.toast.game.engine.property.AnimationGroup;
import com.toast.game.engine.property.Property;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.XmlUtils;
import com.toast.xml.exception.XmlFormatException;

import jdk.nashorn.internal.runtime.regexp.joni.BitSet;

public class Character extends Actor
{
   // **************************************************************************
   //                                Public
   // **************************************************************************
   
   public Character(String id)
   {
      super(id);
   }
   
   public Character(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
      
      updateAnimation();
   }
   
   public String getState()
   {
      return (state);
   }
   
   public void setState(String state)
   {
      if (this.state != state)
      {
         this.state = state;
      
         updateAnimation();
      }
   }
   
   // **************************************************************************
   //                            Actor overrides
   
   public void add(Property property)
   {
      super.add(property);
      
      if (property instanceof AnimationGroup)
      {
         // By convention, a Character must have a single animation group.
         animationGroup = (AnimationGroup)property;
      }
   }
   
   // **************************************************************************
   //                         xml.Serializable interface
   
   /*
   <character id="id">
      <state/>
      <direction/>
   </character>
   */
   
   public String getNodeName()
   {
      return("character");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode playerNode = super.serialize(node);
      
      // state
      if ((state != null) && (!state.isEmpty()))
      {
         playerNode.appendChild("state",  state);
      }
      
      // states
      if (!states.isEmpty())
      {
         XmlNode statesNode = playerNode.appendChild("states");
         
         for (String state : states)
         {
            statesNode.appendChild("state").setAttribute("id",  state);
         }
      }
      
      return (playerNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
   // **************************************************************************
   //                           Syncable interface
   
   public XmlNode syncTo(XmlNode node)
   {
      XmlNode characterNode = super.syncTo(node);
      
      // state
      if (changeSet.at(SyncableProperties.STATE.ordinal()))
      {
         characterNode.appendChild("state").setValue(state);
      }
      
      return (characterNode);      
   }
   
   public void syncFrom(XmlNode node) throws XmlFormatException
   {
      super.syncFrom(node);
      
      // state
      if (node.hasAttribute("state"))
      {
         state = node.getChild("state").getValue();
      }
   }   
   
   // **************************************************************************
   //                                Protected
   // **************************************************************************
   
   protected void updateAnimation()
   {
      if ((state != null) &&
          (animationGroup != null))
      {
         // By convention, animations should have the same id as their associated state.
         animationGroup.setAnimation(state, AnimationType.LOOP, AnimationDirection.FORWARD);
      }
   }
   
   // **************************************************************************
   //                                Private
   // **************************************************************************
   
   public void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // state
      state = node.getChild("state").getValue();
      
      // states
      if (node.hasChild("states"))
      {
         XmlNodeList stateNodes = node.getChild("states").getChildren("state");
         
         for (XmlNode stateNode : stateNodes)
         {
            states.add(stateNode.getAttribute("id").getValue());
         }
      }
   }
   
   private enum SyncableProperties
   {
      STATE,      
   }
   
   private List<String> states = new ArrayList<>();
   
   private String state;
   
   private AnimationGroup animationGroup;
   
   private BitSet changeSet = new BitSet();
}
