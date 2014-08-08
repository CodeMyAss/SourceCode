package de.inventivegames.murder.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class PlayerDeathEvent extends Event {
	private final Player	p;
	private final Arena		arena;
	private final Player	killer;

	public PlayerDeathEvent(Player p, Arena arena, Player killer) {
		this.p = p;
		this.arena = arena;
		this.killer = killer;
	}

	public Player getPlayer() {
		return p;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getKiller() {
		return killer;
	}

	private static final HandlerList	handlers	= new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}

/*
 * Location: C:\Users\Marvin\Dropbox\dfgshj\Murder_Beta[1.9].jar Qualified Name:
 * de.inventivegames.Murder.event.PlayerDeathEvent JD-Core Version: 0.7.0.1
 */