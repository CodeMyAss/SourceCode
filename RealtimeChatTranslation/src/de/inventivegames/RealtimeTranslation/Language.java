package de.inventivegames.RealtimeTranslation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Language {

	private static ArrayList<String>		countries		= new ArrayList<>();
	private static ArrayList<String>		langCodes		= new ArrayList<>();
	private static ArrayList<String>		languages		= new ArrayList<>();

	private static HashMap<String, String>	countryLangMap	= new HashMap<>();

	public static void setup() {
		initLanguageMap();
		getListOfCountries();
		
		try {
			setLanguageCodes();
		} catch (Exception e) {
			System.out.println("[RCT] Could not download Language Codes!");
		}

		try {
			setLanguages();
		} catch (Exception e) {
			System.out.println("[RCT] Could not download Languages!");
		}
	}

	
	private static void setLanguageCodes() throws Exception {
		URL url = new URL("https://dl.dropboxusercontent.com/s/8fr6g8h7ikgg2vc/language-codes.txt");

		System.out.println("[RCT] Downloading Language Codes...");

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String str;
		while ((str = in.readLine()) != null) {
			langCodes.add(str.toLowerCase());
		}
		in.close();
	}

	private static void setLanguages() throws Exception {
		URL url = new URL("https://dl.dropboxusercontent.com/s/fedj0q4ryec94h1/languages.txt");

		System.out.println("[RCT] Downloading Languages...");

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String str;
		while ((str = in.readLine()) != null) {
			languages.add(str.toLowerCase());
		}
	}

	
    private static Map<String, String> languagesMap = new TreeMap<String, String>();
    
  
    public static void getListOfCountries() {
 
	String[] countries = Locale.getISOCountries();
 
	int supportedLocale = 0, nonSupportedLocale = 0;
 
	for (String countryCode : countries) {
 
	  Locale obj = null;
	  if (languagesMap.get(countryCode) == null) {
 
		obj = new Locale("", countryCode);
		nonSupportedLocale++;
 
	  } else {

		obj = new Locale(languagesMap.get(countryCode), countryCode);
		supportedLocale++;
 
	  }

	  countryLangMap.put(obj.getDisplayCountry(Locale.ENGLISH).toLowerCase(), obj.getDisplayLanguage(Locale.ENGLISH).toLowerCase());
 
	  }

    }
 
    // create Map with country code and languages
    public static void initLanguageMap() {
 
	Locale[] locales = Locale.getAvailableLocales();
 
	for (Locale obj : locales) {
 
	  if ((obj.getDisplayCountry() != null) && (!"".equals(obj.getDisplayCountry()))) {
		languagesMap.put(obj.getCountry(), obj.getLanguage());
	  }
 
	}
 
    }
	
	
	public static boolean isValidName(String lang) {
		if (lang.length() > 2) {
			if (languages.contains(lang.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidCode(String lang) {
		if (lang.length() == 2) {
			if (langCodes.contains(lang.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static String countryToLang(String country) {
		String code = " ";
		if(countryLangMap.get(country.toLowerCase()) != null) {
			code = countryLangMap.get(country.toLowerCase());
		}
		return code.toLowerCase();
	}
	
	public static String toCode(String lang) {
		String code = " ";
		if (lang.length() > 2) {
			for (int i = 0; i < languages.size(); i++) {
				if (languages.get(i).equalsIgnoreCase(lang)) {
					code = langCodes.get(i);
					return code;
				}
			}
		}
		return code;
	}

	public static String toName(String code) {
		String lang = " ";
		if (code.length() == 2) {
			for (int i = 0; i < langCodes.size(); i++) {
				if (langCodes.get(i).equalsIgnoreCase(code)) {
					lang = languages.get(i);
					return lang;
				}
			}
		}
		return lang;
	}

}
