package com.naxanria.tools.minecraftforgemodsinfo;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.toml.TomlParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.naxanria.tools.minecraftforgemodsinfo.parser.ParserContext;
import com.naxanria.tools.minecraftforgemodsinfo.parser.ParserVersion;
import com.naxanria.tools.minecraftforgemodsinfo.parser.v1_12.InfoParser;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
  @author: Naxanria
*/
public class Main
{
  public static void main(String[] args)
  {
    Options options = new Options();
    Option input = Option.builder().numberOfArgs(1)
      .longOpt("input")
      .argName("input").desc("The input folder for the modpack")
      .build();
    options.addOption(input);
    
    Option output = Option.builder().numberOfArgs(1)
      .longOpt("output")
      .argName("output").desc("The output file the json will be saved into")
      .build();
    options.addOption(output);
    
    Option help = Option.builder()
      .longOpt("help")
      .argName("help").desc("provides help")
      .build();
    options.addOption(help);
    
    Option version = new Option("mc", "mc-version", true, "The version of Minecraft to target");
    options.addOption(version);
  
    HelpFormatter formatter = new HelpFormatter();
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd;
    try
    {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
      formatter.printHelp("utility-name", options);
      
      System.exit(1);
      return;
    }
    
    if (cmd.hasOption("help"))
    {
      help();
      formatter.printHelp("utility-name", options);
      return;
    }
  
    ParserContext context = ParserContext.getCurrentContext();
    
    String localFile = cmd.hasOption("-input") ? cmd.getOptionValue("-inputs") : ".";
    
    String saveFile = cmd.hasOption("-output") ? cmd.getOptionValue("output") : "modsInfo.json";
    
    String v = cmd.hasOption(version.getOpt()) ? cmd.getOptionValue(version.getOpt()) : "1.15";
    if (v.equals("1.15"))
    {
      context.version = ParserVersion.ONE_FIFTEEN;
    }
    else if (v.equals("1.12"))
    {
      context.version = ParserVersion.ONE_TWELVE;
    }
    else
    {
      Logger.err("Not a valid version to parse: {}", v);
      Logger.err("Valid versions are \"1.12\", \"1.15\"");
      
      return;
    }
    
    context.saveFile = saveFile;
    context.localFile = localFile;
    
    process();
  }
  
  private static void process()
  {
    ParserContext context = ParserContext.getCurrentContext();
    Path local = Paths.get(context.localFile);
    
    List<Path> directories = FileUtil.getDirectories(local);
    Optional<Path> mods = directories.stream().filter(path -> FileUtil.getName(path).equals("mods")).findFirst();
    
    Path modsPath;
    
    if (!mods.isPresent())
    {
      Logger.info("Could not find mods folder. Using root folder.");
      modsPath = local;
    }
    else
    {
      modsPath = mods.get();
    }
    
    List<Path> files = FileUtil.getFiles(modsPath).stream().filter(path -> FileUtil.isExtension(path, ".jar")).collect(Collectors.toList());
    
    if (files.isEmpty())
    {
      Logger.info("Could not find any jar files!");
    }
    else
    {
      if (context.version == ParserVersion.ONE_FIFTEEN)
      {
        parse_1_15(files);
      }
      else if (context.version == ParserVersion.ONE_TWELVE)
      {
        parse_1_12(files);
      }
  
      Logger.info("Finished");
    }
  }
  
  private static void parse_1_12(List<Path> files)
  {
    ParserContext context = ParserContext.getCurrentContext();
    List<JsonObject> objects = new ArrayList<>();
    
    files.forEach(path ->
    {
      Logger.info("Checking " + path);
      context.currentFile = path.toString();
  
      JsonArray mcModInfo = FileUtil.getMcModInfo(path);
      List<JsonObject> parse = InfoParser.parse(mcModInfo);
      objects.addAll(parse);
    });
    
    Logger.info("sorting");
    
    objects.sort((o1, o2) ->
    {
      String name1 = o1.get("name").getAsString();
      String name2 = o2.get("name").getAsString();
      
      return name1.compareToIgnoreCase(name2);
    });
  
    Logger.info("Creating json");
    JsonArray array = new JsonArray();
    
    objects.forEach(array::add);
    
    save(array);
  }
  
