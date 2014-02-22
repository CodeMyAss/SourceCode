package de.inventivegames.AchievementChests;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class AchievementChests extends JavaPlugin implements Listener{

	File achievementFile = new File("plugins/AchievementChests/achievements.txt");
	File achievementFileDir = new File("plugins/AchievementChests");
	String prefix = "§2[§3AchievementChests§2]§r ";
	
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		
		if(!(achievementFile.exists())){
			try {
				achievementFileDir.mkdirs();
				achievementFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setupMetrics();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.hasBlock()){
		if((e.getClickedBlock().getType().equals(Material.SKULL)) && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			Player p = e.getPlayer();
			Action a = e.getAction();
			Block b = e.getClickedBlock();
			Location signPos = new Location(b.getWorld(),b.getLocation().getBlockX(), b.getLocation().getBlockY() - 2, b.getLocation().getBlockZ());
	
			if((signPos.getBlock().getType().equals(Material.SIGN_POST)) || signPos.getBlock().getType().equals(Material.SIGN)){
			
			Sign sign = (Sign) signPos.getBlock().getState();
			
				if(p.hasPermission("AchievementChests.getAchievement")){
					if(a == Action.RIGHT_CLICK_BLOCK){
							if((signPos.getBlock().getType() == Material.SIGN) || (signPos.getBlock().getType() == Material.SIGN_POST)){
								if(sign.getLine(0).equals("§3[AC]")){
									if((p.isSneaking()) && (p.hasPermission("AchievementChests.info"))){
										p.sendMessage("§2========================");
										p.sendMessage("§2=== AchievementChests ===");
										p.sendMessage("§2= Detailed Information =");
										p.sendMessage("§2===== Achievement =====");
										p.sendMessage("§3" + getAchievement(Integer.parseInt(sign.getLine(1))));
										p.sendMessage("§2====== Block Type ======");
										p.sendMessage("§3" + sign.getLine(2) + ":" + sign.getLine(3));
										p.sendMessage("§2========================");
									}else
									performAchievement(Integer.parseInt(sign.getLine(1)), p);
								}
							}
						
					}
				}
			}
		}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignChange(SignChangeEvent e){
		Player p = e.getPlayer();
		Location signPos = e.getBlock().getLocation();
		Location skullPos = new Location(signPos.getWorld(), signPos.getBlockX(), signPos.getBlockY() + 2, signPos.getBlockZ());
		Location blockPos = new Location(signPos.getWorld(), signPos.getBlockX(), signPos.getBlockY() + 1, signPos.getBlockZ());
		
		if(e.getLine(0).equalsIgnoreCase("[AC]")){
			if(p.hasPermission("AchievementChests.create")){
				e.setLine(0, "§3[AC]");
				if(e.getLine(1).equals("")){
					e.setLine(1, "1");
				}
				if(e.getLine(2).equals("")){
					e.setLine(2, "1");
				}
				if(e.getLine(3).equals("")){
					e.setLine(3, "1");
				}
				skullPos.getBlock().setTypeIdAndData(144, (byte) 1, true);
				BlockState skullState = skullPos.getBlock().getState();
				skullPos.getBlock().setData((byte) 3); 
				
					Skull skull = (Skull) skullState;
					skull.setRotation(getClosestFace(p.getLocation().getYaw()));
					skull.setSkullType(SkullType.PLAYER);
					skull.setOwner("MHF_Chest");
					skull.update();

				
				if(e.getLine(2) != null){
					try {
						blockPos.getBlock().setTypeId(Integer.parseInt(e.getLine(2)));
						if(e.getLine(3) != null){
							blockPos.getBlock().setData((byte) Integer.parseInt(e.getLine(3)));
						}
						p.sendMessage(prefix + "§2Sucesfully Created Achievement Sign for Achievement §b#" + e.getLine(1) +"!");
					} catch (Exception ex) {
						blockPos.getBlock().setType(Material.STONE);
						p.sendMessage(prefix + "§2Sucesfully Created Achievement Sign!");
					}
				}
				
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void performAchievement(int ID, Player p){
		int id = ID;
		String achievement = getAchievement(id);
		
		File PlayerFile = new File("plugins/AchievementChests/PlayerData/" + p.getName() + ".yml");
		if(PlayerFile != null){
		YamlConfiguration playerFile = YamlConfiguration.loadConfiguration(PlayerFile);
		
			if(!(playerFile.getString("achievements").contains("" + id))){
				playerFile.set("achievements", playerFile.getString("achievements") + " " + id);
				try {
					playerFile.save(PlayerFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				p.sendMessage(prefix + "§aYou got the Achievement: §2" + achievement);
			
			}else
				p.sendMessage(prefix + "§cYou already got this Achievement!");
		}else
			Bukkit.getLogger().log(Level.SEVERE, "Could not find Data File for Player " + p.getName() + "! File is going to generate after re-loging of the Player!");

			
	}
	
	public String getAchievement(int id){
		String line = "§cAchievement not Found!";
		try {
            FileInputStream fileInputStream = new FileInputStream(achievementFile); /*change your file path here*/
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                if (strLine.startsWith("[" + id + "] ")) {
                	line = strLine.substring(4);
                }
            }
            dataInputStream.close();    
		} catch (Exception e) {
			line = "§cAchievement not Found!";
			e.printStackTrace();
		}
		return colorize(line);
	}
	
	@SuppressWarnings("deprecation")
	public void placeItemStack(ItemStack item, int x, int y, int z, World w)
    {
        Block b = w.getBlockAt(x, y, z);
        b.setType(item.getType());
        b.setData((byte)item.getData().getData());
    }
	
	public static BlockFace getFace(String name, BlockFace def) {
	    for (BlockFace face : BlockFace.values()) {
	        if (face.toString().equalsIgnoreCase(name)) {
	            return face;
	        }
	    }
	    return def;
	}
	
	 public BlockFace getClosestFace(float direction){

	        direction = direction % 360;

	        if(direction < 0)
	            direction += 360;

	        direction = Math.round(direction / 45);

	        switch((int)direction){

	            case 0:
	                return BlockFace.WEST;
	            case 1:
	                return BlockFace.SOUTH_WEST;
	            case 2:
	                return BlockFace.WEST;
	            case 3:
	                return BlockFace.NORTH_WEST;
	            case 4:
	                return BlockFace.NORTH;
	            case 5:
	                return BlockFace.NORTH_EAST;
	            case 6:
	                return BlockFace.EAST;
	            case 7:
	                return BlockFace.SOUTH_EAST;
	            default:
	                return BlockFace.SOUTH;

	        }
	    }
	
	 @SuppressWarnings("unused")
	@EventHandler
	 public void onJoin(PlayerJoinEvent e){
		 Player p = e.getPlayer();
		 File PlayerFileDir = new File("plugins/AchievementChests/PlayerData");
		 File PlayerFile = new File("plugins/AchievementChests/PlayerData/" + p.getName() + ".yml");
		 YamlConfiguration playerFile = YamlConfiguration.loadConfiguration(PlayerFile);
		 
		 if(!(PlayerFile.exists())){
			 try {
//				PlayerFileDir.mkdirs();
//				PlayerFile.createNewFile();
				playerFile.addDefault("achievements", "");
				playerFile.options().copyDefaults(true);
				playerFile.save(PlayerFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 }
	 }
	 
	 @EventHandler
	 public void onBlockBreak(BlockBreakEvent e){
		 Block b = e.getBlock();
		 Player p = e.getPlayer();
		Location signPos = e.getBlock().getLocation();
		Location skullPos = new Location(signPos.getWorld(), signPos.getBlockX(), signPos.getBlockY() + 2, signPos.getBlockZ());
		Location blockPos = new Location(signPos.getWorld(), signPos.getBlockX(), signPos.getBlockY() + 1, signPos.getBlockZ());

		 if((b.getType().equals(Material.SIGN)) || (b.getType().equals(Material.SIGN_POST))){
			 Sign sign = (Sign) b.getState();
			 if(sign.getLine(0).equals("§3[AC]")){
				 if(p.hasPermission("AchievementChests.remove")){
					 signPos.getBlock().setType(Material.AIR);
					 skullPos.getBlock().setType(Material.AIR);
					 blockPos.getBlock().setType(Material.AIR);
					 p.playSound(p.getLocation(), Sound.STEP_WOOD, 1, 1);
					 p.sendMessage(prefix + "§cYou removed a Achievement Chest.");
				 }else
					 e.setCancelled(true);
			 }
		 }
	 }
	 
	 public void setupMetrics(){
			try {
			    MetricsLite metrics = new MetricsLite(this);
			    metrics.start();
			} catch (IOException e) {
			    // Failed to submit the stats :-(
			}
		}
	 
	 public static String colorize(String Message) {
	        return Message.replaceAll("&([a-z0-9])", "§$1");
	    }
	 
}
