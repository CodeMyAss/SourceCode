package de.inventivegames.RealtimeTranslation;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConfig implements Listener {
	private static File	playerFile;

	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		final String defaultLang = RealtimeTranslation.instance.getConfig().getString("defaultLang");
		playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p.getName() + ".yml");
		RealtimeTranslation.instance.getServer().getScheduler().scheduleSyncDelayedTask(RealtimeTranslation.instance, new Runnable() {
			@Override
			public void run() {
				if (!(playerFile.exists())) {
					YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
					try {
						PlayerFile.addDefault("lang", "en");
						PlayerFile.options().copyDefaults(true);
						PlayerFile.save(playerFile);
					} catch (IOException ex) {
						RealtimeTranslation.console.sendMessage(RealtimeTranslation.prefix + "§cCould not create Player File for Player §2" + p.getName());
						ex.printStackTrace();
					}
					PlayerFile.set("lang", defaultLang);
					String dbLang = RealtimeTranslation.getDatabaseValue(p);
					if (dbLang != null) {
						PlayerFile.set("lang", dbLang);
					}
					try {
						PlayerFile.save(playerFile);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					RealtimeTranslation.setDatabaseValue(p, PlayerFile.getString("lang"));
				} else {
					if (RealtimeTranslation.instance.getConfig().getBoolean("UseMySQL")) {
						YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
						String dbLang = RealtimeTranslation.getDatabaseValue(p);
						PlayerFile.set("lang", dbLang);
					}
				}
			}
		}, 10L);
	}
	
	
	public static Boolean fileExists(Player p) {
		playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p.getName() + ".yml");

		if(playerFile.exists()) {
			return true;
		}
		return false;
		
	}
}
