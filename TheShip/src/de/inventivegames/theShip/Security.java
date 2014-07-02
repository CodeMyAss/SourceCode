package de.inventivegames.theShip;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Security implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		Location loc = p.getLocation();

		if (sp.playing()) {
			if (sp.inGame()) {
//				String indicator = "\u2588";

				Material mat = Material.SPONGE;

				if ((loc.getBlock().getLocation().subtract(0, 2, 0).getBlock().getType() == mat) || (loc.getBlock().getLocation().subtract(0, 3, 0).getBlock().getType() == mat)) {
					if (!sp.isObserved()) {
						sp.setObserved(true);
						sp.setObserveNoticed();
//
//						for (int i = 0; i < 20; i++) {
//							p.sendMessage(" ");
//						}
//						p.sendMessage("§c" + "   " + indicator + indicator + indicator);
//						p.sendMessage("§c" + "   " + indicator + indicator + indicator);
//						p.sendMessage("§c" + "   " + indicator + indicator + indicator);
//						p.sendMessage(" ");
						
						SBoard.getBoard(p).setStatus(true);
					}
				} else {
					if (sp.isObserved()) {
						sp.setObserved(false);
//
//						for (int i = 0; i < 20; i++) {
//							p.sendMessage(" ");
//						}
//						p.sendMessage("§2" + "   " + indicator + indicator + indicator);
//						p.sendMessage("§2" + "   " + indicator + indicator + indicator);
//						p.sendMessage("§2" + "   " + indicator + indicator + indicator);
//						p.sendMessage(" ");
						
						SBoard.getBoard(p).setStatus(false);
					}
				}
			}
		}
	}

}
