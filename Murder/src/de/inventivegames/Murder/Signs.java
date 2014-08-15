package de.inventivegames.murder;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Bukkit;
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

import de.inventivegames.murder.commands.Permissions;

public class Signs implements Listener {
	private static File	signFile;

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[Murder]") && e.getLine(1) != null && (e.getPlayer().hasPermission(Permissions.CREATESIGN.perm()) || e.getPlayer().isOp())) {
			for (int i = 0; i <= 3; i++) {
				String line = e.getLine(i);

				line = line.replace("&", "§");
				line = line.replace("&", "§");
				e.setLine(i, line);
			}
			final Arena arena;
			if (e.getLine(2).startsWith("#")) {
				int id;
				try {
					id = Integer.parseInt(e.getLine(2).substring(1));
				} catch (final NumberFormatException e1) {
					return;
				}
				if (ArenaManager.getByID(id) == null) {
					e.getPlayer().sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
					return;
				}
				arena = ArenaManager.getByID(id);
			} else {
				arena = ArenaManager.getByName(e.getLine(2));
				if (ArenaManager.getByName(e.getLine(2)) == null) {
					e.getPlayer().sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
					return;
				}
			}
			e.setLine(0, "§1[§cMurder§1]");
			if (e.getLine(1).equalsIgnoreCase("join") && e.getLine(2) != null) {
				e.setLine(1, "§2Join");
				e.setLine(3, arena.getStatus().toString());
			} else if (e.getLine(1).equalsIgnoreCase("leave")) {
				e.setLine(1, "§cLeave");
				return;
			}
			if (e.getLine(2) != null) {
				createSignFile(arena.getID());

				addSignToFile(arena.getID(), (Sign) e.getBlock().getState());
			}
			if (e.getLine(1) != null) {
				e.getPlayer().sendMessage(Murder.prefix + "§2" + Messages.getMessage("createdSign"));
			}
		}
	}

	private static void createSignFile(int id) {
		signFile = new File("plugins/Murder/Arenas/" + id + "/signs.yml");
		final YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(signFile);
		if (!signFile.exists()) {
			SignFile.options().copyDefaults(true);
			SignFile.createSection("Signs");
			try {
				SignFile.save(signFile);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void addSignToFile(int id, Sign sign) {
		signFile = new File("plugins/Murder/Arenas/" + id + "/signs.yml");
		final YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(signFile);

		final Set<String> IDs = SignFile.getConfigurationSection("Signs").getKeys(false);
		final Object[] ids = IDs.toArray();
		final int id1;
		if (IDs.size() != 0) {
			final String ID = ids[IDs.size() - 1].toString();
			id1 = Integer.parseInt(ID) + 1;
		} else {
			id1 = 1;
		}
		final Location loc = sign.getBlock().getLocation();

		final double X = loc.getX();
		final double Y = loc.getY();
		final double Z = loc.getZ();

		SignFile.createSection("Signs." + id1);
		SignFile.createSection("Signs." + id1 + ".World");
		SignFile.createSection("Signs." + id1 + ".X");
		SignFile.createSection("Signs." + id1 + ".Y");
		SignFile.createSection("Signs." + id1 + ".Z");

		SignFile.set("Signs." + id1 + ".World", loc.getWorld().getName());
		SignFile.set("Signs." + id1 + ".X", Double.valueOf(X));
		SignFile.set("Signs." + id1 + ".Y", Double.valueOf(Y));
		SignFile.set("Signs." + id1 + ".Z", Double.valueOf(Z));
		try {
			SignFile.save(signFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateSigns(int arena) {
		signFile = new File("plugins/Murder/Arenas/" + arena + "/signs.yml");
		final YamlConfiguration SignFile = YamlConfiguration.loadConfiguration(signFile);

		if (SignFile.get("Signs") != null) {
			final Set<String> IDs = SignFile.getConfigurationSection("Signs").getKeys(false);
			final Object[] ids = IDs.toArray();
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
					final World world = Murder.instance.getServer().getWorld(SignFile.getString("Signs." + i + ".World"));
					final double X = SignFile.getDouble("Signs." + i + ".X");
					final double Y = SignFile.getDouble("Signs." + i + ".Y");
					final double Z = SignFile.getDouble("Signs." + i + ".Z");

					final Location loc = new Location(world, X, Y, Z);

					if (loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.WALL_SIGN) {
						final Sign sign = (Sign) loc.getBlock().getState();

						sign.setLine(3, ArenaManager.getByID(arena).getStatus().toString());
						sign.update();
					} else {
						SignFile.set("Signs." + i + "", null);
						try {
							SignFile.save(signFile);
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final Block b = e.getClickedBlock();
			final Player p = e.getPlayer();
			final BlockState state = b.getState();

			final MurderPlayer mp = MurderPlayer.getPlayer(p);

			if (state instanceof Sign) {
				final Sign sign = (Sign) b.getState();

				if (sign.getLine(0).equals("§1[§cMurder§1]")) {
					if (sign.getLine(1).equals("§2Join") && p.hasPermission(Permissions.JOIN.perm())) {
						if (!mp.playing()) {
							final Arena arena;
							if (sign.getLine(2).startsWith("#")) {
								int id;
								try {
									id = Integer.parseInt(sign.getLine(2).substring(1));
								} catch (final NumberFormatException e1) {
									return;
								}
								if (ArenaManager.getByID(id) == null) {
									p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
									return;
								}
								arena = ArenaManager.getByID(id);
							} else {
								arena = ArenaManager.getByName(sign.getLine(2));
								if (ArenaManager.getByName(sign.getLine(2)) == null) {
									p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
									return;
								}
							}
							if (!p.hasPermission((Permissions.JOIN.perm() + "." + arena.getID()).trim())) {
								Messages.getFormattedMessage("noJoinPermission", new Object[] { "" + arena.getID() });
								e.setCancelled(true);
								return;
							}
							if (arena.getStatus() != ArenaStatus.INGAME) {
								if (p.getItemInHand().getType() == Material.AIR) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
										@Override
										public void run() {
											mp.joinArena(arena);

											Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

												@Override
												public void run() {
													sign.setLine(3, arena.getStatus().toString());
													sign.update();
												}
											}, 10);

											Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

												@Override
												public void run() {
													sign.setLine(3, arena.getStatus().toString());
													sign.update();
												}
											}, 20 * 10);

											return;
										}
									}, 1L);
								}
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaIngame"));
							}
							return;
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerIngame"));
						}
						return;
					} else if (sign.getLine(1).equals("§cLeave") && p.hasPermission(Permissions.LEAVE.perm())) {
						if (mp.playing()) {
							mp.leaveArena(true);
							return;
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerNotIngame"));
						}
						return;
					}

				}
			}
		}
	}
}
