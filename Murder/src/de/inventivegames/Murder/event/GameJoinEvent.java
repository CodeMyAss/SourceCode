package de.inventivegames.murder.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class GameJoinEvent extends Event implements Cancellable {
	private final Arena		arena;
	private final Player	p;

	public GameJoinEvent(Arena arena, Player p) {
		this.arena = arena;
		this.p = p;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getPlayer() {
		return p;
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

	private boolean	cancelled;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean paramBoolean) {
		cancelled = paramBoolean;
	}

}

/*
 * Location: C:\Users\Marvin\Dropbox\dfgshj\Murder_Beta[1.9].jar Qualified Name:
 * de.inventivegames.Murder.event.GameJoinEvent JD-Core Version: 0.7.0.1
 */