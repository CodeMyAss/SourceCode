package de.inventivegames.RealtimeTranslation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import de.inventivegames.RealtimeTranslation.mysql.MySQL;

public class RealtimeTranslation extends JavaPlugin implements Listener {
	public static String				prefix					= "§1[§aRealtimeTranslation§1] ";
	public static ConsoleCommandSender	console					= Bukkit.getConsoleSender();
	public static RealtimeTranslation	instance;

	public File							geoFile					= new File(this.getDataFolder(), "GeoIP.dat.gz");
	
	public static Map<String, Locale>	localeMap;

	File								configFile				= new File(this.getDataFolder(), "config.yml");
	private static File					playerFile;
	private static File					senderPlayerFile;

	public static String				serverVersion;

	private static Class<?>				nmsChatSerializer		= Reflection.getNMSClass("ChatSerializer");
	private static Class<?>				nmsPacketPlayOutChat	= Reflection.getNMSClass("PacketPlayOutChat");

	public static MySQL					MySQL;
	public static Connection			c						= null;

	public static String				host;
	public static String				port;
	public static String				database;
	public static String				user;
	public static String				pass;

	public void onEnable() {
		instance = this;
		instance.getServer().getPluginManager().registerEvents(this, this);
		registerEvents(this, new PlayerConfig());
		instance.getCommand("rct").setExecutor(new Commands());
		if (!(configFile.exists())) {
			getConfig().addDefault("defaultLang", "en");
			getConfig().addDefault("noTranslatePrefix", "%notranslation%");
			getConfig().addDefault("UseMySQL", Boolean.valueOf(false));
			getConfig().addDefault("MySQL.Host", "host.name");
			getConfig().addDefault("MySQL.Port", Integer.valueOf(3306));
			getConfig().addDefault("MySQL.Database", "RealtimeChatTranslation");
			getConfig().addDefault("MySQL.User", "username");
			getConfig().addDefault("MySQL.Password", "12345 is a bad Password!");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}

		initGeo();
		
		Language.setup();

		serverVersion = instance.getServer().getBukkitVersion().toString().substring(0, 5);

		if (serverVersion.contains(("1.6"))) {
			System.out.println("[RCT] This Server is running an unsupported Version!");
		}

		loadDatabase();

		setupMetrics();
	}

