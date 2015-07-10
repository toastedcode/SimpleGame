package com.toast.game.engine.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptResource extends Resource
{
   // **************************************************************************
   //                                  Public
   // **************************************************************************
   
   public ScriptResource create(String path) throws ResourceCreationException
   {
      ScriptResource resource = null;
      
      if ((path == null) || (path.isEmpty()))
      {
         throw (new IllegalArgumentException("Null source specified."));
      }
      
      try
      {
         Path resourcePath = getPath(path);
         
         String id = resourcePath.getFileName().toString();
         
         resource = new ScriptResource(id);
         resource.load(path);
         
         Resource.addResource(resource);
         
         logger.log(Level.INFO, String.format("Created image resource [%s].", id));
      }
      catch (IOException e)
      {
         throw (new ResourceCreationException(e));
      }
        
      return (resource);
   }
   
   public static ScriptResource getResource(String id)
   {
      ScriptResource resource = null;
      
      if (resources.containsKey(id) == false)
      {
         logger.log(Level.WARNING, String.format("Unknown resource id [%s].", id));
      }
      else
      {
         try
         {
            resource = (ScriptResource)Resource.getResource(id);
         }
         catch (ClassCastException e)
         {
            logger.log(Level.WARNING, String.format("Resource [%s] is not an image resource.", id));            
         }
      }
      
      return (resource);
   }

   
   // **************************************************************************
   //                                  Public
   // **************************************************************************
   
   public ScriptResource(String id)
   {
      super(id);
   }
   
   public File getFile()
   {
      return (scriptFile);
   }
   
   // **************************************************************************
   //                         Resource override

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
         
         scriptFile = resourceFile;
           
         setLoaded(true);
      }
      catch (IOException e)
      {
         logger.log(Level.WARNING, String.format("Failed to load image [%s].", path.toString()));
         
         setLoaded(false);
         
         throw (new ResourceCreationException(e));
      }
   }

   @Override
   public void save(String path)
   {
      // TODO Auto-generated method stub

   }
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   File scriptFile = null;
}
