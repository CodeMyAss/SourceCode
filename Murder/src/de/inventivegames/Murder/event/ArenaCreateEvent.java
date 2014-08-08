package de.inventivegames.murder.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class ArenaCreateEvent extends Event {
	private final Arena		arena;
	private final Player	creator;

	public ArenaCreateEvent(Arena arena, Player creator) {
		this.arena = arena;
		this.creator = creator;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getCreator() {
		return creator;
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
 * de.inventivegames.Murder.event.ArenaCreateEvent JD-Core Version: 0.7.0.1
 */