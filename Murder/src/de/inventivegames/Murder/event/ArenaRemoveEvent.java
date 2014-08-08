package de.inventivegames.murder.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class ArenaRemoveEvent extends Event {
	private final Arena		arena;
	private final Player	remover;

	public ArenaRemoveEvent(Arena arena, Player remover) {
		this.arena = arena;
		this.remover = remover;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getRemover() {
		return remover;
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
 * de.inventivegames.Murder.event.ArenaRemoveEvent JD-Core Version: 0.7.0.1
 */