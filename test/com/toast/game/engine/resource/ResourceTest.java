package com.toast.game.engine.resource;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.toast.game.engine.resource.Resource;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;

public class ResourceTest
{
   private class TestResource extends Resource
   {
      public TestResource(String id)
      {
         super(id);
      }
      
      public TestResource(XmlNode node) throws ResourceCreationException
      {
         super(node);
      }

      @Override
      public void load(File file)
      {
         setLoaded(true);
      }

      @Override
      public void save(File file)
      {
      }
   }
   
   @Before
   public void resetResourcePath() throws IOException
   {
      String defaultResourcePath = Resource.class.getResource("/").getPath();
      defaultResourcePath = defaultResourcePath.replace("%20", " ");
      File file = new File(defaultResourcePath);
      defaultResourcePath = file.getAbsolutePath();
      
      Resource.setResourcePath(defaultResourcePath);
   }
   
   @Test
   public void testGetResourcePath()
   {
      String defaultResourcePath = Resource.class.getResource("/").getPath();
      defaultResourcePath = defaultResourcePath.replace("%20", " ");
      File file = new File(defaultResourcePath);
      defaultResourcePath = file.getAbsolutePath();
      
      String resourcePath = Resource.getResourcePath();
      
      
      System.out.format("Default resource path: %s\n", defaultResourcePath);
      System.out.format("Default resource path: %s\n", resourcePath);
      
      assertTrue(resourcePath.equals(defaultResourcePath));
      
      System.out.format("Default resource path: %s\n", resourcePath);
   }
   
   @Test
   public void testSetResourcePath_Valid() throws IOException
   {
      String resourcePath = Resource.getResourcePath() + "\\resources";
      
      Resource.setResourcePath(resourcePath);
      
      assertTrue(Resource.getResourcePath().equals(resourcePath));
      
      System.out.format("New resource path: %s\n", Resource.getResourcePath());
   }
   
   @Test(expected=IOException.class)
   public void testSetResourcePath_Invalid() throws IOException
   {
      Resource.setResourcePath("C:/NonexistentDirectory");
   }
   
   @Test
   public void testLoadResources() throws ResourceCreationException
   {
      Resource.loadResources("/resources");
   }
   
   @Test
   public void testLoadResource() throws ResourceCreationException
   {
      Resource.loadResource("/resources/images/boxy.png");      
   }

   @Test
   public void testAddGetResource() throws IOException
   {
      assertTrue(Resource.getResource("myResource") == null);
      
      Resource resource = new TestResource("myResource");
      
      Resource.addResource(resource);
      
      Resource otherResource = Resource.getResource(resource.getId());
      
      assertTrue(resource.getId().contentEquals(otherResource.getId()));
   }
   
   @Test
   public void testGetId() throws IOException
   {
      Resource resource = new TestResource("myResource");
      
      assertTrue(resource.getId().contentEquals("myResource"));
   }
   
   @Test
   public void testIsLoaded() throws ResourceCreationException
   {
      Resource resource = new TestResource("myResource");
      
      assertFalse(resource.isLoaded());
      
      File file = new File(Resource.getResourcePath() + "/resources/myResource.txt");
      
      resource.load(file);
      
      assertTrue(resource.isLoaded());
   }
   
   @Test
   public void testSerializeParse() throws ResourceCreationException
   {
      Resource resource = new TestResource("myResource");
      File file = new File(Resource.getResourcePath() + "/resources/myResource.txt");
      resource.load(file);
      
      XmlDocument document = new XmlDocument();
      XmlNode root = document.createRootNode("resources");
      
      XmlNode node = resource.serialize(root);
      
      Resource otherResource = new TestResource(node);
      
      assertTrue(resource.getId().contentEquals(otherResource.getId()));
   }
   
   @Test
   public void testXmlLoadSave()
   {
      // TODO
   }
}
