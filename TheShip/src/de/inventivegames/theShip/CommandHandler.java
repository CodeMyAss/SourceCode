package de.inventivegames.theShip;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.inventivegames.theShip.commands.AddArena;
import de.inventivegames.theShip.commands.AddSpawn;
import de.inventivegames.theShip.commands.CmdHelp;
import de.inventivegames.theShip.commands.CommandInterface;
import de.inventivegames.theShip.commands.Join;
import de.inventivegames.theShip.commands.Leave;
import de.inventivegames.theShip.commands.RemoveArena;

public class CommandHandler implements CommandExecutor {

	private HashMap<String, CommandInterface>	commands;

	public CommandHandler() {
		commands = new HashMap<String, CommandInterface>();
		loadCommands();
	}

	private void loadCommands() {
		commands.put("help", new CmdHelp());
		commands.put("addarena", new AddArena());
		commands.put("removearena", new RemoveArena());
		commands.put("addspawn", new AddSpawn());
		commands.put("join", new Join());
		commands.put("leave", new Leave());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this Command!");
			return true;
		}

		if ((cmd.getName().equalsIgnoreCase("theship")) || (cmd.getName().equalsIgnoreCase("ts")) || (cmd.getName().equalsIgnoreCase("ship"))) {
			Player p = (Player) sender;

			if ((args.length != 0) && (args[0] != null)) {
				String sub = args[0].toLowerCase();
				if (!commands.containsKey(sub)) {
					p.sendMessage("Command doesn't exist!");
					return true;
				}
				if (!p.hasPermission(commands.get(sub).permission()) && !p.isOp()) {
					p.sendMessage("You don't have Permission to execute this command!");
					return true;
				}
				try {
					commands.get(sub).onCommand(p, args);
				} catch (Exception e) {
					e.printStackTrace();

				}
			} else {
				p.sendMessage("§2TheShip Minigame Version 0.0 by inventivetalent");
				p.sendMessage("§cType §4/TheShip help §cfor a list of commands.");
			}
			return true;
		}
		return false;
	}

}
