package de.inventivegames.Murder;

public class Config {

	public static void Manager() {
		createConfig();
	}

	public static void createConfig() {
		if (!(Murder.configFile.exists())) {
			Murder.instance.getConfig().options().header("Configuration for Murder \n" + "http://dev.bukkit.org/bukkit-plugins/murder/ \n" + "\"useBungeeCord\", \"BungeeArena\" and \"autoRestart\" is only required if you want to use this on a BungeeServer!");
			Murder.instance.getConfig().addDefault("checkForUpdates", true);
			Murder.instance.getConfig().addDefault("MinPlayers", 2);
			Murder.instance.getConfig().addDefault("MaxPlayers", 24);
			Murder.instance.getConfig().addDefault("SmokeDelay", 600);
			Murder.instance.getConfig().addDefault("lobbyCountdown", 30);
			Murder.instance.getConfig().addDefault("countdown", 30);
			Murder.instance.getConfig().addDefault("allowedCommands", null);
			Murder.instance.getConfig().addDefault("useEconomy", false);
			Murder.instance.getConfig().addDefault("useBungeeCord", false);
			Murder.instance.getConfig().addDefault("BungeeArena", 1);
			Murder.instance.getConfig().addDefault("autoRestart", false);
			Murder.instance.getConfig().options().copyDefaults(true);
			Murder.instance.getConfig().options().copyHeader(true);
			Murder.instance.saveConfig();
		}
	}

}
