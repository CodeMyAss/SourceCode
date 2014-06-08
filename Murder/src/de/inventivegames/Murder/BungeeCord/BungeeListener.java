package de.inventivegames.Murder.BungeeCord;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.inventivegames.Murder.Game;
import de.inventivegames.Murder.Murder;

public class BungeeListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(Murder.instance.getConfig().getBoolean("useBungeeCord")) {
			Player p = e.getPlayer();
			int arena = Murder.instance.getConfig().getInt("BungeeArena");
			
			Game.joinArena(arena, p);
		}
	}
	
	@EventHandler 
	public void onQuit(PlayerQuitEvent e){
		if(Murder.instance.getConfig().getBoolean("useBungeeCord")) {
			Player p = e.getPlayer();
			int arena = Murder.instance.getConfig().getInt("BungeeArena");
			
			Game.leaveArena(arena, p);
		}
	}
	
	public static void restartServer() {
		if(Murder.instance.getConfig().getBoolean("useBungeeCord")) {
			Murder.instance.getServer().broadcastMessage(Murder.prefix + "§c§l Server will restart in 10 Seconds!");
			
			Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				
				@Override
				public void run() {
					Server server = Murder.instance.getServer();
	
			        server.savePlayers();
	
			        for (World world : server.getWorlds()) {
			          world.save();
			          server.unloadWorld(world, true);
			        }
	
			        server.shutdown();
				}
			}, 200);
		}
	}
}
