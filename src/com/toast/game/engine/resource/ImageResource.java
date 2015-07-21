package com.toast.game.engine.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class ImageResource extends Resource
{
   // **************************************************************************
   //                                Public (static)
   // **************************************************************************
   
   public static ImageResource getResource(String id)
   {
      ImageResource resource = null;
      
      if (resources.containsKey(id) == false)
      {
         logger.log(Level.WARNING, String.format("Unknown resource id [%s].", id));
      }
      else
      {
         try
         {
            resource = (ImageResource)Resource.getResource(id);
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

   public ImageResource(String id)
   {
      super(id);
   }
   
   public BufferedImage getImage()
   {
      return (image);
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
         
         image = ImageIO.read(file);
           
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
   public void save(File file) throws IOException
   {
      // TODO Auto-generated method stub
      
   }
   
   // **************************************************************************
   //                       xml.Serializable interface
   
   @Override
   public String getNodeName()
   {
      return ("image");
   }

   @Override
   public XmlNode serialize(XmlNode node)
   {
      return (super.serialize(node));
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
   }
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   private BufferedImage image;
}
