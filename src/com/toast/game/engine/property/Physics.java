package com.toast.game.engine.property;

import java.awt.Rectangle;
import java.util.HashMap;
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
      
      deserializeThis(node);         
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
   
   public void addVelocity(Vector2D velocity)
   {
      this.velocity = Vector2D.add(this.velocity, velocity);
   }

   public Vector2D getAcceleration()
   {
      return (acceleration);
   }

   public void setAcceleration(Vector2D acceleration)
   {
      this.acceleration = acceleration.clone();
   }
   
   public Vector2D getThrust()
   {
      return (thrust);
   }

   public void setThrust(Vector2D thrust)
   {
      this.thrust = thrust.clone();
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
   
   // **************************************************************************
   //                        CollisionManager interface
   
   @Override
   public void onCollision(Collision collision)
   {
      Actor actor = getParent();
      Actor collidedActor = (Actor)collision.getOther(actor);
      
      // Resolve collision.
      resolveCollision(collision);
      
      // Adjust velocity.
      velocity.x = 0;
      velocity.y = 0;
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
      
      // Apply acceleration.
      thrust.x += (acceleration.x * elapsedSeconds);  
      thrust.y += (acceleration.y * elapsedSeconds);
      
      // Apply thrust
      //velocity.
      
      // Apply gravity.
      velocity.x += (gravity.x * elapsedSeconds);  
      velocity.y += (gravity.y * elapsedSeconds);
      
      // Apply drag.
      velocity.x += velocity.x * -1.0 * (drag * elapsedSeconds);
      
      // Apply viscosity
      /*
      for (Map.Entry<Actor, Collision> entry : collisions.entrySet())
      {
         Physics physics = entry.getKey().getPhysics();
         
         if ((physics != null) && (physics.isEnabled()))
         {
            Vector2D.multiply(velocity,  (physics.getViscosity() * -1));
         }
      }
      */
      
      // Determine the translation.
      Vector2D delta = new Vector2D((velocity.x * elapsedSeconds), (velocity.y * elapsedSeconds));
      
      if (delta.getMagnitude() > 0)
      {
         getParent().moveBy(delta.x, delta.y);
      }
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <physics id="">
      <isEnabled/>
      <mass/>
      <velocity x="" y=""/>
      <acceleration x="" y=""/>
      <gravity x="" y=""/>
      <drag/>
      <friction/>
      <elasticity/>
      <viscosity/>
   </physics>
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
      if (mass > 0)
      {
         propertyNode.appendChild("mass", mass);
      }
      
      // velocity
      XmlNode vectorNode = propertyNode.appendChild("velocity");
      vectorNode.setAttribute("x",  velocity.x);
      vectorNode.setAttribute("y",  velocity.y);
      
      // velocity
      vectorNode = propertyNode.appendChild("acceleration");
      vectorNode.setAttribute("x",  acceleration.x);
      vectorNode.setAttribute("y",  acceleration.y);
      
      // velocity
      vectorNode = propertyNode.appendChild("gravity");
      vectorNode.setAttribute("x",  gravity.x);
      vectorNode.setAttribute("y",  gravity.y);
      
      // drag
      if (drag > 0)
      {
         propertyNode.appendChild("drag", drag);
      }
      
      // friction
      if (friction > 0)
      {
         propertyNode.appendChild("friction", friction);
      }
      
      // elasticity
      if (elasticity > 0)
      {
         propertyNode.appendChild("elasticity", elasticity);
      }
      
      // viscosity
      if (viscosity > 0)
      {
         propertyNode.appendChild("viscosity", viscosity);
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
   //                          Private Attributes
   // **************************************************************************
   
   public void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // isEnabled
      isEnabled = node.getChild("isEnabled").getBoolValue();
      
      // mass
      if (node.hasChild("mass"))
      {
         mass = node.getChild("mass").getIntValue();
      }
      
      // velocity
      if (node.hasChild("velocity"))
      {
         velocity = XmlUtils.getVector(node.getChild("velocity"));
      }
      
      // acceleration
      if (node.hasChild("acceleration"))
      {
         acceleration = XmlUtils.getVector(node.getChild("acceleration"));
      }
      
      // gravity
      if (node.hasChild("gravity"))
      {
         gravity = XmlUtils.getVector(node.getChild("gravity"));
      }
      
      // drag
      if (node.hasChild("drag"))
      {
         drag = node.getChild("drag").getDoubleValue();
      }
      
      // friction
      if (node.hasChild("friction"))
      {
         friction = node.getChild("friction").getDoubleValue();
      }
      
      // elasticity
      if (node.hasChild("elasticity"))
      {
         elasticity = node.getChild("elasticity").getDoubleValue();
      }
      
      // viscosity
      if (node.hasChild("viscosity"))
      {
         viscosity = node.getChild("viscosity").getDoubleValue();
      }
   }
   
   private void resolveCollision(Collision collision)
   {
      Actor actor = getParent();
      Actor collidedActor = (Actor)collision.getOther(actor);
      
      Rectangle myRect = actor.getCollisionShape().getBounds();
      Rectangle otherRect = collidedActor.getCollisionShape().getBounds();
      
      Vector2D delta = actor.getDelta();
      
      Vector2D resolve = new Vector2D();
      
      // Resolve x-axis.
      if (delta.x > 0)
      {
         resolve.x = (otherRect.x - (myRect.x + myRect.width));    
      }
      else if (delta.x < 0)
      {
         resolve.x = ((otherRect.x + otherRect.width) - myRect.x);
      }
      
      // Resolve y-axis.
      if (delta.y > 0)
      {
         resolve.y = (otherRect.y - (myRect.y + myRect.height)); 
      }
      else if (delta.y < 0)
      {
         resolve.y = ((otherRect.y + otherRect.height) - myRect.y);
      }
      
      if ((resolve.x != 0) && (resolve.y != 0))
      {
         if (Math.abs(resolve.x) > Math.abs(resolve.y))
         {
            resolve.x = 0;
         }
         else if (Math.abs(resolve.y) > Math.abs(resolve.x))
         {
            resolve.y = 0;
         }
      }
      
      actor.moveBy(resolve.x, resolve.y);
   }
   
   private static final int MILLISECONDS_PER_SECOND = 1000;
   
   private boolean isEnabled;
   
   private int mass;
   
   private Vector2D velocity = new Vector2D(0, 0);

   private Vector2D acceleration = new Vector2D(0, 0);
   
   private Vector2D thrust = new Vector2D(0, 0);
   
   private Vector2D gravity = new Vector2D(0, 0);
   
   private double drag;
   
   private double friction;
   
   private double elasticity;
   
   private double viscosity;
   
   private Map<Actor, Collision> collisions = new HashMap<>();
}
