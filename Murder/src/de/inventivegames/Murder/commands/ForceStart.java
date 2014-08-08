package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Arena;
import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;

public class ForceStart implements CommandInterface {

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
		if (ArenaManager.getByID(id).inGame()) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaIngame"));

			return true;
		}
		final Arena arena = ArenaManager.getByID(id);
		arena.game.cancelDelayedStart();
		arena.timer.cancel();
		arena.game.start();
		return true;
	}

	@Override
	public String permission() {
		return Permissions.START.perm();
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
		return "§astart §b<ArenaID>";
	}

}
