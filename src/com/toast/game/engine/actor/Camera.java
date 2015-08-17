package com.toast.game.engine.actor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.toast.game.common.Vector2D;
import com.toast.game.engine.Renderer;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.property.Property;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Camera extends Actor implements Drawable
{
   // **************************************************************************
   //                                Public
   // **************************************************************************
   
   public Camera(String id)
   {
      super(id);
   }
   
   public Camera(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public void follow(Actor actor, int speed)
   {
      this.followActor = actor;
      this.followSpeed = speed;
      setFollowing(true);
   }
   
   public void setFollowing(boolean isFollowing)
   {
      this.isFollowing = isFollowing;
   }
      
   // **************************************************************************
   //                            Actor overrides
  
   @Override
   public void update(long elapsedTime)
   {
      super.update(elapsedTime);
      
      followActor(elapsedTime);
   }
   
   public void draw(Renderer renderer)
   {
      super.draw(renderer);
      
      AffineTransform transform = getTransform();
      
      renderer.draw(this, transform,  getLayer());
   }
  
   // **************************************************************************
   //                         xml.Serializable interface
   
   /*
   <camera id="">
      <position x="" y=""/>
      <dimension width="" height=""/>
   </camera>
   */
   
   public String getNodeName()
   {
      return("camera");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode actorNode = super.serialize(node);

      return (actorNode);
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
   
   private void followActor(long elapsedTime)
   {
      if ((followActor != null) &&
          (isFollowing))
      {
         Point2D.Double center = getCenter();
         Point2D.Double actorCenter = followActor.getCenter();
         Point2D.Double newCenter = new Point2D.Double();
         
         // Snap to position.
         if (followSpeed == 0)
         {
            newCenter = (Point2D.Double)actorCenter.clone();
         }
         // Move to position
         else
         {
            double elapsedSeconds = (double)elapsedTime / (double)MILLISECONDS_PER_SECOND;
            
            Vector2D delta = Vector2D.subtract(new Vector2D(actorCenter),  new Vector2D(center));
            
            if (Math.abs(delta.getMagnitude()) < 1)
            {
               newCenter = center;
            }
            else
            {
               delta.normalize();
               Vector2D.multiply(delta, (followSpeed * elapsedSeconds));
               newCenter = new Point2D.Double((center.x + delta.x), (center.y + delta.y));
            }
         }
         
         // TODO: Restrict to world coordinates.
         
         setCenter(newCenter);
      }
      
   }
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      
   }
   
   private static final int MILLISECONDS_PER_SECOND = 1000;
   
   private Actor followActor = null;
   
   private int followSpeed = 0;
   
   private boolean isFollowing = false;

   @Override
   public void draw(Graphics graphics)
   {
      graphics.setColor(java.awt.Color.WHITE);
      graphics.drawRect((int)getPosition().x, (int)getPosition().y,  getWidth(),  getHeight());// TODO Auto-generated method stub
      
      Point2D.Double center = getCenter();
      Point2D.Double actorCenter = followActor.getCenter();
      graphics.drawLine((int)center.x,  (int)center.y,  (int)actorCenter.x,  (int)actorCenter.y);
   }
}
