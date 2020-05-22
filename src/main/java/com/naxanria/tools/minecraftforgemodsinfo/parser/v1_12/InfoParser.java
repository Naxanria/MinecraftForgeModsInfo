package com.naxanria.tools.minecraftforgemodsinfo.parser.v1_12;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.naxanria.tools.minecraftforgemodsinfo.Logger;
import com.naxanria.tools.minecraftforgemodsinfo.parser.ParserContext;

import java.util.ArrayList;
import java.util.List;

/*
  @author: Naxanria
*/
public class InfoParser
{
  public static List<JsonObject> parse(JsonArray array)
  {
    ParserContext context = ParserContext.getCurrentContext();
  
    List<JsonObject> objects = new ArrayList<>();
  
    for (int i = 0; i < array.size(); i++)
    {
      JsonElement element = array.get(i);
      if (element instanceof JsonObject)
      {
        JsonObject object = (JsonObject) element;
        
        object.addProperty("fileName", context.currentFile);
        
        objects.add(object);
      }
      else
      {
        Logger.err("{} contained an invalid format", context.currentFile);
      }
    }
    
    return objects;
  }
  
}
