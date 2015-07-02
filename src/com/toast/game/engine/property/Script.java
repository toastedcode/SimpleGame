package com.toast.game.engine.property;

import bsh.EvalError;
import bsh.Interpreter;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;

public class Script extends Property implements Drawable, Updatable, MessageHandler
{
   // **************************************************************************
   //                             Enumerations
   // **************************************************************************
   
   public enum Function
   {
      INIT("initialize"),
      UPDATE("update"),
      HANDLE_MESSAGE("handleMessage"),
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
      
      this.file = file;
      
      try
      {
         interpreter.source(file.getAbsolutePath());
         
         scanFunctions();
         
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
   
   public Property clone()
   {
      Script clone = new Script(getId(), file);
      
      return (clone);
   }
   
   public boolean isValid()
   {
      return (isValid);
   }
   
   boolean supports(String functionName)
   {
      return (functions.contains(functionName));
   }
   
   public void evaluate(
      String functionName,
      Script.Variable ... variables)
   {
      try
      {
         // Get the call string.
         String callString = functionName + "()";
         
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
         // TODO: Handle evaluation errors.
         System.out.format("Eval error: %s", e.toString());
      }
   }
   
   public void evaluate(
      Script.Function function,
      Script.Variable ... variables)
   {
      evaluate(function.getCallString(), variables);
   }
   
   // **************************************************************************
   //                             Drawable interface
   
   @Override
   public void draw(Graphics graphics)
   {
      if (supports(Script.Function.DRAW.getCallString()))
      {
         evaluate(Script.Function.DRAW,
                  new Script.Variable("actor", getParent()),
                  new Script.Variable("graphics", graphics));
      }
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
      return (true);
   }
   
   @Override
   public void setVisible(boolean isVisible)
   {
      // TODO.
   }
   
   // **************************************************************************
   //                            Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      evaluate(Script.Function.UPDATE, 
               new Script.Variable("actor", getParent()), 
               new Script.Variable("elapsedTime", elapsedTime));
   }
   
   // **************************************************************************
   //                          MessageHandler interface

   @Override
   public void handleMessage(Message message)
   {
      evaluate(Script.Function.HANDLE_MESSAGE, 
            new Script.Variable("actor", getParent()), 
            new Script.Variable("message", message));
   }
   
   // **************************************************************************
   //                           Private operations
   // **************************************************************************
   
   private void scanFunctions() throws IOException
   {
      final String FUNCTION_PARENTHESIS = "()";
      
      BufferedReader br = null;
      String line = null;
      
      br = new BufferedReader(new FileReader(file));
      
      while ((line = br.readLine()) != null)
      {
         int startPos = (line.length() - FUNCTION_PARENTHESIS.length());
         int endPos = line.length();
         
         if ((line.length() > FUNCTION_PARENTHESIS.length()) &&
             (line.substring(startPos, endPos).equals(FUNCTION_PARENTHESIS)))
         {
            endPos = startPos;
            startPos--;
            
            while ((startPos != 0) &&
                   (Character.isWhitespace(line.charAt(startPos)) == false))
            {
               startPos--;
            }
            
            String functionName = line.substring(startPos, endPos);
            functions.add(functionName);
         }
      }
      
      br.close();
   }
   
   private File file;

   private Interpreter interpreter = new Interpreter();
   
   private boolean isValid;
   
   private List<String> functions = new ArrayList<>();
}
