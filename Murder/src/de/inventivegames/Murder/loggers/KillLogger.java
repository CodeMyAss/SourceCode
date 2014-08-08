package de.inventivegames.murder.loggers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.inventivegames.murder.Murder;
import de.inventivegames.murder.MurderPlayer;
import de.inventivegames.murder.event.PlayerKillEvent;

public class KillLogger implements Listener {
	//
	// private Arena arena;
	//
	// public KillLogger(Arena arena) {
	// this.arena = arena;
	// }

	@EventHandler
	public void log(PlayerKillEvent e) {
		final Player p = e.getDeath();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Player killer = e.getKiller();
		final MurderPlayer mpKiller = MurderPlayer.getPlayer(killer);
		if (e.points()) {
			if (mpKiller.isWeaponBystander()) {
				if (mp.isBystander()) {
					mpKiller.subtractPoints(Murder.POINTS_MINUS);
					System.out.println("Subtracting " + Murder.POINTS_MINUS + " Points from " + killer.getName() + ": KilledInnocent[POINTS_MINUS]");
					// subtractPoints(e.getKiller(), Murder.POINTS_MINUS);
					return;
				}
				if (mp.isMurderer()) {
					if (!e.playersAlive()) {
						mpKiller.addPoints(Murder.POINTS_BYSTANDER_WIN_WEAPON);
						System.out.println("Adding " + Murder.POINTS_BYSTANDER_WIN_WEAPON + " Points to " + killer.getName() + ": KilledMurderer[POINTS_BYSTANDER_WIN_WEAPON]");
						// addPoints(e.getKiller(),
						// Murder.POINTS_BYSTANDER_WIN_WEAPON);
						for (final Player p1 : e.getPlayers()) {
							final MurderPlayer mp1 = MurderPlayer.getPlayer(p1);
							if (!mp1.isMurderer() && !mp1.isWeaponBystander()) {
								mp1.addPoints(Murder.POINTS_BYSTANDER_WIN);
								System.out.println("Adding " + Murder.POINTS_BYSTANDER_WIN + " Points to " + p1.getName() + ": BystandersWon[POINTS_BYSTANDER_WIN]");
								// addPoints(p, Murder.POINTS_BYSTANDER_WIN);
							}
						}
					}
					return;
				}
			}
			if (mpKiller.isMurderer()) {
				mpKiller.addPoints(Murder.POINTS_PLUS);
				System.out.println("Adding " + Murder.POINTS_PLUS + " Points to " + killer.getName() + ": MurdererKill[POINTS_PLUS]");
				// addPoints(e.getKiller(), Murder.POINTS_PLUS);
				if (!e.playersAlive()) {
					mpKiller.addPoints(Murder.POINTS_MURDERER_WIN);
					System.out.println("Adding " + Murder.POINTS_MURDERER_WIN + " Points to " + killer.getName() + ": MurdererWon[POINTS_MURDERER_WIN]");
					// addPoints(e.getKiller(), Murder.POINTS_MURDER_WIN);
				}
				return;
			}
		}
	}

}
