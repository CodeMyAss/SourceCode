package de.inventivegames.murder.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class GameStartEvent extends Event {
	private final Arena	arena;

	public GameStartEvent(Arena arena) {
		this.arena = arena;
	}

	public Arena getArena() {
		return arena;
	}

	public List<Player> getPlayers() {
		final List<Player> players = new ArrayList<Player>();
		for (final Player p : arena.getPlayers()) {
			players.add(p);

		}
		return players;
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
 * de.inventivegames.Murder.event.GameStartEvent JD-Core Version: 0.7.0.1
 */