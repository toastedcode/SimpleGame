package com.toast.game.engine.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.toast.xml.XmlDocument;
import com.toast.xml.exception.XmlParseException;

public class XmlResource extends Resource
{
   public static XmlResource getResource(String id)
   {
      XmlResource resource = null;
      
      if (resources.containsKey(id) == false)
      {
         logger.log(Level.WARNING, String.format("Unknown resource id [%s].", id));
      }
      else
      {
         try
         {
            resource = (XmlResource)Resource.getResource(id);
         }
         catch (ClassCastException e)
         {
            logger.log(Level.WARNING, String.format("Resource [%s] is not an XML resource.", id));            
         }
      }
      
      return (resource);
   }
   
   public XmlResource(String id)
   {
      super(id);
   }
   
   public XmlDocument getDocument()
   {
      return (document);
   }

   @Override
   public void load(String path) throws ResourceCreationException
   {
      if ((path == null) || (path.isEmpty()))
      {
         throw (new IllegalArgumentException("Null path specified."));
      }
      
      try
      {
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
         
         document = new  XmlDocument();
         document.load(resourcePath);
            
         setLoaded(true);
      }
      catch (IOException e)
      {
         logger.log(Level.WARNING, String.format("Failed to load image [%s].", path.toString()));
         
         setLoaded(false);
         
         throw (new ResourceCreationException(e));
      }
      catch (XmlParseException e)
      {
         logger.log(Level.WARNING, String.format("Failed to parse XML document [%s].", path.toString()));
         
         setLoaded(false);
         
         throw (new ResourceCreationException(e));
      }
   }

   @Override
   public void save(String path)
   {
      // TODO Auto-generated method stub
      
   }
   
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   private XmlDocument document;
}
