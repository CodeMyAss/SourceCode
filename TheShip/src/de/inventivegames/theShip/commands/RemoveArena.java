package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

import de.inventivegames.theShip.Arenas;
import de.inventivegames.theShip.Signs;

public class RemoveArena implements CommandInterface {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("removearena")) {
			if (args[1] != null) {
				Arenas.removeArenaFile(args[1], player);
				Signs.removeChestFile(Integer.valueOf(args[1]));
				Signs.removeSignFile(Integer.valueOf(args[1]));
				
			}
		}
		return false;
	}

	@Override
	public String permission() {
		return "TheShip.admin.removearena";
	}

}
