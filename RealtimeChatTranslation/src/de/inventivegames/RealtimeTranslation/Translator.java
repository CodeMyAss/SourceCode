package de.inventivegames.RealtimeTranslation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Translator {
	public static String readURL(String url) {
		String response = "";
		try {
			URL toread = new URL(url);
			URLConnection yc = toread.openConnection();

			yc.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response = response + inputLine;
			}
			in.close();
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static String getTranslation(String text, String lang) {
		HashMap<String, String> hm = new HashMap<String, String>();
		Pattern p = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?������]))");

		Matcher m = p.matcher(text);
		StringBuffer sb = new StringBuffer();
		String urlTmp = "";

		while (m.find()) {
			urlTmp = m.group(1);
			String uuid = UUID.randomUUID().toString().replace("-", "");
			hm.put(uuid, urlTmp);
			text.replace(urlTmp, uuid);
			m.appendReplacement(sb, "");
			sb.append(urlTmp);
		}
		m.appendTail(sb);
		text = sb.toString();

		text = URLEncoder.encode(text);
		String response = readURL("http://translate.google.com/translate_a/t?q=" + text + "&client=p&text=&sl=auto&tl=" + lang + "&ie=UTF-8&oe=UTF-8");
		response = parse(response);

		Set set = hm.entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			response.replace(me.getKey().toString(), me.getValue().toString());
		}

		response = postProcess(response, lang);
		return response;
	}

	public static String postProcess(String response, String lang) {
		response = response.replace(" :", ":");
		response = response.replace(" ,", ",");
		response = response.replace(". / ", "./");

		if ((response.startsWith("�")) && (StringUtils.countMatches(response, "?") == 0)) {
			response = response + "?";
		}
		if ((response.startsWith("�")) && (StringUtils.countMatches(response, "!") == 0)) {
			response = response + "!";
		}
		return response;
	}

	public static String parse(String response) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) parser.parse(response);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		JSONArray sentences = (JSONArray) obj.get("sentences");
		String finalresponse = "";
		for (int i = 0; i < sentences.size(); i++) {
			String line = "" + sentences.get(i);
			String trans = getTrans(line);
			finalresponse = finalresponse + trans;
		}
		return finalresponse;
	}

	public static String getTrans(String sentence) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) parser.parse(sentence);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		sentence = (String) obj.get("trans");
		return sentence;
	}
}
