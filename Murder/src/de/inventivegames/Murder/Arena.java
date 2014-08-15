package de.inventivegames.murder;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.inventivegames.murder.event.GameLeaveEvent;
import de.inventivegames.murder.event.PlayerKillEvent;
import de.inventivegames.murder.loggers.WorldChangeLogger;
import de.inventivegames.utils.xptimer.XPTimer;

public class Arena {

	private final int										id;
	private String											name;
	private ArenaStatus										status;
	private final List<Player>								players				= new ArrayList<Player>();
	private final File										file;
	public final YamlConfiguration							config;
	private World											world;
	private final HashMap<SpawnType, ArrayList<Spawnpoint>>	spawnpoints			= new HashMap<SpawnType, ArrayList<Spawnpoint>>();
	private boolean											starting			= false;
	private boolean											inGame				= false;
	public XPTimer											timer;
	public Game												game;
	private MurderPlayer									murderer;
	// private MurderPlayer weaponBystander;
	private final List<MurderPlayer>						weaponBystanders	= new ArrayList<MurderPlayer>();
	private final List<Item>								loot				= new ArrayList<Item>();
	public boolean											smoke				= false;
	private final List<Item>								items				= new ArrayList<Item>();
	// public int bystanderAmount = 0;
	boolean													started				= false;

	// Loggers
	public WorldChangeLogger								worldLogger;

	// Schedulers
	public int												knifeTimer;
	public int												reloadTimer;
	public int												speedTimer;
	public int												trailTask;
	public Item												droppedKnife;
	public XPTimer											relTimer;

	public Arena(int id) {
		this.id = id;
		file = new File("plugins/Murder/Arenas/" + id + "/arena.yml");
		config = YamlConfiguration.loadConfiguration(file);
		status = ArenaStatus.WAITING;
		game = new Game(this);

		worldLogger = new WorldChangeLogger(this);
	}

