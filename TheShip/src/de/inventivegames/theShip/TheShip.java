package de.inventivegames.theShip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class TheShip extends JavaPlugin implements Listener {

	public static String						prefix					= "§1[§aThe§bShip§1] ";
	public static String						SBTitle					= "§aThe§bShip";
	public static ConsoleCommandSender			console					= Bukkit.getServer().getConsoleSender();
	public static TheShip						instance;
	static File									configFile				= new File("plugins/TheShip/config.yml");
	public static File							nameFile				= new File("plugins/TheShip/names.yml");
	public static File							filePath				= new File("plugins/TheShip/");

	public static int[]							playersAmount			= new int[25];

	public static HashMap<Player, ItemStack[]>	InventoryContent		= new HashMap<Player, ItemStack[]>();
	public static HashMap<Player, ItemStack[]>	InventoryArmorContent	= new HashMap<Player, ItemStack[]>();
	public static HashMap<Player, Location>		prevLocation			= new HashMap<Player, Location>();
	public static HashMap<Player, GameMode>		prevGamemode			= new HashMap<Player, GameMode>();
	public static HashMap<Player, Integer>		prevLevel				= new HashMap<Player, Integer>();
	public static HashMap<Player, Float>		prevExp					= new HashMap<Player, Float>();
	public static HashMap<Player, Double>		prevHealth				= new HashMap<Player, Double>();
	public static HashMap<Player, Integer>		prevFood				= new HashMap<Player, Integer>();

	public static HashMap<Player, String>		nameTag					= new HashMap<Player, String>();

	public static ArrayList<Player>				receiveTag				= new ArrayList<Player>();

	public static ArrayList<Player>				hasTag					= new ArrayList<Player>();

	public static String						serverVersion;

	public static String[]						firstNames;
	public static String[]						lastNames;
	
	public static int 							gameTimer				= -1;

	public static int							lobbyCountdown			= -1;
	public static int							countdown				= -1;

	public static Player[][]					players					= new Player[25][25];

	public static Random						rd						= new Random();

	public static int							minPlayers				= -1;
	public static int							maxPlayers				= -1;

	public static ArrayList<String>				inGame					= new ArrayList<String>();

	private static Class<?>						nmsChatSerializer		= Reflection.getNMSClass("ChatSerializer");
	private static Class<?>						nmsPacketPlayOutChat	= Reflection.getNMSClass("PacketPlayOutChat");
	
	public static ArrayList<String> spawnTypes = new ArrayList<String>(Arrays.asList("lobby", "players", "prison", "prisonOut"));

	public void onEnable() {
		instance = this;

		console = Bukkit.getServer().getConsoleSender();

		instance.getCommand("theship").setExecutor(new CommandHandler());

		TheShip.minPlayers = instance.getConfig().getInt("MinPlayers");
		TheShip.maxPlayers = instance.getConfig().getInt("MaxPlayers");

		TheShip.lobbyCountdown = instance.getConfig().getInt("lobbyCountdown");
		TheShip.countdown = instance.getConfig().getInt("countdown");
		
		TheShip.gameTimer = instance.getConfig().getInt("GameDuration") * 20;

		registerEvents(instance, new Players());
		registerEvents(instance, new Signs());
		registerEvents(instance, new Death());
		registerEvents(instance, new Security());

		setup();
	}

	public static void setup() {
		Config.Manager();
		Weapons.generateConfig();
		Weapons.setupRewards();
		
		setupMetrics();
		// setupNames();
	}

	// public static void setupNames() {
	// YamlConfiguration NameFile = null;
	// if(!(nameFile.exists())) {
	// try {
	// nameFile.createNewFile();
	// NameFile = YamlConfiguration.loadConfiguration(nameFile);
	//
	// NameFile.createSection("first");
	// NameFile.createSection("last");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// List<String> first = (List<String>) NameFile.getList("first");
	// List<String> last = (List<String>) NameFile.getList("last");
	//
	// firstNames = new String[first.size()];
	// lastNames = new String[last.size()];
	//
	// for(int i = 0; i < first.size(); i++) {
	// if(first.get(i) != null) {
	// firstNames[i] = first.get(i);
	// }
	// }
	//
	// for(int i = 0; i < last.size(); i++) {
	// if(last.get(i) != null) {
	// lastNames[i] = last.get(i);
	// }
	// }
	// }

	public static void assignNewQuarry(ShipPlayer sp) {

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

	public static void sendArenaMessage(String message, int arena) {
		for (int i = 0; i < maxPlayers; i++) {

			if (players[arena][i] != null) {
				players[arena][i].sendMessage(message);
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

	public static void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(TheShip.instance);
			metrics.start();
		} catch (IOException e) {
		}
	}

	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
}
