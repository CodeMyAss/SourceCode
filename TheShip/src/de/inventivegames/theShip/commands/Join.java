package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

import de.inventivegames.theShip.Game;
import de.inventivegames.theShip.ShipPlayer;

public class Join implements CommandInterface {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("join")) {
			if (args[1] != null) {
				ShipPlayer sp = ShipPlayer.getShipPlayer(player, true);
				if (!(sp.playing())) {
					int arena = Integer.valueOf(args[1]);
					Game.joinArena(player, arena);
				} else
					player.sendMessage("§cAlready ingame");
			}
		}
		return false;
	}

	@Override
	public String permission() {
		return "TheShip.players.join";
	}

}
