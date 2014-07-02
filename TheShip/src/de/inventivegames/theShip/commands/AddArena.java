package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

import de.inventivegames.theShip.Arenas;

public class AddArena implements CommandInterface {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("addarena")) {
			if (args[1] != null) {
				Arenas.createArenaFile(args[1], player.getWorld(), player);
			}
		}
		return false;
	}

	@Override
	public String permission() {
		return "TheShip.admin.addarena";
	}

}
