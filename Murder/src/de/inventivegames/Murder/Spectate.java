package de.inventivegames.murder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.packetwrapper.WrapperPlayClientPosition;
import com.comphenix.packetwrapper.WrapperPlayServerPosition;

import de.inventivegames.murder.commands.Permissions;
import de.inventivegames.murder.threads.PixelImgTask;

public class Spectate implements Listener {
	private final String					INV_TITLE	= "§7Teleporter";
	public static HashMap<Player, String[]>	faces		= new HashMap();

	@EventHandler
	public void onCompassUse(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.playing() && e.getItem() != null && e.getItem().equals(Items.Compass()) && mp.inSpectate()) {
			try {
				final Inventory inv = createInv(p);
				p.openInventory(inv);
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.playing() && mp.inSpectate() && e.getInventory().getTitle().equals(INV_TITLE)) {
			if (e.getCurrentItem() == null) {
				e.setCancelled(true);
				p.closeInventory();
				return;
			}
			if (e.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
				final String name = e.getCurrentItem().getItemMeta().getDisplayName().substring(2, e.getCurrentItem().getItemMeta().getDisplayName().lastIndexOf("§") - 5);

				final Player target = Murder.instance.getServer().getPlayerExact(name);
				if (target != null) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 255, false));
					p.teleport(target.getLocation().add(0.0D, 2.0D, 0.0D));
					target.setPassenger(p);
				}
				e.setCancelled(true);
				p.closeInventory();
				return;
			}
		}
	}

	public void clientsideTeleport(Player p, Location loc) {
		if (p.getLocation().distance(loc) > 32.0D) return;

		final WrapperPlayClientPosition pos = new WrapperPlayClientPosition();
		pos.setX(loc.getX());
		pos.setY(loc.getY());
		pos.setZ(loc.getZ());
		pos.sendPacket(p);
	}

	public void serversideTeleport(Player p, Location loc) {
		if (p.getLocation().distance(loc) > 32.0D) return;

		final WrapperPlayServerPosition pos = new WrapperPlayServerPosition();
		pos.setX(loc.getX());
		pos.setY(loc.getY());
		pos.setZ(loc.getZ());
		pos.sendPacket(p);
	}

	private Inventory createInv(Player p) {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Arena arena = mp.getArena();
		final int amount = arena.getPlayerAmount();
		final Inventory inv = Murder.instance.getServer().createInventory(p, amount <= 9 ? 9 : amount <= 18 ? 18 : amount <= 27 ? 27 : 36, INV_TITLE);
		int slot = 0;
		for (int i = 0; i < amount; i++) {
			if (arena.getPlayers().get(i) != null) {
				final MurderPlayer mp1 = MurderPlayer.getPlayer(arena.getPlayers().get(i));
				if (!mp1.inSpectate()) {
					try {
						final ItemStack head = head(arena.getPlayers().get(i));
						if (p.hasPermission(Permissions.ADMIN.perm())) {
							final ItemMeta meta = head.getItemMeta();
							final List<String> lore = head.getItemMeta().getLore();
							if (MurderPlayer.getPlayer(arena.getPlayers().get(i)).isMurderer()) {
								lore.add(" ");
								lore.add("§7This Player is the §cMurderer§7." + (mp.isMurderer() && mp.disguisedTag != null ? " §r(Disguised as " + mp.disguisedTag + "§r)" : ""));
								lore.add("§8(This Message is only visible to Admins.)");
								meta.setLore(lore);
								head.setItemMeta(meta);
							}
							if (MurderPlayer.getPlayer(arena.getPlayers().get(i)).isWeaponBystander()) {
								lore.add(" ");
								lore.add("§7This Player is a §9Bystander with a Secret Weapon§7.");
								lore.add("§8(This Message is only visible to Admins.)");
								meta.setLore(lore);
								head.setItemMeta(meta);
							}
						}
						inv.setItem(slot, head);
						slot++;
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return inv;
	}

	private ItemStack head(Player p) {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		final ItemMeta meta = is.getItemMeta();
		meta.setDisplayName("§9" + p.getName() + "§7/§r" + mp.getNameTag());
		meta.setLore(faces.get(p) != null ? new ArrayList<String>(Arrays.asList(faces.get(p))) : new ArrayList<String>(Arrays.asList("")));
		is.setItemMeta(meta);
		return is;
	}

	public static void addFace(Player p) {
		final PixelImgTask task = new PixelImgTask(p);
		task.start();
	}

}
