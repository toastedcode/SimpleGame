package com.toast.game.engine;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toast.game.common.LockableMap;
import com.toast.game.common.XmlUtils;
import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.actor.Camera;
import com.toast.game.engine.interfaces.Syncable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.property.Property;
import com.toast.xml.Serializable;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;
import com.toast.xml.exception.XmlSerializeException;

public class Scene implements Updatable, Serializable, Syncable
{
   // **************************************************************************
   //                                  Public
   // **************************************************************************
   
   public Scene(String id)
   {
      this.id = id;
      dimension = new Dimension();
   }
   
   public String getId()
   {
      return (id);
   }
   
   public Dimension getDimension()
   {
      return ((Dimension)dimension.clone());
   }
   
   public int getWidth()
   {
      return ((int)dimension.getWidth());
   }
   
   public int getHeight()
   {
      return ((int)dimension.getHeight());
   }
   
   public void setDimension(Dimension dimension)
   {
      this.dimension.setSize(dimension.getWidth(), dimension.getHeight());
   }
   
   public Actor getActor(String id)
   {
      return (actors.get(id));
   }
   
   public void add(Actor actor)
   {
      actors.put(actor.getId(), actor);
   }
   
   public void remove(Actor actor)
   {
      actors.remove(actor.getId());
   }
  
   public void remove(String id)
   {
      actors.remove(id);
   }
   
   public Camera getCamera()
   {
      return (camera);
   }
   
   public void setCamera(Camera camera)
   {
      this.camera = camera;
   }
   
   public void initialize()
   {
      if (cameraId != null)
      {
         camera = (Camera)actors.get(cameraId);
      }
      
      initializeActors();
   }
   
   public void draw(Renderer renderer)
   {
      Rectangle clipRectangle = new Rectangle();
      if (camera != null)
      {
         clipRectangle = camera.getBounds().getBounds();
      }
      else
      {
         Dimension screenDimension = renderer.getScreenDimension();
         clipRectangle = 
               new Rectangle(0, 0, (int)screenDimension.getWidth(), (int)screenDimension.getHeight());
      }
      
      drawList.build(actors.values(), clipRectangle);
      
      if (camera != null)
      {
         renderer.setViewport(new Rectangle((int)camera.getPosition().getX(), 
                                            (int)camera.getPosition().getY(),
                                            camera.getWidth(),
                                            camera.getHeight()));
         
      }
      
      drawList.draw(renderer);
   }
   
   public void load(File file) throws IOException, XmlParseException, XmlFormatException
   {
      if (file == null)
      {
         throw (new IllegalArgumentException("Null file specified."));
      }
      
      if (file.exists() == false)
      {
         throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", file.toString())));
      }
      else if (file.isFile() == false)
      {
         throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", file.toString())));
      }
      
      XmlDocument document = new XmlDocument();
      
      document.load(file.getAbsolutePath());
      
      deserialize(document.getRootNode());
   }
   
   public void save(File file) throws IOException, XmlSerializeException
   {
      if (file == null)
      {
         throw (new IllegalArgumentException("Null file specified."));
      }
      
      XmlDocument document = new XmlDocument();
      
      XmlNode rootNode = document.createRootNode("game");
      XmlNode sceneNode = serialize(rootNode);
      
      try
      {
         document.parse(sceneNode.toString());
      }
      catch (XmlParseException e)
      {
         throw (new XmlSerializeException(e));
      }
     
      document.save(file.getAbsolutePath());
   }
   
   // **************************************************************************
   //                           Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      actors.lock();
      
      for (Actor actor : actors.values())
      {
         actor.update(elapsedTime);
      }
      
      actors.unlock();
   }
   
   // **************************************************************************
   //                        xml.Serializable interface

   @Override
   public String getNodeName()
   {
      return ("scene");
   }

   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode sceneNode = node.appendChild(getNodeName());
      
      // id
      sceneNode.setAttribute("id", getId());
      
      // dimension
      XmlNode childNode = sceneNode.appendChild("dimension");
      childNode.setAttribute("width",  getWidth());
      childNode.setAttribute("height",  getHeight());
      
      // camera
      if (camera != null)
      {
         sceneNode.appendChild("camera").setAttribute("id",  camera.getId());
      }
      
      // actors
      childNode = sceneNode.appendChild("actors");
      for (Actor actor : actors.values())
      {
         actor.serialize(childNode);
      }
      
      return (sceneNode);
   }

   @Override
   public void deserialize(XmlNode node)
   {
      try
      {
         // id
         id = node.getAttribute("id").getValue();
         
         // dimension
         setDimension(XmlUtils.getDimension(node.getChild("dimension")));
         
         // camera
         if (node.hasChild("camera"))
         {
            cameraId = node.getChild("camera").getAttribute("id").getValue();
         }
         
         //
         // actors
         //
         
         if (node.hasChild("actors"))
         {
            XmlNodeList actorNodes = node.getChild("actors").getChildren();
            
            for (XmlNode actorNode : actorNodes)
            {
               Actor actor = Actor.createActor(actorNode);
   
               add(actor);
            }
         }
      }
      catch (XmlFormatException e)
      {
         // TODO:
         System.out.println(e.toString());
      }
   }
   
   // **************************************************************************
   //                             Syncable interface

   @Override
   public boolean isChanged()
   {
      // TODO
      return (true);
   }

   @Override
   public XmlNode syncTo(XmlNode node)
   {
      XmlNode sceneNode = node.appendChild("scene");
      
      // id
      sceneNode.setAttribute("id", getId());
      
      // actors
      if (actors.size() > 0)
      {
         XmlNode actorsNode = sceneNode.appendChild("actors");
         
         for (Actor actor : actors.values())
         {
            actor.syncTo(actorsNode);
         }
      }
      
      return (sceneNode);
   }

   @Override
   public void syncFrom(XmlNode node) throws XmlFormatException
   {
      // actors
      if (node.hasChild("actors"))
      {
         XmlNodeList actorNodes = node.getChild("actors").getChildren();
         
         for (XmlNode actorNode : actorNodes)
         {
            String actorId = actorNode.getAttribute("id").getValue();
            
            Actor actor = getActor(actorId);
            
            if (actor != null)
            {
               actor.syncFrom(actorNode);
            }
            else
            {
               logger.log(Level.WARNING, 
                          String.format("Could not sync actor [%s] in scene [%s].", 
                                        actorId, 
                                        getId()));               
            }
         }
      }
   }
   
   // **************************************************************************
   //                                  Private
   // **************************************************************************
   
   private void initializeActors()
   {
      for (Actor actor : actors.values())
      {
         actor.initialize();
      }
   }
   
   private final static Logger logger = Logger.getLogger(Scene.class.getName());
   
   private String id;
   
   Dimension dimension;
   
   LockableMap<String, Actor> actors = new LockableMap<>();
   
   String cameraId;
   
   Camera camera;
   
   DrawList drawList = new DrawList();
}
