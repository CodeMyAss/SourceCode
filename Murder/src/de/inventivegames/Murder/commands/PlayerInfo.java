package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;
import de.inventivegames.murder.MurderPlayer;

public class PlayerInfo implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		if (args.length != 2) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("wrongUsage").replace("%1$s", "§4/murder help§c"));
			return true;
		}
		@SuppressWarnings("deprecation")
		final Player target = Murder.instance.getServer().getPlayerExact(args[1]);
		if (!target.isOnline()) {
			p.sendMessage(Murder.prefix + "§cThis Player is not online.");
			return true;
		}
		final MurderPlayer mp = MurderPlayer.getPlayer(target);
		mp.sendInfo(p);
		return false;
	}

	@Override
	public String permission() {
		return Permissions.PLAYERINFO.perm();
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 2) {
			list.addAll(Arrays.asList(TabCompletionHelper.getOnlinePlayerNames()));
		}
		return list;
	}

	@Override
	public String getUsage() {
		return "§aplayerInfo §b<PlayerName>";
	}

}
