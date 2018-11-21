package com.toast.game.engine.collision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.toast.game.engine.collision.CollisionManager;
import com.toast.game.engine.interfaces.Drawable;

public class CollisionManagerTest
{
   private static class CollidableRect implements Drawable, Collidable
   {
      public CollidableRect(int x, int y, int width, int height)
      {
         rectangle = new Rectangle2D.Double(x, y, width, height);
      }
      
      public Rectangle2D.Double getRectangle()
      {
         return (rectangle);
      }
      
      @Override
      public boolean isCollisionEnabled()
      {
         return (true);
      }

      @Override
      public Shape getCollisionShape()
      {
         return (rectangle);
      }

      @Override
      public void onCollision(Collision collision)
      {
         System.out.format("Collision!\n");
      }

      @Override
      public void onSeparation(Collidable collidable)
      {
         System.out.format("Separation!\n");
      }

      @Override
      public void draw(Graphics graphics)
      {
         boolean isCollided = (CollisionManager.getCollisions(this).size() > 0);
         
         graphics.setColor((isCollided ? Color.RED : Color.WHITE));
         
         graphics.drawRect((int)rectangle.getX(), 
                           (int)rectangle.getY(),
                           (int)rectangle.getWidth(), 
                           (int)rectangle.getHeight());
      }

      @Override
      public int getWidth()
      {
         return ((int)rectangle.getWidth());
      }
      
      @Override
      public int getHeight()
      {
         return ((int)rectangle.getHeight());
      }
      
      Rectangle2D.Double rectangle;
   }
   
   @SuppressWarnings("serial")
   public static void main(final String args[])
   {
      JFrame frame = new JFrame("Collision Manager Test");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 600);
      
      // Rectangles
      createRectangle(0, 0, 50, 50);
      createRectangle(100, 100, 50, 50);
      playerRect = createRectangle(200, 200, 50, 50);
      
      panel = new JPanel()
      {
         @Override
         public void paint(Graphics graphics)
         {
            
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, 800, 600);
            
            for (CollidableRect rect : rectangles)
            {
               rect.draw(graphics);
            }
         }
      };
      
      panel.setFocusable(true);
      panel.addKeyListener(new KeyListener()
      {
         @Override
         public void keyPressed(KeyEvent event)
         {
            switch (event.getKeyCode())
            {
               case KeyEvent.VK_UP:
               case KeyEvent.VK_DOWN:
               case KeyEvent.VK_LEFT:
               case KeyEvent.VK_RIGHT:
               {
                  movePlayer(event.getKeyCode());
                  break;
               }
               
               default:
               {
                  break;
               }
            }
         }

         @Override
         public void keyReleased(KeyEvent event)
         {
         }

         @Override
         public void keyTyped(KeyEvent event)
         {
         }
      });
      
      frame.getContentPane().add(panel, BorderLayout.CENTER);
      frame.setVisible(true);
   }
   
   static private CollidableRect createRectangle(int x, int y, int width, int height)
   {
      CollidableRect rect = new CollidableRect(x, y, width, height);
      rectangles.add(rect);
      CollisionManager.register(rect);
      
      return (rect);
   }
   
   static private void movePlayer(int keyCode)
   {
      switch (keyCode)
      {
         case KeyEvent.VK_UP:
         {
            playerRect.getRectangle().y -= 5;
            break;
         }
         
         case KeyEvent.VK_DOWN:
         {
            playerRect.getRectangle().y += 5;
            break;
         }
         
         case KeyEvent.VK_LEFT:
         {
            playerRect.getRectangle().x -= 5;
            break;
         }
         
         case KeyEvent.VK_RIGHT:
         {
            playerRect.getRectangle().x += 5;
            break;
         }
      }
      
      CollisionManager.update(0);
      
      panel.repaint();
   }
   
   static private JPanel panel;
   
   static CollidableRect playerRect;   
   
   static List<CollidableRect> rectangles = new ArrayList<>();
}
