package de.inventivegames.theShip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.avaje.ebean.validation.ValidatorMeta;

public class Players implements Listener {

	private static File	playerFile;

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, true);
		sp.setLeft();
		playerFile = new File("plugins/TheShip/Players/" + p.getName() + ".yml");
		YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
		if(!(playerFile.exists())) {
			try {
				PlayerFile.addDefault("money", 2000);
				PlayerFile.options().copyDefaults(true);
				PlayerFile.save(playerFile);
			} catch (IOException ex) {
				TheShip.console.sendMessage(TheShip.prefix + "§cCould not create Player File for Player §2" + p.getName());
				ex.printStackTrace();
			}
			try {
				PlayerFile.save(playerFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		sp.setMoney(PlayerFile.getInt("money"));

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		if (sp.playing()) {
			Game.leaveArena(p, sp.getArena());
		}
	}

	@EventHandler
	public void ItemDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		if (sp.playing()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);

			if (sp.inLobby()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
				ShipPlayer dsp = ShipPlayer.getShipPlayer(damager, false);

				if (sp.inLobby() || dsp.inLobby()) {
					e.setCancelled(true);
				}
				if (sp.inSpectate() || dsp.inSpectate()) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		final Item i = e.getItem();

		if (sp.playing()) {
			if (sp.inGame()) {
				ArrayList<ItemStack> armor = new ArrayList<ItemStack>(Arrays.asList(p.getInventory().getArmorContents()));
				if ((p.getInventory().contains(i.getItemStack())) || (armor.contains(i.getItemStack()))) {
					e.setCancelled(true);
					return;
				}
				if (Weapons.items.contains(i)) {
					p.getInventory().addItem(i.getItemStack());
					e.setCancelled(true);
				}
			}
			if (sp.inSpectate() || sp.inLobby()) {
				e.setCancelled(true);
				return;

			}
		}
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		
		if((sp.inLobby()) || (sp.inSpectate())) {
			e.setCancelled(true);
			p.closeInventory();
			return;
		}
		if(sp.inGame()) {
			
		}
	}
}
