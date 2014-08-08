package de.inventivegames.murder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.inventivegames.murder.event.ArenaCreateEvent;
import de.inventivegames.murder.event.ArenaRemoveEvent;

public class ArenaManager {

	private static List<Arena>	arenas	= new ArrayList<Arena>();
	private static File			arenaFile;

	public static void loadArenas() {
		Murder.console.sendMessage(Murder.prefix + "§fLoading Arenas...");
		if (!new File("plugins/Murder/Arenas/").exists()) return;
		for (final String s : new File("plugins/Murder/Arenas/").list()) {
			final File f = new File("plugins/Murder/Arenas/" + s);
			if (f.isDirectory()) {
				final File file = new File("plugins/Murder/Arenas/" + s + "/arena.yml");
				if (file.exists()) {
					final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					final Arena arena = Arena.fromString(config.getString("Data"));
					Murder.console.sendMessage(Murder.prefix + "§fLoading Arena #" + s + "...");
					arenas.add(arena);
				}
			}
		}
	}

	public static void unloadArenas() {
		arenas.clear();
	}

	public static List<Arena> getArenas() {
		return new ArrayList<Arena>(arenas);
	}

	public static void saveArenas() {
		Murder.console.sendMessage(Murder.prefix + "§fSaving Arenas...");
		for (final Arena arena : arenas) {
			arena.save();
		}
	}

	public static Arena getByID(int id) {
		for (final Arena arena : arenas) {
			if (arena.getID() == id) return arena;
		}
		return null;
	}

	public static Arena getByName(String name) {
		for (final Arena arena : arenas) {
			if (arena.getName().equals(name)) return arena;
		}
		return null;
	}

	public static Arena getByWorld(World world) {
		if (world == null) return null;
		for (final Arena arena : arenas) {
			if (arena.getWorld() != null) {
				if (arena.getWorld().equals(world)) return arena;
			}
		}
		return null;
	}

	public static int getIDByName(String name) {
		return getByName(name).getID();
	}

	public static String getNameByID(int id) {
		return getByID(id).getName();
	}

	public static Arena createArena(int id, Player p) {
		final World world = p.getWorld();
		final Arena arena = new Arena(id);

		world.setDifficulty(Difficulty.PEACEFUL);
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("naturalRegeneration", "false");
		if (!arena.exists()) {
			arena.setWorld(world);
			arena.create();

			p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("addedArena").replace("%1$s", "" + id));

			Murder.instance.getServer().getPluginManager().callEvent(new ArenaCreateEvent(arena, p));
			arenas.add(arena);
			return arena;
		} else {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaExists").replace("%1$s", "" + id));
			return null;
		}
	}

	public static void removeArena(Arena arena, Player p) {
		if (arena.exists()) {
			final File file = new File("plugins/Murder/Arenas/" + arena.getID() + "/arena.yml");
			if (file.exists()) {
				file.delete();
				new File("plugins/Murder/Arenas/" + arena.getID()).delete();

				p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("removedArena").replace("%1$s", new StringBuilder("#").append(arena).toString()));
				Murder.instance.getServer().getPluginManager().callEvent(new ArenaRemoveEvent(arena, p));
			}

		}
	}

}
