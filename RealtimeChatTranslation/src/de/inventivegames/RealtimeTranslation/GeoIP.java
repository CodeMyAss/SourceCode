package de.inventivegames.RealtimeTranslation;

import java.io.IOException;

import org.bukkit.entity.Player;

import com.maxmind.geoip.LookupService;

public class GeoIP {
	
	private static LookupService geoIP;
	
	public static String getLanguage(Player p) {
		String country = getCountry(p);
		String langCode = Language.countryToLang(country.toLowerCase());
		return langCode;
	}
	
	public static String getLangCode(Player p) {
		String country = getCountry(p);
		String langCode = Language.toCode(Language.countryToLang(country.toLowerCase()));
		return langCode;
	}
	
	public static String getCountry(Player p) {
		try {
			geoIP = new LookupService(RealtimeTranslation.instance.geoFile);
			String country = geoIP.getCountry(p.getAddress().getAddress()).getName();
			return country;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
