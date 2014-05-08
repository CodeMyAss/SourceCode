package de.inventivegames.offlinechat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class OfflineChat extends JavaPlugin implements Listener {

	public File	logFile		= new File(this.getDataFolder(), "log.txt");
	public File	logFileDir	= new File(this.getDataFolder() + "");

	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		if (logFile.exists()) {
			logFile.delete();
		}
		if (!(logFile.exists())) {
			try {
				logFileDir.mkdirs();
				logFile.setWritable(true);
				logFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setupMetrics();
	}

	public void onDisable() {
		logFile.delete();
	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String message = e.getMessage();
		String fullMessage = e.getFormat().replace("%1$s", p.getDisplayName()).replace("%2$s", message);
		if (!(p.hasPermission("OfflineChat.chatLog.bypass"))) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
				bw.write(fullMessage.toString());
				bw.newLine();
				bw.close();
			} catch (Exception ex) {
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		if (p.hasPermission("OfflineChat.getLog")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				BufferedReader	br	= null;

				@Override
				public void run() {
					try {
						br = new BufferedReader(new FileReader(logFile));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}

					try {
						p.sendMessage("§2===== Start of Chat Log ====");
						String line;
						while ((line = br.readLine()) != null) {
							p.sendMessage(line);
						}
						p.sendMessage("§c===== End of Chat Log ====");

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}, 1);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getLabel().equalsIgnoreCase("offlinechat")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length < 1) {
					p.sendMessage("§cAvailable Arguments: showLog");
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("showLog")) {
						if (p.hasPermission("offlinechat.showlog")) {
							BufferedReader br = null;

							try {
								br = new BufferedReader(new FileReader(logFile));
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}

							try {
								p.sendMessage("§2===== Start of Chat Log ====");
								String line;
								while ((line = br.readLine()) != null) {
									p.sendMessage(line);
								}
								p.sendMessage("§c===== End of Chat Log ====");
							} catch (Exception ex) {
							}
						} else
							p.sendMessage("§cYou don't have Permission to execute this Command!");
					} else
						p.sendMessage("§cAvailable Arguments: showLog");
				}
			}
		}
		return super.onCommand(sender, command, label, args);
	}

}
