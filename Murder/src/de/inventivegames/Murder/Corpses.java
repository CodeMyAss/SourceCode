package de.inventivegames.Murder;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

@SuppressWarnings("deprecation")
public class Corpses implements Listener {

	public static HashMap<Integer, String>		fakePlayerList	= new HashMap<Integer, String>();
	public static HashMap<Location, Integer>	fakePlayerLocs	= new HashMap<Location, Integer>();
	public static HashMap<Player, Integer>		fakePlayerMap	= new HashMap<Player, Integer>();
	public static ProtocolManager				manager;

	public static boolean						oldSpawns		= false;

	public static void spawnCorpse(Location loc, Player p) throws Exception {

		if (!oldSpawns) {
			try {
				createFakePlayer(loc, p);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			loc.getWorld().setDifficulty(Difficulty.EASY);

			Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);
			if (Murder.nameTag.get(p) == null) {
				zombie.setCustomName("----");
			} else {
				zombie.setCustomName(Murder.nameTag.get(p));
			}
			zombie.setCustomNameVisible(true);

			zombie.setCanPickupItems(false);

			zombie.setCanPickupItems(false);
			zombie.setMaxHealth(999999999D);
			zombie.setHealth(999999999D);
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 255));
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 2147000, 255));
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2147000, 255));
			Murder.zombieMap.put(p, zombie);
		}
	}

	public static void despawnCorpse(int arena) {

		if (!oldSpawns) {
			try {
				removeFakePlayers(arena);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < Murder.maxPlayers + 1; i++) {
				if (Murder.players[arena][i] != null) {
					Player p = Murder.players[arena][i];

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
		}
	}

	public static void createFakePlayer(Location loc, Player p) throws Exception {
		String name = Murder.nameTag.get(p);

		if (name == null) {
			name = "----";
		}

		if (name.length() > 16) {
			name = name.substring(0, 16);
		}

		WrapperPlayServerNamedEntitySpawn spawned = new WrapperPlayServerNamedEntitySpawn();

		int eID = new Random().nextInt();

		String uuid = "" + UUID.randomUUID();

		spawned.setEntityID(eID);
		spawned.setPosition(loc.toVector());
		spawned.setPlayerName(name);
		spawned.setPlayerUUID(uuid);

		spawned.setYaw(0);
		spawned.setPitch(0);

		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, (byte) 0);
		spawned.setMetadata(watcher);

		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		WrapperPlayServerBed bed = new WrapperPlayServerBed();
		bed.setEntityId(eID);
		bed.setLocation(loc);

		WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
		teleport.setEntityID(eID);
		teleport.setX(x + 0.5);
		teleport.setY(y + 0.34);
		teleport.setZ(z + 0.5);
		teleport.setPitch(0);
		teleport.setYaw(0);

		for (Player receiver : Bukkit.getServer().getOnlinePlayers()) {
			spawned.sendPacket(receiver);
			bed.sendPacket(receiver);
			teleport.sendPacket(receiver);

		}

		fakePlayerLocs.put(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), eID);
		fakePlayerList.put(eID, name);
		fakePlayerMap.put(p, eID);
	}

	public static void removeFakePlayers(int arena) throws Exception {
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		int[] ids = new int[25];
		for (int i = 0; i < Murder.playersAmount[arena] + 1; i++) {
			Player p = Murder.players[arena][i];
			if (fakePlayerMap.get(p) != null) {
				ids[i] = fakePlayerMap.get(p);
				fakePlayerMap.remove(p);
			}
		}
		destroy.setEntities(ids);
		for (Player receiver : Bukkit.getOnlinePlayers()) {
			destroy.sendPacket(receiver);
		}
	}

	public static Boolean	timeout	= false;

	@EventHandler
	public static void onPlayerInteract(final PlayerInteractEvent e) {

		final Player p = e.getPlayer();

		if (Murder.playersInGame.contains(p)) {
			if ((Murder.isMurderer(p)) && (!(Murder.isSpectator(p)))) {
				if (!timeout) {

					Location loc = e.getPlayer().getLocation();
					Location loc1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

					if (fakePlayerLocs.containsKey(loc1)) {
						int eID = fakePlayerLocs.get(loc1);
						String name = fakePlayerList.get(eID);
						disguiseMurderer(p, name);
					}

					Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

						@Override
						public void run() {
							timeout = false;

						}
					}, 5);
				}

			}
		}
	}

	private static void disguiseMurderer(Player p, String name) {
		Players.disguiseMurderer(p, name);
	}

	public static Boolean	timeout1	= false;

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location loc = e.getPlayer().getLocation();
		Location loc1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if (Murder.playersInGame.contains(p)) {
			if ((Murder.isMurderer(p)) && (!(Murder.isSpectator(p)))) {
				if ((fakePlayerLocs.containsKey(loc1)) && (!timeout1)) {
					e.getPlayer().sendMessage(Murder.prefix + "§2" + Messages.getMessage("disguiseNotification"));
					timeout1 = true;
					Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

						@Override
						public void run() {
							timeout1 = false;

						}
					}, 10);
				}
			}
		}
	}

}
