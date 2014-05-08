package de.inventivegames.Murder;

import java.io.File;
import java.io.IOException;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Arenas {

	private static File	arenaFile;

	public static void createArenaFile(int arena, World world, Player p) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		world.setDifficulty(Difficulty.PEACEFUL);
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("naturalRegeneration", "false");
		if (!(arenaFile.exists())) {
			try {
				ArenaFile.options().copyDefaults(true);
				ArenaFile.save(arenaFile);
				ArenaFile.addDefault("World", world.getName());
				ArenaFile.addDefault("CurrentPlayers", "0");
				ArenaFile.createSection("Players");
				ArenaFile.createSection("Players.type");
				ArenaFile.createSection("SpawnPoints");
				ArenaFile.createSection("SpawnPoints.lobby");
				ArenaFile.createSection("SpawnPoints.players");
				ArenaFile.createSection("SpawnPoints.loot");
				ArenaFile.createSection("SpawnPoints.back");

				try {
					ArenaFile.save(arenaFile);
				} catch (IOException e) {
					e.printStackTrace();
				}

				p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("addedArena").replace("%1$s", "#" + arena));

			} catch (IOException e) {
				Murder.console.sendMessage(Murder.prefix + "§cCould not create Arena File for Arena §2#" + arena);
				e.printStackTrace();
			}
		} else
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaExists").replace("%1$s", "#" + arena));

	}

	public static void removeArenaFile(int arena, Player p) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		if (arenaFile.exists()) {
			arenaFile.delete();
			p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("removedArena").replace("%1$s", "#" + arena));
		} else
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));

	}

	public static void addSpawnPoint(int arena, String Type, int number, Player p) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		if (arenaFile.exists()) {
			String type = Type.toLowerCase();
			Location pLoc = p.getLocation();
			double X = pLoc.getX();
			double Y = pLoc.getY() + 0.125;
			double Z = pLoc.getZ();

			if ((type.equals("lobby")) || (type.equals("players")) || (type.equals("loot"))) {
				ArenaFile.set("SpawnPoints." + type + "." + number + ".X", X);
				ArenaFile.set("SpawnPoints." + type + "." + number + ".Y", Y);
				ArenaFile.set("SpawnPoints." + type + "." + number + ".Z", Z);

				try {
					ArenaFile.save(arenaFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("addedSpawn").replace("%1$s", type).replace("%2$s", "#" + number).replace("%3$s", "#" + arena));
			} else
				p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("unknownSpawn"));
			p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("availableSpawns") + ": §alobby §2| §aplayers §2| §aloot");
		} else
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));

	}

	public static void createArenaFile(String string, World world, Player p) {
		int arena = Integer.parseInt(string);
		createArenaFile(arena, world, p);

	}

	public static void removeArenaFile(String string, Player p) {
		int arena = Integer.parseInt(string);
		removeArenaFile(arena, p);

	}

	public static void addSpawnPoint(String string, String type, String nbr, Player p) {
		int arena = Integer.parseInt(string);
		int number = Integer.parseInt(nbr);
		addSpawnPoint(arena, type, number, p);

	}

	public static void addSpawnPoint(String string, String type, int number, Player p) {
		int arena = Integer.parseInt(string);
		addSpawnPoint(arena, type, number, p);

	}

}
