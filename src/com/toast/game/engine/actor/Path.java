package com.toast.game.engine.actor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.toast.game.engine.Renderer;
import com.toast.game.engine.interfaces.Drawable;

public class Path extends Actor implements Drawable
{
   public Path(String id, Point ... waypoints)
   {
      super(id);
      
      this.waypoints = new ArrayList<Point>(Arrays.asList(waypoints));
      
      calculateBounds();
   }
   
   public Path(String id, List<Point> waypoints)
   {
      super(id);
      
      this.waypoints = waypoints;
      
      calculateBounds();
   }
   
   public Point getWaypoint(int index)
   {
      return (waypoints.get(index));
   }
   
   public int getSize()
   {
      return (waypoints.size());
   }

   @Override
   public void draw(Renderer renderer)
   {
      renderer.draw(this, getTransform(), getLayer());
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
      return ((int)getBounds().getWidth());
   }

   @Override
   public int getHeight()
   {
      return ((int)getBounds().getHeight());
   }
   
   private void calculateBounds()
   {
      double minX = 0;
      double minY = 0;
      double maxX = 0;
      double maxY = 0;
      
      for (Point waypoint : waypoints)
      {
         minX = Math.min(minX,  waypoint.getX());
         minY = Math.min(minY,  waypoint.getY());
         maxX = Math.max(maxX,  waypoint.getX());
         maxY = Math.max(maxY,  waypoint.getY());
      }
      
      setPosition(minX, minY);
      setDimension((int)(maxX - minX), (int)(maxY - minY));
   }

   private List<Point> waypoints;
}
