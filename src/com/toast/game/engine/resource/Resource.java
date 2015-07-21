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
   
   public static void setResourcePath(String srcPath) throws IOException
   {
      if ((srcPath == null) || (srcPath.isEmpty()))
      {
         throw (new IllegalArgumentException("Null path specified."));
      }
      
      File file = new File(srcPath);
      
      if (file.exists() == false)
      {
         throw (new NoSuchFileException(String.format("Resource path [%s] does not exist.", file.toString())));
      }
      else if (file.isDirectory() == false)
      {
         throw (new NoSuchFileException(String.format("Resource path [%s] is not a directory.", file.toString())));
      }
      
      resourcePath = file.getAbsolutePath();
   }
   
   public static String getResourcePath()
   {
      return (resourcePath);
   }
   
   public static File getFile(String path)
   {
      File file = null;
      
      if ((path == null) || (path.isEmpty()))
      {
         throw (new IllegalArgumentException("Null path specified."));
      }
      
      if (path.startsWith("/"))
      {
         // Specifies a path relative to the resource path.
         
         file = new File(resourcePath + path);
      }
      else
      {
         // Specifies an absolute path.
         
         file = new File(path);
      }
      
      return (file);
   }

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
   
         File file = getFile(path);
         
         if (file.exists() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", file.toString())));
         }
         else if (file.isFile() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", file.toString())));
         }
         
         String id = file.getName();
         
         resource = createResource(id, file);
      }
      catch (FileNotFoundException e)
      {
         throw (new ResourceCreationException(e));
      }
      
      return (resource);
   }

   static public Resource createResource(String id, File file) throws ResourceCreationException
   {
      Resource resource = null;
      
      try
      {
         if ((id == null) || (id.isEmpty()))
         {
            throw (new IllegalArgumentException("Null id specified."));
         }
         else if (file == null)
         {
            throw (new IllegalArgumentException("Null file specified."));
         }
         
         if (!file.exists())
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", file.toString())));
         }
         else if (!file.isFile())
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", file.toString())));
         }
         
         String extension = getExtension(file.getAbsolutePath());
         
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
                          String.format("Unsupported file type [%s] for resource [%s].", file.getAbsolutePath(), id));
            }
         }
         
         if (resource != null)
         {
            resource.load(file);
            
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
   
   static public String getLocalPath()
   {
      final String HEX_SPACE_STRING = "%20";
      final String SPACE = " ";
      
      return (Resource.class.getResource("/").getPath().replace(HEX_SPACE_STRING, SPACE));
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
      
         //load(getFile(path));
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
   
   public abstract void load(File file) throws ResourceCreationException;
   
   public abstract void save(File file) throws IOException;
   
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
   
   private static String getDefaultResourcePath()
   {
      String pathString = ResourceExperiments.class.getResource("/").getPath();
      
      pathString = pathString.replace("%20", " ");
      
      File file = new File(pathString);
      
      return (file.getAbsolutePath());
   }
 
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   static String resourcePath = getDefaultResourcePath();
   
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
