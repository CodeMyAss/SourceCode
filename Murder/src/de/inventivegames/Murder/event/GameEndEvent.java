package de.inventivegames.murder.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class GameEndEvent extends Event {
	private final Arena			arena;
	private final String		winner;
	private final List<Player>	players	= new ArrayList();

	public GameEndEvent(Arena arena, List<Player> players, String winner) {
		this.arena = arena;
		this.winner = winner;
		this.players.addAll(players);
	}

	public Arena getArena() {
		return arena;
	}

	public String getWinner() {
		return winner != null ? winner.equals("murderer") ? "murderer" : "bystanders" : "none";
	}

	public List<Player> getPlayers() {
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
 * de.inventivegames.Murder.event.GameEndEvent JD-Core Version: 0.7.0.1
 */