package com.naxanria.tools.minecraftforgemodsinfo;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
  @author: Naxanria
*/
public class FileUtil
{
  public static List<Path> getFiles(Path path)
  {
    if (!Files.isDirectory(path))
    {
      return Collections.emptyList();
    }
    
    List<Path> files = new ArrayList<>();
    
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, new FileFilter()))
    {
      for (Path p : ds)
      {
        files.add(p);
      }
      
      ds.close();
      return files;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  
    return Collections.emptyList();
  }
  
  public static List<Path> getDirectories(Path path)
  {
    if (!Files.isDirectory(path))
    {
      return Collections.emptyList();
    }
  
    List<Path> dirs = new ArrayList<>();
  
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, new DirectoryFilter()))
    {
      for (Path p : ds)
      {
        dirs.add(p);
      }
      ds.close();
      return dirs;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return Collections.emptyList();
  }
  
  public static String getName(Path path)
  {
    int index = path.getNameCount() - 1;
    if (index < 0)
    {
      return path.toString();
    }
    
    return path.getName(index).toString();
  }
  
  public static boolean isExtension(Path path, String extension)
  {
    return getName(path).endsWith(extension);
  }
  
  public static FileSystem getSystemFor(Path path)
  {
    try
    {
      return FileSystems.newFileSystem(path, null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public static List<String> getToml(Path jarPath)
  {
    try (FileSystem system = getSystemFor(jarPath))
    {
      if (system == null)
      {
        return Collections.emptyList();
      }
  
      return Files.readAllLines(system.getPath("META-INF/mods.toml"));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return Collections.emptyList();
  }
  
  public static String getModVersion(Path jarPath)
  {
    try (FileSystem system = getSystemFor(jarPath))
    {
      if (system == null)
      {
        return "UNKNOWN";
      }
  
      List<String> strings = Files.readAllLines(system.getPath("META-INF/MANIFEST.MF"));
      for (String line : strings)
      {
        if (line.startsWith("Implementation-Version:"))
        {
          return line.substring(24);
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  
    return "UNKNOWN";
  }
  
  public static void save(JsonElement element, Path path)
  {
    Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .create();
    
    try (FileWriter writer = new FileWriter(requestFile(path)))
    {
      gson.toJson(element, writer);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private static File requestFile(Path path)
  {
    createIfMissing(path);
    return path.toFile();
  }
  
  public static boolean createIfMissing(Path path)
  {
    boolean exists = Files.exists(path);
    if (!exists)
    {
      try
      {
        Path parent = path.getParent();
        if (parent != null)
        {
          if (!Files.exists(parent))
          {
            Files.createDirectories(parent);
    
            Logger.info("Create directories for {}", parent);
          }
        }
        
        Files.createFile(path);
        
        Logger.info("Created file {}", path);
        return true;
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return false;
      }
    }
    
    return true;
  }
  
  
  public static class DirectoryFilter implements DirectoryStream.Filter<Path>
  {
    @Override
    public boolean accept(Path entry) throws IOException
    {
      return Files.isDirectory(entry);
    }
  }
  
  public static class FileFilter implements DirectoryStream.Filter<Path>
  {
    @Override
    public boolean accept(Path entry) throws IOException
    {
      return !Files.isDirectory(entry);
    }
  }
}
