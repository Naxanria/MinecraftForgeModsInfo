package com.naxanria.tools.minecraftforgemodsinfo.parser;

/*
  @author: Naxanria
*/
public class ParserContext
{
  private static ParserContext context;
  
  public static ParserContext getCurrentContext()
  {
    if (context == null)
    {
      context = new ParserContext();
    }
    
    return context;
  }
  
  public String currentFile;
  public String saveFile;
  public String localFile;
  public ParserVersion version = ParserVersion.ONE_FIFTEEN;
  
  public ParserContext()
  { }
}
