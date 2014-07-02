package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

import de.inventivegames.theShip.Game;
import de.inventivegames.theShip.ShipPlayer;

public class Leave implements CommandInterface {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("leave")) {
			ShipPlayer sp = ShipPlayer.getShipPlayer(player, false);
			if (sp.playing()) {
				if (args[1] == null) {
					int arena = sp.getArena();
					Game.leaveArena(player, arena);
				} else if (args[1] != null) {
					int arena = Integer.valueOf(args[1]);
					Game.leaveArena(player, arena);
				}
			} else
				player.sendMessage("§cNot Ingame");
		}
		return false;
	}

	@Override
	public String permission() {
		return "TheShip.players.leave";
	}

}
