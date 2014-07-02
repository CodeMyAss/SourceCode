package de.inventivegames.Murder;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements Listener, CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("murder")) {
				if ((p.hasPermission("murder.admin")) || (p.isOp())) {
					if (args.length == 0 || args.length == 1)
						return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "join", "leave", "help", "start", "stop", "addarena", "removearena", "addspawn" });
					if (args.length == 1 || args.length == 2) {
						if (args[0].equalsIgnoreCase("start"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
						if (args[0].equalsIgnoreCase("stop"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
						if (args[0].equalsIgnoreCase("addarena"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
						if (args[0].equalsIgnoreCase("removearena"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
						if (args[0].equalsIgnoreCase("addspawn"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
					}

					if (args.length == 2 || args.length == 3) {
						if (args[0].equalsIgnoreCase("addspawn"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "lobby", "players", "loot" });
					}
					if (args.length == 3 || args.length == 4) {
						if (args[0].equalsIgnoreCase("addspawn"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
					}
				}
				if ((p.hasPermission("murder.player")) || (p.isOp())) {

					if (args.length == 0 || args.length == 1)
						return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, new String[] { "join", "leave", "help" });
					if (args.length == 1 || args.length == 2) {
						if (args[0].equalsIgnoreCase("join"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
						if (args[0].equalsIgnoreCase("leave"))
							return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, TabCompletionHelper.getAllIntegers());
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (Murder.playersInGame.contains(p)) {
				if (!(cmd.getLabel().equalsIgnoreCase("murder"))) {
					if (!(Murder.instance.getConfig().getList("allowedCommands").contains(cmd.getLabel()))) {
						p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("cantUseCommand"));
						return true;
					}
				}
			}

			if ((cmd.getLabel().equalsIgnoreCase("murder"))) {
				if (args.length == 0) {
					p.sendMessage("§2===Murder MiniGame Version " + Murder.instance.getDescription().getVersion().toString() + " by inventivetalent===");
					p.sendMessage("§cType §4/murder help §cfor a list of commands!");
				}
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("help")) {
						if (args.length == 1) {
							p.sendMessage("§2==== Murder MiniGame by inventivetalent  ====");
							p.sendMessage("§2=============== Commands ===============");
							p.sendMessage("§2/murder help");
							p.sendMessage("§2/murder join <ArenaNumber>");
							p.sendMessage("§2/murder leave");
							if (p.hasPermission("murder.admin") || p.isOp()) {
								p.sendMessage("§2/murder start <ArenaNumber>");
								p.sendMessage("§2/murder addarena <ArenaNumber>");
								p.sendMessage("§2/murder removearena <ArenaNumber>");
								p.sendMessage("§2/murder addspawn <ArenaNumber> lobby");
								p.sendMessage("§2/murder addspawn <ArenaNumber> players <SpawnNumber>");
								p.sendMessage("§2/murder addspawn <ArenaNumber> loot <SpawnNumber>");
							}
							p.sendMessage("§2=============== Enjoy! =================");
							p.sendMessage("§2========= www.InventiveGames.de ==========");
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
							return true;
						}
					} else if ((args[0].equalsIgnoreCase("join"))) {
						if ((p.hasPermission("murder.player.join"))) {
							if (args.length == 2) {
								if (!(Murder.playersInGame.contains(p))) {
									if (!(Murder.inGame.contains("" + args[1]))) {
										Game.joinArena(args[1], p);
										return true;
									} else {
										p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaIngame"));
									}
									return true;
								} else {
									p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerIngame"));
								}
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else if (args[0].equalsIgnoreCase("leave")) {
						if (p.hasPermission("murder.player.leave")) {
							if (Murder.playersInGame.contains(p)) {
								if (args.length == 1) {
									int arena = Murder.getArena(p);
									Game.leaveArena(arena, p);
									return true;
								} else if (args.length == 2) {
									Game.leaveArena(args[1], p);
									return true;
								} else {
									p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
									return true;
								}
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerNotIngame"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else

						if (args[0].equalsIgnoreCase("start")) {
							if (p.hasPermission("murder.admin.start")) {
								if (args.length == 1) {
									int arena = Murder.getArena(p);
									Game.startGame(arena, p);
									Murder.instance.getServer().getScheduler().cancelTask(Game.countdownLobby[arena]);
									Murder.instance.getServer().getScheduler().cancelTask(Game.delayedStart[arena]);
									return true;
								} else if (args.length == 2) {
									Game.startGame(args[1], p);
									int arena = Integer.parseInt(args[1]);
									Murder.instance.getServer().getScheduler().cancelTask(Game.countdownLobby[arena]);
									Murder.instance.getServer().getScheduler().cancelTask(Game.delayedStart[arena]);
									return true;
								} else {
									p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
									return true;
								}
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
							}
						}
					if (args[0].equalsIgnoreCase("stop")) {
						if (p.hasPermission("murder.admin.start")) {
							if (args.length == 1) {
								int arena = Murder.getArena(p);
								Game.stopGame(arena);
								return true;
							}
							if (args.length == 2) {
								Game.stopGame(Integer.valueOf(args[1]));
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else if (args[0].equalsIgnoreCase("addarena")) {
						if (p.hasPermission("murder.admin.addarena")) {
							if (args.length == 2) {
								Arenas.createArenaFile(args[1], p.getWorld(), p);
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else if (args[0].equalsIgnoreCase("removearena")) {
						if (p.hasPermission("murder.admin.removearena")) {
							if (args.length == 2) {
								Arenas.removeArenaFile(args[1], p);
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else if (args[0].equalsIgnoreCase("addspawn")) {
						if (p.hasPermission("murder.admin.spawns")) {
							if ((args.length == 3) && (args[2].equalsIgnoreCase("lobby"))) {
								Arenas.addSpawnPoint(args[1], args[2], 1, p);
								return true;
							} else if ((args.length == 4) && (!args[2].equalsIgnoreCase("lobby"))) {
								Arenas.addSpawnPoint(args[1], args[2], args[3], p);
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else if (args[0].equalsIgnoreCase("forcemurderer")) {
						if (p.hasPermission("murder.admin.force.murderer")) {
							if (args.length == 2) {
								Game.forceMurderer(Murder.getArena(p), p);
								return true;
							} else if (args.length == 3) {
								Game.forceMurderer(Murder.getArena(p), args[1], p);
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else if (args[0].equalsIgnoreCase("forceweapon")) {
						if (p.hasPermission("murder.admin.force.weapon")) {
							if (args.length == 2) {
								Game.forceWeapon(Murder.getArena(p), p);
								return true;
							} else if (args.length == 3) {
								Game.forceWeapon(Murder.getArena(p), args[1], p);
								return true;
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
								return true;
							}
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
						}
					} else if (args[0].equalsIgnoreCase("arenaInfo")) {
						if (p.hasPermission("murder.admin.DEBUG.arena")) {
							if (args.length == 2) {
								Arenas.printArenaInfo(p, Integer.valueOf(args[1]));
							} else {
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
							}
							return true;
						} else {
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
						}
					} else {
						p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
						return true;
					}
					return true;
				}
			}
		} else {
			sender.sendMessage("Only Players can use this command!");
		}

		return false;
	}

}
