package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;

public class Force implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		if (args.length != 3) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
			return true;
		}
		@SuppressWarnings("deprecation")
		final Player target = Bukkit.getServer().getPlayerExact(args[2]);
		if (target == null) {
			p.sendMessage("§c\"" + args[2] + "§c\" is not online!");
			return true;
		}
		if (args[1].equalsIgnoreCase("MURDERER")) {
			Murder.forcedMurderers.add(target);
		} else if (args[1].equalsIgnoreCase("WEAPON")) {
			Murder.forcedWeapons.add(target);
		} else {
			p.sendMessage("§cInvalid Role!");
			return true;
		}
		p.sendMessage("§2" + target.getName() + " §2will be " + args[1].toUpperCase() + ".");
		return false;
	}

	@Override
	public String permission() {
		return Permissions.FORCE.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 2) {
			list.add("MURDERER");
			list.add("WEAPON");
		}
		if (args.length == 3) {
			list.addAll(Arrays.asList(TabCompletionHelper.getOnlinePlayerNames()));
		}
		return list;
	}

	@Override
	public String getUsage() {
		return "§aforce §b<MURDERER/WEAPON> <Player>";
	}

}