	public void join(final Player p) {
		final Location loc = getSpawnpoint(SpawnType.LOBBY).get(0).toLocation();
		loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
		p.teleport(loc);
		p.setVelocity(new Vector(0, 0.25, 0));
		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				p.setVelocity(new Vector(0, 0.1, 0));

			}
		}, 10L);
		if (timer != null) {
			new XPTimer(Murder.utils).withInterval(20).withTime(timer.getCurrentTime()).withText().forPlayer(p).start();
		}
		players.add(p);
		sendMessage(Murder.prefix + "§1" + Messages.getMessage("joinArena").replace("%1$s", p.getName()) + " " + getPlayerAmount() + "/" + Murder.maxPlayers);
		if (getPlayerAmount() == Murder.minPlayers) {
			if (!started) {
				game.startDelayed();
				started = true;
			}
		}

		Signs.updateSigns(getID());
		if (getPlayerAmount() == Murder.maxPlayers) {
			setStatus(ArenaStatus.FULL);
		}

	}

	public void leave(Player p, boolean broadcast, boolean v) {
		players.remove(p);
		final MurderPlayer mp = MurderPlayer.getPlayer(p);

		if (v) {
			Murder.instance.getServer().getPluginManager().callEvent(new GameLeaveEvent(this, p));
		}

		Signs.updateSigns(getID());

		if (v) {
			if (broadcast) {
				sendMessage(Murder.prefix + "§1" + Messages.getMessage("leaveArena").replace("%1$s", p.getName()));
			}
			if (getAlivePlayerAmount() == 1) {
				if (!game.stopping) {
					game.stopDelayed(20 * 2);
					if (MurderPlayer.getPlayer(getAlivePlayers().get(0)).isMurderer()) {
						sendMessage(Murder.prefix + "§c" + Messages.getMessage("murdererWin1"));
						sendMessage(Murder.prefix + "§2" + Messages.getMessage("murdererWin2").replace("%1$s", getMurderer().getNameTag()).replace("%2$s", getMurderer().player().getName()));

						Murder.instance.getServer().getPluginManager().callEvent(new PlayerKillEvent(this, true, "murderer", getMurderer().player(), p, false));
					}
					for (final Player p1 : getMurdererBlacklist()) {
						Murder.murdererBlacklist.remove(p1);
					}
					for (final Player p1 : getWeaponBlacklist()) {
						Murder.weaponBlacklist.remove(p1);
					}
				}
			} else {
				if (getMurderer() == mp) {
					if (inGame()) {
						if (!game.stopping) {
							if (getBystanderAmount() >= 2) {
								// reassignMurderer();
								sendMessage(Murder.prefix + "§1" + Messages.getMessage("bystanderWin1"));
								sendMessage(Murder.prefix + "§2" + Messages.getMessage("bystanderWin2").replace("%1$s", mp.getNameTag()).replace("%2$s", p.getName()));
								game.stopDelayed(20 * 2);
								for (final Player p1 : getMurdererBlacklist()) {
									Murder.murdererBlacklist.remove(p1);
								}
								for (final Player p1 : getWeaponBlacklist()) {
									Murder.weaponBlacklist.remove(p1);
								}
							}
						}
					}
				}
				if (getWeaponBystanders().contains(mp)) {
					if (inGame()) {
						if (!game.stopping) {
							removeWeaponBystander(mp);

							Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
								@Override
								public void run() {
									final Item gun = world.dropItemNaturally(mp.player().getLocation(), Items.Gun());
									addItem(gun);
								}
							}, 2L);
						}
					}
				}
			}
			if (getPlayerAmount() <= 0) {
				game.stopDelayed(1);
			}

		}

		// if (mp.isBystander()) bystanderAmount--;

	}

	public void addEffects() {
		for (final Player pl : getPlayers()) {
			pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 255));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147000, 255));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147000, 128));
			// pl.addPotionEffect(new
			// PotionEffect(PotionEffectType.NIGHT_VISION, 2147000, 120));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 255));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2147000, 255));

			pl.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));

			pl.playSound(pl.getEyeLocation(), Sound.GHAST_SCREAM, 5, 1);

		}
	}

	public void addItem(Item i) {
		items.add(i);
	}

	public void removeItem(Item i) {
		items.remove(i);
	}

	public List<Item> getItems() {
		return items;
	}

	public boolean exists() {
		return file.exists();
	}

	public void setWorld(World w) {
		world = w;
	}

	public void sendMessage(String message) {
		for (final Player p : getPlayers()) {
			p.sendMessage(message);
		}
	}

	public void sendRawMessage(String raw) {
		for (final Player p : getPlayers()) {
			MurderPlayer.getPlayer(p).sendRawMessage(raw);
		}
	}

	public void sendSpectatorMessage(String message) {
		for (final Player p : getPlayers()) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.inSpectate()) {
				p.sendMessage(message);
			}
		}
	}

	public void create() {
		config.options().copyDefaults(true);
		config.options().header("Do NOT Edit this File manually, unless you know how to properly edit JSON Strings!");
		config.addDefault("Data", null);
		config.addDefault("Reset", null);
		try {
			config.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void setMurderer(MurderPlayer mp) {
		murderer = mp;
	}

	public void addWeaponBystander(MurderPlayer mp) {
		if (!weaponBystanders.contains(mp)) {
			weaponBystanders.add(mp);
		}
	}

	public void removeWeaponBystander(MurderPlayer mp) {
		weaponBystanders.remove(mp);
	}

	public void starting(boolean b) {
		starting = b;
	}

	public boolean starting() {
		return starting;
	}

	public void inGame(boolean b) {
		inGame = b;
	}

	public boolean inGame() {
		return inGame;
	}

	public void addLoot(Item item) {
		loot.add(item);
	}

	public void removeLoot(Item item) {
		loot.add(item);
	}

	public List<Item> getLoot() {
		return loot;
	}

	public void addSpawnpoint(SpawnType type, Location loc) {
		final List<Spawnpoint> list = new ArrayList<Spawnpoint>();
		if (spawnpoints != null && !spawnpoints.isEmpty() && spawnpoints.get(type) != null) {
			list.addAll(spawnpoints.get(type));
			if (type == SpawnType.LOBBY) {
				list.clear();
			}
		}
		list.add(new Spawnpoint(loc));
		spawnpoints.remove(type);
		spawnpoints.put(type, (ArrayList<Spawnpoint>) list);
	}

	public List<Spawnpoint> getSpawnpoint(SpawnType type) {
		return spawnpoints.get(type);
	}

	public boolean spawnpointExists(SpawnType type) {
		if (type.equals(SpawnType.LOBBY)) {
			if (spawnpoints.get(SpawnType.LOBBY) != null && spawnpoints.get(SpawnType.LOBBY).get(0) != null) return true;
			return false;
		}
		if (type.equals(SpawnType.PLAYERS)) {
			if (spawnpoints.get(SpawnType.PLAYERS).get(0) != null) return true;
			return false;
		}
		if (type.equals(SpawnType.LOOT)) {
			if (spawnpoints.get(SpawnType.LOOT).get(0) != null) return true;
			return false;
		}
		return false;
	}

	public void setSpawnpoints(Map<SpawnType, ArrayList<Spawnpoint>> map) {
		spawnpoints.clear();
		spawnpoints.putAll(map);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getPlayerAmount() {
		return players.size();
	}

	public int getBystanderAmount() {
		int i = 0;
		for (final Player p : getAlivePlayers()) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.isBystander()) {

				i++;

			}
		}
		return i;
	}

	public int getID() {
		return id;
	}

	public void setStatus(ArenaStatus status) {
		this.status = status;
	}

	public ArenaStatus getStatus() {
		return status;
	}

	public List<Player> getPlayers() {
		return new ArrayList<Player>(players);
	}

	public World getWorld() {
		return world;
	}

	public void addPlayer(Player p) {
		players.add(p);
	}

	public void removePlayer(Player p) {
		players.remove(p);
	}

	public List<Player> getAlivePlayers() {
		final List<Player> players = new ArrayList<Player>();
		for (final Player p : getPlayers()) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (!mp.inSpectate()) {
				players.add(p);
			}
		}
		return players;
	}

	public int getAlivePlayerAmount() {
		return getAlivePlayers().size();
	}

	public void save() {
		config.set("Data", toString());
		try {
			config.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	// @SuppressWarnings("deprecation")
	// public void reassignMurderer() {
	// if (getAlivePlayerAmount() < 2) return;
	// // int r = Murder.rd.nextInt(getAlivePlayerAmount());
	// final List<Player> temp = new ArrayList<Player>();
	// for (final Player p : getAlivePlayers()) {
	// final MurderPlayer mp = MurderPlayer.getPlayer(p);
	// if (mp.inGame()) {
	// if (!mp.isWeaponBystander() && !mp.inSpectate()) {
	// temp.add(p);
	// }
	// }
	// }
	//
	// final int r = Murder.rd.nextInt(temp.size());
	// final Player p = temp.get(r);
	// final MurderPlayer mp = MurderPlayer.getPlayer(p);
	// sendMessage(Murder.prefix + "§cThe Murderer left the Arena!");
	// sendMessage(Murder.prefix + "§2Assigning random Player...");
	// Murder.console.sendMessage(Murder.debugPrefix + "§3Arena §2#" + getID() +
	// "§3 - ReAssigning Murderer: " + p.getName());
	//
	// p.sendMessage(Murder.prefix + Messages.getMessage("nowMurderer"));
	//
	// mp.setBystander(false);
	// mp.setWeaponBystander(false);
	//
	// mp.getArena().despawnKnifes();
	// p.getInventory().setItem(4, Items.Knife());
	//
	// p.getInventory().setItem(8, new ItemStack(Material.AIR));
	// p.getInventory().getItem(8).setAmount(0);
	//
	// p.updateInventory();
	//
	// p.getInventory().setHeldItemSlot(0);
	//
	// setMurderer(mp);
	// mp.setMurderer(true);
	//
	// Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new
	// Runnable() {
	//
	// @Override
	// public void run() {
	// setMurderer(mp);
	// mp.setMurderer(true);
	// }
	// }, 1L);
	//
	// }

	// @SuppressWarnings("deprecation")
	// public void reassignWeaponBystander() {
	// if (getAlivePlayerAmount() <= 1) return;
	// int r = Murder.rd.nextInt(getAlivePlayerAmount());
	//
	// boolean assigned = false;
	// while (!assigned) {
	// final Player p = getAlivePlayers().get(r);
	// final MurderPlayer mp = MurderPlayer.getPlayer(p);
	// if (mp.inGame() && !mp.inSpectate()) {
	// if (!mp.isMurderer()) {
	// sendMessage(Murder.prefix +
	// "§cThe Bystander with the Secret weapon left the Arena!");
	// sendMessage(Murder.prefix + "§2Assigning random Player...");
	// Murder.console.sendMessage(Murder.debugPrefix + "§3Arena §2#" + getID() +
	// "§3 - ReAssigning WeaponBystander: " + p.getName());
	//
	// p.sendMessage(Murder.prefix + Messages.getMessage("nowBystanderWeapon"));
	//
	// p.getInventory().setItem(4, Items.Gun());
	// p.getInventory().setItem(8, Items.Bullet());
	//
	// p.updateInventory();
	//
	// mp.setBystander(false);
	// mp.setMurderer(false);
	// p.getInventory().setHeldItemSlot(0);
	//
	//
	// setMurderer(mp);
	//
	// mp.setWeaponBystander(true);
	// assigned = true;
	//
	// return;
	// }
	// if (r <= getPlayerAmount()) {
	// r++;
	// } else {
	// r--;
	// }
	// } else if (r <= getPlayerAmount()) {
	// r++;
	// } else {
	// r--;
	// }
	// }
	// }

	public void despawnAllItems() {
		for (final Entity ent : world.getEntities()) {
			if (ent.getType() == EntityType.DROPPED_ITEM) {
				ent.remove();
			}
		}
	}

	public void despawnAllArrows() {
		for (final Entity ent : world.getEntities()) {
			if (ent.getType() == EntityType.ARROW) {
				ent.remove();
			}
		}
	}

	public void despawnKnifes() {
		for (final Entity ent : world.getEntities()) {
			if (ent.getType() == EntityType.DROPPED_ITEM) {
				if (((Item) ent).getItemStack().getType() == Items.Knife().getType()) {
					// System.out.println("Despawning Knife @" +
					// ent.getLocation());
					ent.remove();
				}
			}
		}
	}

	public MurderPlayer getMurderer() {
		return murderer;
	}

	public List<MurderPlayer> getWeaponBystanders() {
		return weaponBystanders;
	}

	public List<String> getWeaponBystanderNames() {
		final List<String> names = new ArrayList<String>();
		for (final MurderPlayer mp : getWeaponBystanders()) {
			names.add(mp.player().getName());
		}
		return names;
	}

	public List<Player> getForcedMurderers() {
		final List<Player> list = new ArrayList<Player>();
		for (final Player p : Murder.forcedMurderers) {
			if (MurderPlayer.getPlayer(p).getArena() == this) {
				list.add(p);
			}
		}
		return list;
	}

	public List<Player> getForcedWeaponBystanders() {
		final List<Player> list = new ArrayList<Player>();
		for (final Player p : Murder.forcedWeapons) {
			if (MurderPlayer.getPlayer(p).getArena() == this) {
				list.add(p);
			}
		}
		return list;
	}

	public List<Player> getMurdererBlacklist() {
		final List<Player> list = new ArrayList<Player>();
		for (final Player p : Murder.murdererBlacklist) {
			if (MurderPlayer.getPlayer(p).getArena() == this) {
				list.add(p);
			}
		}
		return list;
	}

	public List<Player> getWeaponBlacklist() {
		final List<Player> list = new ArrayList<Player>();
		for (final Player p : Murder.weaponBlacklist) {
			if (MurderPlayer.getPlayer(p).getArena() == this) {
				list.add(p);
			}
		}
		return list;
	}

	public void sendInfo(Player sender) {
		sender.sendMessage("§7---------------------------------");
		sender.sendMessage("§7>> §2Arena §a#" + getID() + "§2/§a\"" + getName() + "\"§2 §7<<");
		sender.sendMessage("§7---------------------------------");
		sender.sendMessage("§7World=§a" + getWorld().getName());
		// sender.sendMessage(toString());
		sender.sendMessage("§7Status=§a" + getStatus());
		// sender.sendMessage("§7Players=§a" + this.players);
		MurderPlayer.getPlayer(sender).sendRawMessage(generatePlayersString("Players", getPlayers()));
		MurderPlayer.getPlayer(sender).sendRawMessage(generatePlayersString("AlivePlayers", getAlivePlayers()));
		sender.sendMessage("§7PlayerAmount=§a" + getPlayerAmount());
		sender.sendMessage("§7AlivePlayerAmount=§a" + getAlivePlayerAmount());
		sender.sendMessage("§7BystanderAmount=§a" + getBystanderAmount());
		sender.sendMessage("§7Murderer=§a" + (getMurderer() != null ? getMurderer().player().getName() : ""));
		sender.sendMessage("§7WeaponBystander=§a" + (getWeaponBystanders() != null ? getWeaponBystanderNames() : ""));
		sender.sendMessage("§7---------------------------------");

	}

	private String generatePlayersString(String s, List<Player> players) {
		final StringBuilder sb = new StringBuilder();
		sb.append("{\"text\":\"\",\"extra\":[{\"text\":\"" + s + "=\",\"color\":\"gray\"}");
		for (final Player p : players) {
			if (!players.get(0).equals(p)) {
				sb.append(",{\"text\":\", \",\"color\":\"gray\"}");
			}
			sb.append(",{\"text\":\"" + p.getName() + "\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/murder playerinfo " + p.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Click here to show Information for §a" + p.getName() + "§7.\"}}");
		}
		sb.append("]}");
		return sb.toString();
	}

	// ////////////

	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		final JSONObject object = new JSONObject();
		object.put("id", id);
		object.put("name", name);
		object.put("world", world.getName());
		object.put("spawnpoints", MapToJSON(spawnpoints));
		return object.toJSONString();
	}

	@SuppressWarnings("rawtypes")
	private static String MapToJSON(Map map) {
		final StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(map, out);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final String jsonText = out.toString();
		return jsonText;
	}

	public static Arena fromString(String s) {
		Arena arena;
		JSONObject property = null;
		try {
			property = (JSONObject) new JSONParser().parse(s);
		} catch (final ParseException e) {
			e.printStackTrace();
		}

		final long id = (long) property.get("id");
		arena = new Arena((int) id);
		final String name = (String) property.get("name");
		arena.setName(name);
		final World world = Murder.instance.getServer().getWorld((String) property.get("world"));
		arena.setWorld(world);
		// final Map<SpawnType, ArrayList<Spawnpoint>> points = (Map<SpawnType,
		// ArrayList<Spawnpoint>>) property.get("spawnpoints");
		final Map<SpawnType, ArrayList<Spawnpoint>> points = StringToMap(((String) property.get("spawnpoints")).replaceAll("\\\"", "\""));
		arena.setSpawnpoints(points);

		return arena;
	}

	private static HashMap<SpawnType, ArrayList<Spawnpoint>> StringToMap(String s) {
		try {
			final JSONObject obj = (JSONObject) new JSONParser().parse(s);
			return StringToMap(obj);
		} catch (final ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static HashMap<SpawnType, ArrayList<Spawnpoint>> StringToMap(JSONObject s) {
		final HashMap<SpawnType, ArrayList<Spawnpoint>> map = new HashMap<SpawnType, ArrayList<Spawnpoint>>();

		JSONObject property = null;
		property = s;

		for (final Object o : property.entrySet()) {
			final String[] sA = o.toString().split("=");
			final List<Spawnpoint> points = new ArrayList<Spawnpoint>();
			JSONArray obj = null;
			try {
				obj = (JSONArray) new JSONParser().parse(sA[1]);
			} catch (final ParseException e) {
				e.printStackTrace();
			}
			for (final Object s1 : obj.toArray()) {
				points.add(Spawnpoint.fromString(String.valueOf(s1).replace("\\\"", "\"")));
			}
			map.put(SpawnType.valueOf(sA[0]), (ArrayList<Spawnpoint>) points);
			// HashMap obj = (HashMap) o;
			// for(Object o1 : obj.entrySet()) {
			// System.out.println(">>> " + o1);
			// }
		}

		return map;
	}

	public void reset() {
		status = ArenaStatus.WAITING;
		players.clear();
		game.stopping = false;
		game.stopped = false;
		inGame = false;
		timer = null;
		murderer = null;
		weaponBystanders.clear();
		loot.clear();
		smoke = false;
		items.clear();

		// bystanderAmount = 0;

		for (final Player p1 : getMurdererBlacklist()) {
			Murder.murdererBlacklist.remove(p1);
		}
		for (final Player p1 : getWeaponBlacklist()) {
			Murder.weaponBlacklist.remove(p1);
		}

		Bukkit.getScheduler().cancelTask(game.smokeDelay);
		Bukkit.getScheduler().cancelTask(knifeTimer);
		Bukkit.getScheduler().cancelTask(speedTimer);
		Bukkit.getScheduler().cancelTask(reloadTimer);
		Bukkit.getScheduler().cancelTask(trailTask);
		if (relTimer != null) {
			relTimer.cancel();
		}

		for (final Item i : loot) {
			i.remove();
		}
		loot.clear();

		for (final Item i : items) {
			i.remove();
		}

		items.clear();

		smoke = false;

		game.cancelAllTaks();

		setMurderer(null);
		despawnAllArrows();
		despawnAllItems();
		despawnKnifes();

		worldLogger.resetModifiedBlocks();

		status = ArenaStatus.WAITING;

	}

}
