package de.inventivegames.Murder;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Location;
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

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if ((e.getLine(0).equalsIgnoreCase("[Murder]")) && ((e.getPlayer().hasPermission("murder.admin.createsign")) || (e.getPlayer().isOp()))) {
			for (int i = 0; i <= 3; i++) {
				String line = e.getLine(i);

				line = line.replace("&", "§");
				line = line.replace("&", "§");
				e.setLine(i, line);
			}
			e.setLine(0, "§1[§cMurder§1]");
			if (e.getLine(1).equalsIgnoreCase("join")) {
				e.setLine(1, "§2Join");
				e.setLine(3, Murder.getStatus(Integer.valueOf(e.getLine(2))));
			} else if (e.getLine(1).equalsIgnoreCase("leave")) {
				e.setLine(1, "§cLeave");
			}

			createSignFile(Integer.parseInt(e.getLine(2)));

			addSignToFile(Integer.parseInt(e.getLine(2)), (Sign) e.getBlock().getState());

			e.getPlayer().sendMessage(Murder.prefix + "§2" + Messages.getMessage("createdSign"));
		}
	}

	private static void createSignFile(int arena) {
		signFile = new File("plugins/Murder/Arenas/" + arena + "/signs.yml");
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

	private static void addSignToFile(int arena, Sign sign) {
		signFile = new File("plugins/Murder/Arenas/" + arena + "/signs.yml");
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

	public static void updateSigns(int arena) {
		signFile = new File("plugins/Murder/Arenas/" + arena + "/signs.yml");
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

		for (int i = 0; i < id; i++) {
			if (SignFile.get("Signs." + i) != null) {
				World world = Murder.instance.getServer().getWorld(SignFile.getString("Signs." + i + ".World"));
				double X = SignFile.getDouble("Signs." + i + ".X");
				double Y = SignFile.getDouble("Signs." + i + ".Y");
				double Z = SignFile.getDouble("Signs." + i + ".Z");

				Location loc = new Location(world, X, Y, Z);

				Sign sign = (Sign) loc.getBlock().getState();

				sign.setLine(3, Murder.getStatus(arena));
				sign.update();
			}
		}

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = e.getClickedBlock();
			Player p = e.getPlayer();
			BlockState state = b.getState();

			if (state instanceof Sign) {
				final Sign sign = (Sign) b.getState();

				if (sign.getLine(0).equals("§1[§cMurder§1]")) {
					if ((sign.getLine(1).equals("§2Join")) && (p.hasPermission("murder.player.join"))) {
						if (!(Murder.playersInGame.contains(p.getName()))) {
							if (!(Murder.inGame.contains("" + sign.getLine(2)))) {
								Game.joinArena(sign.getLine(2), p);

								Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

									@Override
									public void run() {
										sign.setLine(3, Murder.getStatus(Integer.valueOf(sign.getLine(2))));
										sign.update();
									}
								}, 10);

								Murder.instance.getServer().getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {

									@Override
									public void run() {
										sign.setLine(3, Murder.getStatus(Integer.valueOf(sign.getLine(2))));
										sign.update();
									}
								}, 0, 20 * 10);

								return;
							} else
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaIngame"));
							return;
						} else
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerIngame"));
						return;
					} else if ((sign.getLine(1).equals("§cLeave")) && (p.hasPermission("murder.player.leave"))) {
						if (Murder.playersInGame.contains(p.getName())) {
							int arena = Murder.getArena(p);
							Game.leaveArena(arena, p);
							return;
						} else
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerNotIngame"));
						return;
					}

				}
			}
		}
	}

}
