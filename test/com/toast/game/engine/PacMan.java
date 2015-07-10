package com.toast.game.engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.sun.glass.events.KeyEvent;
import com.toast.game.common.Vector2D;
import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.actor.Generator;
import com.toast.game.engine.actor.Path;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.Messenger;
import com.toast.game.engine.property.Animation;
import com.toast.game.engine.property.AnimationGroup;
import com.toast.game.engine.property.Animation.AnimationDirection;
import com.toast.game.engine.property.Animation.AnimationType;
import com.toast.game.engine.property.Follower.FollowDirection;
import com.toast.game.engine.property.Follower;
import com.toast.game.engine.property.Follower.FollowType;
import com.toast.game.engine.property.Image;
import com.toast.game.engine.property.Motor;
import com.toast.game.engine.property.Physics;
import com.toast.game.engine.property.Script;
import com.toast.game.engine.resource.ImageResource;
import com.toast.game.engine.resource.Resource;
import com.toast.game.engine.resource.ResourceCreationException;
import com.toast.game.engine.resource.ScriptResource;
import com.toast.game.engine.resource.XmlResource;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;

public class PacMan
{
   public static void main(final String args[])
   {
      final Dimension SCREEN_DIMENSION = new Dimension(800, 600);
      
      JFrame frame = new JFrame("Test");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize((int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight());
      
      // Resources
      try
      {
         Resource.loadResource("/resources/images/jason.png");
         Resource.loadResource("/resources/images/boxy.png");
         Resource.loadResource("/resources/images/rain.png");
         Resource.loadResource("/resources/images/cloud.png");
         Resource.loadResource("/resources/images/pacMan.png");
         Resource.loadResource("/resources/scripts/test.bsh");
         Resource.loadResource("/resources/scripts/cloud.bsh");
         Resource.loadResource("/resources/animations/boxy.anim");
         Resource.loadResource("/resources/animations/pacMan.anim");
      }
      catch (ResourceCreationException e)
      {
         throw (new RuntimeException(e));
      }
      
      Game.create("Pac Man", (int)SCREEN_DIMENSION.getWidth(), (int)SCREEN_DIMENSION.getHeight(), 1);
      
      Scene levelOne = new Scene("level 1");
      Game.setCurrentScene(levelOne);
      
      // Jason
      
      Actor jason = new Actor("jason");
      jason.add(new Image("jasonHead", ImageResource.getResource("jason.png")));
      jason.setZOrder(0);
      
      Physics physics = new Physics("physics");
      physics.setVelocity(new Vector2D(150, 0));
      jason.add(physics);
      
      Script script = new Script("script", ScriptResource.getResource("test.bsh"));
      jason.add(script);
      
      jason.setState("highScore",  100);
      int highScore = (Integer)jason.getState("highScore");
      System.out.format("Value of jason.highScore: %d\n", highScore);
      
      Messenger.register(jason, "msgTEST_BROADCAST");
      Messenger.register(jason, "msgKEY_PRESSED");
      Messenger.register(jason, "msgKEY_RELEASED");

      levelOne.add(jason);
      
      // Path
      
      Path path = new Path("boxyPath", new Point(0, 0), new Point(50, 50), new Point(100, 50), new Point(300, 250));
      path.setVisible(true);
      levelOne.add(path);

      // Boxy
      
      Actor boxy = new Actor("boxy");
      boxy.setZOrder(1);
      
      Animation idleAnim = new Animation("walk", 
                                         ImageResource.getResource("boxy.png"),
                                         XmlResource.getResource("boxy.anim"),
                                         "walk",
                                          3);
      idleAnim.start(AnimationType.LOOP, AnimationDirection.FORWARD);
      boxy.add(idleAnim);
      
      Follower follower = new Follower("follower");
      follower.follow(path, FollowType.BOUNCE, FollowDirection.FORWARD, 80);
      boxy.add(follower);
      
      levelOne.add(boxy);
      
      // Rain drop
      
      Actor drop = new Actor("drop");
      drop.add(new Image("rainDrop", ImageResource.getResource("rain.png")));
      
      physics = new Physics("physics");
      physics.setGravity(new Vector2D(0, 100));
      drop.add(physics);
      
      // Rain cloud
      
      Generator cloud = new Generator("cloud");
      cloud.setFrequency(500);
      cloud.setActor(drop);
      cloud.moveTo(300, 300);
      cloud.add(new Image("rainCloud", ImageResource.getResource("cloud.png")));
      
      script = new Script("rainScript", ScriptResource.getResource("cloud.bsh"));
      cloud.add(script);
      
      levelOne.add(cloud);
      
      // Pac Man
      
      Actor pacMan = new Actor("pacMan");
      pacMan.moveTo(0, 300);
      
      ImageResource spriteMap = ImageResource.getResource("pacMan.png");
      XmlResource animMap = XmlResource.getResource("pacMan.anim");
      Animation right = new Animation("right", 
                                      spriteMap,
                                      animMap,
                                      "right",
                                      10);
      Animation left = new Animation("left", 
                                     spriteMap,
                                     animMap,
                                     "left",
                                     10);
      
      AnimationGroup animationGroup = new AnimationGroup("pacManAnimations");
      animationGroup.addAnimation(right);
      animationGroup.addAnimation(left);
      animationGroup.setAnimation("right", AnimationType.LOOP, AnimationDirection.FORWARD);
      
      pacMan.add(animationGroup);
      
      Messenger.register(pacMan, "msgKEY_PRESSED");
      
      Motor motor = new Motor("motor");
      motor.mapKey(KeyEvent.VK_LEFT, Motor.Direction.LEFT);
      motor.mapKey(KeyEvent.VK_RIGHT, Motor.Direction.RIGHT);
      motor.mapKey(KeyEvent.VK_UP, Motor.Direction.UP);
      motor.mapKey(KeyEvent.VK_DOWN, Motor.Direction.DOWN);
      motor.mapKey(KeyEvent.VK_A, Motor.Direction.LEFT);
      motor.mapKey(KeyEvent.VK_D, Motor.Direction.RIGHT);
      motor.mapKey(KeyEvent.VK_W, Motor.Direction.UP);
      motor.mapKey(KeyEvent.VK_S, Motor.Direction.DOWN);

      pacMan.add(motor);
      
      levelOne.add(pacMan);

      frame.getContentPane().add(Game.getGamePanel(), BorderLayout.CENTER);
      frame.setVisible(true);
      
      Game.startTimer("testTimer", 1000, true, new Message("msgTIMEOUT", "testTimer", "jason"));
      
      XmlDocument document = new XmlDocument();
      document.createRootNode("game");
      XmlNode sceneNode = Game.getCurrentScene().serialize(document.getRootNode());
      System.out.println(sceneNode.toString());
      
      Game.play();
      
      // TODO: Not a good test because we're sending a message from outside the game loop.
      Messenger.sendMessage(new Message("msgTEST", "game", "jason"));
      Messenger.sendMessage(new Message("msgTEST_BROADCAST", "game", null));
   }
}
