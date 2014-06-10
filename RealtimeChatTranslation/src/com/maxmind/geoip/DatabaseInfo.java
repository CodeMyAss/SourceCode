package com.maxmind.geoip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseInfo
{
  public static final int COUNTRY_EDITION = 1;
  public static final int REGION_EDITION_REV0 = 7;
  public static final int REGION_EDITION_REV1 = 3;
  public static final int CITY_EDITION_REV0 = 6;
  public static final int CITY_EDITION_REV1 = 2;
  public static final int ORG_EDITION = 5;
  public static final int ISP_EDITION = 4;
  public static final int PROXY_EDITION = 8;
  public static final int ASNUM_EDITION = 9;
  public static final int NETSPEED_EDITION = 10;
  public static final int DOMAIN_EDITION = 11;
  public static final int COUNTRY_EDITION_V6 = 12;
  public static final int ASNUM_EDITION_V6 = 21;
  public static final int ISP_EDITION_V6 = 22;
  public static final int ORG_EDITION_V6 = 23;
  public static final int DOMAIN_EDITION_V6 = 24;
  public static final int CITY_EDITION_REV1_V6 = 30;
  public static final int CITY_EDITION_REV0_V6 = 31;
  public static final int NETSPEED_EDITION_REV1 = 32;
  public static final int NETSPEED_EDITION_REV1_V6 = 33;
  private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
  private String info;

  public DatabaseInfo(String info)
  {
    this.info = info;
  }

  public int getType() {
    if ((this.info == null) || (this.info.equals(""))) {
      return 1;
    }

    return Integer.parseInt(this.info.substring(4, 7)) - 105;
  }

  public boolean isPremium()
  {
    return this.info.indexOf("FREE") < 0;
  }

  public Date getDate()
  {
    for (int i = 0; i < this.info.length() - 9; i++)
      if (Character.isWhitespace(this.info.charAt(i))) {
        String dateString = this.info.substring(i + 1, i + 9);
        try {
          synchronized (formatter) {
            return formatter.parse(dateString);
          }
        }
        catch (ParseException localParseException)
        {
        }
      }
    return null;
  }

  public String toString()
  {
    return this.info;
  }
}