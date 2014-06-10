package de.inventivegames.RealtimeTranslation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	private static File	playerFile;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if ((cmd.getLabel().equalsIgnoreCase("rct"))) {
				if (args.length == 0) {
					p.sendMessage("§2===RealtimeChatTranslation by inventivetalent===");
					p.sendMessage("§cType §4/rct help §cfor a list of commands!");
				}
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("help")) {
						p.sendMessage("§2=====Commands=====");
						p.sendMessage("§a/rct setlang");
					} else
						playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p.getName() + ".yml");
					YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
					if (args.length > 0) {
					if (args[0].equalsIgnoreCase("setlang")) {
						if(args.length == 2) {
							if (p.hasPermission("rct.setlang")) {
								String lang = null;
								if ((args[1].length() == 2) && (Language.isValidCode(args[1]))) {
									lang = args[1];
								} else if ((args[1].length() > 2) && (Language.isValidName(args[1]))) {
									lang = Language.toCode(args[1]);
								}
								if (lang != null) {
									RealtimeTranslation.setDatabaseValue(p, lang);
									PlayerFile.set("lang", lang);
									if (lang.equalsIgnoreCase("de")) {
										p.sendMessage(RealtimeTranslation.prefix + "§2Deine Sprache wurde erfolgreich in §a" + Language.toName(lang) + "/" + lang + " §2geändert!");
									} else
										p.sendMessage(RealtimeTranslation.prefix + "§2Successfully changed your language to §a" + Language.toName(lang) + "/" + lang + "§2!");
									try {
										PlayerFile.save(playerFile);
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									p.sendMessage(RealtimeTranslation.prefix + "§cPlease specify a valid Language");
								}
							} else
								p.sendMessage(RealtimeTranslation.prefix + "§cYou don't have Permission to execute this command!");
						} else
							p.sendMessage(RealtimeTranslation.prefix + "§cPlease specify a Language!");
					}else
						p.sendMessage(RealtimeTranslation.prefix + "§cUnknown Argument");
					} else if (args[0].equalsIgnoreCase("reload")) {
						if (args.length == 1) {
							if (p.hasPermission("rct.reload")) {
								RealtimeTranslation.instance.reloadConfig();
								for (Player online : RealtimeTranslation.instance.getServer().getOnlinePlayers()) {
									Player p1 = online;
									playerFile = new File("plugins/RealtimeChatTranslation/Players/" + p1.getName() + ".yml");
									YamlConfiguration PlayerFile1 = YamlConfiguration.loadConfiguration(playerFile);

									try {
										PlayerFile1.load(playerFile);
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (InvalidConfigurationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									p.sendMessage("§2Reloaded Successfully§2!");
								}
							}

						} else
							p.sendMessage(RealtimeTranslation.prefix + "You don't have Permission to execute this command!");
					}
				} else
					p.sendMessage(RealtimeTranslation.prefix + "§cUnknown Argument!");
				return true;
			}

		} else
			sender.sendMessage("[RCT] Only Players can use this command!");

		return false;
	}

}
