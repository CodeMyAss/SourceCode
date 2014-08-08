package de.inventivegames.murder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.inventivegames.murder.event.GameJoinEvent;

public class MurderPlayer {

	private final Player							p;
	private Arena									arena;
	private String									nameTag;
	public String									disguisedTag;

	private boolean									bystander				= false;
	private boolean									murderer				= false;
	private boolean									weapon					= false;

	private boolean									inGame					= false;
	private boolean									inLobby					= false;
	private boolean									inSpectate				= false;

	private boolean									disguised				= false;

	private static HashMap<Player, MurderPlayer>	murderPlayers			= new HashMap<Player, MurderPlayer>();

	private static Class<?>							nmsChatSerializer		= Reflection.getNMSClass("ChatSerializer");
	private static Class<?>							nmsPacketPlayOutChat	= Reflection.getNMSClass("PacketPlayOutChat");

	private boolean									cantPickup				= false;

	private ItemStack[]								InventoryContent		= null;
	private ItemStack[]								InventoryArmorContent	= null;
	private Location								prevLocation			= null;
	private GameMode								prevGamemode			= null;
	private Float									prevExp					= -1F;
	private int										prevLevel				= -1;
	private double									prevHealth				= -1;
	private double									prevFood				= -1;
	private int										points					= 0;

	public boolean									resetRP					= true;

