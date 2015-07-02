package com.toast.game.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;

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
   
   /*
   public static void handleMousePressed(
      MouseEvent e)
   {
      mousePosition.x = e.getX();
      mousePosition.y = e.getY();
      
      Sprite clickedSprite = getClickedSprite(e);
      
      if (clickedSprite != null)
      {
         Event event = new Event("eventMOUSE_PRESSED");
         event.addPayload("mouseEvent", e);
         EventManager.sendEvent(event, clickedSprite.getSpriteId());
      }
      
      SelectionManager.getInstance().mousePressed(e);
   }
      
      
   public static void handleMouseReleased(
      MouseEvent e)
   {
      mousePosition.x = e.getX();
      mousePosition.y = e.getY();
      
      Sprite clickedSprite = getClickedSprite(e);
      
      if (clickedSprite != null)
      {
         Event event = new Event("eventMOUSE_RELEASED");
         event.addPayload("mouseEvent", e);
         EventManager.sendEvent(event, clickedSprite.getSpriteId());
      }
      
      SelectionManager.getInstance().mouseReleased(e);
   }
   
   
   public static void handleMouseMoved(
      MouseEvent e)         
   {
      mousePosition.x = e.getX();
      mousePosition.y = e.getY();
   }
   
   
   public static void handleMouseDragged(
      MouseEvent e)
   {
      SelectionManager.getInstance().mouseDragged(e);
   }

   
   public static Point getMousePosition()
   {
      return (mousePosition);
   }

   
   public static Point getMouseWorldPosition()
   {
      return (ViewManager.getWorldPosition(mousePosition));
   }
   */
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
  
   //static Point mousePosition;
   
   
   // **************************************************************************
   //                          Private Operations
   // **************************************************************************
   
   /*
   private static Sprite getClickedSprite(
      MouseEvent e)
   {
      Sprite clickedSprite = null;
      
      Point mousePosition = new Point(e.getX(), e.getY());
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
      
      return (clickedSprite);
   }
   */  
}