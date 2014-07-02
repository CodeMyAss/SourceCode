package de.inventivegames.theShip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Weapons {

	private static File						chestFile;
	private static File						weaponFile;

	public static ItemStack[][]				spawnedWeapons	= new ItemStack[24][50];
	public static ArrayList<Item>			items			= new ArrayList<Item>();
	public static Item[][]					itemLocs		= new Item[29][29];
	public static int[]						pos				= new int[29];
//	public static HashMap<Integer, Item>	itemLoc			= new HashMap<Integer, Item>();
	
	public static HashMap<Material, Integer> weaponRewards = new HashMap<Material, Integer>();
	
	
	public static void generateConfig() {
		weaponFile = new File("plugins/TheShip/weapons.yml");
		
		if(!(weaponFile.exists())) {
			try {
				weaponFile.createNewFile();
				
				YamlConfiguration WeaponFile = YamlConfiguration.loadConfiguration(weaponFile);
				
				WeaponFile.addDefault("Weapons", null);
				
				HashMap<String, String> temp1 = new HashMap<String, String>();
				ArrayList<String> temp = new ArrayList<String>();
				
				for(Material mat : Material.values()) {
					temp.add(mat + ":" + "100");
				}
				WeaponFile.set("Weapons", temp);
				
				WeaponFile.options().copyDefaults(true);
				WeaponFile.save(weaponFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
	}
	
	public static void setupRewards() {
		YamlConfiguration WeaponFile = YamlConfiguration.loadConfiguration(weaponFile);
//		
//		HashMap<String, String> temp = new HashMap<String, String>();
//		
		if(WeaponFile.get("Weapons") != null) {
			for(Object o : WeaponFile.getList("Weapons")) {
				if(o != null) {
					String s = (String) o;
					String[] line = s.split(":");
					weaponRewards.put(Material.valueOf(line[0]), Integer.valueOf(line[1]));
				}
			}
		}
	}
	
	public static int getReward(Material mat) {
		if(weaponRewards.containsKey(mat)) {
			return weaponRewards.get(mat);
		}
		return 0;
	}

	public static void spawnWeapons(int arena) {
		chestFile = new File("plugins/TheShip/Arenas/" + arena + "/chests.yml");
		YamlConfiguration ChestFile = YamlConfiguration.loadConfiguration(chestFile);

		if (ChestFile.get("Chests") != null) {
			Set<String> IDs = ChestFile.getConfigurationSection("Chests").getKeys(false);
			Object[] ids = IDs.toArray();
			String ID;
			int id;
			if (IDs.size() != 0) {
				ID = ids[IDs.size() - 1].toString();
				id = Integer.parseInt(ID) + 1;
			} else {
				id = 1;
			}

			for (int i = 0; i < id; i++) {
				if (ChestFile.get("Chests." + i) != null) {
					World world = TheShip.instance.getServer().getWorld(ChestFile.getString("Chests." + i + ".World"));
					double X = ChestFile.getDouble("Chests." + i + ".X");
					double Y = ChestFile.getDouble("Chests." + i + ".Y");
					double Z = ChestFile.getDouble("Chests." + i + ".Z");

					final Location loc = new Location(world, X, Y, Z);

					if (loc.getBlock().getType() == Material.CHEST) {
						Block b = loc.getBlock();
						Inventory inv = ((Chest) b.getState()).getBlockInventory();

						ItemStack[] items = inv.getContents();
						for (ItemStack item : items) {
							if (item != null) {
								final Item spawned = loc.getBlock().getLocation().getWorld().dropItemNaturally(loc.getBlock().getLocation().add(0, 3, 0), item);
								Weapons.items.add(spawned);
								pos[arena]++;
								itemLocs[arena][pos[arena]] = spawned;
								spawned.setVelocity(new Vector(0, 0, 0));
							}
						}
					} else {
						ChestFile.set("Chests." + i + "", null);
						try {
							ChestFile.save(chestFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	}
	
//	public static void spawnItems(Location chestLoc) {
//		Location loc = chestLoc;
//		if (loc.getBlock().getType() == Material.CHEST) {
//			Block b = loc.getBlock();
//			Inventory inv = ((Chest) b.getState()).getBlockInventory();
//
//			ItemStack[] items = inv.getContents();
//			for (ItemStack item : items) {
//				if (item != null) {
//					final Item spawned = loc.getBlock().getLocation().getWorld().dropItemNaturally(loc.getBlock().getLocation().add(0, 3, 0), item);
//					Weapons.items.add(spawned);
//					Weapons.itemLoc.put(spawned, spawned.getLocation());
//					System.out.println(loc);
//					System.out.println(spawned.getLocation());
//					spawned.setVelocity(new Vector(0, 0, 0));
//				}
//			}
//		}
//	}
	
	public static void despawn(int arena) {
		for(int i = 0; i < 29; i++) {
			if(itemLocs[arena][i] != null) {
				itemLocs[arena][i].remove();
			}
		}
	}

}