	public static void loadDatabase() {
		if (instance.getConfig().getBoolean("UseMySQL")) {
			try {
				host = instance.getConfig().getString("MySQL.Host");
				port = "" + instance.getConfig().getInt("MySQL.Port");
				database = instance.getConfig().getString("MySQL.Database");
				user = instance.getConfig().getString("MySQL.User");
				pass = instance.getConfig().getString("MySQL.Password");

				MySQL = new MySQL(instance, host, port, database, user, pass);
				c = MySQL.openConnection();

				Statement statement = c.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS `rct` (`name` varchar(16), `lang` varchar(16));");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public static void initGeo() {
		if(!(instance.geoFile.exists())) {
			try {
				instance.geoFile.createNewFile();
				System.out.println("[RCT] Downloading GeoIP File...");
	            String link = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
	            DownloadFile(link, instance.geoFile);
	          } catch (IOException e) {
	            
	          } finally {
	            
	          }
		}		
	}

	private static Boolean DownloadFile(String url, File storeFile) {
		try {
			URL uri = new URL(url);
			URLConnection ucon = uri.openConnection();
			ucon.setUseCaches(false);
			ucon.setDefaultUseCaches(false);
			ucon.setReadTimeout(10000);
			ucon.setConnectTimeout(10000);
			ucon.connect();

			InputStream is = ucon.getInputStream();
			if (url.endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}

			OutputStream os = new FileOutputStream(storeFile);
			byte[] buffer = new byte[2048];
			int lenght = is.read(buffer);
			while (lenght >= 0) {
				os.write(buffer, 0, lenght);
				lenght = is.read(buffer);
			}

			os.close();
			is.close();

			return Boolean.valueOf(true);
		} catch (Exception ex) {
		}
		return Boolean.valueOf(false);
	}
	
	public static void setDatabaseValue(Player p, String lang) {
		if (instance.getConfig().getBoolean("UseMySQL")) {
			try {
				Statement statement = c.createStatement();
				statement.executeUpdate("DELETE FROM rct WHERE name = '" + p.getName() + "';");
				statement.executeUpdate("INSERT INTO rct (`name`, `lang`) VALUES ('" + p.getName() + "', '" + lang + "');");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getDatabaseValue(Player p) {
		if (instance.getConfig().getBoolean("UseMySQL")) {
			ResultSet res;
			String lang = "en";
			if (c != null) {
				try {
					Statement statement = c.createStatement();
					res = statement.executeQuery("SELECT * FROM rct WHERE name = '" + p.getName() + "';");
					res.next();

					if (res.getString("name") == null) {
						lang = "en";
					} else {
						lang = res.getString("lang");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return lang;
		}
		return null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onChat(AsyncPlayerChatEvent e) {
		if (!(e.isCancelled())) {
			String message = filterMessage(e.getMessage());
			Player sender = e.getPlayer();

			senderPlayerFile = new File("plugins/RealtimeChatTranslation/Players/" + sender.getName() + ".yml");
			YamlConfiguration SenderPlayerFile = YamlConfiguration.loadConfiguration(senderPlayerFile);

			if (sender.hasPermission("rct.getTranslated")) {
				if (Bukkit.getServer().getOnlinePlayers().length > 0) {
					if (!(message.startsWith(instance.getConfig().getString("noTranslatePrefix")))) {
						for (Player online : Bukkit.getOnlinePlayers()) {
							Player p = online;

							playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p.getName() + ".yml");
							YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
							String lang = PlayerFile.getString("lang");
							String senderLang = SenderPlayerFile.getString("lang");

							String translation = Translator.getTranslation(message, lang);
							String orgMessageString = Translator.getTranslation("Original Message", lang);

							if (!(senderLang == lang)) {
								sendRawMessage(p, "{\"text\":\"" + e.getFormat().replace("%1$s", sender.getDisplayName()).replace("%2$s", "\",\"extra\":[{\"text\":\"" + translation + "\",\"color\":\"white\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + ChatColor.GOLD + orgMessageString + " (" + ChatColor.GREEN + senderLang.toUpperCase() + ChatColor.GOLD + ")" + ": " + ChatColor.GRAY + message + ChatColor.RESET + "\"}}]}"));
							} else {
								p.sendMessage(message);
							}
							playerFile = null;
						}
						e.setCancelled(true);
					} else
						e.setMessage(message.replaceAll(instance.getConfig().getString("noTranslatePrefix"), ""));
				}
			}
		}else
			RealtimeTranslation.console.sendMessage(RealtimeTranslation.prefix + "§Unable to Translate Message: Event Cancelled");
	}

	 @EventHandler
	 public void onJoin(PlayerJoinEvent e) {
		 Player p = e.getPlayer();
		 if(!(PlayerConfig.fileExists(p))) {
			 String lang = GeoIP.getLanguage(p);
			 String langCode = GeoIP.getLangCode(p);
			 String country = GeoIP.getCountry(p);
			 
			 String message = "[{\"text\":\"[\",\"color\":\"dark_blue\"},{\"text\":\"RealtimeChatTranslation\",\"color\":\"green\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\"},{\"text\":\"] \",\"color\":\"dark_blue\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\"},{\"text\":\"Detected that you are from \",\"color\":\"dark_green\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/rct setlang %lang%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Click here to change your language to %lang% / %langCode%\"}},{\"text\":\"%country%\",\"color\":\"green\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\"},{\"text\":\"! Click \",\"color\":\"dark_green\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/rct setlang %lang%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Click here to change your language to %lang% / %langCode%\"}},{\"text\":\"[HERE] \",\"color\":\"aqua\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/rct setlang %lang%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Click here to change your language to §2%lang%/%langCode%§7\"}},{\"text\":\"to change your language.\",\"color\":\"dark_green\",\"bold\":\"false\",\"italic\":\"false\",\"underlined\":\"false\",\"strikethrough\":\"false\",\"obfuscated\":\"false\"}]"
					 .replace("%lang%", lang).replace("%langCode%", langCode).replace("%country%", country);
			 
			 sendRawMessage(p, message);
		 }
	 }



	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
	}

	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
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

	public static String filterMessage(String msg) {
		String[] symbols = "\"§$%&/()=\\}][{*+#'~°^".split("");
		for (String symbol : symbols) {
			if (msg.contains(symbol)) {
				msg = msg.replace(symbol, "");
			}
		}
		return msg;
	}

}
