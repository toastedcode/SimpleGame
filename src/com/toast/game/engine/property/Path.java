package com.toast.game.engine.property;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import com.toast.game.common.Vector2D;
import com.toast.game.engine.Actor;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.property.Animation.AnimationDirection;

public class Path extends Property implements Updatable, Drawable
{
   public enum FollowType
   {
      NONE,
      NORMAL,
      LOOP,
      BOUNCE      
   }
   
   public enum FollowDirection
   {
      FORWARD(1),
      REVERSE(-1);
      
      private FollowDirection(int increment)
      {
         this.increment = increment;
      }
      
      public int getIncrement()
      {
         return (increment);
      }
      
      private int increment;
   }
   
   public Path(String id, List<Point> waypoints)
   {
      super(id);
      
      this.waypoints = waypoints; 
   }
   
   public Path(String id, Point ... waypoints)
   {
      super(id);
      
      this.waypoints = Arrays.asList(waypoints); 
   }
   
   public int getWaypointIndex()
   {
      return (waypointIndex);
   }
   
   public void setWaypointIndex(int waypointIndex)
   {
      this.waypointIndex = waypointIndex;
   }
   
   public boolean isFollowing()
   {
      return (isFollowing);
   }
   
   public void setFollowing(boolean isFollowing)
   {
      this.isFollowing = isFollowing;
   }
   
   public FollowType getFollowType()
   {
      return (followType);
   }
   
   public void setFollowType(FollowType followType)
   {
      this.followType = followType;
   }
   
   public FollowDirection getFollowDirection()
   {
      return (followDirection);
   }
   
   public void setFollowDirection(FollowDirection followDirection)
   {
      this.followDirection = followDirection;
   }
   
   public int getSpeed()
   {
      return (getSpeed());
   }
   
   public void setSpeed(int speed)
   {
      this.speed = speed;
   }

   @Override
   public void draw(Graphics graphics)
   {
      final int RADIUS = 5;
      
      graphics.setColor(Color.WHITE);
      
      for (int i = 0; i < waypoints.size(); i++)
      {
         Point waypoint = waypoints.get(i);
         
         // Draw points.
         graphics.drawOval((int)(waypoint.getX() - RADIUS), (int)(waypoint.getY() - RADIUS), (RADIUS * 2), (RADIUS * 2));
         
         // Draw lines.
         if (i < (waypoints.size() - 1))
         {
            Point nextWaypoint = waypoints.get(i + 1);
            graphics.drawLine((int)waypoint.getX(), (int)waypoint.getY(),  (int)nextWaypoint.getX(), (int)nextWaypoint.getY());
         }
      }
   }

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
   public void update(long elapsedTime)
   {
      Actor parent = getParent();
   
      if ((parent != null) &&
          (isFollowing == true) &&
          (waypointIndex < waypoints.size()))
      {
         Vector2D translation = getTranslation(elapsedTime);
         parent.moveBy(translation.x, translation.y);
         
         if (hasReachedWaypoint() == true)
         {
            waypointIndex = getNextWaypointIndex();
         }
      }
   }
   
   private int getStartWaypointIndex()
   {
      return (0);
   }
   
   
   private int getEndWaypointIndex()
   {
      return (waypoints.size() - 1);
   }
   
   private int getNextWaypointIndex()
   {
      int nextIndex = waypointIndex;      
      
      switch (followType)
      {
         case NONE:
         {
            // No change.
            break;
         }
         
         case NORMAL:
         {
            // Increment/decrement based on the follow direction.
            nextIndex = waypointIndex += followDirection.getIncrement();      

            if (followDirection == FollowDirection.FORWARD)
            {
               nextIndex = (nextIndex > getEndWaypointIndex()) ? getEndWaypointIndex() : nextIndex;   
            }
            else
            {
               nextIndex = (nextIndex < getEndWaypointIndex()) ? getEndWaypointIndex() : nextIndex;               
            }
            break;
         }

         case LOOP:
         {
            // Increment/decrement based on the follow direction.
            nextIndex = waypointIndex += followDirection.getIncrement();      
            
            if (followDirection == FollowDirection.FORWARD)
            {
               nextIndex = (nextIndex > getEndWaypointIndex()) ? getStartWaypointIndex() : nextIndex;   
            }
            else
            {
               nextIndex = (nextIndex < getEndWaypointIndex()) ? getStartWaypointIndex() : nextIndex;               
            }            
            break;
         }

         case BOUNCE:
         {
            // Increment/decrement the current frame based on the animation direction.
            nextIndex = waypointIndex += followDirection.getIncrement();
            
            if ((nextIndex > getEndWaypointIndex()) || (nextIndex < getStartWaypointIndex()))
            {
               followDirection = (followDirection == FollowDirection.FORWARD) ? 
                                    FollowDirection.REVERSE : 
                                    FollowDirection.FORWARD;
               
               nextIndex = nextIndex + followDirection.getIncrement();
            }
            break;
         }
      }
      
      return (nextIndex);
   }
   
