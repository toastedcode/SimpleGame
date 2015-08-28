package com.toast.game.engine.actor;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import com.toast.game.common.Vector2D;
import com.toast.game.common.XmlUtils;
import com.toast.game.engine.Game;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Camera extends Actor
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
   public void initialize()
   {
      if (followActorId != null)
      {
         followActor = Game.getActor(followActorId);
      }
   }
  
   @Override
   public void update(long elapsedTime)
   {
      super.update(elapsedTime);
      
      followActor(elapsedTime);
   }
  
   // **************************************************************************
   //                         xml.Serializable interface
   
   /*
   <camera id="">
      <position x="" y=""/>
      <dimension width="" height=""/>
      <followActor id="" isFollowing="" followSpeed=""/>
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
      
      // dimension
      XmlNode childNode = actorNode.appendChild("dimension");
      childNode.setAttribute("width", getWidth());
      childNode.setAttribute("height", getHeight());
      
      // followActor, isFollowing, followSpeed
      if (followActor != null)
      {
         childNode = actorNode.appendChild("followActor");
         childNode.setAttribute("id", followActor.getId());
         childNode.setAttribute("isFollowing", isFollowing);
         childNode.setAttribute("followSpeed", followSpeed);
      }

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
         
         // Center on the followed actor.
         setCenter(newCenter);
         
         // Restrict to scene dimension.
         Dimension sceneDimension = Game.getCurrentScene().getDimension();
         Point2D.Double adjustedPosition = getPosition();
         adjustedPosition.setLocation(Math.max(adjustedPosition.getX(), 0),
                                      Math.max(adjustedPosition.getY(), 0));
         adjustedPosition.setLocation(Math.min(adjustedPosition.getX(), (sceneDimension.getWidth() - getWidth())),
                                      Math.min(adjustedPosition.getY(), (sceneDimension.getHeight() - getHeight())));
         
         setPosition(adjustedPosition);
      }
      
   }
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // dimension
      this.setDimension(XmlUtils.getDimension(node.getChild("dimension")));
      
      // followActor, isFollowing, followSpeed
      if (node.hasChild("followActor"))
      {
         XmlNode followActorNode = node.getChild("followActor");
         
         followActorId = followActorNode.getAttribute("id").getValue();
         isFollowing = followActorNode.getAttribute("isFollowing").getBoolValue();
         followSpeed = followActorNode.getAttribute("followSpeed").getIntValue();
      }
   }
   
   private static final int MILLISECONDS_PER_SECOND = 1000;
   
   private String followActorId = null;
   
   private Actor followActor = null;
   
   private int followSpeed = 0;
   
   private boolean isFollowing = false;
}
