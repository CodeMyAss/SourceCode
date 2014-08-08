package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Arena;
import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;

public class Stop implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		if (args.length != 2) {
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
		if (ArenaManager.getByID(id) == null) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
			return true;
		}
		if (!ArenaManager.getByID(id).inGame() && !ArenaManager.getByID(id).starting()) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotIngame"));
			return true;
		}
		final Arena arena = ArenaManager.getByID(id);
		arena.game.cancelAllTaks();
		arena.game.stopDelayed(10);
		return false;
	}

	@Override
	public String permission() {
		return Permissions.STOP.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 2) {
			list.addAll(Arrays.asList(TabCompletionHelper.getAvailableArenaIDs()));
		}
		return list;
	}

	@Override
	public String getUsage() {
		return "§astop §b<ArenaID>";
	}

}
