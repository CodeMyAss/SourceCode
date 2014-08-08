package de.inventivegames.murder;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {
	public static ItemStack Knife() {
		final ItemStack Knife = new ItemStack(Material.DIAMOND_AXE);
		final ItemMeta knifeMeta = Knife.getItemMeta();
		knifeMeta.setDisplayName("§c§l" + Messages.getMessage("knife"));
		knifeMeta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
		Knife.setItemMeta(knifeMeta);

		return Knife;
	}

	public static ItemStack Gun() {
		final ItemStack Gun = new ItemStack(Material.DIAMOND_HOE);
		final ItemMeta gunMeta = Gun.getItemMeta();
		gunMeta.setDisplayName("§1§l" + Messages.getMessage("gun"));
		gunMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		Gun.setItemMeta(gunMeta);

		return Gun;
	}

	public static ItemStack Bullet() {
		final ItemStack Bullet = new ItemStack(Material.ARROW);
		final ItemMeta bulletMeta = Bullet.getItemMeta();
		bulletMeta.setDisplayName("§8" + Messages.getMessage("bullet"));
		Bullet.setItemMeta(bulletMeta);

		return Bullet;
	}

	public static ItemStack Loot() {
		final ItemStack Loot = new ItemStack(Material.DIAMOND);
		final ItemMeta lootMeta = Loot.getItemMeta();
		lootMeta.setDisplayName("§6" + Messages.getMessage("loot"));
		lootMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		Loot.setItemMeta(lootMeta);

		return Loot;
	}

	public static ItemStack NameInfo(Player p) {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final ItemStack Loot = new ItemStack(Material.NAME_TAG);
		final ItemMeta lootMeta = Loot.getItemMeta();
		lootMeta.setDisplayName("§l" + (mp.getNameTag() != null ? (String) mp.getNameTag() : "§r§cUnable to get NameTag!"));
		lootMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		Loot.setItemMeta(lootMeta);

		return Loot;
	}

	public static ItemStack SpeedBoost() {
		final ItemStack sb = new ItemStack(Material.SUGAR);
		final ItemMeta sbMeta = sb.getItemMeta();
		sbMeta.setDisplayName("§7Speed-Boost");
		sb.setItemMeta(sbMeta);

		return sb;
	}

	public static ItemStack Compass() {
		final ItemStack c = new ItemStack(Material.COMPASS);
		final ItemMeta cMeta = c.getItemMeta();
		cMeta.setDisplayName("§7Teleporter");
		c.setItemMeta(cMeta);

		return c;
	}
}
