package de.inventivegames.Murder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.comphenix.protocol.ProtocolLibrary;

import de.inventivegames.Murder.Updater.UpdateResult;
import de.inventivegames.Murder.BungeeCord.BungeeListener;

public class Murder extends JavaPlugin implements Listener {

	public static String						prefix					= "§1[§4Murder§1] ";
	public static ConsoleCommandSender			console					= Bukkit.getServer().getConsoleSender();
	public static Murder						instance;
	static File									configFile				= new File("plugins/Murder/config.yml");

	public static List<Player>					playersInLobby			= new ArrayList<Player>();
	public static List<Player>					playersInGame			= new ArrayList<Player>();
	public static List<Player>					playersInSpectate		= new ArrayList<Player>();
	public static List<Integer>					gameStarted				= new ArrayList<Integer>();
	public static List<Integer>					peacePeriod				= new ArrayList<Integer>();

	public static ArrayList<Player>				Murderers				= new ArrayList<Player>();
	public static ArrayList<Player>				Bystanders				= new ArrayList<Player>();

	public static HashMap<Player, ItemStack[]>	InventoryContent		= new HashMap<Player, ItemStack[]>();
	public static HashMap<Player, ItemStack[]>	InventoryArmorContent	= new HashMap<Player, ItemStack[]>();
	public static HashMap<Player, Location>		prevLocation			= new HashMap<Player, Location>();
	public static HashMap<Player, GameMode>		prevGamemode			= new HashMap<Player, GameMode>();
	public static HashMap<Player, Integer>		prevLevel				= new HashMap<Player, Integer>();
	public static HashMap<Player, Float>		prevExp					= new HashMap<Player, Float>();
	public static HashMap<Player, Double>		prevHealth				= new HashMap<Player, Double>();
	public static HashMap<Player, Integer>		prevFood				= new HashMap<Player, Integer>();

	public static HashMap<Player, Integer>		playerInLobby			= new HashMap<Player, Integer>();
	public static HashMap<Player, Integer>		playerInGame			= new HashMap<Player, Integer>();
	public static HashMap<Player, Integer>		playerInSpectate		= new HashMap<Player, Integer>();
	public static HashMap<Integer, Integer>		playerAmount			= new HashMap<Integer, Integer>();
	public static HashMap<Player, Integer>		playerType				= new HashMap<Player, Integer>();

	public static HashMap<Player, Zombie>		zombieMap;

	public static HashMap<Player, String>		nameTag					= new HashMap<Player, String>();

	public static ArrayList<Player>				invisibleTags			= new ArrayList<Player>();

	public static ArrayList<Player>				hasTag					= new ArrayList<Player>();

	public static String						serverVersion;

	public static String[]						nameTags				= { "Alfa ", "Bravo ", "Charlie ", "Delta ", "Echo ", "Foxtrot ", "Golf ", "Hotel ", "India ", "Juliett ", "Kilo ", "Lima ", "Miko ", "November ", "Oscar ", "Papa ", "Quebec ", "Romeo ", "Sierra ", "Tango ", "Uniform ", "Victor ", "Whiskey ", "X-ray ", "Yankee ", "Zulu " };

	public static String[]						colorCode				= { "§1 ", "§2 ", "§3 ", "§4 ", "§5 ", "§6 ", "§7 ", "§8 ", "§9 ", "§a ", "§b ", "§c ", "§d ", "§e " };

	public static ArrayList<String>				inGame					= new ArrayList<String>();

	public static int[]							playersAmount			= new int[25];

	public static Player[][]					players					= new Player[25][25];
	public static Player[][]					murderers				= new Player[25][25];
	public static Player[][]					bystanders				= new Player[25][25];
	public static Player[][]					weaponBystanders		= new Player[25][25];

	public static int[]							bystanderAmount			= new int[29];
	public static Item[][]						Loot					= new Item[29][99];
	public static Item[][]						Items					= new Item[29][99];
	public static int[]							ItemAmount				= new int[29];

	public static Random						rd						= new Random();

	public static int							minPlayers				= -1;
	public static int							maxPlayers				= -1;
	public static int							smokeTimer				= -1;

	public static int							lobbyCountdown			= -1;
	public static int							countdown				= -1;

	private static File							arenaFile;

	public static Updater						updater;
	public static boolean						updateNeeded;

	private static Class<?>						nmsChatSerializer		= Reflection.getNMSClass("ChatSerializer");
	private static Class<?>						nmsPacketPlayOutChat	= Reflection.getNMSClass("PacketPlayOutChat");

