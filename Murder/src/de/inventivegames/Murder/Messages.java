package de.inventivegames.Murder;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
	private static File					messageFile	= new File("plugins/Murder/messages.yml");
	private static YamlConfiguration	MessageFile	= YamlConfiguration.loadConfiguration(messageFile);

	public static void Manager() {
		if (!(messageFile.exists())) {
			setDefaults();
		}
	}

	public static String getMessage(String key) {
		if (MessageFile.contains(key)) {
			String message = MessageFile.getString(key);
			return message.replaceAll("&([a-z0-9])", "§$1");
		}
		return "";
	}

	private static void setDefaults() {

		MessageFile.addDefault("arenaIngame", "Arena is Ingame!");
		MessageFile.addDefault("arenaFull", "Arena is Full!");
		MessageFile.addDefault("playerIngame", "You are already Ingame!");
		MessageFile.addDefault("playerNotIngame", "You are not Ingame!");
		MessageFile.addDefault("notEnoughPlayers", "Not enough Players to start!");
		MessageFile.addDefault("lobbySpawnNotExisting", "Lobby Spawnpoint does not exist!");
		MessageFile.addDefault("joinArena", "%1$s &2joined the Arena!");
		MessageFile.addDefault("leaveArena", "%1$s &2left the Arena!");
		MessageFile.addDefault("lobbyCountdown", "Lobby ends in %1$s Seconds");
		MessageFile.addDefault("gameCountdown", "Game starts in %1$s Seconds");
		MessageFile.addDefault("bystander", "You are a Bystander.");
		MessageFile.addDefault("bystanderWeapon", "You are a Bystander with a secret Weapon.");
		MessageFile.addDefault("murderer", "You are the Murderer.");
		MessageFile.addDefault("knife", "Knife");
		MessageFile.addDefault("gun", "Gun");
		MessageFile.addDefault("bullet", "Bullet");
		MessageFile.addDefault("loot", "Loot");
		MessageFile.addDefault("noPermission", "You don't have Permission to execute this command!");
		MessageFile.addDefault("wrongUsage", "Wrong usage! Type %1$s for a list of commands!");
		MessageFile.addDefault("cantUseCommand", "You can't use this command while you're Ingame!");
		MessageFile.addDefault("createdSign", "Sucessfully created Murder Sign!");
		MessageFile.addDefault("addedArena", "Successfully added Arena %1$s");
		MessageFile.addDefault("removedArena", "Successfully removed Arena %1$s");
		MessageFile.addDefault("arenaExists", "Arena %1$s already exists!");
		MessageFile.addDefault("arenaNotExisting", "Arena does not exist!");
		MessageFile.addDefault("addedSpawn", "Successfully added %1$s Spawnpoint %2$s for Arena %3$s");
		MessageFile.addDefault("unknownSpawn", "Unknown SpawnPoint type!");
		MessageFile.addDefault("availableSpawns", "Available types");
		MessageFile.addDefault("lootCollected", "You collected %1$s Loot.");
		MessageFile.addDefault("murdererWin1", "Murderer &2wins!");
		MessageFile.addDefault("murdererWin2", "The Murderer was&r %1$s &a/&1 %2$s");
		MessageFile.addDefault("killedMurderer", "%1$s killed the Murderer.");
		MessageFile.addDefault("bystanderWin1", "Bystanders &2win!");
		MessageFile.addDefault("bystanderWin2", "The Murderer was&r %1$s &a/&1 %2$s");
		MessageFile.addDefault("killedInnocent", "%1$s &2killed an innocent Bystander.");
		MessageFile.addDefault("reloadNotification", "You have to wait for the Gun to reload!");
		MessageFile.addDefault("disguiseNotification", "Right Click to disguise as this Player");

		MessageFile.options().copyDefaults(true);
		try {
			MessageFile.save(messageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
