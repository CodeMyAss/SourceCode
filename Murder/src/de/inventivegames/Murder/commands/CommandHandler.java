package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;
import de.inventivegames.murder.MurderPlayer;

public class CommandHandler implements CommandExecutor, TabCompleter, Listener {

	private final HashMap<String, CommandInterface>	commands;

	public CommandHandler() {
		commands = new HashMap<String, CommandInterface>();
		loadCommands();
	}

	private void loadCommands() {
		commands.put("join", new Join());
		commands.put("leave", new Leave());
		commands.put("addarena", new AddArena());
		commands.put("help", new Help());
		commands.put("removearena", new RemoveArena());
		commands.put("addspawn", new AddSpawn());
		commands.put("start", new ForceStart());
		commands.put("stop", new Stop());
		commands.put("reload", new Reload());
		commands.put("setname", new SetName());
		commands.put("savearena", new SaveArena());
		commands.put("arenainfo", new ArenaInfo());
		commands.put("playerinfo", new PlayerInfo());
		commands.put("report", new ReportCommand());
	}

	public ArrayList<String> getCommandList() {
		return new ArrayList<String>(commands.keySet());
	}

	public HashMap<String, CommandInterface> getCommandMap() {
		return new HashMap<String, CommandInterface>(commands);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Murder.prefix + "Only Players can use this Command!");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("murder")) {
			final Player p = (Player) sender;

			if (args.length != 0 && args[0] != null) {
				final String sub = args[0].toLowerCase();
				if (sub.equalsIgnoreCase("test")) {
					ArenaManager.getByID(1).worldLogger.resetModifiedBlocks();
					return true;
				}
				if (!commands.containsKey(sub)) {
					p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("unknownCommand"));
					return true;
				}
				if (!p.hasPermission(commands.get(sub).permission()) && !p.isOp()) {
					p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("noPermission"));
					return true;
				}
				try {
					commands.get(sub).onCommand(p, args);
				} catch (final Exception e) {
					e.printStackTrace();

				}
			} else {
				p.sendMessage("§2=== Murder MiniGame Version " + Murder.instance.getDescription().getVersion().toString() + " by inventivetalent ===");
				if (p.hasPermission(Permissions.PLAYER.perm())) {
					p.sendMessage("§cType §4/murder help §cfor a list of commands!");
				}
			}
			return true;
		}
		return false;
	}

	ArrayList<String>	empty	= new ArrayList<String>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String paramString, String[] args) {
		if (!(sender instanceof Player)) return empty;
		if (!cmd.getName().equalsIgnoreCase("murder")) return empty;
		final Player p = (Player) sender;

		final List<String> completions = new ArrayList<String>();

		if (args.length == 1) {
			for (final Entry<String, CommandInterface> e : commands.entrySet()) {
				if (p.hasPermission(e.getValue().permission())) {
					completions.add(e.getKey());
				}
			}
		}
		if (args.length >= 2) {
			if (!p.hasPermission(commands.get(args[0]).permission())) return empty;
			completions.addAll(commands.get(args[0]).getCompletions(args));
		}

		return TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, completions.toArray(new String[completions.size()]));
	}

	@EventHandler
	public void preprocess(PlayerCommandPreprocessEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.playing()) {
			if (!e.getMessage().toLowerCase().contains("murder")) {
				if (!Murder.instance.getConfig().getList("allowedCommands").contains(e.getMessage().toLowerCase()) && !p.hasPermission("murder.admin")) {
					p.sendMessage(Murder.prefix + Messages.getMessage("cantUseCommand"));
					e.setCancelled(true);
				}
			}
		}
	}

}
