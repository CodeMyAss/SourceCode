package de.inventivegames.theShip;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Signs implements Listener {

	private static File	signFile;
	private static File	chestFile;

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if ((e.getLine(0).equalsIgnoreCase("[Ship]")) && (e.getLine(1) != null) && ((e.getPlayer().hasPermission("theship.admin.createsign")) || (e.getPlayer().isOp()))) {
			for (int i = 0; i <= 3; i++) {
				String line = e.getLine(i);

				line = line.replace("&", "§");
				line = line.replace("&", "§");
				e.setLine(i, line);
			}
			e.setLine(0, "§1[§bShip§1]");
			if (e.getLine(1).equalsIgnoreCase("join") && (e.getLine(2) != null)) {
				e.setLine(1, "§2Join");
				e.setLine(3, TheShip.getStatus(Integer.valueOf(e.getLine(2))));
			} else if (e.getLine(1).equalsIgnoreCase("leave")) {
				e.setLine(1, "§cLeave");
			} else if (e.getLine(1).equalsIgnoreCase("chest")) {
				if (e.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType() == Material.CHEST) {
					if (e.getLine(2) != null) {
						createChestFile(Integer.parseInt(e.getLine(2)));

						addChestToFile(Integer.parseInt(e.getLine(2)), e.getBlock().getLocation().subtract(0, 1, 0).getBlock());
						e.getPlayer().sendMessage(TheShip.prefix + "§2Successfully created Supply Chest");
					}
				} else {
					e.getPlayer().sendMessage(TheShip.prefix + "§cYou have to place this Sign on top of a Chest!");
				}
			}

			if (e.getLine(2) != null) {
				createSignFile(Integer.parseInt(e.getLine(2)));

				addSignToFile(Integer.parseInt(e.getLine(2)), (Sign) e.getBlock().getState());

			}
			if (e.getLine(1) != null) {
				e.getPlayer().sendMessage(TheShip.prefix + "§2Successfully created Sign");
			}
		}
	}

	private static void createSignFile(int arena) {
		signFile = new File("plugins/TheShip/Arenas/" + arena + "/signs.yml");
		YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(signFile);

		if (!(signFile.exists())) {
			SignFile.options().copyDefaults(true);
			SignFile.createSection("Signs");
			try {
				SignFile.save(signFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void createChestFile(int arena) {
		chestFile = new File("plugins/TheShip/Arenas/" + arena + "/chests.yml");
		YamlConfiguration ChestFile = YamlConfiguration.loadConfiguration(chestFile);

		if (!(chestFile.exists())) {
			ChestFile.options().copyDefaults(true);
			ChestFile.createSection("Chests");
			try {
				ChestFile.save(chestFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void addSignToFile(int arena, Sign sign) {
		signFile = new File("plugins/TheShip/Arenas/" + arena + "/signs.yml");
		YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(signFile);

		Set<String> IDs = SignFile.getConfigurationSection("Signs").getKeys(false);
		Object[] ids = IDs.toArray();
		String ID;
		int id;
		if (IDs.size() != 0) {
			ID = ids[IDs.size() - 1].toString();
			id = Integer.parseInt(ID) + 1;
		} else {
			id = 1;
		}

		Location loc = sign.getBlock().getLocation();

		double X = loc.getX();
		double Y = loc.getY();
		double Z = loc.getZ();

		SignFile.createSection("Signs." + id);
		SignFile.createSection("Signs." + id + ".World");
		SignFile.createSection("Signs." + id + ".X");
		SignFile.createSection("Signs." + id + ".Y");
		SignFile.createSection("Signs." + id + ".Z");

		SignFile.set("Signs." + id + ".World", loc.getWorld().getName());
		SignFile.set("Signs." + id + ".X", X);
		SignFile.set("Signs." + id + ".Y", Y);
		SignFile.set("Signs." + id + ".Z", Z);

		try {
			SignFile.save(signFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void addChestToFile(int arena, Block chest) {
		signFile = new File("plugins/TheShip/Arenas/" + arena + "/chests.yml");
		YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(chestFile);

		Set<String> IDs = SignFile.getConfigurationSection("Chests").getKeys(false);
		Object[] ids = IDs.toArray();
		String ID;
		int id;
		if (IDs.size() != 0) {
			ID = ids[IDs.size() - 1].toString();
			id = Integer.parseInt(ID) + 1;
		} else {
			id = 1;
		}

		Location loc = chest.getLocation();

		double X = loc.getX();
		double Y = loc.getY();
		double Z = loc.getZ();

		SignFile.createSection("Chests." + id);
		SignFile.createSection("Chests." + id + ".World");
		SignFile.createSection("Chests." + id + ".X");
		SignFile.createSection("Chests." + id + ".Y");
		SignFile.createSection("Chests." + id + ".Z");

		SignFile.set("Chests." + id + ".World", loc.getWorld().getName());
		SignFile.set("Chests." + id + ".X", X);
		SignFile.set("Chests." + id + ".Y", Y);
		SignFile.set("Chests." + id + ".Z", Z);

		try {
			SignFile.save(chestFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void updateSigns(int arena) {
		signFile = new File("plugins/TheShip/Arenas/" + arena + "/signs.yml");
		YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(signFile);

		if (SignFile.get("Signs") != null) {
			Set<String> IDs = SignFile.getConfigurationSection("Signs").getKeys(false);
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
				if (SignFile.get("Signs." + i) != null) {
					World world = TheShip.instance.getServer().getWorld(SignFile.getString("Signs." + i + ".World"));
					double X = SignFile.getDouble("Signs." + i + ".X");
					double Y = SignFile.getDouble("Signs." + i + ".Y");
					double Z = SignFile.getDouble("Signs." + i + ".Z");

					Location loc = new Location(world, X, Y, Z);

					if ((loc.getBlock().getType() == Material.SIGN) || (loc.getBlock().getType() == Material.WALL_SIGN)) {
						Sign sign = (Sign) loc.getBlock().getState();

						sign.setLine(3, TheShip.getStatus(arena));
						sign.update();
					} else {
						SignFile.set("Signs." + i + "", null);
						try {
							SignFile.save(signFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public static void removeSignFile(int arena) {
		signFile = new File("plugins/TheShip/Arenas/" + arena + "/signs.yml");
		signFile.delete();
	}
	

	public static void removeChestFile(int arena) {
		chestFile = new File("plugins/TheShip/Arenas/" + arena + "/chests.yml");
		chestFile.delete();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = e.getClickedBlock();
			final Player p = e.getPlayer();
			ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
			BlockState state = b.getState();

			if (state instanceof Sign) {
				final Sign sign = (Sign) b.getState();

				if (sign.getLine(0).equals("§1[§bShip§1]")) {
					if ((sign.getLine(1).equals("§2Join")) && (p.hasPermission("murder.player.join"))) {
						if (!(sp.playing())) {
							if (!(TheShip.inGame.contains("" + sign.getLine(2)))) {
								if (p.getItemInHand().getType() == Material.AIR) {
									TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {
										public void run() {
											Game.joinArena(p, Integer.parseInt(sign.getLine(2)));

											TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

												@Override
												public void run() {
													sign.setLine(3, TheShip.getStatus(Integer.valueOf(sign.getLine(2))));
													sign.update();
												}
											}, 10);

											TheShip.instance.getServer().getScheduler().scheduleSyncRepeatingTask(TheShip.instance, new Runnable() {

												@Override
												public void run() {
													sign.setLine(3, TheShip.getStatus(Integer.valueOf(sign.getLine(2))));
													sign.update();
												}
											}, 0, 20 * 10);

											return;
										}
									}, 1L);
								}
							} else
								p.sendMessage(TheShip.prefix + "§cArena is Ingame");
							return;
						} else
							p.sendMessage(TheShip.prefix + "§cYou're already Ingame");
						return;
					} else if ((sign.getLine(1).equals("§cLeave")) && (p.hasPermission("murder.player.leave"))) {
						if (sp.playing()) {
							int arena = sp.getArena();
							Game.leaveArena(p, arena);
							return;
						} else
							p.sendMessage(TheShip.prefix + "§cYou are not Ingame");
						return;
					}

				}
			}
		}
	}

}
