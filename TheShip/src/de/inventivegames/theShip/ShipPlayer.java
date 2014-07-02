package de.inventivegames.theShip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShipPlayer {

	private Player								p				= null;
	private int									arena			= -1;
	private int									number			= -1;
	private String								firstName		= null;
	private String								lastName		= null;
	private boolean								inLobby			= false;
	private boolean								inGame			= false;
	private boolean								inSpectate		= false;
	private ShipPlayer							quarry			= null;
	private ShipPlayer							hunter			= null;
	private boolean								assigned		= false;
	private boolean								firstHit		= false;
	private boolean								observed		= false;
	private boolean								observeNoticed	= false;
	private int									money			= 0;
	private File playerFile;
	private File arenaFile;
	private static HashMap<Player, ShipPlayer>	shipPlayers		= new HashMap<Player, ShipPlayer>();

	public ShipPlayer(Player p) {
		this.p = p;

		ShipPlayer.shipPlayers.put(p, this);

		TheShip.console.sendMessage(TheShip.prefix + "§rCreating new ShipPlayer{player=" + p + "}");
	}

	public static ShipPlayer getShipPlayer(Player p, boolean createNew) {

		if (p != null) {
			if (!createNew) {
				return shipPlayers.get(p);
			}
			if (!(shipPlayers.containsKey(p))) {
				return new ShipPlayer(p);
			} else {
				return shipPlayers.get(p);
			}
		}
		return null;
	}

	public void setPlayerNumber(int n) {
		this.number = n;
	}

	public int getPlayerNumber() {
		return this.number;
	}

	public void setArena(int arena) {
		this.arena = arena;
	}

	public int getArena() {
		return this.arena;
	}

	public String getFakeName() {
		return (this.firstName + " " + this.lastName);
	}

	public boolean inLobby() {
		return this.inLobby;
	}

	public boolean inGame() {
		return this.inGame;
	}

	public boolean inSpectate() {
		return this.inSpectate;
	}

	public boolean playing() {
		if ((this.inGame) || (this.inLobby) || (this.inSpectate)) {
			return true;
		}
		return false;
	}

	public void setInLobby() {
		this.inLobby = true;
		this.inGame = false;
		this.inSpectate = false;
		

		p.setFlying(false);
		p.setAllowFlight(false);
	}

	public void setInGame() {
		this.inGame = true;
		this.inLobby = false;
		this.inSpectate = false;
		

		p.setFlying(false);
		p.setAllowFlight(false);

	}

	public void setInSpectate() {
		this.inSpectate = true;
		this.inGame = false;
		this.inLobby = false;
		
		
		p.setAllowFlight(true);

		p.setFlying(true);
	}

	public void setLeft() {
		this.inSpectate = false;
		this.inLobby = false;
		this.inGame = false;
		
		this.quarry = null;
		this.hunter = null;
		this.arena = -1;
		this.firstHit = false;
		this.money = -1;
		this.number = -1;
		this.observed = false;

	}

	public void setQuarry(ShipPlayer sp) {
		this.quarry = sp;

		TheShip.console.sendMessage(TheShip.prefix + "§rAssigned Quarry for Player " + this.getPlayer().getName() + ": " + this.quarry.getPlayer().getName());
	}

	public void resetQuarry() {
		this.quarry = null;
	}

	public ShipPlayer getQuarry() {
		return this.quarry;
	}

	public boolean hasQuarry() {
		return (this.quarry != null ? true : false);
	}

	public void setHunter(ShipPlayer sp) {
		this.hunter = sp;

		TheShip.console.sendMessage(TheShip.prefix + "§rAssigned Hunter for Player " + this.getPlayer().getName() + ": " + this.hunter.getPlayer().getName());
	}

	public boolean hasHunter() {
		return (this.hunter != null ? true : false);
	}

	public ShipPlayer getHunter() {
		return this.hunter;
	}

	public Player getPlayer() {
		return this.p;
	}

	public void setAssigned() {
		this.assigned = true;
	}

	public boolean isAssigned() {
		return this.assigned;
	}

	public boolean holdingWeapon() {
		if (this.getPlayer().getItemInHand().getType() == Material.AIR) {
			return false;
		} else
			return true;
	}

	public boolean hasFirstHit() {
		return this.firstHit;
	}

	public void setFirstHit() {
		this.firstHit = true;

		TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

			@Override
			public void run() {
				firstHit = false;
			}
		}, 20 * 5);
	}

	public void setObserved(boolean b) {
		this.observed = b;
		if (!b) {
			this.observeNoticed = false;
		}
	}

	public boolean isObserved() {
		return this.observed;
	}

	public void setObserveNoticed() {
		this.observeNoticed = true;
	}
	
	public void setFrozen(boolean b) {
		if(b) {
			this.p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147000, 128));
			this.p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 255));
		} else if(!b) {
			this.p.removePotionEffect(PotionEffectType.JUMP);
			this.p.removePotionEffect(PotionEffectType.SLOW);
		}
	}
	
	public void setBlind(boolean b) {
		if(b) {
			this.p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 255));
			this.p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
		}else if(!b) {
			this.p.removePotionEffect(PotionEffectType.BLINDNESS);
			this.p.getInventory().setHelmet(new ItemStack(Material.AIR));
		}
	}
	
	public void setInvisible(boolean b) {
		if(b) {
			this.p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147000, 255));
		}else if(!b) {
			this.p.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}
	
	public void addMoney(int i) {
		SBoard.getBoard(p).resetOldMoney(this.money);
		
		this.money += i;
		
		SBoard.getBoard(this.p).updateMoney(this.money);
	}
	
	public void removeMoney(int i) {
		SBoard.getBoard(p).resetOldMoney(this.money);
		
		this.money -= i;
		
		SBoard.getBoard(this.p).updateMoney(this.money);
	}
	
	public void setMoney(int i) {
		SBoard.getBoard(p).resetOldMoney(this.money);
		
		this.money = i;
		
		SBoard.getBoard(this.p).updateMoney(this.money);
	}
	
	public int getMoney() {
		return this.money;
	}
	
	public void saveMoneyToFile() {
		playerFile = new File("plugins/TheShip/Players/" + this.p.getName() + ".yml");
		YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
		
		int pre = PlayerFile.getInt("money");
		
		PlayerFile.set("money", pre + this.money);
		
		try {
			PlayerFile.save(playerFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void respawn() {

		
		for(Player online : TheShip.instance.getServer().getOnlinePlayers()) {
			online.showPlayer(this.p);
		}

		arenaFile = new File("plugins/TheShip/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		Set<String> IDs = ArenaFile.getConfigurationSection("SpawnPoints.players").getKeys(false);
		Object[] ids = IDs.toArray();
		String ID;
		int id;
		if (IDs.size() != 0) {
			ID = ids[IDs.size() - 1].toString();
			id = Integer.parseInt(ID) + 1;
		} else {
			id = 1;
		}

		
		int r = TheShip.rd.nextInt(id);		
		
				World world = TheShip.instance.getServer().getWorld(ArenaFile.getString("World"));
				double X = ArenaFile.getInt("SpawnPoints.players." + r + ".X");
				double Y = ArenaFile.getInt("SpawnPoints.players." + r + ".Y");
				double Z = ArenaFile.getInt("SpawnPoints.players." + r + ".Z");

				Location loc = new Location(world, X, Y, Z);
				
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 255));
				this.p.teleport(loc);
				
				this.setInGame();

	}
	
	
	

	public static List<ShipPlayer> getShipPlayers() {
		List<ShipPlayer> ps = new ArrayList<ShipPlayer>();

		for (Player p : TheShip.instance.getServer().getOnlinePlayers()) {
			ps.add(ShipPlayer.shipPlayers.get(p));
		}

		return ps;
	}

	public static List<String> getShipPlayerNames() {
		List<String> ps = new ArrayList<String>();

		for (Player p : TheShip.instance.getServer().getOnlinePlayers()) {
			ps.add(ShipPlayer.shipPlayers.get(p).getPlayer().getName());
		}

		return ps;
	}

	public void DEBUG() {
		System.out.println(this.arena);
		System.out.println(this.inGame);
		System.out.println(this.inLobby);
		System.out.println(this.number);
		System.out.println(this.hunter.getPlayer().getName());
		System.out.println(this.p);
		System.out.println(this.quarry.getPlayer().getName());
	}
}
