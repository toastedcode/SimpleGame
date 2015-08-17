package com.toast.game.engine;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.resource.Resource;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;

public class SceneTest
{
   private final String actorId = "actor";
   
   private final String sceneId = "sceneOne"; 

   @Test
   public void testGetId()
   {
      Scene scene = new Scene(sceneId);
      assertTrue(scene.getId().equals(sceneId));
   }

   @Test
   public void testAddGetActor()
   {
      Scene scene = new Scene(sceneId);
      
      Actor actor = new Actor(actorId);
      
      scene.add(actor);
      
      Actor retrievedActor = scene.getActor(actorId);
      
      assertTrue(actor.equals(retrievedActor));
   }

   @Test
   public void testRemove_Actor()
   {
      Scene scene = new Scene(sceneId);
      
      Actor actor = new Actor(actorId);
      
      scene.add(actor);
      scene.remove(actor);
      
      Actor retrievedActor = scene.getActor(actorId);
      
      assertNull(retrievedActor);
   }

   @Test
   public void testRemove_String()
   {
      Scene scene = new Scene(sceneId);
      
      Actor actor = new Actor(actorId);
      
      scene.add(actor);
      scene.remove(actorId);
      
      Actor retrievedActor = scene.getActor(actorId);
      
      assertNull(retrievedActor);
   }

   @Test
   public void testDraw()
   {
      Renderer renderer = new Renderer(800, 600, 1);
      
      Scene scene = new Scene(sceneId);
      
      scene.draw(renderer);
   }

   @Test
   public void testLoad() throws IOException, URISyntaxException, XmlParseException, XmlFormatException
   {
      Scene scene = new Scene(sceneId);
      scene.load(Resource.getFile("/resources/scenes/testScene_00.xml"));
      
      assertTrue(scene.getId().equals(sceneId));
      assertTrue(scene.getActor(actorId) != null);
   }

   @Test
   public void testSave()
   {
      fail("Not yet implemented");      
   }

   @Test
   public void testUpdate()
   {
      Scene scene = new Scene(sceneId);
      scene.update(1000);
   }

   @Test
   public void testGetNodeName()
   {
      Scene scene = new Scene(sceneId);
      assertTrue(scene.getNodeName().equals("scene"));
   }

   @Test
   public void testSerialize() throws XmlFormatException
   {
      Scene scene = new Scene(sceneId);
      
      XmlDocument document = new  XmlDocument();
      document.createRootNode("game");
      
      XmlNode node = scene.serialize(document.getRootNode());
      
      // nodeName
      assertTrue(node.getName().equals(scene.getNodeName()));
      
      // id
      assertTrue(node.getAttribute("id").equals(sceneId));
   }

   @Test
   public void testDeserialize() throws XmlFormatException
   {
      XmlDocument document = new  XmlDocument();
      XmlNode node = document.createRootNode("scene");
      node.setAttribute("id",  "sceneOne");
      
      Scene scene = new Scene("sceneTwo");
      scene.deserialize(node);
      
      // id
      assertTrue(node.getAttribute("id").equals(sceneId));
   }

}
