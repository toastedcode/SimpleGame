package com.toast.game.engine.property;

import bsh.EvalError;
import bsh.Interpreter;

import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Updatable;

public class Script extends Property implements Drawable, Updatable
{
   // **************************************************************************
   //                             Enumerations
   // **************************************************************************
   
   public enum Function
   {
      INIT("initialize"),
      UPDATE("update"),
      HANDLE_EVENT("handleEvent"),
      DESTROY("destroy"),
      DRAW("draw");
      
      private Function(String callString)
      {
         CALL_STRING = callString;
      }
      
      public String getCallString()
      {
         return (CALL_STRING);
      }
      
      private final String CALL_STRING;
   }
   
   
   public static class Variable
   {
      public Variable(
         String name,
         Object value)
      {
         NAME = name;
         VALUE = value;
      }
      
      public String getName()
      {
         return (NAME);
      }
      
      public Object getValue()
      {
         return (VALUE);
      }
      
      private final String NAME;
      
      private final Object VALUE;
   }
   
   // **************************************************************************
   //                           Public Operations
   // **************************************************************************
   
   public Script(
      String id,
      File file)
   {
      super(id);
      
      try
      {
         interpreter.source(file.getAbsolutePath());
         isValid = true;
      } 
      catch (FileNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (EvalError e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public boolean isValid()
   {
      return (isValid);
   }
   
   public void evaluate(
      Script.Function function,
      Script.Variable ... variables)
   {
      try
      {
         // Get the call string.
         String callString = function.getCallString() + "()";
         
         // Set parameters.
         for (Script.Variable variable : variables)
         {
            interpreter.set(variable.getName(),  variable.getValue());
         }
         
         // Evaluate.
         interpreter.eval(callString);
      }
      catch (EvalError e)
      {
      }
   }
   
   @Override
   public void draw(Graphics graphics)
   {
      evaluate(Script.Function.DRAW,
               new Script.Variable("actor", getParent()),
               new Script.Variable("graphics", graphics));
   }
  
   @Override
   public int getWidth()
   {
      // TODO
      return (0);
   }
   
   @Override
   public int getHeight()
   {
      // TODO
      return (0);
   }
   
   @Override
   public boolean isVisible()
   {
      // TODO      
      return false;
   }   
   
   @Override
   public void update(long elapsedTime)
   {
      evaluate(Script.Function.UPDATE, 
               new Script.Variable("actor", getParent()), 
               new Script.Variable("elapsedTime", elapsedTime));
   }
   
   private Interpreter interpreter = new Interpreter();
   
   private boolean isValid;
}
