package com.toast.game.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.toast.game.engine.resource.Resource;

public class ResourceTest
{
   @Test
   public void testLoadResources() throws IOException
   {
      // TODO: Find out a good way to get a Path from "/resources".
      //String pathString = "C:\\Users\\jtost\\Pictures";
      String pathString = ResourceTest.class.getResource("/resources").getFile();
      Path path = Paths.get(pathString);
      Resource.loadResources(path);
   }
}
