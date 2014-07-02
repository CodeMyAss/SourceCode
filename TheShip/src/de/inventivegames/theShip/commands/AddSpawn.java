package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

import de.inventivegames.theShip.Arenas;
import de.inventivegames.theShip.TheShip;


public class AddSpawn implements CommandInterface {
	
	@Override
	public boolean onCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("addspawn")) {
			if((args[2] != null) && (TheShip.spawnTypes.contains(args[2]))) {
				if ((args.length == 3) && ((args[1] != null) && (args[2] != null))) {
					Arenas.addSpawnPoint(args[1], args[2], 1, player);
	
				} else if ((args.length == 4) && ((args[1] != null) && (args[2] != null) && (args[3] != null))) {
					Arenas.addSpawnPoint(args[1], args[2], args[3], player);
				}
			}else {
				player.sendMessage(TheShip.prefix + "§cUnknown Spawnpoint Type!");
			}
		}
		return false;
	}

	@Override
	public String permission() {
		return "TheShip.admin.addspawn";
	}

}
