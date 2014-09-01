package de.inventivegames.antiminimap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class AntiMinimap extends JavaPlugin implements Listener {

	private static Class<?>			nmsChatSerializer		= Reflection.getNMSClass("ChatSerializer");
	private static Class<?>			nmsPacketPlayOutChat	= Reflection.getNMSClass("PacketPlayOutChat");
	public static ArrayList<String>	whitelist				= new ArrayList<String>();

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		getConfig().options().header("You can provide the whitelist in Player Names, the Plugin will convert them to the proper UUID's.");
		getConfig().addDefault("whitelist", new ArrayList<String>());
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		saveConfig();

		for (Object o : getConfig().getList("whitelist")) {
			if (((String) o).length() < 16) {
				whitelist.add(Bukkit.getOfflinePlayer((String) o).getUniqueId().toString().replace("-", ""));
			} else
				whitelist.add((String) o);
		}
		setupMetrics();
	}

	@Override
	public void onDisable() {
		getConfig().set("whitelist", new ArrayList<>(whitelist));
		saveConfig();
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		if (!whitelist.contains(e.getPlayer().getUniqueId().toString().replace("-", ""))) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				@Override
				public void run() {
					sendRawMessage(e.getPlayer(), "{\"text\":\"\",\"extra\":[{\"text\":\"§A§n§t§i§M§i§n§i§m§a§p\"}]}");
					sendRawMessage(e.getPlayer(), "{\"text\":\"\",\"extra\":[{\"text\":\"§3 §6 §3 §6 §3 §6 §e\"}]}");
					sendRawMessage(e.getPlayer(), "{\"text\":\"\",\"extra\":[{\"text\":\"§0§0§1§2§3§5§e§f\"}]}");
				}
			}, 10);
		}
	}

	public void sendRawMessage(Player p, String message) {
		try {
			final Object handle = Reflection.getHandle(p);
			final Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			final Object serialized = Reflection.getMethod(nmsChatSerializer, "a", String.class).invoke(null, message);
			final Object packet = nmsPacketPlayOutChat.getConstructor(Reflection.getNMSClass("IChatBaseComponent")).newInstance(serialized);
			Reflection.getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public String extractUUID(UUID id) {
		return Bukkit.getOfflinePlayer(id).getUniqueId().toString();
	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

}