	// ////////////////////////
	public MurderPlayer(Player p) {
		this.p = p;

		final YamlConfiguration config = YamlConfiguration.loadConfiguration(Murder.playerFile);
		config.createSection("Players." + p.getUniqueId());
		config.addDefault("Players." + p.getUniqueId() + ".name", p.getName());
		config.addDefault("Players." + p.getUniqueId() + ".points", points);
		try {
			config.save(Murder.playerFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		points = config.getInt("Players." + p.getUniqueId() + ".points");

		MurderPlayer.murderPlayers.put(p, this);
	}

	public static MurderPlayer getPlayer(Player p) {
		if (murderPlayers.containsKey(p))
			return murderPlayers.get(p);
		else
			return new MurderPlayer(p);
	}

	// /////////////////

	public void setNameTag(String tag) {
		nameTag = tag;
	}

	public String getDisguisedTag() {
		return disguisedTag;
	}

	public String getNameTag() {
		return nameTag != null ? nameTag : "§cNULL";
	}

	public int getPoints() {
		return points;
	}

	public void addPoints(int i) {
		setPoints(getPoints() + i);
	}

	public void subtractPoints(int i) {
		setPoints(getPoints() - i);
	}

	public void setPoints(int i) {
		points = i;
		final YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(Murder.playerFile);
		PlayerFile.set("Players." + p.getUniqueId() + ".name", p.getName());
		PlayerFile.set("Players." + p.getUniqueId() + ".points", points);
		try {
			PlayerFile.save(Murder.playerFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void joinArena(Arena arena) {
		final GameJoinEvent event = new GameJoinEvent(arena, p);
		Murder.instance.getServer().getPluginManager().callEvent(event);
		this.arena = arena;
		if (event.isCancelled()) return;

		if (resetRP) {
			ResourcePack.setResourcePack(p);
		}

		setInLobby();

		InventoryContent = p.getInventory().getContents();
		p.getInventory().clear();
		InventoryArmorContent = p.getInventory().getArmorContents();
		p.getInventory().setArmorContents(null);
		prevLocation = p.getLocation();
		prevGamemode = p.getGameMode();
		p.setGameMode(GameMode.ADVENTURE);
		prevLevel = p.getLevel();
		p.setLevel(0);
		prevExp = p.getExp();
		p.setExp(0F);
		prevHealth = ((Damageable) p).getHealth();
		((Damageable) p).setHealth(20D);
		prevFood = p.getFoodLevel();
		p.setWalkSpeed(0.2F);
		p.setFoodLevel(20);
		p.updateInventory();
		for (final PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		setInLobby();
		p.getInventory().setHeldItemSlot(0);

		p.setAllowFlight(false);
		p.setFlying(false);

		Game.join(arena.getID(), p);

	}

	public void leaveArena(boolean v) {
		leaveArena(true, v);
	}

	public void leaveArena(boolean broadcast, boolean v) {
		leaveArena(arena, broadcast, v);
	}

	@SuppressWarnings("deprecation")
	public void leaveArena(Arena arena, boolean broadcast, boolean v) {
		if (playing()) {
			if (resetRP) {
				ResourcePack.resetResourcePack(p);
			}

			p.getInventory().clear();
			p.updateInventory();
			if (p == null) return;
			if (p.getInventory() != null) {
				if (InventoryContent != null) {
					p.getInventory().setContents(InventoryContent);
				}
				InventoryContent = null;
				if (InventoryArmorContent != null) {
					p.getInventory().setArmorContents(InventoryArmorContent);
				}
				InventoryArmorContent = null;
			}
			if (p.getPassenger() != null) {
				p.getPassenger().eject();
				p.setPassenger(null);
			}
			if (prevLocation != null) {
				p.teleport(prevLocation);
			}
			prevLocation = null;
			if (prevGamemode != null) {
				p.setGameMode(prevGamemode);
				if (prevGamemode != GameMode.CREATIVE) {
					p.setAllowFlight(false);
					p.setFlying(false);
				}
			}
			prevGamemode = null;
			p.setLevel(prevLevel >= 0 ? prevLevel : 0);
			prevLevel = -1;
			p.setExp(prevExp >= 0 ? prevExp : 0);
			prevExp = -1F;
			if (prevHealth <= 20) {
				if (prevHealth >= 0) {
					p.setHealth(prevHealth);
				} else if (prevHealth != -1) {
					p.setHealth(0);
				}
			} else {
				p.setHealth(20);
			}
			prevHealth = -1;
			if (prevFood <= 20) {
				if (prevFood >= 0) {
					p.setFoodLevel((int) prevFood);
				} else if (prevFood != -1) {
					p.setFoodLevel(0);
				}
			} else {
				p.setFoodLevel(20);
			}
			prevFood = -1;
			removeEffects();

			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 255, false));

			for (final Player online : Murder.instance.getServer().getOnlinePlayers()) {
				online.showPlayer(p);
			}

			setLeft();

			if (arena != null) {
				Game.leave(arena.getID(), p, broadcast, v);
			}

			// for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
			// Murder.mod.refreshPlayer(p);
			// }
		}

		this.arena = null;
	}

	public void sendRawMessage(String message) {
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

	public void removeEffects() {
		final Player pl = p;
		final List<PotionEffect> effects = new ArrayList<PotionEffect>();
		for (final PotionEffect effect : pl.getActivePotionEffects()) {
			pl.removePotionEffect(effect.getType());
			effects.add(effect);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				for (final PotionEffect effect : effects) {
					pl.addPotionEffect(new PotionEffect(effect.getType(), 2, 1));
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

					@Override
					public void run() {

						for (final PotionEffect effect : pl.getActivePotionEffects()) {
							pl.removePotionEffect(effect.getType());
						}
						effects.clear();

					}
				}, 2);

			}
		}, 2);

		for (final Player online : Murder.instance.getServer().getOnlinePlayers()) {
			online.showPlayer(pl);
		}
		pl.getInventory().setHelmet(new ItemStack(Material.AIR));
		p.getInventory().setHeldItemSlot(0);

	}

	public Arena getArena() {
		return arena;
	}

	public boolean playing() {
		if (inGame || inLobby || inSpectate) return true;
		return false;
	}

	public boolean murdererDisguised() {
		return disguised;
	}

	public void setInLobby() {
		inLobby = true;
		inGame = false;
		inSpectate = false;

		p.setFlying(false);
		p.setAllowFlight(false);
	}

	public void setInGame() {
		inGame = true;
		inLobby = false;
		inSpectate = false;

		p.setFlying(false);
		p.setAllowFlight(false);

	}

	public void setInSpectate() {
		inSpectate = true;
		inGame = false;
		inLobby = false;

		p.setAllowFlight(true);

		p.setFlying(true);
		p.setAllowFlight(true);
	}

	public void setLeft() {
		inSpectate = false;
		inLobby = false;
		inGame = false;

		murderer = false;
		weapon = false;
		bystander = false;

	}

	public boolean isBystander() {
		return bystander || weapon;
	}

	public void setBystander(boolean b) {
		bystander = b;
		p.setFoodLevel(6);
	}

	public boolean isWeaponBystander() {
		return weapon;
	}

	public void setWeaponBystander(boolean b) {
		weapon = b;
		p.setFoodLevel(6);
	}

	public boolean isMurderer() {
		return murderer;
	}

	public void setMurderer(boolean b) {
		murderer = b;
		p.setFoodLevel(20);
	}

	public String getRole() {
		return isWeaponBystander() ? "§9WeaponBystander" : isMurderer() ? "§cMurderer" : isBystander() ? "§9Bystander" : "§8NONE";
	}

	public String getStatus() {
		return inGame() ? "inGame" : inLobby() ? "inLobby" : inSpectate() ? "inSpectate" : "NONE";
	}

	public boolean cantPickup() {
		return cantPickup;
	}

	public void cantPickup(boolean b) {
		cantPickup = b;
	}

	public boolean inGame() {
		return inGame;
	}

	public boolean inLobby() {
		return inLobby;
	}

	public boolean inSpectate() {
		return inSpectate;
	}

	public void disguiseMurderer(String name) {
		disguised = true;
		disguisedTag = name;
		try {
			Murder.mod.changeDisplay(p, Murder.SKIN_NAME, name);
		} catch (final Exception localException) {
		}
		p.setLevel(p.getLevel() - 1);
		p.sendMessage(Murder.prefix + Messages.getMessage("disguised").replace("%1$s", new StringBuilder("§r").append(name).toString()));
	}

	public Player player() {
		return p;
	}

	@Override
	public String toString() {
		return "MurderPlayer{name=" + p.getName() + ",nameTag=" + getNameTag() + ",arena=" + arena.getID() + ",role=" + getRole().replaceAll("(&([a-fk-or0-9]))", "") + ",status=" + getStatus() + ",points=" + getPoints() + "}";
	}

	// ////////

	public void sendInfo(Player sender) {
		sender.sendMessage("§7---------------------------------");
		sender.sendMessage("§7>> §2Player §a" + player().getName() + "§r(" + getNameTag() + (isMurderer() && disguisedTag != null ? "§r(Disguised as " + disguisedTag + "§r)" : "") + "§r) §7<<");
		sender.sendMessage("§7---------------------------------");
		MurderPlayer.getPlayer(sender).sendRawMessage("{\"text\":\"\",\"extra\":[{\"text\":\"Arena=\",\"color\":\"gray\"},{\"text\":\"" + getArena().getID() + "/" + getArena().getName() + "\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/murder arenainfo " + getArena().getID() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Click here to show Information for this Arena.\"}}]}");
		sender.sendMessage("§7Playing=§a" + playing());
		sender.sendMessage("§7InLobby=§a" + inLobby());
		sender.sendMessage("§7InGame=§a" + inGame());
		sender.sendMessage("§7Spectating=§a" + inSpectate());
		sender.sendMessage("§7Role=§a" + (isWeaponBystander() ? "§9WeaponBystander" : isMurderer() ? "§cMurderer" : isBystander() ? "§9Bystander" : "§8NONE"));
		sender.sendMessage("§7---------------------------------");
	}

}
