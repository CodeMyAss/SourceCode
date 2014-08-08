package de.inventivegames.murder.commands;

import java.util.List;

import org.bukkit.entity.Player;

public interface CommandInterface {

	public boolean onCommand(Player p, String[] args);

	public String permission();

	public List<String> getCompletions(String[] args);

	public String getUsage();

}
