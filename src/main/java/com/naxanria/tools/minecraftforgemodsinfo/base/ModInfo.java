package com.naxanria.tools.minecraftforgemodsinfo.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
  @author: Naxanria
*/
public class ModInfo
{
  public static class Entry
  {
    public static final Entry EMPTY = new Entry("EMPTY", "EMPTY");
    
    public final String key;
    public final String value;
    public final boolean isList;
    public final List<String> list;
  
    public Entry(String key, String value)
    {
      this.key = key;
      this.value = value;
      
      isList = false;
      list = Collections.emptyList();
    }
  
    public Entry(String key, List<String> list)
    {
      this.key = key;
      this.isList = true;
      this.list = list;
      
      value = "LIST";
    }
  }
  
  private final Map<String, Entry> keyMap = new HashMap<>();
  
  public void add(String key, String value)
  {
    keyMap.put(key, new Entry(key, value));
  }
  
  public void add(String key, List<String> list)
  {
    keyMap.put(key, new Entry(key, list));
  }
  
  public Entry getEntry(String key)
  {
    return keyMap.getOrDefault(key, Entry.EMPTY);
  }
  
  public String getValue(String key)
  {
    Entry entry = getEntry(key);
    
    if (entry.isList)
    {
      throw new IllegalStateException("The value for " + key + " is a list!");
    }
    
    return entry.value;
  }
  
  public List<String> getList(String key)
  {
    Entry entry = getEntry(key);
    
    if (entry.equals(Entry.EMPTY))
    {
      return Collections.emptyList();
    }
    
    if (!entry.isList)
    {
      throw new IllegalStateException("The value for " + key + " is not a list!");
    }
    
    return entry.list;
  }
}
