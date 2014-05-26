package de.inventivegames.Murder;

import org.bukkit.entity.Player;

public class Titles {

	public static void showStartTitles(int arena) {
		for (int i = 0; i < 25; i++) {
			if (Murder.players[arena][i] != null) {
				Player p = Murder.players[arena][i];

				if (Murder.isMurderer(p)) {
					showStartMurderTitle(p);
				}
				if (Murder.isBystander(p)) {
					showStartBystanderTitle(p);
				}
				if (Murder.isWeaponBystander(p)) {
					showStartWeaponBystanderTitle(p);
				}
			}
		}
	}

	private static void showStartMurderTitle(Player p) {
		String title = "You are the Murderer";
		String subtitle = "Kill everyone";
		String subtitle2 = "Don't get caught";

	}

	private static void showStartBystanderTitle(Player p) {
		String title = "You are a Bystander";
		String subtitle = "There is a murderer on the loose";
		String subtitle2 = "Don't get killed";

	}

	private static void showStartWeaponBystanderTitle(Player p) {
		String title = "You are a Bystander";
		String title2 = "with a secret Weapon";
		String subtitle = "There is a murderer on the loose";
		String subtitle2 = "Find and kill him";

	}
}
