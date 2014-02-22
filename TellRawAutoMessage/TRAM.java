package de.inventivegames.TellRawAutoMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public final class TRAM extends JavaPlugin implements Listener {
	
	public final Logger logger = Logger.getLogger("Minecraft");
	public static int currentline = 0;
	public static int tid = 0;
	public static int running = 1;
	public static long interval = 0;
	ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static TRAM instance;
	File configFile = new File(this.getDataFolder(), "config.yml");
	
	
	public void onDisable(){
		Bukkit.getServer().getScheduler().cancelAllTasks();
	}
	
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this,this);
		instance = this;
		
		console = Bukkit.getConsoleSender();

			console.sendMessage(ChatColor.GRAY + "====Enabling TellRawAutoMessage====");
			console.sendMessage(ChatColor.GRAY + "=======www.InventiveGames.de=======");

			
			
			if(!(configFile.exists())) {
				//add defaults here
				getConfig().options().copyDefaults(true);
				saveConfig();
				}
			
			File msgFile = new File(getDataFolder() + File.separator + "messages.txt");
			if(!(msgFile.exists())){
				try {
					msgFile.createNewFile();
					new File("messages.txt");
				} catch (Exception e) {
					console.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "TRAM" + ChatColor.GRAY + "] " + ChatColor.RED + "Cant create Message File!");
					if(getConfig().getBoolean("debug")){
					e.printStackTrace();
					}
				}
			}
		
			setupMetrics();

			if(getConfig().getBoolean("checkForUpdates")){
				@SuppressWarnings("unused")
				Updater updater = new Updater(this, 72192, this.getFile(), Updater.UpdateType.DEFAULT, false);
			}
			
			
			interval = getConfig().getLong("Interval");
			
			
		tid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			File msgFile = new File(getDataFolder() + File.separator + "messages.txt");
			
			@Override
			public void run() {
				if(Bukkit.getOnlinePlayers().length > getConfig().getInt("MinPlayers") - 1){
					if(msgFile.length() !=0){
					
						try{
							broadcastMessage("plugins/TellRawAutoMessage/messages.txt");
						}catch (Exception e){

							console.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "TRAM" + ChatColor.GRAY + "] Config is in the wrong Format! Please remove the " + ChatColor.DARK_GRAY + "tellraw @a" + ChatColor.GRAY + " part");
					
							if(getConfig().getBoolean("debug")){
								e.printStackTrace();
							}
						}
					}else
						if(getConfig().getBoolean("debug")){
							console.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "TRAM" + ChatColor.GRAY + "] Message File is empty! cant Broadcast Message.");
						}
				
				}
			}
			
		}, 0, interval * 20);
			
			
	}
	
	public void setupMetrics(){
		try {
		    MetricsLite metrics = new MetricsLite(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
	}

	
	@SuppressWarnings("resource")
	public static void broadcastMessage(String filename) throws IOException{
		
		FileInputStream fs;
		fs = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for(int i = 0; i < currentline; ++i)
			br.readLine();
		String message = br.readLine();
		
		if(message != null){
		
			if(message.endsWith("%next%")){	
				String message1 = message.substring(0, message.length() - 6);
				for (Player player : Bukkit.getOnlinePlayers()){
					IChatBaseComponent comp = ChatSerializer.a(message1);
				    PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
				    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				}
				
				LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
				lnr.skip(Long.MAX_VALUE);
				int lastline = lnr.getLineNumber();
				if(currentline + 1 == lastline + 1){
					currentline = 0;
				}else{
					currentline++;
				}
				broadcastMessage("plugins/TellRawAutoMessage/messages.txt");
			}
			else
			
				if(!(message.endsWith("%next%"))){	
				
			for (Player player : Bukkit.getOnlinePlayers()){
				IChatBaseComponent comp = ChatSerializer.a(message);
			    PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
			    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}
			
			LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filename)));
			lnr.skip(Long.MAX_VALUE);
			int lastline = lnr.getLineNumber();
			if(currentline + 1 == lastline + 1){
				currentline = 0;
			}else{
				currentline++;
			}
				}
		}

		
	}
	
}

	
	