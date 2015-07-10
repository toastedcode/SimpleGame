package com.toast.game.engine.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.toast.xml.Serializable;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public abstract class Resource implements Serializable
{

// **************************************************************************
//                              Public (static)
// **************************************************************************

   public static void loadResources(String path) throws ResourceCreationException
   {
      try
      {
         if ((path == null) || (path.isEmpty()))
         {
            throw (new IllegalArgumentException("Null path specified."));
         }
         
         Path resourcePath = getPath(path);
         File resourceFile = resourcePath.toFile();
         
         if (resourceFile.exists() == false)
         {
            throw (new NoSuchFileException(String.format("Resource path [%s] does not exist.", resourceFile.toString())));
         }
         else if (resourceFile.isDirectory() == false)
         {
            throw (new NoSuchFileException(String.format("Resource path [%s] is not a directory.", resourceFile.toString())));
         }
         
         Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>()
         {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
            {
               if (Files.isRegularFile(path) == true)
               {
                  try
                  {
                     loadResource(path.toString());
                  }
                  catch (ResourceCreationException e)
                  {
                     // Skip this file.
                  }
               }
   
                return FileVisitResult.CONTINUE;
            }
         });
      }
      catch (IOException e)
      {
         throw (new ResourceCreationException(e));
      }
   }

   public static Resource loadResource(String path) throws ResourceCreationException
   {
      Resource resource = null;
      
      try
      {
         if ((path == null) || (path.isEmpty()))
         {
            throw (new IllegalArgumentException("Null path specified."));
         }
   
         Path resourcePath = getPath(path);
         File resourceFile = resourcePath.toFile();
         
         if (resourceFile.exists() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", resourceFile.toString())));
         }
         else if (resourceFile.isFile() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", resourceFile.toString())));
         }
         
         String id = resourcePath.getFileName().toString();
         
         resource = createResource(id, path);
      }
      catch (FileNotFoundException e)
      {
         throw (new ResourceCreationException(e));
      }
      
      return (resource);
   }
   
   static public Resource createResource(String id, String path) throws ResourceCreationException
   {
      Resource resource = null;
      
      try
      {
         if ((path == null) || (path.isEmpty()))
         {
            throw (new IllegalArgumentException("Null path specified."));
         }
   
         Path resourcePath = getPath(path);
         File resourceFile = resourcePath.toFile();
         
         if (resourceFile.exists() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", resourceFile.toString())));
         }
         else if (resourceFile.isFile() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", resourceFile.toString())));
         }
         
         String extension = getExtension(path);
         
         switch (extension)
         {
            case ".jpg":
            case ".png":
               resource = new ImageResource(id);
               break;
               
            case ".xml":
            case ".anim":
               resource = new XmlResource(id);
               break;
               
            case ".bsh":
               resource = new ScriptResource(id);
               break;
               
            default:
            {
               logger.log(Level.WARNING, 
                          String.format("Unsupported file type [%s] for resource [%s].", path.toString(), id));
            }
         }
         
         if (resource != null)
         {
            resource.load(path);
            
            addResource(resource);
            
            logger.log(Level.INFO, String.format("Created resource [%s].", id));
         }
      }
      catch (FileNotFoundException e)
      {
         throw (new ResourceCreationException(e));
      }
      
      return (resource);
   }
   
   public static void addResource(Resource resource)
   {
      if (resources.containsKey(resource.getId()) == true)
      {
         logger.log(Level.WARNING, String.format("Duplicate resource id [%s].", resource.getId()));
      }
      else
      {
         resources.put(resource.getId(),  resource);
      }
   }
   
   public static Resource getResource(String id)
   {
      Resource resource = null;
      
      if (resources.containsKey(id) == false)
      {
         logger.log(Level.WARNING, String.format("Unknown resource id [%s].", id));
      }
      else
      {
         resource = resources.get(id);
      }
      
      return (resource);
   }
   
   static public Path getPath(String pathString) throws FileNotFoundException
   {
      Path path = null;
      
      boolean isRelativePath = pathString.startsWith("/");
      
      if (isRelativePath == true)
      {
         URL url = Resource.class.getResource(pathString);
         
         if (url == null)
         {
            throw (new FileNotFoundException(String.format("Path [%s] does not exist.", pathString)));
         }
         else
         {
            try
            {
               path = Paths.get(url.toURI());
            }
            catch (URISyntaxException e)
            {
               throw (new FileNotFoundException(String.format("Path [%s] does not exist.", pathString)));
            }
         }
      }
      else
      {
         path = Paths.get(pathString);
      }
      
      return (path);
   }
   
   // **************************************************************************
   //                                 Public
   //**************************************************************************
   
   public Resource(String id)
   {
      this.id = id;
   }
   
   public Resource(XmlNode node) throws ResourceCreationException
   {
      try
      {
         deserialize(node);
      
         load(path);
      }
      catch (XmlFormatException e)
      {
         throw (new ResourceCreationException(e));
      }
   }
   
   public String getId()
   {
      return (id);
   }
   
   public String getPath()
   {
      return (path);
   }
   
   public boolean isLoaded()
   {
      return (isLoaded);
   }
   
   public abstract void load(String path) throws ResourceCreationException;
   
   public abstract void save(String path) throws IOException;
   
   // **************************************************************************
   //                       xml.Serializable interface
   
   @Override
   public String getNodeName()
   {
      return ("resource");
   }

   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode resourceNode = node.appendChild(getNodeName());
      
      // id
      resourceNode.setAttribute("id",  id);
      
      // path
      if (path != null)
      {
         resourceNode.setAttribute("path",  path);
      }
      
      return (resourceNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      // id
      id = node.getAttribute("id").getValue();
      
      // path
      if (node.hasAttribute("path"))
      {
         path = node.getAttribute("path").getValue();
      }
   }
   
   // **************************************************************************
   //                                 Protected
   // **************************************************************************
   
   static protected String getExtension(String path)
   {
      return (path.substring(path.lastIndexOf(".")));
   }
   
   protected void setLoaded(boolean isLoaded)
   {
      this.isLoaded = isLoaded;
   }
  
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   static Map<String, Resource> resources = new HashMap<>();
   
   private String id = null;
   
   private String path = null;
   
   private boolean isLoaded = false;
}

/*
Resource boxyImage = Resource.createResource("/resources/images/boxy.png");
String resourceId = boxyImage.getId();
Image image = new Image(boxyImage);

Resource.createResources(resourcePath);
new Image(Resource.get("boxy.png"));
*/
