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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class Murder extends JavaPlugin implements Listener {

	public static String						prefix					= "§1[§4Murder§1] ";
	public static ConsoleCommandSender			console					= Bukkit.getServer().getConsoleSender();
	public static Murder						instance;
	static File									configFile				= new File("plugins/Murder/config.yml");

	public static List<String>					playersInLobby			= new ArrayList<String>();
	public static List<String>					playersInGame			= new ArrayList<String>();
	public static List<String>					playersInSpectate		= new ArrayList<String>();
	public static List<Integer>					gameStarted				= new ArrayList<Integer>();
	public static List<Integer>					peacePeriod				= new ArrayList<Integer>();

	public static ArrayList<String>				Murderers				= new ArrayList<String>();
	public static ArrayList<String>				Bystanders				= new ArrayList<String>();

	public static HashMap<String, ItemStack[]>	InventoryContent		= new HashMap<String, ItemStack[]>();
	public static HashMap<String, ItemStack[]>	InventoryArmorContent	= new HashMap<String, ItemStack[]>();
	public static HashMap<String, Location>		prevLocation			= new HashMap<String, Location>();
	public static HashMap<String, GameMode>		prevGamemode			= new HashMap<String, GameMode>();
	public static HashMap<String, Integer>		prevLevel				= new HashMap<String, Integer>();
	public static HashMap<String, Float>		prevExp					= new HashMap<String, Float>();
	public static HashMap<String, Double>		prevHealth				= new HashMap<String, Double>();
	public static HashMap<String, Integer>		prevFood				= new HashMap<String, Integer>();

	public static HashMap<String, Integer>		playerInLobby			= new HashMap<String, Integer>();
	public static HashMap<String, Integer>		playerInGame			= new HashMap<String, Integer>();
	public static HashMap<String, Integer>		playerInSpectate		= new HashMap<String, Integer>();
	public static HashMap<Integer, Integer>		playerAmount			= new HashMap<Integer, Integer>();
	public static HashMap<String, Integer>		playerType				= new HashMap<String, Integer>();

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

	public static int							lobbyCountdown			= -1;
	public static int							countdown				= -1;

	private static File							arenaFile;

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

		Messages.Manager();

		Murder.minPlayers = instance.getConfig().getInt("MinPlayers");
		Murder.maxPlayers = instance.getConfig().getInt("MaxPlayers");

		Murder.lobbyCountdown = instance.getConfig().getInt("lobbyCountdown");
		Murder.countdown = instance.getConfig().getInt("countdown");

		Bukkit.getServer().getPluginManager().registerEvents(instance, instance);
		instance.getCommand("murder").setExecutor(new Commands());

		Config.Manager();
		registerEvents(this, new Players());
		registerEvents(this, new Signs());
		registerEvents(this, new Commands());

		Murder.zombieMap = new HashMap<Player, Zombie>();

		setupMetrics();
		

		serverVersion = instance.getServer().getBukkitVersion().toString();

		console = Bukkit.getServer().getConsoleSender();

		for (int i = 0; i < 24; i++) {
			Game.BystanderSelected[i] = false;
			Game.MurdererSelected[i] = false;
			Game.rolesSelected[i] = false;
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



	public static void sendArenaMessage(String message, int arena) {
		for (int i = 0; i < maxPlayers; i++) {

			if (players[arena][i] != null) {
				players[arena][i].sendMessage(message);
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

	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}

}
