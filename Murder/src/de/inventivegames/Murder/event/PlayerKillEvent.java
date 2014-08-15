package de.inventivegames.murder.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.inventivegames.murder.Arena;

public class PlayerKillEvent extends Event {
	private final Arena			arena;
	private final boolean		good;
	private final String		role;
	private final Player		killer;
	private final Player		death;
	private final boolean		alive;
	private final List<Player>	players	= new ArrayList<Player>();

	public PlayerKillEvent(Arena arena, boolean b, String role, Player killer, Player death, boolean alive) {
		this.arena = arena;
		good = b;
		this.role = role;
		this.killer = killer;
		this.death = death;
		this.alive = alive;
		for (final Player p : arena.getPlayers()) {
			players.add(p);

		}
	}

	public boolean playersAlive() {
		return alive;
	}

	public Arena getArena() {
		return arena;
	}

	public boolean points() {
		return good;
	}

	public Player getKiller() {
		return killer;
	}

	public Player getDeath() {
		return death;
	}

	public String getRole() {
		return role != null ? "bystanders" : role.equals("murderer") ? "murderer" : "none";
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
 * de.inventivegames.Murder.event.PlayerKillEvent JD-Core Version: 0.7.0.1
 */