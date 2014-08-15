package de.inventivegames.murder.bungeecord;

import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;
import de.inventivegames.murder.MurderPlayer;
import de.inventivegames.murder.commands.Permissions;
import de.inventivegames.murder.event.GameCancelEvent;
import de.inventivegames.murder.event.GameEndEvent;
import de.inventivegames.murder.event.GameLeaveEvent;

public class BungeeCordHandler implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(e.getPlayer());
		if (mp.playing()) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerIngame"));
			return;
		}
		final int id = Murder.bungeeArena;
		if (ArenaManager.getByID(id) == null) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));

			return;
		}
		if (ArenaManager.getByID(id).inGame()) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaIngame"));
			return;
		}
		if (!p.hasPermission(Permissions.JOIN.perm() + "." + id)) {
			Messages.getFormattedMessage("noJoinPermission", new Object[] { "" + ArenaManager.getByID(id).getID() });
			return;
		}
		mp.joinArena(ArenaManager.getByID(id));
	}

	@EventHandler
	public void onGameEnd(GameEndEvent e) {
		for (final Player p : e.getPlayers()) {
			sendPlayerToHub(p);
		}
	}

	@EventHandler
	public void onGameCancel(GameCancelEvent e) {
		for (final Player p : e.getPlayers()) {
			sendPlayerToHub(p);
		}
	}

	@EventHandler
	public void onGameLeave(GameLeaveEvent e) {
		sendPlayerToHub(e.getPlayer());
	}

	private static void sendPlayerToHub(Player p) {
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(Murder.bungeeHub);
		p.sendPluginMessage(Murder.instance, "BungeeCord", out.toByteArray());
	}

}
