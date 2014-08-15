package de.inventivegames.murder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.metrics.Metrics;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.inventivegames.murder.bungeecord.BungeeCordHandler;
import de.inventivegames.murder.commands.CommandHandler;
import de.inventivegames.murder.listeners.PlayerListener;
import de.inventivegames.murder.listeners.WorldListener;
import de.inventivegames.murder.loggers.KillLogger;
import de.inventivegames.utils.IGUtils;
import de.inventivegames.utils.fakeequipment.FakeEquipment;
import de.inventivegames.utils.skin.PlayerDisplayModifier;

public class Murder extends JavaPlugin implements Listener {

	public static String				permBase					= "murder.";
	public static String				prefix						= "§1[§4Murder§1] ";
	public static String				debugPrefix					= "§1[§4Murder§7|§aDEBUG§1]§r ";
	public static ConsoleCommandSender	console						= Bukkit.getServer().getConsoleSender();
	public static Murder				instance;
	static File							configFile					= new File("plugins/Murder/config.yml");
	static File							playerFile					= new File("plugins/Murder/players.yml");
	public static Random				rd							= new Random();
	public static String				serverVersion;
	public static String[]				nameTags					= { "Alfa ", "Bravo ", "Charlie ", "Delta ", "Echo ", "Foxtrot ", "Golf ", "Hotel ", "India ", "Juliett ", "Kilo ", "Lima ", "Miko ", "November ", "Oscar ", "Papa ", "Quebec ", "Romeo ", "Sierra ", "Tango ", "Uniform ", "Victor ", "Whiskey ", "X-ray ", "Yankee ", "Zulu " };
	public static String[]				colorCode					= { "§1 ", "§2 ", "§3 ", "§4 ", "§5 ", "§6 ", "§7 ", "§8 ", "§9 ", "§a ", "§b ", "§c ", "§d ", "§e " };
	public static String				SKIN_NAME					= "Janiboy554";
	public static String				CORPSE_SKIN_NAME			= "Janiboy554";
	public static int					minPlayers					= -1;
	public static int					maxPlayers					= -1;
	public static int					smokeTimer					= -1;
	public static int					lobbyCountdown				= -1;
	public static int					countdown					= -1;

	// BungeeCordSettings
	public static boolean				bungeeCord					= false;
	public static String				bungeeHub					= "lobby";
	public static int					bungeeArena					= 1;

	public static int					POINTS_PLUS					= 2;																																																																				// MurdererKill
	public static int					POINTS_MINUS				= 20;																																																																				// KilledInnocent
	public static int					POINTS_MURDERER_WIN			= 40;																																																																				// MurdererWon
	public static int					POINTS_BYSTANDER_WIN_WEAPON	= 40;																																																																				// KilledMurderer
	public static int					POINTS_BYSTANDER_WIN		= 2;																																																																				// BystandersWon

	public static WorldEditPlugin		worldEdit;

	public static CommandHandler		cmdHandler;

	public static PlayerDisplayModifier	mod;
	public static ProtocolManager		manager;

	public static IGUtils				utils;

	public static ArrayList<Game>		games						= new ArrayList<Game>();

	public static ArrayList<Player>		murdererBlacklist			= new ArrayList<Player>();
	public static ArrayList<Player>		forcedMurderers				= new ArrayList<Player>();

	public static ArrayList<Player>		weaponBlacklist				= new ArrayList<Player>();
	public static ArrayList<Player>		forcedWeapons				= new ArrayList<Player>();

