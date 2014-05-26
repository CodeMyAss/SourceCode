package de.inventivegames.Murder;

public class Config {

	public static void Manager() {
		createConfig();
	}

	public static void createConfig() {
		if (!(Murder.configFile.exists())) {
			Murder.instance.getConfig().addDefault("checkForUpdates", true);
			Murder.instance.getConfig().addDefault("MinPlayers", 2);
			Murder.instance.getConfig().addDefault("MaxPlayers", 24);
			Murder.instance.getConfig().addDefault("SmokeDelay", 600);
			Murder.instance.getConfig().addDefault("lobbyCountdown", 30);
			Murder.instance.getConfig().addDefault("countdown", 30);
			Murder.instance.getConfig().addDefault("allowedCommands", null);
			Murder.instance.getConfig().options().copyDefaults(true);
			Murder.instance.saveConfig();
		}
	}

}