	public void onEnable() {
		instance = this;

		console = Bukkit.getServer().getConsoleSender();

		if (instance.getServer().getPluginManager().isPluginEnabled("TagAPI")) {
			console.sendMessage(prefix + "§2Successfully hooked into TagAPI!");
		} else {
			console.sendMessage(prefix + "§cCould not hook into TagAPI! Please download it here:§a http://dev.bukkit.org/bukkit-plugins/tag/");
			console.sendMessage("§cDisabling...");
			instance.getServer().getPluginManager().disablePlugin(instance);
			return;
		}

		if (instance.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			console.sendMessage(prefix + "§2Successfully hooked into ProtocolLib!");
		} else {
			console.sendMessage(prefix + "§cCould not hook into ProtocolLib! Please download it here:§a http://dev.bukkit.org/bukkit-plugins/protocollib/");
			console.sendMessage("§cDisabling...");
			instance.getServer().getPluginManager().disablePlugin(instance);
			return;
		}

		Messages.Manager();

		Murder.minPlayers = instance.getConfig().getInt("MinPlayers");
		Murder.maxPlayers = instance.getConfig().getInt("MaxPlayers");
		Murder.smokeTimer = instance.getConfig().getInt("SmokeDelay");

		Murder.lobbyCountdown = instance.getConfig().getInt("lobbyCountdown");
		Murder.countdown = instance.getConfig().getInt("countdown");

		Bukkit.getServer().getPluginManager().registerEvents(instance, instance);
		instance.getCommand("murder").setExecutor(new Commands());

		Corpses.manager = ProtocolLibrary.getProtocolManager();

		Config.Manager();
		registerEvents(this, new Players());
		registerEvents(this, new Signs());
		registerEvents(this, new Commands());
		registerEvents(this, new Chat());
		registerEvents(this, new Corpses());
		
		if(instance.getConfig().getBoolean("useBungeeCord")) {
			registerEvents(this, new BungeeListener());
		}

		Murder.zombieMap = new HashMap<Player, Zombie>();

		setupMetrics();

		serverVersion = instance.getServer().getBukkitVersion().toString();

			if((serverVersion != null) && (serverVersion.contains("1.7.9"))) {
				Corpses.oldSpawns = true;
				console.sendMessage(Murder.prefix + "§cIncompatible Server Version (" + serverVersion + ")! Using old Corpse spawns...");
			}


		for (int i = 0; i < 24; i++) {
			Game.BystanderSelected[i] = false;
			Game.MurdererSelected[i] = false;
			Game.rolesSelected[i] = false;
		}

		if (getConfig().getBoolean("checkForUpdates")) {
			updater = new Updater(this, 72593, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				updateNeeded = true;
			}
		}

	}

	public void onDisable() {
		if (zombieMap != null) {
			for (Zombie controlledZombie : Murder.zombieMap.values()) {
				controlledZombie.remove();
			}
			Murder.zombieMap.clear();
			Murder.zombieMap = null;
		}

		players = null;
		playerAmount.clear();
		playerInGame.clear();
		playerInLobby.clear();
		playerInSpectate.clear();
		playersAmount = null;
		playersInGame.clear();
		playersInLobby.clear();
		playersInSpectate.clear();
		bystanderAmount = null;
		Bystanders.clear();
		bystanders = null;
		gameStarted.clear();
		hasTag.clear();
		inGame.clear();
		invisibleTags.clear();
		Loot = null;
		Murderers.clear();
		murderers = null;
		playerType.clear();
		weaponBystanders = null;

		instance.getServer().getPluginManager().disablePlugin(instance);

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (updateNeeded) {
			if (e.getPlayer().isOp()) {
				String message = "[{\"text\":\"A new Version is Available. Download it here: \",\"color\":\"white\"},{\"text\":\"[" + updater.getLatestName() + "]\",\"color\":\"gold\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + updater.getLatestFileLink() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Click here to download the Latest Version.\"}}]";
				sendRawMessage(e.getPlayer(), message);
			}
		}
	}

	public static void sendArenaMessage(String message, int arena) {
		for (int i = 0; i < maxPlayers; i++) {

			if (players[arena][i] != null) {
				players[arena][i].sendMessage(message);
			}
		}
	}

	public static void sendSpectatorMessage(String message, int arena) {
		for (Player spec : Murder.playersInSpectate) {
			if (Murder.getArena(spec) == arena) {
				spec.sendMessage(message);
			}
		}
	}

