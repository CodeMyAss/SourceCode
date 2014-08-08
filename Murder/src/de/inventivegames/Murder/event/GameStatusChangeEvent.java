package de.inventivegames.murder.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;
import de.inventivegames.murder.ArenaStatus;

public class GameStatusChangeEvent extends Event {
	private final Arena			arena;
	private final ArenaStatus	status;

	public GameStatusChangeEvent(Arena arena, ArenaStatus status) {
		this.arena = arena;
		this.status = status;
	}

	public Arena getArena() {
		return arena;
	}

	public ArenaStatus getStatus() {
		return status;
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
 * de.inventivegames.Murder.event.GameStatusChangeEvent JD-Core Version: 0.7.0.1
 */