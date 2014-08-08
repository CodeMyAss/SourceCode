package de.inventivegames.murder.tutorial;

import org.bukkit.entity.Player;

import de.inventivegames.murder.MurderPlayer;

public class Tutorial {

	private final MurderPlayer	mp;
	private final Player		p;

	public Tutorial(MurderPlayer mp) {
		this.mp = mp;
		p = mp.player();
	}

}
