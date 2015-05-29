package com.toast.game.engine;

public class Timing
{
   // **************************************************************************
   //                          Public Operations
   // **************************************************************************
   
   public static void reset()
   {
      gameTime = System.currentTimeMillis();
      lastFrameSecond = gameTime;
      frameCount = 0;
      frameRate = 0.0;
   }
   
   public static long update()         
   {
      long elapsedTime = System.currentTimeMillis() - gameTime;
      gameTime = System.currentTimeMillis();
      
      return (elapsedTime);
   }
   
   // This operation gets the amount of sleep time (in milliseconds) for a single cycle of
   // the game loop, based on how long it has been since the last sleep time.
   public static long getSleepTime()
   {
      // Initialize the return value.
      long sleepTime = sMIN_SLEEP_TIME;  // miliseconds
      
      long currentTime = System.currentTimeMillis();
      long elapsedTime = (currentTime - gameTime);
      
      // Calculate the sleep time based on our target frame time 
      // and how much time has actually passed.
      sleepTime = (sTARGET_FRAME_TIME - elapsedTime);
      if (sleepTime < 0)
      {
         sleepTime = sMIN_SLEEP_TIME;
      }
      
      return (sleepTime);
   }
   
   // This operation increments the current frame count and calculates the current frame rate.
   public static void incrementFrameCount()
   {
      frameCount++;
      
      long currentTime = System.currentTimeMillis();
      long elapsedTime = (currentTime - lastFrameSecond);
      
      if (elapsedTime > MILISECONDS_PER_SECOND)
      {
         // Calculate the new frame rate.
         frameRate = ((double)frameCount / ((double)elapsedTime / (double)MILISECONDS_PER_SECOND));
         
         // Reset the counting variables.
         lastFrameSecond = currentTime;
         frameCount = 0;
      }
   }
   
   // This operation retrieves the current game frame rate.
   public static double getFrameRate()
   {
      return (frameRate);
   }
   
   // **************************************************************************
   //                          Private Operations
   // **************************************************************************
   
   private Timing()
   {
   }   
   
   // **************************************************************************
   //                          Private Attributes
   // **************************************************************************
   
   // A constant specifying the target frames per second
   private final static int sTARGET_FPS = 30;
   
   private static final long MILISECONDS_PER_SECOND = 1000;
   
   private final static long sTARGET_FRAME_TIME = (MILISECONDS_PER_SECOND / sTARGET_FPS);   
  
   private static final long sMIN_SLEEP_TIME = 1;  // miliseconds
   
   // The last recorded game time (in nanoseconds).
   private static long gameTime;
   
   private static long lastFrameSecond;
   
   private static int frameCount;
   
   private static double frameRate;
}
