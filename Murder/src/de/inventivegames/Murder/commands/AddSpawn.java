package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Arena;
import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;
import de.inventivegames.murder.SpawnType;

public class AddSpawn implements CommandInterface {

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
		final Arena arena = ArenaManager.getByID(id);
		arena.addSpawnpoint(SpawnType.valueOf(args[2].toUpperCase()), p.getLocation());
		p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("addedSpawn").replace("%1$s", args[2].toUpperCase()).replace("%2$s", "" + arena.getSpawnpoint(SpawnType.valueOf(args[2].toUpperCase())).size()).replace("%3$s", "#" + arena.getID()));
		return true;
	}

	@Override
	public String permission() {
		return Permissions.ADDSPAWN.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 2) {
			list.addAll(Arrays.asList(TabCompletionHelper.getAvailableArenaIDs()));
		}
		if (args.length == 3) {
			for (final SpawnType type : SpawnType.values()) {
				list.add(type.toString());
			}
		}
		return list;
	}

	@Override
	public String getUsage() {
		return "§aaddSpawn §b<ArenaID> <SpawnType>";
	}

}
