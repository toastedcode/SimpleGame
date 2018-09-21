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

import com.toast.game.engine.actor.Actor;
import com.toast.game.engine.collision.Collidable;
import com.toast.game.engine.collision.Collision;
import com.toast.game.engine.collision.CollisionHandler;
import com.toast.game.engine.interfaces.Drawable;
import com.toast.game.engine.interfaces.Updatable;
import com.toast.game.engine.message.Message;
import com.toast.game.engine.message.MessageHandler;
import com.toast.game.engine.resource.ScriptResource;
import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Script extends Property implements Drawable, Updatable, MessageHandler, CollisionHandler
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
      DRAW("draw"),
      ON_COLLISION("onCollision"),
      ON_SEPARATION("onSeparation");
      
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
   
   public Script(
      String id,
      ScriptResource resource)
   {
      super(id);
      
      this.resource = resource;
      
      try
      {
         interpreter.source(resource.getFile().getAbsolutePath());
         
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
   
   public Script(XmlNode node) throws XmlFormatException
   {
      super(node);
      
      deserializeThis(node);
   }
   
   public Property clone()
   {
      Script clone = null;
      
      // TODO
      
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
   
   // **************************************************************************
   //                            Updatable interface
   
   @Override
   public void update(long elapsedTime)
   {
      if (supports(Script.Function.UPDATE.getCallString()))
      {
         evaluate(Script.Function.UPDATE, 
                  new Script.Variable("actor", getParent()), 
                  new Script.Variable("elapsedTime", elapsedTime));
      }
   }
   
   // **************************************************************************
   //                          MessageHandler interface

   @Override
   public void handleMessage(Message message)
   {
      if (supports(Script.Function.HANDLE_MESSAGE.getCallString()))
      {
         evaluate(Script.Function.HANDLE_MESSAGE, 
               new Script.Variable("actor", getParent()), 
               new Script.Variable("message", message));
      }
   }
   
   // **************************************************************************
   //                       CollisionHandler interface
   
   @Override
   public void onCollision(Collision collision)
   {
      if (supports(Script.Function.ON_COLLISION.getCallString()))
      {
         evaluate(Script.Function.ON_COLLISION, 
                  new Script.Variable("actor", getParent()),
                  new Script.Variable("collision", collision));
      }
   }
   
   @Override
   public void onSeparation(Collidable collided)
   {
      if (supports(Script.Function.ON_SEPARATION.getCallString()))
      {
         evaluate(Script.Function.ON_SEPARATION, 
                  new Script.Variable("collided", (Actor)collided));
      }
   }
   
   // **************************************************************************
   //                        xml.Serializable interface
   
   /*
      <script resource=""/>
   */
   
   @Override
   public String getNodeName()
   {
      return("script");
   }
   
   @Override
   public XmlNode serialize(XmlNode node)
   {
      XmlNode propertyNode = super.serialize(node);
      
      // resource
      if (resource != null)
      {
         propertyNode.setAttribute("resource",  resource.getId());
      }
      
      return (propertyNode);
   }

   @Override
   public void deserialize(XmlNode node) throws XmlFormatException
   {
      super.deserialize(node);
      
      deserializeThis(node);
   }
   
   // **************************************************************************
   //                           Private operations
   // **************************************************************************
   
   private void scanFunctions() throws IOException
   {
      final String FUNCTION_PARENTHESIS = "()";
      
      BufferedReader br = null;
      String line = null;
      
      br = new BufferedReader(new FileReader(resource.getFile()));
      
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
            
            startPos++;
            
            String functionName = line.substring(startPos, endPos);
            functions.add(functionName);
         }
      }
      
      br.close();
   }
   
   private void deserializeThis(XmlNode node) throws XmlFormatException
   {
      // resource
      if (node.hasAttribute("resource"))
      {
         String resourceId = node.getAttribute("resource").getValue();
         
         resource = ScriptResource.getResource(resourceId);
         
         if ((resource != null) &&
             (resource.isLoaded()))
         {
            try
            {
               interpreter.source(resource.getFile().getAbsolutePath());
               
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
      }
   }
   
   private ScriptResource resource;

   private Interpreter interpreter = new Interpreter();
   
   private boolean isValid;
   
   private List<String> functions = new ArrayList<>();
}
