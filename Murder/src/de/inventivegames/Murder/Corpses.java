package de.inventivegames.murder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.inventivegames.utils.CMapUtils;

public class Corpses implements Listener {
	public static HashMap<Integer, String>		fakePlayerList	= new HashMap<Integer, String>();
	public static HashMap<Location, Integer>	fakePlayerLocs	= new HashMap<Location, Integer>();
	public static HashMap<Player, Integer>		fakePlayerMap	= new HashMap<Player, Integer>();
	public static boolean						oldSpawns		= false;
	public static HashMap<Player, Zombie>		zombieMap		= new HashMap<Player, Zombie>();

	public static void spawnCorpse(Location loc, Player p) throws Exception {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (!oldSpawns) {
			try {
				createFakePlayer(loc, p);
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		} else {
			loc.getWorld().setDifficulty(Difficulty.EASY);

			final Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);
			if (mp.getNameTag() == null) {
				zombie.setCustomName("----");
			} else {
				zombie.setCustomName(mp.getNameTag());
			}
			zombie.setCustomNameVisible(true);

			zombie.setCanPickupItems(false);

			zombie.setCanPickupItems(false);
			zombie.setMaxHealth(999999999.0D);
			zombie.setHealth(999999999.0D);
			zombie.setVillager(false);
			zombie.setBaby(false);
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 255));
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 2147000, 255));
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2147000, 255));
			zombie.getEquipment().clear();
			zombieMap.put(p, zombie);
		}
	}

	public static void despawnCorpse(Arena arena) {
		if (!oldSpawns) {
			try {
				removeFakePlayers(arena);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			for (final Player p : arena.getPlayers()) {
				final Zombie zombie = zombieMap.get(p);
				if (zombie != null) {
					zombie.damage(999999999.0D);
					for (final PotionEffect effect : zombie.getActivePotionEffects()) {
						zombie.removePotionEffect(effect.getType());
					}
					zombie.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2147000, 255));
					zombie.damage(999999999.0D);
					zombie.setFireTicks(10);
					zombie.remove();
					zombieMap.remove(p);

				}
			}
		}
	}

	private final static String	SKIN_URL	= "http://api.tuxcraft.eu/murderskin/%%var%%";

	public static void createFakePlayer(Location loc, Player p) throws Exception {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		String name = mp.getNameTag();
		if (name == null) {
			name = "----";
		}
		if (name.length() > 16) {
			name = name.substring(0, 16);
		}
		final WrapperPlayServerNamedEntitySpawn spawned = new WrapperPlayServerNamedEntitySpawn();

		final int eID = new Random().nextInt();

		final String uuid = UUID.randomUUID().toString();

		spawned.setEntityID(eID);
		spawned.setPosition(loc.toVector());
		spawned.setPlayerName(name);

		spawned.setPlayerUUID(uuid);

		// Murder.mod.setURL(SKIN_URL.replace("%%var%%", name.substring(0,
		// 2).replace("§", "").replace(" ", "")) + "b");
		Murder.mod/*
				 * .withURL(SKIN_URL.replace("%%var%%", name.substring(0,
				 * 2).replace("§", "").replace(" ", "")) + "b")
				 */.updateSkin(spawned.getProfile(), Murder.CORPSE_SKIN_NAME, SKIN_URL.replace("%%var%%", name.substring(0, 2).replace("§", "").replace(" ", "")) + "b");

		spawned.setCurrentItem((short) 0);

		spawned.setYaw(0.0F);
		spawned.setPitch(0.0F);

		final WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, Byte.valueOf((byte) 0));
		spawned.setMetadata(watcher);

		final int x = loc.getBlockX();
		final int y = loc.getBlockY();
		final int z = loc.getBlockZ();

		final WrapperPlayServerBed bed = new WrapperPlayServerBed();
		bed.setEntityId(eID);
		bed.setLocation(loc);

		final WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
		teleport.setEntityID(eID);
		teleport.setX(x + 0.5D);
		teleport.setY(y + 0.34D);
		teleport.setZ(z + 0.5D);
		teleport.setPitch(0.0F);
		teleport.setYaw(0.0F);
		for (final Player receiver : Bukkit.getServer().getOnlinePlayers()) {
			spawned.sendPacket(receiver);
			bed.sendPacket(receiver);
			teleport.sendPacket(receiver);
		}
		fakePlayerLocs.put(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), Integer.valueOf(eID));
		fakePlayerList.put(Integer.valueOf(eID), name);
		fakePlayerMap.put(p, Integer.valueOf(eID));
	}

	public static void removeFakePlayers(Arena arena) throws Exception {
		final WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		final int[] ids = new int[29];
		Player p;
		final List<Player> players = new ArrayList<Player>(arena.getPlayers());
		for (int i = 0; i < players.size(); i++) {
			p = players.get(i);
			if (fakePlayerMap.get(p) != null) {
				ids[i] = fakePlayerMap.get(p);
				try {
					fakePlayerLocs.remove(new CMapUtils(Murder.utils).getKeyByValue(fakePlayerLocs, fakePlayerMap.get(p)));
				} catch (final Exception e) {
					e.printStackTrace();
				}
				try {
					fakePlayerList.remove(fakePlayerMap.get(p));
				} catch (final Exception e) {
					e.printStackTrace();
				}
				fakePlayerMap.remove(p);
			}
		}
		destroy.setEntities(ids);
		for (final Player receiver : Bukkit.getOnlinePlayers()) {
			destroy.sendPacket(receiver);
		}
	}

	public static Boolean	timeout	= false;

	@EventHandler
	public static void onPlayerInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.inGame() && mp.isMurderer() && !mp.inSpectate()) {

			if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			if (!timeout) {
				final Location loc = e.getPlayer().getLocation();
				final Location loc1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

				if (fakePlayerLocs.containsKey(loc1)) {
					final int eID = fakePlayerLocs.get(loc1);
					final String name = fakePlayerList.get(Integer.valueOf(eID));
					disguiseMurderer(p, name);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
					@Override
					public void run() {
						Corpses.timeout = false;
					}
				}, 5L);
			}
		}
	}

	private static void disguiseMurderer(Player p, String name) {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (p.getLevel() > 0) {
			mp.disguiseMurderer(name);
		} else {
			p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("notEnoughLoot"));
		}
	}

	public static Boolean	timeout1	= false;

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Location loc = e.getPlayer().getLocation();
		final Location loc1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if (mp.inGame() && mp.isMurderer() && !mp.inSpectate() && fakePlayerLocs.containsKey(loc1) && !timeout1) {
			e.getPlayer().sendMessage(Murder.prefix + "§2" + Messages.getMessage("disguiseNotification"));
			timeout1 = true;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					Corpses.timeout1 = false;
				}
			}, 10L);
		}
	}
}
