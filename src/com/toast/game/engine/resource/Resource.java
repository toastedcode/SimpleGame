package com.toast.game.engine.resource;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import com.toast.xml.Serializable;

public abstract class Resource implements Serializable
{
   public static void loadResources(Path path) throws IOException
   {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>()
      {
         @Override
         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
         {
            if (Files.isRegularFile(file) == true)
            {
               System.out.println(file);
            }

             return FileVisitResult.CONTINUE;
         }
      });
   }
   
   public static Resource loadResource(Path path)
   {
      return (null);
   }
   
   public static Resource getResource(String id)
   {
      return (null);
   }
   
   public Resource(String id)
   {
      this.id = id;
   }
   
   public String getId()
   {
      return (id);
   }
   
   public abstract void load(Path path);
   
   public abstract void save(Path path);
   
   static Map<String, Resource> resources = new HashMap<>();
   
   private String id = null;
}
