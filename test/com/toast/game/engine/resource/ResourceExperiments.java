package com.toast.game.engine.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.toast.game.engine.resource.Resource;
import com.toast.xml.XmlDocument;
import com.toast.xml.exception.XmlSerializeException;

public class ResourceExperiments
{
   @Test
   public void testPath() throws FileNotFoundException, IOException
   {
      // Local resources.
      String pathString = ResourceExperiments.class.getResource("/").getPath();
      pathString = pathString.replace("%20", " ");
      File file = new File(pathString);
      System.out.printf("Full path: %s\n", file.getAbsolutePath());
      System.out.printf("File %s exists: %s\n",  file.getName(), file.exists());
      
      // Absolute resources.
      pathString = "D:\\Programming\\Git Hub\\SimpleGame\\bin";
      file = new File(pathString);
      System.out.printf("Full path: %s\n", file.getAbsolutePath());
      System.out.printf("File %s exists: %s\n",  file.getName(), file.exists());
   }
   
   @Test
   public void testXmlLoadSave() throws IOException, XmlSerializeException
   {
      XmlDocument document = new XmlDocument();
      document.createRootNode("fish");
      document.getRootNode().appendChild("carp", "flipper");
      
      String pathString = ResourceExperiments.class.getResource("/").getPath() + "/resources/test.xml";
      pathString = pathString.replace("%20", " ");
      document.save(pathString);
   }
}
