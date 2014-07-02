package de.inventivegames.Murder;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Rewards {

	public static Economy	economy;

	public static void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Murder.instance.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
	}

	@SuppressWarnings("deprecation")
	public static void addMoney(Player p, int i) {
		if (Murder.useEconomy) {
			economy.depositPlayer(p.getName(), (double) i);
		}
	}

	@SuppressWarnings("deprecation")
	public static void removeMoney(Player p, int i) {
		if (Murder.useEconomy) {
			if (economy.has(p.getName(), (double) i)) {
				economy.withdrawPlayer(p.getName(), (double) i);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean canPlay(Player p) {
		if (Murder.useEconomy) {
			if (economy.has(p.getName(), (double) Murder.requiredMoney))
				return true;
			return false;
		}
		return true;
	}

	//	public void setMoney(int i) {

	//	}

}
