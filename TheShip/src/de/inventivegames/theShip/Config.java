package de.inventivegames.theShip;

public class Config {

	public static void Manager() {
		createConfig();
	}

	public static void createConfig() {
		if (!(TheShip.configFile.exists())) {
			// TheShip.instance.getConfig().addDefault("checkForUpdates", true);
			TheShip.instance.getConfig().addDefault("MinPlayers", 2);
			TheShip.instance.getConfig().addDefault("MaxPlayers", 24);
			TheShip.instance.getConfig().addDefault("GameDuration", 300);
			TheShip.instance.getConfig().addDefault("lobbyCountdown", 30);
			TheShip.instance.getConfig().addDefault("countdown", 30);
			TheShip.instance.getConfig().addDefault("allowedCommands", null);
			TheShip.instance.getConfig().options().copyDefaults(true);
			TheShip.instance.saveConfig();
		}
	}

}
