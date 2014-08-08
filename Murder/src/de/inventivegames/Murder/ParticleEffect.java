package de.inventivegames.murder;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleEffect {
	private Location				loc;
	private final ParticleEffects	effect;
	private float					x		= 0.0F;
	private float					y		= 0.0F;
	private float					z		= 0.0F;
	private float					speed	= 0.0F;
	private int						count	= 0;

	public ParticleEffect(Location loc, ParticleEffects effect) {
		this.loc = loc;
		this.effect = effect;
	}

	public ParticleEffect setLocation(Location loc) {
		this.loc = loc;
		return this;
	}

	public ParticleEffect setOffset(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public ParticleEffect setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public ParticleEffect setCount(int count) {
		this.count = count;
		return this;
	}

	public void sendToPlayer(Player p) {
		try {
			// effect.sendToPlayer(p, loc, x, y, z, speed, count);
			effect.display(loc.add(0.0D, 0.1D, 0.0D), x, y, z, speed, count, p);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void sendToPlayers(Player[] players) {
		// for (final Player p : players) {
		try {
			// effect.sendToPlayer(p, loc, x, y, z, speed, count);
			effect.display(loc.add(0.0D, 0.1D, 0.0D), x, y, z, speed, count, players);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		// }
	}

	public void sendToPlayers(List<Player> players) {
		final Player[] pArray = new Player[players.size()];
		for (int i = 0; i < players.size(); i++) {
			pArray[i] = players.get(i);
		}
		sendToPlayers(pArray);
	}
}
