package de.inventivegames.MapInfo;

import java.io.File;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MapInfo extends JavaPlugin implements Listener{

	File configFile = new File(this.getDataFolder(), "config.yml");

	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		if(!(configFile.exists())) {
			createConfiguration();
			}
	}

	public String quote =  " ";
	
	@EventHandler
	public void worldChange(PlayerChangedWorldEvent e){
		String worldPrefix = getConfig().getString("WorldPrefix");
		String builderPrefix = getConfig().getString("BuilderPrefix");
		Player p = e.getPlayer();
		World world = p.getLocation().getWorld();
		String w =world.getName();
		
		if(getConfig().getString("worlds." + w) != null){
		p.sendMessage("§2§m===============§r");
		if(getConfig().getString("worlds." + w + ".name")!= null){
		p.sendMessage(colorize("  §b" + worldPrefix + ": §3" + getConfig().getString("worlds." + w + ".name")));
		}
		if(getConfig().getString("worlds." + w + ".by") != null){
		p.sendMessage(colorize("  §b" + builderPrefix + ": §3" + getConfig().getString("worlds." + w + ".by")));
		p.sendMessage("§2§m===============§r");
		}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("addworld")){
			Player player = (Player)sender;
			if(player.hasPermission("MapInfo.add")){
			if(args.length == 0){
				getConfig().createSection("worlds." + player.getWorld().getName());
				saveConfig();
				reloadConfig();
				
				getConfig().createSection("worlds." + player.getWorld().getName() + ".name");
				saveConfig();
				reloadConfig();
				
				getConfig().createSection("worlds." + player.getWorld().getName() + ".by");
				saveConfig();
				reloadConfig();
				
				getConfig().set("worlds." + player.getWorld().getName() + ".name", quote + player.getWorld().getName() + quote);
				getConfig().set("worlds." + player.getWorld().getName() + ".by", quote + player.getName() + quote);
				saveConfig();
				reloadConfig();
				player.sendMessage("§7[§1MapInfo§7] §2Successfully added world §b" + player.getWorld().getName());
			}
			if(args.length == 1){
				getConfig().createSection("worlds." + args[0]);
				saveConfig();
				reloadConfig();
				
				getConfig().createSection("worlds." + args[0] + ".name");
				saveConfig();
				reloadConfig();
				
				getConfig().createSection("worlds." + args[0] + ".by");
				saveConfig();
				reloadConfig();
				
				getConfig().set("worlds." + args[0] + ".name",  quote + args[0] + quote);
				getConfig().set("worlds." + args[0] + ".by", quote + player.getName() + quote);
				saveConfig();
				reloadConfig();
				player.sendMessage("§7[§1MapInfo§7] §2Successfully added world §b" + args[0]);
			}
			if(args.length > 1){
			player.sendMessage("§cToo many Arguments!");
			player.sendMessage("§cUsage: §4/addworld [world]");
			}
		}
		}
		else
			if(cmd.getName().equalsIgnoreCase("reloadmap")){
				Player player = (Player)sender;
				if(player.hasPermission("MapInfo.reload")){
				if(args.length == 0){
					reloadConfig();
					player.sendMessage("§7[§1MapInfo§7] §2Successfully reloaded Config");
				}
				}
			}
		return true;
	}
	

	
	
	
	public void createConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();

	}
	
	
    public static String colorize(String Message) {
        return Message.replaceAll("&([a-z0-9])", "§$1");
    }
}
