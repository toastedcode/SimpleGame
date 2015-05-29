package com.toast.game.common;

import java.awt.geom.Point2D;
import java.lang.Math;

public class Vector2D implements Cloneable
{
   // **************************************************************************
   //                          Public Attributes
   // **************************************************************************
   
   public double x;
   public double y;
   
   // **************************************************************************
   //                          Public Operations
   // **************************************************************************
   
   public Vector2D()
   {
      x = 0.0;
      y = 0.0;
   }   

   
   public Vector2D(
      double initX,
      double initY)
   {
      x = initX;
      y = initY;
   }
   
   
   public Vector2D(
      Point2D point)
   {
      x = point.getX();
      y = point.getY();
   }   
   
   
   public Vector2D clone()
   {
      Vector2D clonedVector = new Vector2D();
      
      clonedVector.x = x;
      clonedVector.y = y;
      
      return (clonedVector);
   }

   
   public boolean equals(
      Vector2D vector)
   {
      return ((x == vector.x) &&
              (y == vector.y));
   }
   
   
   public double getMagnitude()
   {
      return (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
   }
   
   
   public void normalize()
   {
      double magnitude = getMagnitude();
      if (magnitude == 0)
      {
         x = 0.0;
         y = 0.0;
      }
      else
      {
         x = x / magnitude;
         y = y / magnitude;
      }
   }
   
   
   public Vector2D getNormalized()
   {
      Vector2D normalizedVector = clone();
      normalizedVector.normalize();
     return (normalizedVector);
   }
   
   
   public double dotProduct(
      Vector2D vector)
   {
      return (x * vector.x + y * vector.y);
   }
   
   
   public double dotProduct(
      Point2D point)
   {
      return (dotProduct(new Vector2D(point.getX(), point.getY())));
   }   
   
   
   public double distanceTo(
      Vector2D vector)
   {
      return (Math.sqrt(Math.pow(vector.x - x, 2) + Math.pow(vector.y - y, 2)));
   }
   
   
   public static double angleBetween(
      Vector2D vectorA,
      Vector2D vectorB)
   {
      double angle = 0.0;
      
      double denominator = (vectorA.getMagnitude() * vectorB.getMagnitude());
      if (denominator != 0)
      {
         angle = (Math.acos(vectorA.dotProduct(vectorB) / denominator));
      }
      
      return (Math.toDegrees(angle));
   }   
   
   
   public static Vector2D add(
      Vector2D vectorA, 
      Vector2D vectorB)
   {
      return (new Vector2D((vectorA.x + vectorB.x), (vectorA.y + vectorB.y)));   
   }
   
   
   public static Vector2D subtract(
      Vector2D vectorA, 
      Vector2D vectorB)
   {
      return (new Vector2D((vectorA.x - vectorB.x), (vectorA.y - vectorB.y)));   
   }
   
   
   public static Vector2D multiply(
      Vector2D vector, 
      double value)
   {
      return (new Vector2D((vector.x * value), (vector.y * value)));   
   }     
   
}
