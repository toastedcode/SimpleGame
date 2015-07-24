package com.toast.game.engine.property;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.toast.game.engine.AnimationMap;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.Messenger;
import com.toast.game.engine.resource.ImageResource;
import com.toast.game.engine.resource.XmlResource;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Animation extends Property implements Updatable, Drawable
{
   // **************************************************************************
   //                          Public Attributes
   // **************************************************************************
   
   public enum AnimationType
   {
      NONE,
      NORMAL,
      LOOP,
      BOUNCE      
   }
   
   public enum AnimationDirection
   {
      FORWARD(1),
      REVERSE(-1);
      
      private AnimationDirection(int frameIncrement)
      {
         this.frameIncrement = frameIncrement;
      }
      
      public int getFrameIncrement()
      {
         return (frameIncrement);
      }
      
      private int frameIncrement;
   }
   
   // **************************************************************************
   //                          Public Operations
   // **************************************************************************
   
   public Animation(
      String id,
      BufferedImage bufferedImage,
      AnimationMap animationMap,
      String animationId,
      int frameRate)
   {
      super(id);
      
      this.bufferedImage = bufferedImage;
      this.animationMap = animationMap;
      this.animationId = animationId;
      this.frameRate = frameRate;
      elapsedAnimationTime = 0;
      currentFrame = 0;
      animationDirection = AnimationDirection.FORWARD;
      animationType = AnimationType.NONE;
   }
   
   public Animation(
      String id,
      ImageResource imageResource,
      XmlResource xmlResource,
      String animationId,
      int frameRate)
   {
      super(id);
      
      this.imageResource = imageResource;
      this.xmlResource = xmlResource;
      this.bufferedImage = imageResource.getImage();
      animationMap = new AnimationMap(xmlResource);
      this.animationId = animationId;
      this.frameRate = frameRate;
      elapsedAnimationTime = 0;
      currentFrame = 0;
      animationDirection = AnimationDirection.FORWARD;
      animationType = AnimationType.NONE;
   }
   
   public Animation(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public Property clone()
   {
      Animation clone = new Animation(getId(),
                                      bufferedImage,
                                      animationMap,
                                      animationId,
                                      frameRate);
      
      return (clone);
   }
   
   public void setCurrentFrame(
      int frame)
   {
      if (frame < 0)
      {
         frame = 0;
      }
      else if (frame > getEndFrame())
      {
         frame = getEndFrame();
      }
      
      currentFrame = frame;
   }
   
   public void start(
      AnimationType animationType,
      AnimationDirection animationDirection)
   {
      this.animationType = animationType;
      this.animationDirection = animationDirection;
   }
   
   public void pause()
   {
      animationType = AnimationType.NONE;
   }
   
   public void stop()
   {
      animationType = AnimationType.NONE;
      currentFrame = 0;
   }
   
   public AnimationType getAnimationType()
   {
      return (animationType);
   }
   
   public AnimationDirection getAnimationDirection()
   {
      return (animationDirection);
   }

   // **************************************************************************
   //                             Updatable interface   
   
   public void update(
      long elapsedTime)
   {
      // Remember the current frame.
      int previousFrame = currentFrame;
      
      elapsedAnimationTime += elapsedTime;
      if (shouldAdvanceFrame(elapsedAnimationTime))
      {
         elapsedAnimationTime = 0;
         currentFrame = getNextFrame(currentFrame);
      }
      
      // Determine if our current animation just finished.
      if ((currentFrame != previousFrame) &&
          (isFinished(currentFrame) == true))
      {
         if (getParent() != null)
         {
            // Send the "animation finished" message.
            Message message = new Message("ANIMATION_FINISHED", 
                                          getParent().getId(), 
                                          null, 
                                          new Message.Parameter("animation", this.getId()));
            Messenger.sendMessage(message);
         }
      }
   }
   
   // **************************************************************************
   //                           Drawable interface
   
   public int getWidth()
   {
      return (bufferedImage.getWidth());
   }
   
   
   public int getHeight()
   {
      return (bufferedImage.getHeight());
   }      
   
   public void draw(Graphics graphics)
   {
      if (isVisible() == true)
      {
         Point position = new Point(0, 0);
         double scale = 1.0;
         
         // Retrieve the current frame.
         AnimationMap.Frame frame = animationMap.getFrame(animationId,  currentFrame);
         
         Rectangle sourceRectangle = new Rectangle(frame.getPosition(), frame.getDimension());
         
         Rectangle destinationRectangle = new Rectangle(position,
                                                        new Dimension((int)(frame.getDimension().getWidth() * scale), 
                                                                      (int)(frame.getDimension().getHeight() * scale)));
         
         ((Graphics2D)graphics).drawImage(
            bufferedImage, 
            destinationRectangle.x, 
            destinationRectangle.y, 
            (destinationRectangle.x + destinationRectangle.width), 
            (destinationRectangle.y + destinationRectangle.height), 
            sourceRectangle.x, 
            sourceRectangle.y, 
            (sourceRectangle.x+ sourceRectangle.width), 
            (sourceRectangle.y + sourceRectangle.height), 
            null);
      }
   }
   
   @Override
   public boolean isVisible()
   {
      return (isVisible);
   }
   
   @Override
   public void setVisible(boolean isVisible)
   {
      this.isVisible = isVisible;
   } 
   
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
   <animation id="walk">
      <image></image>
      <animationMap></animationMap>
      <frameRate></frameRate>
      <isVisible></isVisible>
   </animation>
   */
   
   @Override
   public String getNodeName()
   {
      return("animation");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // imageResource
      if (imageResource != null)
      {
         propertyNode.setAttribute("imageResource",  imageResource.getId());
      }
      
      // animationResource
      if (xmlResource != null)
      {
         propertyNode.setAttribute("animationResource",  xmlResource.getId());
      }
      
      // animationId
      if (animationId.equals(getId()))
      {
         propertyNode.setAttribute("animationId", animationId);
      }
      
      // frameRate
      propertyNode.setAttribute("frameRate", frameRate);
      
      // isVisible
      propertyNode.appendChild("isVisible", isVisible);
      
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
   
   private int getStartFrame()
   {
      return (0);
   }
   
   
   private int getEndFrame()
   {
      return (animationMap.getNumberOfFrames(animationId) - 1);
   }
   
   
   // This operation determines if it is time to advance the current frame of the animation.
   // TODO: This isn't right.  A very long frame rate will cause the animation not to advance at all.
   private boolean shouldAdvanceFrame(
      long elapsedTime)
   {
      return (elapsedTime >= (MILLISECONDS_PER_SECOND / frameRate));
   }

   
   // Determines the first frame of the animation.
   public int getFirstFrame()
   {
      int firstFrame = 0;
      
      if (animationDirection == AnimationDirection.REVERSE)
      {
         firstFrame = getEndFrame();
      }
      else
      {
         firstFrame = getStartFrame();         
      }
      
      return (firstFrame);
   }
      
   
   // Determines the next frame of the animation, based on the supplied current animation frame. 
   private int getNextFrame(
      // The current animation frame.
      int currentFrame)
   {
      // Initialize the return value.
      int nextFrame = currentFrame;      
      
      switch (animationType)
      {
         case NONE:
         {
            // No change.
            break;
         }
         
         case NORMAL:
         {
            // Increment/decrement the current frame based on the animation direction.
            nextFrame = currentFrame += animationDirection.getFrameIncrement();      

            if (animationDirection == AnimationDirection.FORWARD)
            {
               nextFrame = (nextFrame > getEndFrame()) ? getEndFrame() : nextFrame;   
            }
            else
            {
               nextFrame = (nextFrame < getEndFrame()) ? getEndFrame() : nextFrame;               
            }
            break;
         }

         case LOOP:
         {
            // Increment/decrement the current frame based on the animation direction.
            nextFrame = currentFrame += animationDirection.getFrameIncrement();      
            
            if (animationDirection == AnimationDirection.FORWARD)
            {
               nextFrame = (nextFrame > getEndFrame()) ? getStartFrame() : nextFrame;   
            }
            else
            {
               nextFrame = (nextFrame < getEndFrame()) ? getStartFrame() : nextFrame;               
            }            
            break;
         }

         case BOUNCE:
         {
            // Increment/decrement the current frame based on the animation direction.
            nextFrame = currentFrame += animationDirection.getFrameIncrement();
            
            if ((nextFrame > getEndFrame()) || (nextFrame < getStartFrame()))
            {
               animationDirection = (animationDirection == AnimationDirection.FORWARD) ? 
                                       AnimationDirection.REVERSE : 
                                       AnimationDirection.FORWARD;
               
               nextFrame = nextFrame + animationDirection.getFrameIncrement();
            }
            break;
         }
      }
      
      return (nextFrame);
   }
   
   
   // Determines if the animation has finished, based on the supplied current frame.
   private boolean isFinished(
      int currentFrame)
   {
      // Does this animation have a logical end?
      boolean isFiniteAnimation = ((animationType == AnimationType.NONE) ||
                                   (animationType == AnimationType.NORMAL));
      
      // Determine the last frame of the animation.
      boolean isFinalFrame =        
         (animationDirection == AnimationDirection.FORWARD) ? 
            (currentFrame == getEndFrame()) :
            (currentFrame == getStartFrame());
               
      return ((isFiniteAnimation == true) && 
              (isFinalFrame == true));
   }
   
   public void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // imageResource
      if (node.hasAttribute("imageResource"))
      {
         String resourceId = node.getAttribute("imageResource").getValue();
         imageResource = ImageResource.getResource(resourceId);
         
         if (imageResource != null)
         {
            bufferedImage = imageResource.getImage();
         }
      }
      
      // animationResource
      if (node.hasAttribute("animationResource"))
      {
         String resourceId = node.getAttribute("animationResource").getValue();
         xmlResource = XmlResource.getResource(resourceId);
         
         if (xmlResource != null)
         {
            animationMap = new AnimationMap(xmlResource);
         }
      }
      
      // animationId
      if (node.hasAttribute("animationId"))
      {
         animationId = node.getAttribute("animationId").getValue();         
      }
      else
      {
         animationId = getId();
      }

      // frameRate
      frameRate = node.getAttribute("frameRate").getIntValue();
      
      // isVisible
      if (node.hasChild("isVisible"))
      {
         isVisible = node.getChild("isVisible").getBoolValue();
      }  
   }
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
   
   // A constant specifying the number of milliseconds in a second.
   private static final int MILLISECONDS_PER_SECOND = 1000;
   
   private ImageResource imageResource = null;
   
   private XmlResource xmlResource = null;
   
   private BufferedImage bufferedImage;

   private AnimationMap animationMap;
   
   private String animationId;
   
   // The speed (frames per second) at which the Animation should be played.
   private int frameRate;
   
   // A value holding the elapsed time since the last animation frame update.
   private long elapsedAnimationTime;
   
   private int currentFrame;
   
   // The direction of animation.
   private AnimationDirection animationDirection = AnimationDirection.FORWARD;
   
   // An enumeration determining how the Animation should animate.
   private AnimationType animationType = AnimationType.NONE;
   
   private boolean isVisible = true;
}
