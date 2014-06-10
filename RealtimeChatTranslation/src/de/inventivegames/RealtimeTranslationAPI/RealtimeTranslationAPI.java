package de.inventivegames.RealtimeTranslationAPI;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.inventivegames.RealtimeTranslation.RealtimeTranslation;
import de.inventivegames.RealtimeTranslation.Translator;

public class RealtimeTranslationAPI extends RealtimeTranslation {
	private static File	playerFile;

	public static void translateToPlayers(String message) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			Player p = online;

			playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p.getName() + ".yml");
			YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
			String lang = PlayerFile.getString("lang");

			String translation = Translator.getTranslation(message, lang);

			p.sendMessage(translation);
		}
	}

	public static void translateToPlayer(String message, Player p) {

		playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p.getName() + ".yml");
		YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
		String lang = PlayerFile.getString("lang");

		String translation = Translator.getTranslation(message, lang);

		p.sendMessage(translation);

	}

	public static String getTranslation(String message, String lang) {
		String translation = Translator.getTranslation(message, lang);
		return translation;
	}
}
