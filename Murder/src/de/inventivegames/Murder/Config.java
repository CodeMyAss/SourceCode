package de.inventivegames.murder;

import java.util.ArrayList;
import java.util.Arrays;

public class Config {

	public static void Manager() {
		createConfig();
	}

	public static void createConfig() {
		if (!Murder.configFile.exists()) {
			Murder.instance.getConfig().options().header("Configuration for Murder \nhttp://dev.bukkit.org/bukkit-plugins/murder/ \n\"useBungeeCord\", \"BungeeArena\" and \"autoRestart\" is only required if you want to use this on a BungeeServer!");
			Murder.instance.getConfig().addDefault("checkForUpdates", Boolean.valueOf(true));
			Murder.instance.getConfig().addDefault("forceNewCorpses", Boolean.valueOf(false));
			Murder.instance.getConfig().addDefault("MinPlayers", Integer.valueOf(2));
			Murder.instance.getConfig().addDefault("MaxPlayers", Integer.valueOf(24));
			Murder.instance.getConfig().addDefault("SmokeDelay", Integer.valueOf(600));
			Murder.instance.getConfig().addDefault("lobbyCountdown", Integer.valueOf(30));
			Murder.instance.getConfig().addDefault("countdown", Integer.valueOf(30));
			Murder.instance.getConfig().addDefault("allowedCommands", new ArrayList<String>(Arrays.asList(new String[] { "murder" })));
			Murder.instance.getConfig().addDefault("useEconomy", Boolean.valueOf(false));
			Murder.instance.getConfig().addDefault("useBungeeCord", Boolean.valueOf(false));
			Murder.instance.getConfig().addDefault("BungeeArena", Integer.valueOf(1));
			Murder.instance.getConfig().addDefault("autoRestart", Boolean.valueOf(false));
			Murder.instance.getConfig().options().copyDefaults(true);
			Murder.instance.getConfig().options().copyHeader(true);
			Murder.instance.saveConfig();
		}
	}

}
