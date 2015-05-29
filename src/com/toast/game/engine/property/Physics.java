package com.toast.game.engine.property;

import com.toast.game.common.Vector2D;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.xml.XmlNode;

public class Physics extends Property implements Updatable
{
   public Physics(String id)
   {
      super(id);
   }

   public Physics(XmlNode node)
   {
      super(node);
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
      this.velocity = velocity;
   }

   public Vector2D getAcceleration()
   {
      return (acceleration);
   }

   public void setAcceleration(Vector2D acceleration)
   {
      this.acceleration = acceleration;
   }

   public Vector2D getGravity()
   {
      return (gravity);
   }

   public void setGravity(Vector2D gravity)
   {
      this.gravity = gravity;
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
      
      // Determine the translation.
      double deltaX = velocity.x * elapsedSeconds;
      double deltaY = velocity.y * elapsedSeconds;
      
      getParent().moveBy(deltaX, deltaY);
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
}
