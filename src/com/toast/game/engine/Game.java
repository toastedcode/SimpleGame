package com.toast.game.engine;

import java.awt.Graphics;
import java.util.Map;

import javax.swing.JPanel;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.actor.Timer;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.Messenger;
import com.toast.xml.XmlNode;

public class Game
{
   // **************************************************************************
   //                             Public operations
   // **************************************************************************
   
   public static void create(String title, int width, int height, int layers)
   {
      Game.title = title;
      
      createRenderer(width, height, layers);
      createGamePanel();
      createGameLoop();      
   }
   
   public static void load(XmlNode node)
   {
      title = "Simple Game";
      createRenderer(800, 600, 3);
      createGamePanel();
      createGameLoop();      
   }
   
   public static void add(Scene scene)
   {
      scenes.put(scene.getId(), scene);
   }
   
   public static Scene getCurrentScene()
   {
      return (currentScene);
   }
   
   public static void setCurrentScene(Scene scene)
   {
      currentScene = scene;
   }
   
   public static void remove(Scene scene)
   {
      if (currentScene == scene)
      {
         currentScene = null;
      }
      
      scenes.remove(scene.getId());
   }
   
   public static void add(Actor actor)
   {
      if (currentScene != null)
      {
         Messenger.register(actor);
         currentScene.add(actor);
      }
   }
   
   public static Actor getActor(String id)
   {
      Actor actor = null;
      
      if (currentScene != null)
      {
         actor = currentScene.getActor(id);
      }
      
      return (actor);
   }
   
   public static void remove(Actor actor)
   {
      if (currentScene != null)
      {
         Messenger.unregister(actor);
         currentScene.remove(actor);
      }
   }
   
   public static JPanel getGamePanel()
   {
      return (gamePanel);
   }
   
   public static void play()
   {
      Timing.reset();
      isPaused = false;
   }
   
   public static void pause()
   {
      isPaused = true;
   }
   
   public static boolean isPaused()
   {
      return (isPaused);
   }
   
   public static Timer startTimer(
      String id,
      double duration,
      boolean isPeriodic,
      Message message)
   {
      Timer timer = new Timer(id, duration, isPeriodic, message);
      
      Game.getCurrentScene().add(timer);
      timer.start();
      
      return (timer);
   }
   
   public static void cancelTimer(String id)
   {
      Game.remove(Game.getActor(id));
   }
   
   // **************************************************************************
   //                             Private operations
   // **************************************************************************
   
   private static void createRenderer(int width, int height, int layers)
   {
      renderer = new Renderer(width, height, layers);
   }
   
   @SuppressWarnings("serial")
   private static void createGamePanel()
   {
      gamePanel = new JPanel()
      {
         @Override
         public void paint(Graphics graphics)
         {
            if (renderer != null)
            {
               renderer.paint(graphics,  this);
            }
         }
      };
      
      Input.observe(gamePanel);
   }
   
   private static void createGameLoop()
   {
      // Create the game loop on a separate thread.
      Thread loop = new Thread()
      {
         @Override
         public void run()
         {
            gameLoop();
         }
      };
      
      // Go!
      loop.start();        
   }
   
   
   private static void gameLoop()
   {
      while (true)
      {
         // Get the elapsed time (in milliseconds) since the last update.         
         long elapsedTime = Timing.update();
        
         if (elapsedTime > 0)
         {
            if (isPaused() == false)
            {
               update(elapsedTime);
            
               draw();
            }
            
            // Force the game panel to repaint.
            gamePanel.repaint();
            
            // Update the frame count.
            Timing.incrementFrameCount();
         }
         
         // Sleep
         try
         {
            Thread.sleep(Timing.getSleepTime());
         }
         catch (Exception e)
         {
            // TODO: Logging.
         }
      }
   }
   
   private static void update(long elapsedTime)
   {
      if (currentScene != null)
      {
         currentScene.update(elapsedTime);
      }
   }
   
   private static void draw()
   {
      if ((renderer != null) &&
          (currentScene != null))
      {
         renderer.clear();
         currentScene.draw(renderer);
      }
   }
   
   private static String title;
   
   private static JPanel gamePanel;
   
   private static Renderer renderer;
   
   private static Map<String, Scene> scenes;
   
   private static Scene currentScene;
   
   private static boolean isPaused = false;
}
