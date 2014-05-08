package de.inventivegames.LetterAPICommands;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import de.inventivegames.LetterAPI.Direction;
import de.inventivegames.LetterAPI.LetterAPI;
import de.inventivegames.LetterAPI.ParticleEffect;

public class Commands extends JavaPlugin implements Listener {

	public void onEnable() {
		setupMetrics();

		getLogger().info("===================================");
		getLogger().info("===================================");
		getLogger().info("====Activating LetterAPICommands===");
		getLogger().info("===================================");
		getLogger().info("==Copyright inventivetalent 2013!==");
		getLogger().info("===================================");
		getLogger().info("=======www.InventiveGames.de=======");
		getLogger().info("===================================");

		if (getServer().getPluginManager().isPluginEnabled("LetterAPI")) {
			getLogger().info("===================================");
			getLogger().info("=Successfully hooked into LetterAPI=");
			getLogger().info("===================================");
			getLogger().info("===LetterAPICommands Activated=====");
			getLogger().info("===================================");
			getLogger().info("===================================");
		} else if (!(getServer().getPluginManager().isPluginEnabled("LetterAPI"))) {
			getLogger().info("===================================");
			getLogger().info("=====Could not detect LetterAPI====");
			getLogger().info("===================================");
			getLogger().info("=====Please install LetterAPI======");
			getLogger().info("===================================");
			getLogger().info("===================================");
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}


	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("letterapi")) {
				if (args.length == 0 || args.length == 1) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "Example$Text" });
				}
				if (args.length == 1 || args.length == 2) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockX() });
				}
				if (args.length == 2 || args.length == 3) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockY() });
				}
				if (args.length == 3 || args.length == 4) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockZ() });
				}
				if (args.length == 4 || args.length == 5) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "NORTH", "SOUTH", "EAST", "WEST", "NORTHEAST", "SOUTHEAST", "NORTHWEST", "SOUTHWEST" });
				}
				if (args.length == 5 || args.length == 6) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "true", "false" });
				}
				if (args.length == 6 || args.length == 7) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getMaterialNames());
				}
				if (args.length == 7 || args.length == 8) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
				}
				if (args.length > 8) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" });
				}
			}

			if (cmd.getName().equalsIgnoreCase("letterapiparticles")) {
				if (args.length == 0 || args.length == 1) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "Example$Text" });
				}
				if (args.length == 1 || args.length == 2) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockX() });
				}
				if (args.length == 2 || args.length == 3) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockY() });
				}
				if (args.length == 3 || args.length == 4) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockZ() });
				}
				if (args.length == 4 || args.length == 5) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "NORTH", "SOUTH", "EAST", "WEST", "NORTHEAST", "SOUTHEAST", "NORTHWEST", "SOUTHWEST" });
				}
				if (args.length == 5 || args.length == 6) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "true", "false" });
				}
				if (args.length == 6 || args.length == 7) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "hugeexplosion", "largeexplode", "bubble", "suspended", "depthsuspend", "townaura", "crit", "magicCrit", "smoke", "mobSpell", "spell", "instantSpell", "note", "portal", "enchantmenttable", "explode", "flame", "lava", "footstep", "splash", "largesmoke", "cloud", "reddust", "snowballpoof", "dripWater", "dripLava", "snowshovel", "slime", "heart" });
				}
				if (args.length > 7) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" });
				}
			}
			if (cmd.getName().equalsIgnoreCase("letterapiplayer")) {
				if (args.length == 0 || args.length == 1) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "Example$Text" });
				}
				if (args.length == 1 || args.length == 2) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockX() });
				}
				if (args.length == 2 || args.length == 3) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockY() });
				}
				if (args.length == 3 || args.length == 4) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockZ() });
				}
				if (args.length == 4 || args.length == 5) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "NORTH", "SOUTH", "EAST", "WEST", "NORTHEAST", "SOUTHEAST", "NORTHWEST", "SOUTHWEST" });
				}
				if (args.length == 5 || args.length == 6) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "true", "false" });
				}
				if (args.length == 6 || args.length == 7) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getMaterialNames());
				}
				if (args.length == 7 || args.length == 8) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
				}
				if (args.length == 8 || args.length == 9) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getOnlinePlayerNames());
				}
				if (args.length > 9) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" });
				}
			}

			if (cmd.getName().equalsIgnoreCase("letterapireset")) {
				if (args.length == 0 || args.length == 1) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "block", "player", "normal", "specific" });
				}
				if (args.length == 1 || args.length == 2) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "Example$Text" });
				}
				if (args.length == 2 || args.length == 3) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockX() });
				}
				if (args.length == 3 || args.length == 4) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockY() });
				}
				if (args.length == 4 || args.length == 5) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" + p.getLocation().getBlockZ() });
				}
				if (args.length == 5 || args.length == 6) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "NORTH", "SOUTH", "EAST", "WEST", "NORTHEAST", "SOUTHEAST", "NORTHWEST", "SOUTHWEST" });
				}
				if (args.length == 6 || args.length == 7) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "true", "false" });
				}
				if (args.length == 7 || args.length == 8) {
					if (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("specifc")) {
						return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getOnlinePlayerNames());
					} else
						return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" });
				}
				if (args.length > 8) {
					return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "" });
				}
			}
		}
		return null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("letterapireset")) {
			args[0] = args[0].replace("$", " ");
		}
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("letterAPI")) {
				Player player = (Player) sender;
				if (player.isOp()) {
					if (args.length < 6) {
						player.sendMessage("§cToo few Arguments: §4/letterapi [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] [MATERIAL] [DATA]§r");
					} else if (args.length == 6) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createBlockText(args[0], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.STONE, (byte) 1);
						} else
							LetterAPI.createBlockText(args[0], new Location(player.getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.STONE, (byte) 1);
					} else if (args.length == 8) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createBlockText(args[0], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]));
						} else
							LetterAPI.createBlockText(args[0], new Location(player.getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]));
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}

			if (cmd.getName().equalsIgnoreCase("letterAPIParticles")) {
				Player player = (Player) sender;
				if (player.isOp()) {
					if (args.length < 6) {
						player.sendMessage("§cToo few Arguments: §4/letterapiparticles [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] [ParticleEffect]§r");
					} else if (args.length == 6) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createParticleText(args[0], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.MAGIC_CRIT);
						} else
							LetterAPI.createParticleText(args[0], new Location(player.getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.MAGIC_CRIT);
					} else if (args.length == 7) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createParticleText(args[0], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.fromName(args[6].toUpperCase()));
						} else
							LetterAPI.createParticleText(args[0], new Location(player.getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.fromName(args[6].toUpperCase()));
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}
			if (cmd.getName().equalsIgnoreCase("letterAPIPlayer")) {
				Player player = (Player) sender;
				if (player.isOp()) {
					if (args.length < 9) {
						player.sendMessage("§cToo few Arguments: §4/letterapiplayer [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] [MATERIAL] [DATA] [PLAYER]§r");
					} else if (args.length == 9) {
						if (Bukkit.getPlayerExact(args[8]) == null) {
							player.sendMessage("§cPlayer not found!");
							return true;
						}
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createPlayerSpecificText(args[0], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]), Bukkit.getPlayerExact(args[8]));
						} else
							LetterAPI.createPlayerSpecificText(args[0], new Location(player.getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]), Bukkit.getPlayerExact(args[8]));
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}
			if (cmd.getName().equalsIgnoreCase("letterAPIRESET")) {
				Player player = (Player) sender;
				if (player.isOp()) {
					if (args.length == 8) {
						if (Bukkit.getPlayerExact(args[7]) == null) {
							player.sendMessage("§cPlayer not found!");
							return true;
						}
						if (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("specific")) {
							if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
								LetterAPI.resetPlayerSpecificText(args[1], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[5]), Boolean.valueOf(args[6]), Bukkit.getPlayerExact(args[7]));
							} else {
								LetterAPI.resetPlayerSpecificText(args[1], new Location(player.getWorld(), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3]), (double) Integer.parseInt(args[4])), Direction.getDirection(args[5]), Boolean.valueOf(args[6]), Bukkit.getPlayerExact(args[7]));
							}
						}
					} else if (args.length == 7) {
						if (args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("normal")) {
							if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
								LetterAPI.resetBlockText(args[1], new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), Direction.getDirection(args[5]), Boolean.valueOf(args[6]));
							} else {
								LetterAPI.resetBlockText(args[1], new Location(player.getWorld(), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3]), (double) Integer.parseInt(args[4])), Direction.getDirection(args[5]), Boolean.valueOf(args[6]));
							}
						}
					} else if (args.length < 8) {
						player.sendMessage("§cToo few Arguments: §4/letterapireset [Type (block/player)] [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] <PLAYER>§r");
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}
		} else {
			if (cmd.getName().equalsIgnoreCase("letterAPI")) {
				BlockCommandSender player = (BlockCommandSender) sender;
				if (player.isOp()) {
					if (args.length < 6) {
						player.sendMessage("§cToo few Arguments: §4/letterapi [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] [MATERIAL] [DATA]§r");
					} else if (args.length == 6) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createBlockText(args[0], new Location(player.getBlock().getWorld(), player.getBlock().getLocation().getX(), player.getBlock().getLocation().getY(), player.getBlock().getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.STONE, (byte) 1);
						} else
							LetterAPI.createBlockText(args[0], new Location(player.getBlock().getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.STONE, (byte) 1);
					} else if (args.length == 8) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createBlockText(args[0], new Location(player.getBlock().getWorld(), player.getBlock().getLocation().getX(), player.getBlock().getLocation().getY(), player.getBlock().getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]));
						} else
							LetterAPI.createBlockText(args[0], new Location(player.getBlock().getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]));
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}

			if (cmd.getName().equalsIgnoreCase("letterAPIParticles")) {
				BlockCommandSender player = (BlockCommandSender) sender;
				if (player.isOp()) {
					if (args.length < 4) {
						player.sendMessage("§cToo few Arguments: §4/letterapiparticles [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] [ParticleEffect]§r");
					} else if (args.length == 6) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createParticleText(args[0], new Location(player.getBlock().getWorld(), player.getBlock().getLocation().getX(), player.getBlock().getLocation().getY(), player.getBlock().getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.MAGIC_CRIT);
						} else
							LetterAPI.createParticleText(args[0], new Location(player.getBlock().getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.MAGIC_CRIT);
					} else if (args.length == 8) {
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createParticleText(args[0], new Location(player.getBlock().getWorld(), player.getBlock().getLocation().getX(), player.getBlock().getLocation().getY(), player.getBlock().getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.fromName(args[6]));
						} else
							LetterAPI.createParticleText(args[0], new Location(player.getBlock().getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), ParticleEffect.fromName(args[6]));
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}
			if (cmd.getName().equalsIgnoreCase("letterAPIPlayer")) {
				BlockCommandSender player = (BlockCommandSender) sender;
				if (player.isOp()) {
					if (args.length < 9) {
						player.sendMessage("§cToo few Arguments: §4/letterapi [TEXT] [X] [Y] [Z] [DIRECTION] [centered (true/false)] [MATERIAL] [DATA] [PLAYER]§r");
					} else if (args.length == 9) {
						if (Bukkit.getPlayerExact(args[8]) == null) {
							player.sendMessage("§cPlayer not found!");
							return true;
						}
						if ((args[1].equals("~")) && (args[2].equals("~")) && (args[3].equals("~"))) {
							LetterAPI.createPlayerSpecificText(args[0], new Location(player.getBlock().getWorld(), player.getBlock().getLocation().getX(), player.getBlock().getLocation().getY(), player.getBlock().getLocation().getZ()), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]), Bukkit.getPlayerExact(args[8]));
						} else
							LetterAPI.createPlayerSpecificText(args[0], new Location(player.getBlock().getWorld(), (double) Integer.parseInt(args[1]), (double) Integer.parseInt(args[2]), (double) Integer.parseInt(args[3])), Direction.getDirection(args[4]), Boolean.valueOf(args[5]), Material.getMaterial(args[6].toUpperCase()), Byte.valueOf(args[7]), Bukkit.getPlayerExact(args[8]));
					}
				} else
					player.sendMessage("§cYou don't have permission to execute this Command!");
			}
		}
		return false;
	}
}
