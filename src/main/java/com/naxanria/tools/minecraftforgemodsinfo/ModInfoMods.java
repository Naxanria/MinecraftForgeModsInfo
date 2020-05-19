package com.naxanria.tools.minecraftforgemodsinfo;

/*
  @author: Naxanria
*/
public class ModInfoMods
{
  private String modId;
  private String version;
  private String displayName;
  private String description;
  
  private String updateJSONURL;
  private String displayUrl;
  private String logoFile;
  private String credits;
  private String authors;
  
  public String getModId()
  {
    return modId;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public void setVersion(String modVersion)
  {
    version = modVersion;
  }
  
  public String getDisplayName()
  {
    return displayName;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public String getUpdateJSONURL()
  {
    return updateJSONURL;
  }
  
  public String getDisplayUrl()
  {
    return displayUrl;
  }
  
  public String getLogoFile()
  {
    return logoFile;
  }
  
  public String getCredits()
  {
    return credits;
  }
  
  public String getAuthors()
  {
    return authors;
  }
  
  @Override
  public String toString()
  {
    return "ModInfoMods{" +
      "modid='" + modId + '\'' +
      ", version='" + version + '\'' +
      ", displayName='" + displayName + '\'' +
      ", description='" + description + '\'' +
      ", updateJSONURL='" + updateJSONURL + '\'' +
      ", displayUrl='" + displayUrl + '\'' +
      ", logoFile='" + logoFile + '\'' +
      ", credits='" + credits + '\'' +
      ", authors='" + authors + '\'' +
      '}';
  }
  
}