	@Override
	public void onEnable() {
		instance = this;

		cmdHandler = new CommandHandler();
		registerEvents(instance, new PlayerListener(), new WorldListener(), new Signs(), new Spectate(), new Corpses(), new ChatManager(), new KillLogger(), cmdHandler);
		console = instance.getServer().getConsoleSender();

		if (instance.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			console.sendMessage(debugPrefix + "§2Successfully hooked into ProtocolLib!");
		} else {
			console.sendMessage(debugPrefix + "§cCould not hook into ProtocolLib! Please download it here:§a http://dev.bukkit.org/bukkit-plugins/protocollib/");
			console.sendMessage("§cDisabling...");
			instance.getServer().getPluginManager().disablePlugin(instance);
			return;
		}
		// if
		// (instance.getServer().getPluginManager().isPluginEnabled("WorldEdit"))
		// {
		// console.sendMessage(prefix +
		// "§2Successfully hooked into WorldEdit!");
		// worldEdit = (WorldEditPlugin)
		// instance.getServer().getPluginManager().getPlugin("WorldEdit");
		// } else {
		// console.sendMessage(prefix +
		// "§cCould not hook into WorldEdit! Please download it here:§a http://dev.bukkit.org/bukkit-plugins/worldedit/");
		// console.sendMessage("§cDisabling...");
		// instance.getServer().getPluginManager().disablePlugin(instance);
		// return;
		// }

		manager = ProtocolLibrary.getProtocolManager();

		instance.getCommand("murder").setExecutor(cmdHandler);

		Messages.Manager();
		Config.Manager();
		initPlayerConfig();
		initMetrics();

		minPlayers = instance.getConfig().getInt("MinPlayers");
		maxPlayers = instance.getConfig().getInt("MaxPlayers");
		smokeTimer = instance.getConfig().getInt("SmokeDelay");

		lobbyCountdown = instance.getConfig().getInt("lobbyCountdown");
		countdown = instance.getConfig().getInt("countdown");

		bungeeCord = instance.getConfig().getBoolean("useBungeeCord");
		bungeeHub = instance.getConfig().getString("BungeeCordHubName");
		bungeeArena = instance.getConfig().getInt("BungeeCordArena");

		utils = new IGUtils(instance);

		serverVersion = instance.getServer().getBukkitVersion().toString();
		// if (serverVersion != null && serverVersion.contains("1.7.9")) {
		// Corpses.oldSpawns = true;
		// console.sendMessage(prefix + "§cIncompatible Server Version (" +
		// serverVersion + ")! Using old Corpse spawns...");
		// }
		if (bungeeCord) {
			Bukkit.getServer().getPluginManager().registerEvents(new BungeeCordHandler(), instance);
		}

		if (instance.getConfig().getBoolean("forceNewCorpses")) {
			Corpses.oldSpawns = false;
		}

		ArenaManager.loadArenas();

		initProtocolListener();

		mod = new PlayerDisplayModifier(instance, manager);
	}

	private static void initMetrics() {
		try {
			final Metrics metrics = new Metrics(instance);
			metrics.start();
		} catch (final IOException e) {
			// Failed to submit the stats :-(
		}
	}

