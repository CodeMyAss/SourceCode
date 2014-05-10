package de.inventivegames.Murder;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Corpses {

	public static void spawnCorpse(Location loc, Player p) throws Exception {
		loc.getWorld().setDifficulty(Difficulty.EASY);

		Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);
		zombie.setCustomName(Murder.nameTag.get(p));
		zombie.setCustomNameVisible(true);
		if (zombie.getCustomName().equalsIgnoreCase(null) || zombie.getCustomName().equalsIgnoreCase("Zombie")) {
			zombie.setCustomName("");
		}
		zombie.setCanPickupItems(false);
		zombie.setMaxHealth(999999999D);
		zombie.setHealth(999999999D);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 255));
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 2147000, 255));
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2147000, 255));
		Murder.zombieMap.put(p, zombie);
	}

	public static void despawnCorpse(Player p) throws Exception {
		Zombie zombie = Murder.zombieMap.get(p);
		if (zombie != null) {
			zombie.damage(999999999D);
			for (PotionEffect effect : zombie.getActivePotionEffects()) {
				zombie.removePotionEffect(effect.getType());
			}
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2147000, 255));
			zombie.damage(999999999D);
			zombie.setFireTicks(10);
			zombie.remove();
			Murder.zombieMap.remove(p);
		}
	}
}
