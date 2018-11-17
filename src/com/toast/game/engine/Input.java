package com.toast.game.engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JComponent;

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.collision.Collidable;
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
      
      component.addMouseWheelListener(new MouseWheelListener()
      {
         @Override
         public void mouseWheelMoved(MouseWheelEvent event)
         {
            Input.handleMouseWheelMoved(event);
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
      Messenger.sendMessage(new Message("msgMOUSE_CLICKED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("mouseEvent", event)));
      
      if (mouseOverActor != null)
      {
         Messenger.sendMessage(new Message("msgMOUSE_CLICKED", 
                                           "input", 
                                           mouseOverActor.getAddress(), 
                                           new Message.Parameter("mouseEvent", event)));         
      }
   }

   protected static void handleMousePressed(MouseEvent event)
   {
      Messenger.sendMessage(new Message("msgMOUSE_PRESSED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("mouseEvent", event)));
   }
      
   public static void handleMouseReleased(MouseEvent event)
   {
      Messenger.sendMessage(new Message("msgMOUSE_RELEASED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("mouseEvent", event)));
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
      
      updateMouseOver(event);
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
      
      updateMouseOver(event);
   }
   
   public static void handleMouseWheelMoved(
      MouseEvent event)
   {
      Messenger.sendMessage(new Message("msgMOUSE_WHEEL_MOVED", 
                                        "input", 
                                        null, 
                                        new Message.Parameter("mouseEvent", event)));
      
      updateMouseOver(event);
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
   
   public static Actor getMouseOverActor()
   {
      return (mouseOverActor);
   }
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
  
   static Point mousePosition = new Point();
   
   static Actor mouseOverActor = null;
   
   // **************************************************************************
   //                          Private Operations
   // **************************************************************************
   
   private static void updateMouseOver(
      MouseEvent event)
   {
      Actor actor = null;
      Actor mouseOverActor = null;
      
      List<Collidable> collidables = CollisionManager.checkIntersection(getMouseWorldPosition());
      
      for (Collidable collidable : collidables)
      {
         if (collidable instanceof Actor)
         {
            actor = (Actor)collidable;
            
            if ((mouseOverActor == null) || (actor.getZOrder() > mouseOverActor.getZOrder()))
            {
               mouseOverActor = actor;          
            }
         }
      }
      
      if (mouseOverActor != Input.mouseOverActor)
      {
         if (Input.mouseOverActor != null)
         {
            Messenger.sendMessage(new Message("msgMOUSE_OFF", 
                                              "input", 
                                              Input.mouseOverActor.getAddress(),
                                              new Message.Parameter("mouseEvent", event)));            
         }
         
         if (mouseOverActor != null)
         {
            Messenger.sendMessage(new Message("msgMOUSE_ON", 
                                              "input", 
                                              mouseOverActor.getAddress(),
                                              new Message.Parameter("mouseEvent", event)));            
         }
         
         Input.mouseOverActor = mouseOverActor;
      }
   }
}