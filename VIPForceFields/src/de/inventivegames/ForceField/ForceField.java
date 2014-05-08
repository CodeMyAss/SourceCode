package de.inventivegames.ForceField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.mcstats.MetricsLite;

public class ForceField extends JavaPlugin implements Listener {

	public ArrayList<Player>		toggled		= new ArrayList<Player>();
	public static ArrayList<Player>	msgCooldown	= new ArrayList<Player>();
	File							configFile	= new File(this.getDataFolder(), "config.yml");
	public static ForceField		instance;

	public void onEnable() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(instance, instance);

		if (!(configFile.exists())) {
			getConfig().addDefault("Message", "&cYou can't stay close to this Player!");
			getConfig().addDefault("Radius", 5);
			getConfig().options().copyDefaults(true);
			saveConfig();
		}

		setupMetrics();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((cmd.getLabel().equalsIgnoreCase("forcefield"))) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("forcefield.toggle")) {
					if (args.length == 0) {
						toggle(p);
					} else if (args.length == 1) {
						if (p.hasPermission("forcefield.toggle.other")) {
							if (Bukkit.getPlayerExact(args[0]) != null) {
								toggle(Bukkit.getPlayerExact(args[0]), p);
							} else
								p.sendMessage("§cPlayer §2" + args[0] + " §cis not online!");
						} else
							p.sendMessage("§cYou don't have permission to execute this Command!");
					} else
						p.sendMessage("§cToo many Arguments!");
				} else
					p.sendMessage("§cYou don't have permission to execute this Command!");
			} else
				sender.sendMessage("Only Players can use this Command!");
		}
		return false;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		double radius = getConfig().getDouble("Radius");
		if (toggled.contains(player)) {
			List<Entity> nearbyPlayers = player.getNearbyEntities(radius, radius, radius);
			for (Entity entity : nearbyPlayers) {
				if (entity instanceof Player) {
					if (!((Player) entity).hasPermission("forcefield.bypass")) {
						entity.setVelocity(calculateVelocity(player, entity));
						sendMessage((Player) entity, player);
						ParticleEffects eff = ParticleEffects.PORTAL;
						for (Player pl : Bukkit.getOnlinePlayers()) {
							try {
								for (int i = 0; i < 10; i++) {
									float x = (float) 0.5;
									float y = (float) 1;
									float z = (float) 0.5;
									float speed = 0.1F;
									int count = 5;
									Location loc = new Location(entity.getLocation().getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
									eff.sendToPlayer(pl, loc, x, y, z, speed, count);
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}
		} else {
			List<Entity> nearbyPlayers = player.getNearbyEntities(radius, radius, radius);
			for (Entity entity : nearbyPlayers) {
				if (entity instanceof Player) {
					if (toggled.contains(entity))
						if (!(player.hasPermission("forcefield.bypass"))) {
							player.setVelocity(calculateVelocity((Player) entity, player));
							sendMessage(player, (Player) entity);
							ParticleEffects eff = ParticleEffects.PORTAL;
							for (Player pl : Bukkit.getOnlinePlayers()) {
								try {
									for (int i = 0; i < 10; i++) {
										float x = (float) 0.5;
										float y = (float) 1;
										float z = (float) 0.5;
										float speed = 0.1F;
										int count = 5;
										Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
										eff.sendToPlayer(pl, loc, x, y, z, speed, count);
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
				}

			}
		}
	}

	public Vector calculateVelocity(Player p, Entity e) {
		Location ploc = p.getLocation();
		Location eloc = e.getLocation();

		double px = ploc.getX();
		double py = ploc.getY();
		double pz = ploc.getZ();
		double ex = eloc.getX();
		double ey = eloc.getY();
		double ez = eloc.getZ();

		double x = 0;
		double y = 0;
		double z = 0;

		if (px < ex) {
			x = 1;
		} else if (px > ex) {
			x = -1;
		}

		if (py < ey) {
			y = 0.5;
		} else if (py > ey) {
			y = -0.5;
		}

		if (pz < ez) {
			z = 1;
		} else if (pz > ez) {
			z = -1;
		}

		return new Vector(x, y, z);
	}

	public void toggle(Player p) {
		if (!toggled.contains(p)) {
			toggled.add(p);
			p.sendMessage("§aForceField is now §2ON");
		} else if (toggled.contains(p)) {
			toggled.remove(p);
			p.sendMessage("§aForceField is now §cOFF");
		}
	}

	public void toggle(Player p, Player sender) {
		if (!toggled.contains(p)) {
			toggled.add(p);
			sender.sendMessage("§aForceField for Player §2" + p.getName() + " §ais now §2ON");
		} else if (toggled.contains(p)) {
			toggled.remove(p);
			sender.sendMessage("§aForceField for Player §2" + p.getName() + " §ais now §cOFF");
		}
	}

	public static void sendMessage(final Player p, final Player owner) {
		if (!(msgCooldown.contains(p))) {
			msgCooldown.add(p);
			p.sendMessage("§3[§aVIPForceField§3] " + colorize(instance.getConfig().getString("Message")).replaceAll("%Player%", owner.getName()));

			instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

				@Override
				public void run() {
					msgCooldown.remove(p);

				}

			}, 10L);
		}
	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

	public static String colorize(String Message) {
		return Message.replaceAll("&([a-z0-9])", "§$1");
	}
}
