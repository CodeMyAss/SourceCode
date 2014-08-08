package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Murder;

public class Help implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		p.sendMessage("§2==== Murder MiniGame by inventivetalent  ====");
		p.sendMessage("§2=============== Commands ===============");
		final List<CommandInterface> interfaces = new ArrayList<CommandInterface>(Murder.cmdHandler.getCommandMap().values());
		for (final CommandInterface a : interfaces) {
			p.sendMessage("§2/murder " + a.getUsage());
		}
		p.sendMessage("§2=============== Enjoy! =================");
		p.sendMessage("§2========= www.InventiveGames.de ==========");
		return false;
	}

	@Override
	public String permission() {
		return Permissions.HELP.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();

		return list;
	}

	@Override
	public String getUsage() {
		return "§ahelp";
	}

}