	public static void initPlayerConfig() {
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		config.options().copyDefaults(true);
		config.addDefault("Players", null);
		try {
			config.save(playerFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void initProtocolListener() {
		new FakeEquipment(instance) {
			@Override
			protected boolean onEquipmentSending(FakeEquipment.EquipmentSendingEvent equipmentEvent) {
				if (equipmentEvent.getSlot() == FakeEquipment.EquipmentSlot.HELD) {
					if (equipmentEvent.getEquipment() != null && equipmentEvent.getEquipment().getType() != Material.AIR) {
						if (equipmentEvent.getEquipment().equals(Items.SpeedBoost()) || equipmentEvent.getEquipment().equals(Items.Bullet()) || equipmentEvent.getEquipment().getType().equals(Material.NAME_TAG)) {
							equipmentEvent.setEquipment(new ItemStack(Material.AIR));
							return true;
						}
					}
				}
				return false;
			}

			@Override
			protected void onEntitySpawn(Player client, LivingEntity visibleEntity) {
				if (FakeEquipment.EquipmentSlot.HELD.isEmpty(visibleEntity)) {
					updateSlot(client, visibleEntity, FakeEquipment.EquipmentSlot.HELD);
				}
			}
		};
		manager.addPacketListener(new PacketAdapter(instance, new PacketType[] { PacketType.Play.Server.ENTITY_METADATA }) {
			@Override
			public void onPacketSending(PacketEvent event) {
				final Player p = event.getPlayer();
				final MurderPlayer mp = MurderPlayer.getPlayer(p);
				final Entity entity = event.getPacket().getEntityModifier(event).read(0);
				if (event.getPlayer().equals(entity)) {
					if (mp.playing()) {
						Murder.modifyWatchable(event, 7, 0);
					}
				}
			}
		});
		manager.addPacketListener(new PacketAdapter(Murder.instance, new PacketType[] { PacketType.Play.Server.NAMED_SOUND_EFFECT }) {
			@Override
			public void onPacketSending(PacketEvent event) {
				final PacketContainer packet = event.getPacket();
				final World world = event.getPlayer().getWorld();
				if (!MurderPlayer.getPlayer(event.getPlayer()).inSpectate()) return;

				final String soundName = packet.getStrings().read(0);
				final double x = packet.getIntegers().read(0) / 8.0;
				final double y = packet.getIntegers().read(1) / 8.0;
				final double z = packet.getIntegers().read(2) / 8.0;
				final Location loc = new Location(world, x, y, z);

				if (soundName.startsWith("step.")) {
					Player closest = null;
					double bestDistance = Double.MAX_VALUE;

					// Find the player closest to the sound
					for (final Player player : world.getPlayers()) {
						final double distance = player.getLocation().distance(loc);

						if (distance < bestDistance) {
							bestDistance = distance;
							closest = player;
						}
					}

					// System.out.println("Cancelled " + soundName +
					// " caused by " + closest);
					if (MurderPlayer.getPlayer(closest).inSpectate() && MurderPlayer.getPlayer(event.getPlayer()).inSpectate()) {
						event.setCancelled(true);
					}
				}
			}
		});

	}

	private static void modifyWatchable(PacketEvent event, int index, Object value) {
		if (hasIndex(getWatchable(event), index)) {
			event.setPacket(event.getPacket().deepClone());
			for (final WrappedWatchableObject object : getWatchable(event)) {
				if (object.getIndex() == index) {
					object.setValue(value);
				}
			}
		}
	}

	private static List<WrappedWatchableObject> getWatchable(PacketEvent event) {
		return event.getPacket().getWatchableCollectionModifier().read(0);
	}

	private static boolean hasIndex(List<WrappedWatchableObject> list, int index) {
		for (final WrappedWatchableObject object : list) {
			if (object.getIndex() == index) return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(Murder.instance);
		ArenaManager.saveArenas();
	}

	public static void reload() {
		ArenaManager.saveArenas();

		ArenaManager.unloadArenas();

		Messages.Manager();
		Config.Manager();

		minPlayers = instance.getConfig().getInt("MinPlayers");
		maxPlayers = instance.getConfig().getInt("MaxPlayers");
		smokeTimer = instance.getConfig().getInt("SmokeDelay");

		lobbyCountdown = instance.getConfig().getInt("lobbyCountdown");
		countdown = instance.getConfig().getInt("countdown");

		Bukkit.getScheduler().cancelTasks(Murder.instance);

		ArenaManager.loadArenas();
	}

	public static void addName(String name) {
		if (name.length() + 2 > 16) return;

		final int length = nameTags.length;
		final String[] New = new String[length + 1];
		for (int i = 0; i < length; i++) {
			New[i] = nameTags[i];
		}
		nameTags = null;
		New[length] = name;
		nameTags = new String[length + 1];
		for (int i = 0; i < length + 1; i++) {
			nameTags[i] = New[i];
		}
	}

	public static void registerEvents(Plugin plugin, Listener... listeners) {
		for (final Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
}
