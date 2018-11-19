package com.toast.game.engine;

import java.awt.Graphics;
import java.net.InetAddress;
import java.util.Map;

import javax.swing.JPanel;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.actor.Timer;
import com.toast.game.engine.collision.CollisionManager;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.Messenger;
import com.toast.game.engine.network.Client;
import com.toast.game.engine.network.Server;
import com.toast.game.engine.network.Synchronize;

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
   
   public String getTitle()
   {
      return (title);
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
      currentScene.initialize();
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
   
   public static Renderer getRenderer()
   {
      return (renderer);
   }
   
   public static void startServer(int listenPort)
   {
      server = new Server();
      server.setup(listenPort);
      server.start();
   }
   
   public static void stopServer()
   {
      if (server != null)
      {
         server.end();
         server = null;
      }
   }
   
   public static Server getServer()
   {
      return (server);
   }
   
   public static void startClient(InetAddress address, int listenPort, int sendPort)
   {
      client = new Client();
      client.setup(address, listenPort, sendPort);
      client.start();
   }
   
   public static void stopClient()
   {
      if (client != null)
      {
         client.end();
         client = null;
      }
   }
   
   public static Client getClient()
   {
      return (client);
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
         
         CollisionManager.update(elapsedTime);
         
         Synchronize.syncronize();
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
   
   private static Server server;
   
   private static Client client;
}
