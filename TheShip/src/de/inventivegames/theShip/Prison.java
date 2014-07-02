package de.inventivegames.theShip;

import java.io.File;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class Prison {

	private static File	arenaFile;
	
	public static int[] cd0 = new int[29];

	public static void arrest(final ShipPlayer sp) {
		final int arena = sp.getArena();
		arenaFile = new File("plugins/TheShip/Arenas/" + arena + "/arena.yml");
		final YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		if (ArenaFile.get("SpawnPoints.prison") != null) {
			Set<String> IDs = ArenaFile.getConfigurationSection("SpawnPoints.prison").getKeys(false);
			Object[] ids = IDs.toArray();
			String ID;
			int id;
			if (IDs.size() != 0) {
				ID = ids[IDs.size() - 1].toString();
				id = Integer.parseInt(ID) + 1;
			} else {
				id = 1;
			}

			int i = TheShip.rd.nextInt(id);
			if (ArenaFile.get("SpawnPoints.prison." + i) != null) {
				World world = TheShip.instance.getServer().getWorld(ArenaFile.getString("World"));
				double X = ArenaFile.getDouble("SpawnPoints.prison." + i + ".X");
				double Y = ArenaFile.getDouble("SpawnPoints.prison." + i + ".Y");
				double Z = ArenaFile.getDouble("SpawnPoints.prison." + i + ".Z");

				Location loc = new Location(world, X, Y, Z);

				sp.getPlayer().teleport(loc);
				sp.getPlayer().getInventory().clear();
				sp.setFrozen(false);
			}

		}
		
		cd0[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {
			
			@Override
			public void run() {
				if (ArenaFile.get("SpawnPoints.prisonOut.1") != null) {
					World world = TheShip.instance.getServer().getWorld(ArenaFile.getString("World"));
					double X = ArenaFile.getDouble("SpawnPoints.prisonOut.1.X");
					double Y = ArenaFile.getDouble("SpawnPoints.prisonOut.1.Y");
					double Z = ArenaFile.getDouble("SpawnPoints.prisonOut.1.Z");

					Location loc = new Location(world, X, Y, Z);

					sp.setFrozen(false);
					
					sp.getPlayer().teleport(loc);
					
					resetTimer(arena);
				}
			}
		}, (15 * 20) + (TheShip.rd.nextInt(15) * 20));
		
	}
	
	private static void resetTimer(int arena) {
		cd0[arena] = -1;
	}

}
