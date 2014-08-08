package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;

public class Reload implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		if (args.length != 1) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
			return true;
		}

		Murder.reload();

		p.sendMessage(Murder.prefix + "§aReload Successful.");
		return false;
	}

	@Override
	public String permission() {
		return Permissions.RELOAD.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();

		return list;
	}

	@Override
	public String getUsage() {
		return "§areload";
	}

}
