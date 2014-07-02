package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;
import de.inventivegames.theShip.*;

public interface CommandInterface {

	public boolean onCommand(Player player, String[] args);

	public String permission();

}
