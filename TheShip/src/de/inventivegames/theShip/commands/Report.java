package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

import de.inventivegames.theShip.TheShip;

public class Report implements CommandInterface {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if(player.isOp()) {
			TheShip.reportCmd(player);
		}
		return false;
	}

	@Override
	public String permission() {
		return "yjJe66eF.eUgfAdMB.cNJfpQjj"; //unnecessary
	}

}
