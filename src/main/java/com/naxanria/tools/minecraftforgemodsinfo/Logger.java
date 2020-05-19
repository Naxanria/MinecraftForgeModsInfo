package com.naxanria.tools.minecraftforgemodsinfo;

/*
  @author: Naxanria
*/
public class Logger
{
  public static void info(String msg, Object... args)
  {
    System.out.println(format(msg, args));
  }
  
  public static void err(String msg, Object... args)
  {
    System.err.println(format(msg, args));
  }
  
  public static String format(String msg, Object... args)
  {
    int index = 0;
    while (msg.contains("{}") && index < args.length)
    {
      Object o = args[index];
      String obj = o == null ? "NULL" : o.toString();
      
      
      msg = replaceFirst(msg, "{}", obj);
      index++;
    }
    
    return msg;
  }
  
  public static String replaceFirst(String string, String search, String replace)
  {
    if (string.contains(search))
    {
      int index = string.indexOf(search);
      string = string.substring(0, index) + replace + string.substring(index + search.length());
    }
    
    return string;
  }
}
