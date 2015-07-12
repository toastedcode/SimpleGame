package com.toast.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import bsh.EvalError;
import bsh.Interpreter;

import com.toast.game.engine.Game;
import com.toast.game.engine.Scene;
import com.toast.game.engine.resource.Resource;
import com.toast.game.engine.resource.ResourceCreationException;
import com.toast.game.engine.resource.ScriptResource;
import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;

public class Game_00
{
   public static void main(final String args[])
   {
      final Dimension SCREEN_DIMENSION = new Dimension(1020, 435);
      
      JFrame frame = new JFrame("Game_00");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize((int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight());
      
      // Resources
      try
      {
         Resource.loadResource("/resources/images/background.png");
         Resource.loadResource("/resources/images/player.png");
         Resource.loadResource("/resources/scripts/saveGame.bsh");
      }
      catch (ResourceCreationException e)
      {
      }
      
      Game.create("Game_00", (int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight(), 1);
      
      Scene levelOne = new Scene("level 1");
      
      try
      {
         levelOne.load("/resources/scenes/scene_00.xml");
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