  private static void parse_1_15(List<Path> files)
  {
    ParserContext context = ParserContext.getCurrentContext();
    List<ModInfo> modInfoList = new ArrayList<>();
    
    files.forEach(path ->
    {
      Logger.info("Checking " + path);
      context.currentFile = path.toString();
      
      List<String> tomlUnParsed = FileUtil.getToml(path);
      CommentedConfig parsed = parse(tomlUnParsed);
      ObjectConverter converter = new ObjectConverter();
      ModInfo modInfo = converter.toObject(parsed, ModInfo::new);
      modInfo.setFileName(FileUtil.getName(path));
      
      ArrayList<?> list = parsed.get("mods");
      if (list != null && list.size() > 0)
      {
        if (list.get(0) instanceof CommentedConfig)
        {
          ModInfoMods modInfoMods = converter.toObject((CommentedConfig) list.get(0), ModInfoMods::new);
          modInfo.setMods(modInfoMods);
        }
      }
      
      if (modInfo.getMods() == null)
      {
        Logger.info("Invalid format");
      }
      else
      {
        if (modInfo.getMods().getVersion().equals("${file.jarVersion}"))
        {
          modInfo.getMods().setVersion(FileUtil.getModVersion(path));
        }
        
        modInfoList.add(modInfo);
      }
    });
    
    Logger.info("Sorting");
    modInfoList.sort((o1, o2) -> o1.getMods().getDisplayName().compareToIgnoreCase(o2.getMods().getDisplayName()));
    
    Logger.info("Creating json");
    JsonArray array = new JsonArray();
    for (ModInfo modInfo: modInfoList)
    {
      array.add(toObject(modInfo));
    }
  
    save(array);
  }
  
  private static void save(JsonArray array)
  {
    ParserContext context = ParserContext.getCurrentContext();
    String saveFile = context.saveFile;
    Logger.info("saving to " + saveFile);
    
    FileUtil.save(array, Paths.get(saveFile));
  }
  
  private static void help()
  {
    Logger.info("This tool creates a json file with the mod information in a pack");
    Logger.info("It will search for a \"mods\" folder, if it can not find it, ");
    Logger.info("it will search in given folder (default local folder)");
    Logger.info("");
  }
  
  private static CommentedConfig parse(List<String> toml)
  {
    return new TomlParser().parse(convert(toml));
  }
  
  private static String convert(List<String> strings)
  {
    StringBuilder builder = new StringBuilder();
    for (String string : strings)
    {
      builder.append(string);
      builder.append("\n");
    }
    return builder.toString();
  }
  
  private static JsonObject toObject(ModInfo modInfo)
  {
    JsonObject object = new JsonObject();
    ModInfoMods mods = modInfo.getMods();
    
    object.addProperty("fileName", modInfo.getFileName());
    object.addProperty("version", mods.getVersion());
    object.addProperty("issueTrackerURL", modInfo.getIssueTrackerURL());
    object.addProperty("loaderVersion", modInfo.getLoaderVersion());
    
    object.addProperty("modId", mods.getModId());
    object.addProperty("displayName", mods.getDisplayName());
    object.addProperty("description", mods.getDescription());
    
    addIfNotNull(object, "updateJSONURL", mods.getUpdateJSONURL());
    addIfNotNull(object, "displayUrl", mods.getDisplayUrl());
    addIfNotNull(object, "logoFile", mods.getLogoFile());
    addIfNotNull(object, "credits", mods.getCredits());
    addIfNotNull(object, "authors", mods.getAuthors());
    
    return object;
  }
  
  private static void addIfNotNull(JsonObject object, String key, String value)
  {
    if (value != null)
    {
      object.addProperty(key, value);
    }
  }
}
