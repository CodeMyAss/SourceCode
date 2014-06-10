package com.maxmind.geoip;

public class Country
{
  private String code;
  private String name;

  public Country(String code, String name)
  {
    this.code = code;
    this.name = name;
  }

  public String getCode()
  {
    return this.code;
  }

  public String getName()
  {
    return this.name;
  }
}