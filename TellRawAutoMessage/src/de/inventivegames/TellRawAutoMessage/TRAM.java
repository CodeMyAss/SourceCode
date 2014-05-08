package de.inventivegames.TellRawAutoMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public final class TRAM extends JavaPlugin implements Listener {

	public final Logger		logger					= Logger.getLogger("Minecraft");
	public static int		currentline				= 0;
	public static int		currentlineVIP			= 0;
	public static int		tid						= 0;
	public static int		running					= 1;
	public static long		interval				= 0;
	ConsoleCommandSender	console					= Bukkit.getConsoleSender();
	public static TRAM		instance;
	File					configFile				= new File(this.getDataFolder(), "config.yml");
	public static String	serverVersion;

	private static Class<?>	nmsChatSerializer		= Reflection.getNMSClass("ChatSerializer");
	private static Class<?>	nmsPacketPlayOutChat	= Reflection.getNMSClass("PacketPlayOutChat");



	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelAllTasks();
	}

	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		instance = this;

		console = Bukkit.getConsoleSender();

		console.sendMessage(ChatColor.GRAY + "====Enabling TellRawAutoMessage====");
		console.sendMessage(ChatColor.GRAY + "=======www.InventiveGames.de=======");

		serverVersion = instance.getServer().getBukkitVersion().toString().substring(0, 5);

		if (!(configFile.exists())) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}

		File msgFile = new File(getDataFolder() + File.separator + "messages.txt");
		File VIPmsgFile = new File(getDataFolder() + File.separator + "VIPmessages.txt");
		if (!(msgFile.exists())) {
			try {
				msgFile.createNewFile();
				new File("messages.txt");
				VIPmsgFile.createNewFile();
				new File("VIPmessages.txt");
			} catch (Exception e) {
				console.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "TRAM" + ChatColor.GRAY + "] " + ChatColor.RED + "Cant create Message File!");
				if (getConfig().getBoolean("debug")) {
					e.printStackTrace();
				}
			}
		}

		setupMetrics();

		if (getConfig().getBoolean("checkForUpdates")) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, 72192, this.getFile(), Updater.UpdateType.DEFAULT, false);
		}

		interval = getConfig().getLong("Interval");

		tid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			File	msgFile	= new File(getDataFolder() + File.separator + "messages.txt");

			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().length > getConfig().getInt("MinPlayers") - 1) {
					if (msgFile.length() != 0) {

						try {
							broadcastMessage("plugins/TellRawAutoMessage/messages.txt");
							broadcastVIPMessage("plugins/TellRawAutoMessage/VIPmessages.txt");
						} catch (Exception e) {

							console.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "TRAM" + ChatColor.GRAY + "] Config is in the wrong Format! Please remove the " + ChatColor.DARK_GRAY + "tellraw @a" + ChatColor.GRAY + " part");

							if (getConfig().getBoolean("debug")) {
								e.printStackTrace();
							}
						}
					} else if (getConfig().getBoolean("debug")) {
						console.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "TRAM" + ChatColor.GRAY + "] Message File is empty! cant Broadcast Message.");
					}

				}
			}

		}, 0, interval * 20);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getLabel().equalsIgnoreCase("tram")) {
			if (args.length <= 2) {
				sender.sendMessage("§cUsage: /tram broadcast <message/vip> <line>");
				return true;
			}
			if (args[0].equalsIgnoreCase("broadcast")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (p.hasPermission("tram.broadcast")) {
						if (args[1].equalsIgnoreCase("message")) {
							try {
								broadcastSpecificMessage("plugins/TellRawAutoMessage/messages.txt", Integer.valueOf(args[2]) - 1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (args[1].equalsIgnoreCase("vip")) {
							try {
								broadcastSpecificMessage("plugins/TellRawAutoMessage/VIPmessages.txt", Integer.valueOf(args[2]) - 1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else
							sender.sendMessage("§cUsage: /tram broadcast <message/vip> <line>");
					} else
						p.sendMessage("§cYou don't have permission to execute this Command!");
				} else {
					if (args[1].equalsIgnoreCase("message")) {
						try {
							broadcastSpecificMessage("plugins/TellRawAutoMessage/messages.txt", Integer.valueOf(args[2]) - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (args[1].equalsIgnoreCase("vip")) {
						try {
							broadcastSpecificMessage("plugins/TellRawAutoMessage/VIPmessages.txt", Integer.valueOf(args[2]) - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else
						sender.sendMessage("§cUsage: /tram broadcast <message/vip> <line>");
				}
			} else
				sender.sendMessage("§cUsage: /tram broadcast <message/vip> <line>");
		}
		return super.onCommand(sender, command, label, args);
	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

	@SuppressWarnings("resource")
	public static void broadcastMessage(String filename) throws IOException {

		FileInputStream fs;
		fs = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for (int i = 0; i < currentline; ++i)
			br.readLine();
		String message = br.readLine();
		if (message != null) {

			if (message.endsWith("%next%")) {
				String message1 = message.substring(0, message.length() - 6);
				for (Player player : Bukkit.getOnlinePlayers()) {
					sendRawMessage(player, message1);

				}

				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
				if (currentline + 1 == lastline + 1) {
					currentline = 0;
				} else {
					currentline++;
				}
				broadcastMessage("plugins/TellRawAutoMessage/messages.txt");
			} else

			if (!(message.endsWith("%next%"))) {

				for (Player player : Bukkit.getOnlinePlayers()) {

					sendRawMessage(player, message);

				}

				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
				if (currentline + 1 == lastline + 1) {
					currentline = 0;
				} else {
					currentline++;
				}
			}
		}

	}

	@SuppressWarnings("resource")
	public static void broadcastVIPMessage(String filename) throws IOException {

		FileInputStream fs;
		fs = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for (int i = 0; i < currentlineVIP; ++i)
			br.readLine();
		String message = br.readLine();
		if (message != null) {

			if (message.endsWith("%next%")) {
				String message1 = message.substring(0, message.length() - 6);
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("TRAM.VIP")) {
						sendRawMessage(player, message1);
					}
				}

				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
				if (currentlineVIP + 1 == lastline + 1) {
					currentlineVIP = 0;
				} else {
					currentlineVIP++;
				}
				broadcastMessage("plugins/TellRawAutoMessage/VIPmessages.txt");
			} else

			if (!(message.endsWith("%next%"))) {

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("TRAM.VIP")) {
						sendRawMessage(player, message);
					}
				}

				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
				if (currentlineVIP + 1 == lastline + 1) {
					currentlineVIP = 0;
				} else {
					currentlineVIP++;
				}
			}
		}
	}

	@SuppressWarnings("resource")
	public static void broadcastSpecificMessage(String filename, int line) throws IOException {

		FileInputStream fs;
		fs = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for (int i = 0; i < line; ++i)
			br.readLine();
		String message = br.readLine();
		if (message != null) {

			if (message.endsWith("%next%")) {
				String message1 = message.substring(0, message.length() - 6);
				for (Player player : Bukkit.getOnlinePlayers()) {
					sendRawMessage(player, message1);

				}

				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
			} else

			if (!(message.endsWith("%next%"))) {

				for (Player player : Bukkit.getOnlinePlayers()) {

					sendRawMessage(player, message);

				}
			}
		}
	}

	@SuppressWarnings("resource")
	public static void broadcastSpecificVIPMessage(String filename, int line) throws IOException {

		FileInputStream fs;
		fs = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for (int i = 0; i < line; ++i)
			br.readLine();
		String message = br.readLine();
		if (message != null) {

			if (message.endsWith("%next%")) {
				String message1 = message.substring(0, message.length() - 6);
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("TRAM.VIP")) {
						sendRawMessage(player, message1);
					}
				}

				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
				if (currentlineVIP + 1 == lastline + 1) {
					broadcastMessage("plugins/TellRawAutoMessage/VIPmessages.txt");
				} else

				if (!(message.endsWith("%next%"))) {

					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.hasPermission("TRAM.VIP")) {
							sendRawMessage(player, message);
						}
					}

				}
			}
		}
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

}