	public static void sendArenaAdminMessage(String message, int arena) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[arena][i] != null) {
				if (players[arena][i].hasPermission("murder.admin")) {
					players[arena][i].sendMessage(message);
				}
			}
		}
	}

	public static int getArena(Player p) {
		int arena = 0;

		for (int i = 0; i < 25; i++) {
			for (int m = 0; m < 25; m++) {
				if ((players[i][m] != null) && (players[i][m] == p)) {
					arena = i;
				}
			}
		}

		return arena;
	}

	public static int getPlayerNumber(Player p, int arena) {
		for (int i = 0; i < 25; i++) {
			for (int m = 0; m < 25; m++) {
				if ((players[i][m] != null) && (players[i][m] == p)) {
					return m;
				}
			}
		}
		return 0;
	}

	public static Player getMurderer(int arena) {
		int i = arena;
		{
			for (int m = 0; m < 25; m++) {
				if ((players[i][m] != null) && (players[i][m] == murderers[i][m])) {
					return murderers[i][m];
				}
			}
		}
		return null;
	}

	public static boolean isMurderer(Player p) {
		int i = Murder.getArena(p);
		int m = getPlayerNumber(p, Murder.getArena(p));

		if ((players[i][m] != null) && (players[i][m] == murderers[i][m])) {
			return true;
		}

		return false;
	}

	public static boolean isBystander(Player p) {
		int i = Murder.getArena(p);
		int m = getPlayerNumber(p, Murder.getArena(p));

		if ((players[i][m] != null) && (players[i][m] == bystanders[i][m])) {
			return true;
		}

		return false;
	}

	public static boolean isWeaponBystander(Player p) {
		int i = Murder.getArena(p);
		int m = getPlayerNumber(p, Murder.getArena(p));

		if ((players[i][m] != null) && (players[i][m] == weaponBystanders[i][m])) {
			return true;
		}

		return false;
	}

	public static boolean isSpectator(Player p) {
		if (Murder.playersInSpectate.contains(p)) {
			return true;
		}
		return false;
	}

	public static Player getPlayerName(int arena, int m) {
		if ((players[arena][m] != null)) {
			return players[arena][m];
		}
		return null;
	}

	public static boolean ArenaExists(int arena) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		if (arenaFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getStatus(int arena)

	{
		if ((playersAmount[arena] < maxPlayers) && (playersAmount[arena] >= 0) && (!inGame.contains(Integer.valueOf(arena)))) {
			return "§2" + playersAmount[arena] + " / " + (maxPlayers);
		}
		if (playersAmount[arena] == maxPlayers) {
			return "§c§l[Full]";
		}
		if (inGame.contains("" + arena)) {
			return "§c§l[InGame]";
		}
		return "§4[ERROR]";
	}

	public static String getNameTag(Player p) {
		String tag = nameTag.get(p);
		return tag;
	}

	public static ItemStack Knife() {
		ItemStack Knife = new ItemStack(Material.IRON_SWORD);
		ItemMeta knifeMeta = Knife.getItemMeta();
		knifeMeta.setDisplayName("§c§l" + Messages.getMessage("knife"));
		knifeMeta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
		Knife.setItemMeta(knifeMeta);

		return Knife;

	}

	public static ItemStack Gun() {
		ItemStack Gun = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta gunMeta = Gun.getItemMeta();
		gunMeta.setDisplayName("§1§l" + Messages.getMessage("gun"));
		gunMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		Gun.setItemMeta(gunMeta);

		return Gun;

	}

	public static ItemStack Bullet() {
		ItemStack Bullet = new ItemStack(Material.ARROW);
		ItemMeta bulletMeta = Bullet.getItemMeta();
		bulletMeta.setDisplayName("§8" + Messages.getMessage("bullet"));
		Bullet.setItemMeta(bulletMeta);

		return Bullet;

	}

	public static ItemStack Loot() {
		ItemStack Loot = new ItemStack(Material.DIAMOND);
		ItemMeta lootMeta = Loot.getItemMeta();
		lootMeta.setDisplayName("§6" + Messages.getMessage("loot"));
		lootMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		Loot.setItemMeta(lootMeta);

		return Loot;

	}

	public static ItemStack NameInfo(Player p) {
		ItemStack Loot = new ItemStack(Material.NAME_TAG);
		ItemMeta lootMeta = Loot.getItemMeta();
		lootMeta.setDisplayName("§l" + (nameTag.get(p) != null ? nameTag.get(p) : "§r§cUnable to get NameTag!"));
		lootMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		Loot.setItemMeta(lootMeta);

		return Loot;

	}

	public static void sendRawMessage(Player player, String message) {
		try {
			Object handle = Reflection.getHandle(player);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			Object serialized = Reflection.getMethod(nmsChatSerializer, "a", String.class).invoke(null, message);
			Object packet = nmsPacketPlayOutChat.getConstructor(Reflection.getNMSClass("IChatBaseComponent")).newInstance(serialized);
			Reflection.getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Server getBukkit() {
		return Murder.instance.getServer();
	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

	public static void DEBUG() {
		System.out.println("" + "\n" + Murder.maxPlayers + "\n" + Murder.minPlayers + "\n" + Murder.prefix + "\n" + Murder.serverVersion + "\n" + Murder.arenaFile + "\n" + Murder.bystanderAmount + "\n" + Murder.Bystanders + "\n" + Murder.bystanders + "\n" + Murder.gameStarted + "\n" + Murder.hasTag + "\n" + Murder.inGame + "\n" + Murder.InventoryArmorContent + "\n" + Murder.InventoryContent + "\n" + Murder.invisibleTags + "\n" + Murder.Murderers + "\n" + Murder.nameTag + "\n" + Murder.playersInGame + "\n" + Murder.zombieMap + "\n" + "" + "\n" + Players.knifeTimer + "\n" + Players.murdererDisguise + "\n");
	}

	public static void PlayerDEBUG(Player p) {
		System.out.println(p.getName());
		System.out.println("murder.players -- " + (p.hasPermission("murder.player") ? "true" : "false"));
		System.out.println("murder.admin -- " + (p.hasPermission("murder.admin") ? "true" : "false"));
	}

	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}

}
