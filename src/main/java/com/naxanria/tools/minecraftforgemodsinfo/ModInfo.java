package com.naxanria.tools.minecraftforgemodsinfo;

/*
  @author: Naxanria
*/
public class ModInfo
{
  private String issueTrackerURL;
  private String loaderVersion;
  private transient ModInfoMods mods;
  
  private String fileName;
  
  public ModInfo setMods(ModInfoMods mods)
  {
    this.mods = mods;
    return this;
  }
  
  public ModInfoMods getMods()
  {
    return mods;
  }
  
  public String getIssueTrackerURL()
  {
    return issueTrackerURL;
  }

  public String getLoaderVersion()
  {
    return loaderVersion;
  }
  
  public String getFileName()
  {
    return fileName;
  }
  
  public ModInfo setFileName(String fileName)
  {
    this.fileName = fileName;
    return this;
  }
  
  @Override
  public String toString()
  {
    return "ModInfo{" +
      "issueTrackerUrl='" + issueTrackerURL + '\'' +
      ", loaderVersion='" + loaderVersion + '\'' +
      ", mods=" + mods +
      ", fileName='" + fileName + '\'' +
      '}';
  }
}
