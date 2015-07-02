package com.toast.game.engine;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toast.xml.*;

public class AnimationMap
{
   public class Frame
   {
      Frame(
         Point position,
         Dimension dimension)
      {
         POSITION = (Point)position.clone();
         DIMENSION = (Dimension)dimension.clone();
      }

      
      public Point getPosition()
      {
         return (POSITION);
      }

      
      public Dimension getDimension()
      {
         return (DIMENSION);
      }
      
      private final Point POSITION;

      private final Dimension DIMENSION;
   }
   
   @SuppressWarnings("serial")
   private class FrameList extends ArrayList<Frame>{};
   
   public AnimationMap(XmlDocument document)
   {
      /*
          XML format
            
         <!-- A single frame -->
         <frame x="0" y="0" width="50" height="50">
         
         <!-- Three frames in a single row -->
         <frame x="0" y="0" width="50" height="50" columns="3">
         
         <!-- Five frames in a single column -->
         <frame x="0" y="0" width="50" height="50" rows="5">
         
         <!-- Fifteen frames in a 3x5 grid.  Read right to left, top to bottom. -->
         <frame x="0" y="0" width="50" height="50" columns="3" rows="5">
         
         <!-- Single frame animation ->
         <animation id="run" frame="0"/>
         
         <!-- Multi-frame animation ->
         <animation id="run"/>
            <frame index="0"/>
            <frame index="5"/>
            <frame index="7"/>
         </animation>
         
         <!-- Sequential-frame animation ->
         <animation id="run" startFrame="0" endFrame="5"/>
      */

      loadFrames(document);
      loadAnimations(document);
   }
   
   public Frame getFrame(
      String animationId, 
      int frameIndex)
   {
      Frame frame = null;
      
      if ((animations.containsKey(animationId) == true) &&
          (frameIndex < animations.get(animationId).size()))
      {
         frame =  animations.get(animationId).get(frameIndex);
      }
      
      return (frame);
   }
   
   
   public int getNumberOfFrames(String animationId)
   {
      int numberOfFrames = 0;
      
      if (animations.containsKey(animationId) == true)
      {
         numberOfFrames =  animations.get(animationId).size();
      }
      
      return (numberOfFrames);
   }
   
   private void loadFrames(
      XmlDocument document)
   {
      XmlNodeList frameNodes = document.getRootNode().getNodes("frame");
      for (int i = 0; i < frameNodes.getLength(); i++)
      {
         XmlNode frameNode = frameNodes.item(i);
         
         Point startPosition = new Point(Integer.valueOf(frameNode.getAttribute("x")),
                                         Integer.valueOf(frameNode.getAttribute("y")));
         
         Dimension dimension = new Dimension(Integer.valueOf(frameNode.getAttribute("width")),
                                             Integer.valueOf(frameNode.getAttribute("height")));
         
         int numColumns = 1;
         if (frameNode.hasAttribute("columns") == true)
         {
            numColumns = Integer.valueOf(frameNode.getAttribute("columns"));
         }
         
         int numRows = 1;
         if (frameNode.hasAttribute("rows") == true)
         {
            numRows = Integer.valueOf(frameNode.getAttribute("rows"));
         }

         for (int row = 0; row < numRows; row++)
         {
            for (int col = 0; col < numColumns; col++)
            {
               Point position = new Point((int)(startPosition.getX() + (col * dimension.getWidth())),
                                          (int)(startPosition.getY() + (row * dimension.getHeight())));
               
               frames.add(new Frame(position, dimension));
            }
         }
      }      
   }
   
   private void loadAnimations(
      XmlDocument document)
   {
      XmlNodeList animationNodes = document.getRootNode().getNodes("animation");
      for (int i = 0; i < animationNodes.getLength(); i++)
      {
         XmlNode animationNode = animationNodes.item(i);
         
         FrameList frameSet = new FrameList();
         
         String id = animationNode.getAttribute("id");
         
         // Single frame animation
         if (animationNode.hasAttribute("frame") == true)
         {
            int frameIndex = Integer.valueOf(animationNode.getAttribute("frame"));
            
            if (validateFrameIndex(frameIndex) == true)
            {
               frameSet.add(frames.get(frameIndex));
            }
         }
         // Sequential frame animation
         else if ((animationNode.hasAttribute("startFrame") == true) &&
                  (animationNode.hasAttribute("endFrame") == true))
         {
            int startFrame = Integer.valueOf(animationNode.getAttribute("startFrame"));
            int endFrame = Integer.valueOf(animationNode.getAttribute("endFrame"));
            
            if ((validateFrameIndex(startFrame) == true) &&
                (validateFrameIndex(startFrame) == true))
            {
               for (int frameIndex = startFrame; frameIndex <= endFrame; frameIndex++)
               {
                  frameSet.add(frames.get(frameIndex));
               }
            }
         }
         // Multi-frame animation 
         else
         {
            XmlNodeList frameNodes = animationNode.getChildren("frame");
            for (int j = 0; j < frameNodes.getLength(); j++)
            {
               XmlNode frameNode = frameNodes.item(j);
               
               int frameIndex = Integer.valueOf(frameNode.getAttribute("index"));
               
               if (validateFrameIndex(frameIndex) == true)
               {
                  frameSet.add(frames.get(frameIndex));                  
               }
            }
         }
         
         animations.put(id, frameSet);
      }
   }
   
   private boolean validateFrameIndex(int frameIndex)
   {
      return ((frames.size() > 0) &&
              (frameIndex >= 0) &&
              (frameIndex < frames.size()));
   }
   
   private List<Frame> frames = new ArrayList<>();
   
   private Map<String, FrameList> animations = new HashMap<>();
}