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
   public void load(File file) throws ResourceCreationException
   {
      if (file == null)
      {
         throw (new IllegalArgumentException("Null file specified."));
      }
      
      try
      {
         if (file.exists() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] does not exist.", file.toString())));
         }
         else if (file.isFile() == false)
         {
            throw (new FileNotFoundException(String.format("Resource file [%s] is not a file.", file.toString())));
         }
         
         scriptFile = file;
           
         setLoaded(true);
      }
      catch (IOException e)
      {
         logger.log(Level.WARNING, String.format("Failed to load image [%s].", file.toString()));
         
         setLoaded(false);
         
         throw (new ResourceCreationException(e));
      }
   }

   @Override
   public void save(File file)
   {
      // TODO Auto-generated method stub

   }
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   File scriptFile = null;
}
