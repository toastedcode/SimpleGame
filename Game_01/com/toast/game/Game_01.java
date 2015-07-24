package com.toast.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import com.toast.game.engine.Game;
import com.toast.game.engine.Scene;
import com.toast.game.engine.resource.Resource;
import com.toast.game.engine.resource.ResourceCreationException;
import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;

public class Game_01
{
   public static void main(final String args[])
   {
      final Dimension SCREEN_DIMENSION = new Dimension(1200, 445);
      
      JFrame frame = new JFrame("Game_00");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize((int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight());
      
      // Resources
      try
      {
         Resource.loadResource("/resources/images/background.png");
         Resource.loadResource("/resources/images/camel.png");
         Resource.loadResource("/resources/animations/camel.anim");
         Resource.loadResource("/resources/scripts/saveGame.bsh");
      }
      catch (ResourceCreationException e)
      {
      }
      
      Game.create("Game_01", (int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight(), 1);
      
      Scene levelOne = new Scene("level 1");
      
      try
      {
         levelOne.load(Resource.getFile("/resources/scenes/scene_00.xml"));
      }
      catch (IOException | XmlParseException | XmlFormatException e)
      {
         e.printStackTrace();
      }
      
      Game.setCurrentScene(levelOne);
      
      frame.getContentPane().add(Game.getGamePanel(), BorderLayout.CENTER);
      frame.setVisible(true);
      
      Game.play();
   }
}
