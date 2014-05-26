package de.inventivegames.Murder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String pn = p.getName();
		String format = e.getFormat();
		String message = e.getMessage();

		if (((Murder.playersInGame.contains(p)) || (Murder.playersInLobby.contains(p))) && (!(Murder.playersInSpectate.contains(p)))) {

			int arena = Murder.getArena(p);
			Murder.sendArenaMessage(format.replace("%1$s", pn).replace("%2$s", message), arena);
			e.setCancelled(true);

		} else if (Murder.playersInSpectate.contains(p)) {

			int arena = Murder.getArena(p);
			Murder.sendSpectatorMessage("§2[§cDEAD§2]§r " + format.replace("%1$s", pn).replace("%2$s", message), arena);
			e.setCancelled(true);

		}
	}

}
