package com.toast.game.engine.resource;

import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Test;

import com.toast.game.engine.resource.Resource;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

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
      public void load(String path)
      {
         setLoaded(true);
      }

      @Override
      public void save(String path)
      {
      }
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
      
      resource.load("/resources/myResource.txt");
      
      assertTrue(resource.isLoaded());
   }
   
   @Test
   public void testSerializeParse() throws ResourceCreationException
   {
      Resource resource = new TestResource("myResource");
      resource.load("/resources/myResource.txt");
      
      XmlDocument document = new XmlDocument();
      XmlNode root = document.createRootNode("resources");
      
      XmlNode node = resource.serialize(root);
      
      Resource otherResource = new TestResource(node);
      
      assertTrue(resource.getId().contentEquals(otherResource.getId()));
   }
}
