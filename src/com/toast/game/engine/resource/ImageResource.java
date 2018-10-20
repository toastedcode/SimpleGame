package com.toast.game.engine.resource;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
   
   public BufferedImage getImage(Color transparentColor)
   {
      return (makeColorTransparent(image, transparentColor));
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
   
   private static BufferedImage makeColorTransparent(BufferedImage image, final Color color)
   {
      // https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
      
      BufferedImage filteredImage = null;
      
      ImageFilter filter = new RGBImageFilter()
      {
         // the color we are looking for... Alpha bits are set to opaque
         public int markerRGB = color.getRGB() | 0xFF000000;

         public final int filterRGB(int x, int y, int rgb)
         {
            if ((rgb | 0xFF000000) == markerRGB)
            {
               // Mark the alpha bits as zero - transparent
               return 0x00FFFFFF & rgb;
            }
            else
            {
               // nothing to do
               return rgb;
            }
         }
      };

       ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
       
       java.awt.Image img = Toolkit.getDefaultToolkit().createImage(ip);
       
       // Create a buffered image with transparency
       filteredImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

       // Draw the image on to the buffered image
       Graphics2D bGr = filteredImage.createGraphics();
       bGr.drawImage(img, 0, 0, null);
       bGr.dispose();
       
       return (filteredImage);
   }   
   
   private final static Logger logger = Logger.getLogger(Resource.class.getName());
   
   private BufferedImage image;
}
