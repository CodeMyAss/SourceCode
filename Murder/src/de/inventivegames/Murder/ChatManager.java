package de.inventivegames.murder;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		Arena arena;
		if (mp.getArena() != null) {
			arena = mp.getArena();
		} else
			return;

		final String pn = p.getName();
		final String format = e.getFormat();
		final String message = e.getMessage();

		if (mp.playing()) {
			if (!mp.inSpectate()) {
				final String prefix = Murder.prefix + "§f" + (arena.getName() != null ? arena.getName() : arena.getID()) + "§l>>§r";
				arena.sendMessage(prefix + format.replace("%1$s", pn).replace("%2$s", message));
			} else if (mp.inSpectate()) {
				final String prefix = Murder.prefix + "§f" + (arena.getName() != null ? arena.getName() : arena.getID()) + "§2[§cDEAD§2]§r " + "§l>>§r";
				arena.sendSpectatorMessage(prefix + format.replace("%1$s", pn).replace("%2$s", message));
			}
		}
	}

	@SuppressWarnings("unused")
	private String replaceNick(String s) {
		for (final String name : Murder.nameTags) {
			final String raw = name.trim().replaceAll("(&([a-fk-or0-9]))", "");
			if (s.contains(raw)) return s.replace(raw, "§c*****§r" + ChatColor.getLastColors(s));
		}
		return s;
	}

}
