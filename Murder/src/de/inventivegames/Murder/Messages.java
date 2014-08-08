package de.inventivegames.murder;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public enum Messages {

	ARENAINGAME(""),
	ARENAFULL(""),
	PLAYERINGAME(""),
	PLAYERNOTINGAME(""),
	NOTENOUGHPLAYERS(""),
	LOBBYSPAWNNOTEXISTING(""),
	JOINARENA(""),
	LEAVEARENA(""),
	LOBBYCOUNTDOWN(""),
	GAMECOUNTDOWN(""),
	BYSTANDER(""),
	BYSTANDERWEAPON(""),
	MURDERER(""),
	SPECTATOR(""),
	KNIFE(""),
	GUN(""),
	BULLET(""),
	LOOT(""),
	NOPERMISSION(""),
	WRONGUSAGE(""),
	UNKNOWNCOMMAND(""),
	CANTUSECOMMAND(""),
	CREATEDSIGN(""),
	ADDEDARENA(""),
	UNKNOWNSPAWN(""),
	AVAILABLESPAWNS(""),
	LOOTCOLLECTED(""),
	DISGUISED(""),
	MURDERERWIN1(""),
	MURDERERWIN2(""),
	KILLEDMURDERER(""),
	BYSTANDERWIN1(""),
	BYSTANDERWIN2(""),
	KILLEDINNOCENT(""),
	RELOADNOTIFICATION(""),
	DISGUISENOTIFICATION(""),
	NOTENOUGHLOOT(""),
	SMOKENOTIFICATION("");

	private String	message;

	Messages(String s) {
		message = s;
	}

	private static File					messageFile	= new File("plugins/Murder/messages.yml");
	private static YamlConfiguration	MessageFile	= YamlConfiguration.loadConfiguration(messageFile);

	public static void Manager() {
		if (!messageFile.exists()) {
			setDefaults();
		}
	}

	public static String getMessage(String key) {
		if (MessageFile.contains(key)) {
			final String message = MessageFile.getString(key);
			return message.replaceAll("&([a-z0-9])", "§$1");
		}
		return "§cInvalid Message! [" + key.toUpperCase() + "]";
	}

	private static void setDefaults() {
		MessageFile.addDefault("arenaIngame", "§cArena is Ingame!");
		MessageFile.addDefault("arenaFull", "§cArena is Full!");
		MessageFile.addDefault("playerIngame", "§cYou are already Ingame!");
		MessageFile.addDefault("playerNotIngame", "§cYou are not Ingame!");
		MessageFile.addDefault("notEnoughPlayers", "§cNot enough Players to start!");
		MessageFile.addDefault("lobbySpawnNotExisting", "§cLobby Spawnpoint does not exist!");
		MessageFile.addDefault("joinArena", "%1$s &2joined the Arena!");
		MessageFile.addDefault("leaveArena", "%1$s &2left the Arena!");
		MessageFile.addDefault("lobbyCountdown", "§2Lobby ends in %1$s Seconds");
		MessageFile.addDefault("gameCountdown", "§2Game starts in %1$s Seconds");
		MessageFile.addDefault("bystander", "§l§9You are a Bystander.");
		MessageFile.addDefault("bystanderWeapon", "§l§9You are a Bystander with a secret Weapon.");
		MessageFile.addDefault("nowBystanderWeapon", "§l§9You are now a Bystander with a secret Weapon.");
		MessageFile.addDefault("murderer", "§c§lYou are the Murderer.");
		MessageFile.addDefault("nowMurderer", "§c§lYou are now the Murderer.");
		MessageFile.addDefault("spectator", "§aYou are now a Spectator.");
		MessageFile.addDefault("knife", "§cKnife");
		MessageFile.addDefault("gun", "§9Gun");
		MessageFile.addDefault("bullet", "§7Bullet");
		MessageFile.addDefault("loot", "§7Loot");
		MessageFile.addDefault("noPermission", "§cYou don't have Permission to execute this command!");
		MessageFile.addDefault("wrongUsage", "§cWrong usage! Type %1$s for a list of commands!");
		MessageFile.addDefault("unknownCommand", "§cUnknown Command!");
		MessageFile.addDefault("cantUseCommand", "§cYou can't use this Command while you're Ingame!");
		MessageFile.addDefault("createdSign", "§2Sucessfully created Murder Sign!");
		MessageFile.addDefault("addedArena", "§2Successfully added Arena %1$s");
		MessageFile.addDefault("removedArena", "§2Successfully removed Arena %1$s");
		MessageFile.addDefault("arenaExists", "§cArena %1$s already exists!");
		MessageFile.addDefault("arenaNotExisting", "§cArena does not exist!");
		MessageFile.addDefault("addedSpawn", "§2Successfully added %1$s Spawnpoint %2$s for Arena %3$s");
		MessageFile.addDefault("unknownSpawn", "§cUnknown SpawnPoint type!");
		MessageFile.addDefault("availableSpawns", "§2Available types");
		MessageFile.addDefault("lootCollected", "§2You collected %1$s Loot.");
		MessageFile.addDefault("disguised", "§2You disguised as %1$s");
		MessageFile.addDefault("murdererWin1", "§cMurderer &2wins!");
		MessageFile.addDefault("murdererWin2", "The §cMurderer §2was&r %1$s &a/&1 %2$s");
		MessageFile.addDefault("killedMurderer", "%1$s killed the §cMurderer.");
		MessageFile.addDefault("bystanderWin1", "§9Bystanders &2win!");
		MessageFile.addDefault("bystanderWin2", "§2The §cMurderer §2was&r %1$s &a/&1 %2$s");
		MessageFile.addDefault("killedInnocent", "§1%1$s &2killed an innocent Bystander.");
		MessageFile.addDefault("reloadNotification", "§cYou have to wait for the Gun to reload!");
		MessageFile.addDefault("disguiseNotification", "§2Right Click to disguise as this Player for 1 Loot");
		MessageFile.addDefault("notEnoughLoot", "§cYou don't have enough Loot to disguise as this Player!");
		MessageFile.addDefault("smokeNotification", "§aOther Players can now recognize you as the Murderer.");

		MessageFile.options().copyDefaults(true);
		try {
			MessageFile.save(messageFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