   // This operation calculates a vector representing the amount of translation that the AI will
   // induced in the parent Sprite in the specified amount of time.
   private Vector2D getTranslation(
      // A value representing the elapsed time (in milliseconds) since the last
      // update.
      long elapsedTime)
   {
      // Initialize the return value.
      Vector2D translation = new Vector2D(0.0, 0.0);
      
      // Determine the velocity based on the speed and the current target.
      Vector2D velocity = getVelocity();
      
      // Calculate the translation for this period of time.
      translation.x = (velocity.x * ((double)elapsedTime / (double)MILLISECONDS_PER_SECOND));
      translation.y = (velocity.y * ((double)elapsedTime / (double)MILLISECONDS_PER_SECOND));
      
      //
      // Handle case where we overshoot.
      //
      
      Point2D.Double position = getParent().getPosition();
      Point waypoint = waypoints.get(waypointIndex);
      Point2D.Double projectedPosition = new Point2D.Double((position.getX() + translation.x), 
                                                            (position.getY() + translation.y)); 
      
      double distanceSquared = position.distanceSq(waypoint);
      double projectedDistanceSquared = position.distanceSq(projectedPosition);
      
      if (projectedDistanceSquared > distanceSquared)
      {
         // Overshoot!
         translation.x = waypoint.getX() - position.getX();
         translation.y = waypoint.getY() - position.getY();         
      }
     
      return (translation);
   }
   
   private Vector2D getVelocity()
   {
      Vector2D velocity = new Vector2D(0.0, 0.0);
      
      // Validate the current waypoint.
      if ((waypoints.isEmpty() == false) &&
          (waypointIndex >= 0) &&
          (waypointIndex < waypoints.size()))
      {
         velocity = Vector2D.multiply(Vector2D.subtract(new Vector2D(waypoints.get(waypointIndex)),
                                                        new Vector2D(getParent().getPosition())).getNormalized(),
                                                        (double)speed);
      }
      
      return (velocity);      
   }
   
   private boolean hasReachedWaypoint()
   {
      final double WAYPOINT_THRESHOLD = 1.0;  // pixels
      
      // Initialize the return value.
      boolean hasReachedWaypoint = false;
      
      // Validate the current waypoint.
      if ((waypointIndex >= 0) &&
          (waypointIndex < waypoints.size()))
      {
         // Determine if the parent is "close enough" to the current waypoint.
         hasReachedWaypoint = (getParent().getPosition().distance(waypoints.get(waypointIndex)) < WAYPOINT_THRESHOLD);
      }
    
      return (hasReachedWaypoint);
   }
   
   /*
   private int validateWaypointIndex(int index)
   {
      int validatedIndex = index;
      if (validatedIndex < 0)
      {
         validatedIndex = 0;
      }
      else if (validatedIndex > (waypoints.size() - 1))
      {
         validatedIndex = (waypoints.size() - 1);
      }
      
      return (validatedIndex);
   }
   */
   
   // A constant specifying the number of milliseconds in a second.
   private static final int MILLISECONDS_PER_SECOND = 1000;  
   
   private List<Point> waypoints;
   
   Dimension dimension = new Dimension(0, 0);
   
   private int waypointIndex = 0;
   
   private boolean isVisible = true;
   
   private boolean isFollowing = true;
   
   private int speed;
   
   private FollowType followType = FollowType.NORMAL;
   
   private FollowDirection followDirection = FollowDirection.FORWARD;
}
