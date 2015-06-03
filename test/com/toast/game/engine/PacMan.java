package com.toast.game.engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import com.toast.game.common.Vector2D;
import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.actor.Path;
import com.toast.game.engine.property.Animation;
import com.toast.game.engine.property.Animation.AnimationDirection;
import com.toast.game.engine.property.Animation.AnimationType;
import com.toast.game.engine.property.Follower.FollowDirection;
import com.toast.game.engine.property.Follower;
import com.toast.game.engine.property.Follower.FollowType;
import com.toast.game.engine.property.Image;
import com.toast.game.engine.property.Physics;
import com.toast.game.engine.property.Script;
import com.toast.swing.Resource;

public class PacMan
{
   public static void main(final String args[])
   {
      final Dimension SCREEN_DIMENSION = new Dimension(800, 600);
      
      JFrame frame = new JFrame("Test");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize((int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight());
      
      Game.create("Pac Man", (int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight(), 1);
      
      Scene levelOne = new Scene("level 1");
      Game.setCurrentScene(levelOne);
      
      // Jason
      
      Actor jason = new Actor("jason");
      jason.add(new Image("jasonHead", Resource.getImage("/resources/images/jason.png")));
      jason.setZOrder(0);
      
      Physics physics = new Physics("physics");
      physics.setVelocity(new Vector2D(150, 0));
      jason.add(physics);
      
      Script script = new Script("script", Resource.getFile("/resources/scripts/test.bsh"));
      jason.add(script);

      levelOne.add(jason);
      
      // Path
      
      Path path = new Path("boxyPath", new Point(0, 0), new Point(50, 50), new Point(100, 50), new Point(300, 250));
      path.setVisible(true);
      levelOne.add(path);

      // Boxy
      
      Actor boxy = new Actor("boxy");
      boxy.setZOrder(1);
      
      Animation idleAnim = new Animation("walk", 
                                         Resource.getImage("/resources/images/boxy.png"),
                                         new AnimationMap(Resource.getXmlDocument("/resources/animations/boxy.anim")),
                                         "walk",
                                          3);
      idleAnim.start(AnimationType.NORMAL, AnimationDirection.FORWARD);
      boxy.add(idleAnim);
      
      Follower follower = new Follower("follower");
      follower.follow(path, FollowType.BOUNCE, FollowDirection.FORWARD, 80);
      boxy.add(follower);
      
      levelOne.add(boxy);

      frame.getContentPane().add(Game.getGamePanel(), BorderLayout.CENTER);
      frame.setVisible(true);
      
      Game.play();
   }
}
