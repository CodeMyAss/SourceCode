package de.inventivegames.murder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.MurderPlayer;

public class WorldListener implements Listener {

	@EventHandler
	public void onBlockChange(BlockPhysicsEvent e) {
		if (ArenaManager.getByWorld(e.getBlock().getWorld()) != null) {
			// ArenaManager.getByWorld(e.getBlock().getWorld()).worldLogger.onPhysics(e);
		}
		;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		final MurderPlayer mp = MurderPlayer.getPlayer(e.getPlayer());
		if (mp.playing()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		final MurderPlayer mp = MurderPlayer.getPlayer(e.getPlayer());
		if (mp.playing()) {
			e.setCancelled(true);
		}
	}

}
