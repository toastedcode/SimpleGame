package com.toast.game.engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.collision.CollisionManager;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.Messenger;

public class Input
{
   public static void observe(JComponent component)
   {
      component.setFocusable(true);
      
      component.addKeyListener(new KeyListener()
      {
         @Override
         public void keyPressed(KeyEvent event)
         {
            Input.handleKeyPressed(event);
         }

         @Override
         public void keyReleased(KeyEvent event)
         {
            Input.handleKeyReleased(event);
         }

         @Override
         public void keyTyped(KeyEvent event)
         {
            Input.handleKeyTyped(event);
         }
      });
      
      component.addMouseListener(new MouseListener()
      {
         @Override
         public void mouseClicked(MouseEvent event)
         {
            Input.handleMouseClicked(event);
         }

         @Override
         public void mouseEntered(MouseEvent event)
         {
         }

         @Override
         public void mouseExited(MouseEvent event)
         {
         }

         @Override
         public void mousePressed(MouseEvent event)
         {
            Input.handleMousePressed(event);
         }

         @Override
         public void mouseReleased(MouseEvent event)
         {
            Input.handleMouseReleased(event);
         }
      });
      
      component.addMouseMotionListener(new MouseMotionListener()
      {
         @Override
         public void mouseDragged(MouseEvent event)
         {
            Input.handleMouseDragged(event);
         }

         @Override
         public void mouseMoved(MouseEvent event)
         {
            Input.handleMouseMoved(event);
         }
      });
   }
      
   protected static void handleKeyPressed(KeyEvent event)
   {
      Messenger.sendMessage(new Message("msgKEY_PRESSED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("keyEvent", event)));
   }
   
   protected static void handleKeyReleased(KeyEvent event)
   {
      Messenger.sendMessage(new Message("msgKEY_RELEASED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("keyEvent", event)));
  
   }
   
   protected static void handleKeyTyped(KeyEvent event)
   {
      Messenger.sendMessage(new Message("msgKEY_TYPED", 
                            "input", 
                            null, 
                            new Message.Parameter("keyEvent", event)));
  
   }
   
   protected static void handleMouseClicked(MouseEvent event)
   {
      //Actor clickedActor = getClickedActor(event);
      
      //if (clickedActor != null)
      {
         Messenger.sendMessage(new Message("msgMOUSE_CLICKED", 
                                           "input", 
                                           null, 
                                           new Message.Parameter("mouseEvent", event)));
      }
      
      //SelectionManager.getInstance().mouseClicked(event);
   }

   protected static void handleMousePressed(MouseEvent event)
   {
      //Actor clickedActor = getClickedActor(event);
      
      //if (clickedActor != null)
      {
         Messenger.sendMessage(new Message("msgMOUSE_PRESSED", 
                                           "input", 
                                           null, 
                                           new Message.Parameter("mouseEvent", event)));
      }
      
      //SelectionManager.getInstance().mousePressed(e);
   }
      
   public static void handleMouseReleased(MouseEvent event)
   {
      //Actor clickedActor = getClickedActor(event);
      
      //if (clickedActor != null)
      {
         Messenger.sendMessage(new Message("msgMOUSE_RELEASED", 
                                           "input", 
                                           null, 
                                           new Message.Parameter("mouseEvent", event)));
      }      
      
      //SelectionManager.getInstance().mouseReleased(e);
   }
      
   public static void handleMouseMoved(
      MouseEvent event)         
   {
      mousePosition.x = event.getX();
      mousePosition.y = event.getY();
      
      Messenger.sendMessage(new Message("msgMOUSE_MOVED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("mouseEvent", event)));
   }
      
   public static void handleMouseDragged(
      MouseEvent event)
   {
      mousePosition.x = event.getX();
      mousePosition.y = event.getY();
      
      Messenger.sendMessage(new Message("msgMOUSE_DRAGGED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("mouseEvent", event)));
   }

   
   public static Point getMousePosition()
   {
      return (mousePosition);
   }
   
   public static Point getMouseWorldPosition()
   {
      Point worldPosition = new Point();

      AffineTransform transform = Game.getRenderer().getWorldTransform();
     
      transform.transform(mousePosition,  worldPosition);
      
      return (worldPosition);
   }
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
  
   static Point mousePosition = new Point();
   
   
   // **************************************************************************
   //                          Private Operations
   // **************************************************************************
   
   private static Actor getClickedActor(
      MouseEvent event)
   {
      Actor clickedActor = null;
      
      /*
      Sprite clickedSprite = null;
      
      Point mousePosition = new Point(event.getX(), event.getY());
      Point worldMousePosition = ViewManager.getWorldPosition(mousePosition);
      
      // Get a list of Sprites that contain the clicked position.
      SpriteList sprites = CollisionManager.checkIntersection(worldMousePosition);
      
      // Loop through the Sprites until we find the top-most one that has it's collision enabled.
      for (Sprite sprite : sprites)
      {
         if ((sprite.getCollision() != null) &&
             (sprite.getCollision().isEnabled == true))
         {
            clickedSprite = sprite;
            break;
         }
      }
      */
      
      return (clickedActor);
   }
}