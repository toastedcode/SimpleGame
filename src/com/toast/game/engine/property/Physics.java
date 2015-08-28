package com.toast.game.engine.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toast.game.common.Vector2D;
import com.toast.game.common.XmlUtils;
import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.collision.Collidable;
import com.toast.game.engine.collision.Collision;
import com.toast.game.engine.collision.CollisionHandler;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Physics extends Property implements Updatable, CollisionHandler
{
   // **************************************************************************
   //                                Public
   // **************************************************************************
   
   public Physics(String id)
   {
      super(id);
   }

   public Physics(XmlNode node) throws XmlFormatException
   {
      super(node);
   }
   
   public Property clone()
   {
      Physics clone = new Physics(getId());
      
      clone.isEnabled = isEnabled;
      clone.mass = mass;
      clone.setVelocity(velocity);
      clone.setAcceleration(acceleration);
      clone.setGravity(gravity);
      clone.drag = drag;
      clone.friction = friction;
      clone.elasticity = elasticity;
      clone.viscosity = viscosity;
      
      return (clone);
   }
   
   public boolean isEnabled()
   {
      return isEnabled;
   }

   public void setEnabled(boolean isEnabled)
   {
      this.isEnabled = isEnabled;
   }

   public int getMass()
   {
      return (mass);
   }

   public void setMass(int mass)
   {
      this.mass = mass;
   }

   public Vector2D getVelocity()
   {
      return (velocity);
   }

   public void setVelocity(Vector2D velocity)
   {
      this.velocity = velocity.clone();
   }

   public Vector2D getAcceleration()
   {
      return (acceleration);
   }

   public void setAcceleration(Vector2D acceleration)
   {
      this.acceleration = acceleration.clone();
   }

   public Vector2D getGravity()
   {
      return (gravity);
   }

   public void setGravity(Vector2D gravity)
   {
      this.gravity = gravity.clone();
   }

   public double getDrag()
   {
      return (drag);
   }

   public void setDrag(double drag)
   {
      this.drag = drag;
   }

   public double getFriction()
   {
      return (friction);
   }

   public void setFriction(double friction)
   {
      this.friction = friction;
   }

   public double getElasticity()
   {
      return (elasticity);
   }

   public void setElasticity(double elasticity)
   {
      this.elasticity = elasticity;
   }
   
   public double getViscosity()
   {
      return (viscosity);
   }

   public void setViscosity(double viscosity)
   {
      this.viscosity = viscosity;
   }
   
   public boolean isCollided(Actor actor)
   {
      return (collisions.containsKey(actor.getId()));
   }
   
   // **************************************************************************
   //                        CollisionManager interface
   
   @Override
   public void onCollision(Collision collision)
   {
      Actor actor = getParent();
      Actor collidedActor = (Actor)collision.getOther(actor);
   }
   
   @Override
   public void onSeparation(Collidable collided)
   {
      Actor collidedActor = (Actor)collided;
   }

   // **************************************************************************
   //                           Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      double elapsedSeconds = (double)elapsedTime / (double)MILLISECONDS_PER_SECOND;
      
      // Apply gravity.
      velocity.x += gravity.x * elapsedSeconds;  
      velocity.y += gravity.y * elapsedSeconds;
      
      // Apply acceleration.
      velocity.x += acceleration.x * elapsedSeconds;  
      velocity.y += acceleration.y * elapsedSeconds;
      
      // Apply drag.
      velocity.x += velocity.x * -1.0 * (drag * elapsedSeconds);
      
      // Apply viscosity
      for (Map.Entry<Actor, Collision> entry : collisions.entrySet())
      {
         Physics physics = entry.getKey().getPhysics();
         
         if ((physics != null) && (physics.isEnabled()))
         {
            Vector2D.multiply(velocity,  (physics.getElasticity() * -1));
         }
      }
      
      // Determine the translation.
      double deltaX = velocity.x * elapsedSeconds;
      double deltaY = velocity.y * elapsedSeconds;
      
      getParent().moveBy(deltaX, deltaY);
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <motor id="">
      <keyMap>
         <key keyId=""></key>
      </keyMap>
   </motor>
   */
   
   @Override
   public String getNodeName()
   {
      return("physics");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // isEnabled
      propertyNode.appendChild("isEnabled", isEnabled);
      
      // mass
      propertyNode.appendChild("mass", mass);
      
      // velocity
      XmlNode vectorNode = propertyNode.appendChild("velocity");
      vectorNode.appendChild("x", velocity.x);
      vectorNode.appendChild("y", velocity.y);
      
      // velocity
      vectorNode = propertyNode.appendChild("acceleration");
      vectorNode.appendChild("x", acceleration.x);
      vectorNode.appendChild("y", acceleration.y);
      
      // velocity
      vectorNode = propertyNode.appendChild("gravity");
      vectorNode.appendChild("x", gravity.x);
      vectorNode.appendChild("y", gravity.y);
      
      // drag
      propertyNode.appendChild("drag", drag);
      
      // friction
      propertyNode.appendChild("friction", friction);
      
      // elasticity
      propertyNode.appendChild("elasticity", elasticity);
      
      // elasticity
      propertyNode.appendChild("viscosity", viscosity);

      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      // isEnabled
      isEnabled = node.getChild("isEnabled").getBoolValue();
      
      // mass
      mass = node.getChild("mass").getIntValue();
      
      // velocity
      velocity = XmlUtils.getVector(node.getChild("vecocity"));
      
      // acceleration
      acceleration = XmlUtils.getVector(node.getChild("acceleration"));
      
      // gravity
      gravity = XmlUtils.getVector(node.getChild("gravity"));
      
      // drag
      drag = node.getChild("drag").getDoubleValue();
      
      // friction
      friction = node.getChild("friction").getDoubleValue();
      
      // elasticity
      elasticity = node.getChild("elasticity").getDoubleValue();
      
      // viscosity
      viscosity = node.getChild("viscosity").getDoubleValue();
   }
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
   
   private static final int MILLISECONDS_PER_SECOND = 1000;
   
   private boolean isEnabled;
   
   private int mass;
   
   private Vector2D velocity = new Vector2D(0, 0);

   private Vector2D acceleration = new Vector2D(0, 0);
   
   private Vector2D gravity = new Vector2D(0, 0);
   
   private double drag;
   
   private double friction;
   
   private double elasticity;
   
   private double viscosity;
   
   private Map<Actor, Collision> collisions = new HashMap<>();
}
