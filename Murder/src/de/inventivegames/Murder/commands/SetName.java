package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;

public class SetName implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		if (args.length != 3) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
			return true;
		}
		int id;
		try {
			id = Integer.parseInt(args[1]);
		} catch (final NumberFormatException e) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
			return true;
		}
		if (ArenaManager.getByID(Integer.parseInt(args[1])) == null) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));

			return true;
		}
		ArenaManager.getByID(id).setName(args[2]);
		p.sendMessage(Murder.prefix + "§2Name for Arena #" + id + " set to \"" + args[2] + "\".");
		return false;
	}

	@Override
	public String permission() {
		return Permissions.SETNAME.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 2) {
			list.addAll(Arrays.asList(TabCompletionHelper.getAvailableArenaIDs()));
		}
		if (args.length == 3) {
			list.add("[INSERT NAME HERE]");
		}
		return list;
	}

	@Override
	public String getUsage() {
		return "§asetName §b<ArenaID> <Name>";
	}

}
