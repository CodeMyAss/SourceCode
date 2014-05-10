package de.inventivegames.Murder;

import java.io.File;
import java.io.IOException;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.kitteh.tag.TagAPI;

public class Game {

	private static File		arenaFile;
	private static File		playerFile;
	private static File		playerFile2;

	private static int		m					= 0;

	static int				countdown			= -1;
	static int				countdownLobby		= -1;
	static int				loot				= -1;
	static int				delayedStart		= -1;

	public static boolean[]	BystanderSelected	= new boolean[24];
	public static boolean[]	MurdererSelected	= new boolean[24];
	public static boolean[]	rolesSelected		= new boolean[24];

	public static void joinArena(int arena, Player p) {
		Damageable dp = (Damageable) p;
		if (Murder.ArenaExists(arena)) {
			if (Murder.playersAmount[arena] < Murder.maxPlayers) {
				arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
				YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);
				if (ArenaFile.get("SpawnPoints.lobby.1") != null) {

					Murder.InventoryContent.put(p.getName(), p.getInventory().getContents());
					p.getInventory().clear();
					Murder.InventoryArmorContent.put(p.getName(), p.getInventory().getArmorContents());
					p.getInventory().setArmorContents(null);
					Murder.prevLocation.put(p.getName(), p.getLocation());
					Murder.prevGamemode.put(p.getName(), p.getGameMode());
					p.setGameMode(GameMode.ADVENTURE);
					Murder.prevLevel.put(p.getName(), p.getLevel());
					p.setLevel(0);
					Murder.prevExp.put(p.getName(), p.getExp());
					p.setExp(0);
					Murder.prevHealth.put(p.getName(), dp.getHealth());
					dp.setHealth(20D);
					Murder.prevFood.put(p.getName(), p.getFoodLevel());
					p.setFoodLevel(20);

					clearInv(p);

					ResourcePack.setResourcePack(p, Murder.serverVersion);

					Murder.playersInGame.add(p.getName());
					Murder.playerInLobby.put(p.getName(), arena);

					Murder.playersAmount[arena] += 1;

					Murder.players[arena][Murder.playersAmount[arena]] = p;

					Location loc = new Location((Murder.instance.getServer().getWorld(ArenaFile.getString("World"))), (ArenaFile.getDouble("SpawnPoints.lobby.1.X")), (ArenaFile.getDouble("SpawnPoints.lobby.1.Y")), (ArenaFile.getDouble("SpawnPoints.lobby.1.Z")));

					p.teleport(loc);

					Murder.sendArenaMessage(Murder.prefix + "§1" + Messages.getMessage("joinArena").replace("%1$s", p.getName()) + " " + Murder.playersAmount[arena] + "/" + (Murder.maxPlayers), arena);

					clearInv(p);
					if (Murder.playersAmount[arena] == Murder.minPlayers) {
						startDelayed(arena);
					}
				} else
					p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("lobbySpawnNotExisting"));
			} else
				p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaFull"));

		} else
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));

	}

	public static void leaveArena(int arena, Player p) {
		p.getInventory().setContents(Murder.InventoryContent.get(p.getName()));
		p.getInventory().setArmorContents(Murder.InventoryArmorContent.get(p.getName()));
		p.teleport(Murder.prevLocation.get(p.getName()));
		p.setGameMode(Murder.prevGamemode.get(p.getName()));
		p.setLevel(Murder.prevLevel.get(p.getName()));
		p.setExp(Murder.prevExp.get(p.getName()));
		p.setHealth(Murder.prevHealth.get(p.getName()));
		p.setFoodLevel(Murder.prevFood.get(p.getName()));

		Murder.playersInLobby.remove(p.getName());
		Murder.playerInLobby.remove(p.getName());
		Murder.playersInGame.remove(p.getName());
		Murder.playersInSpectate.remove(p.getName());

		removeEffects(arena);
		ResourcePack.resetResourcePack(p);

		if (Murder.prevGamemode.get(p.getName()) != GameMode.CREATIVE) {
			p.setAllowFlight(false);
			p.setFlying(false);
		}

		for (Player online : Murder.instance.getServer().getOnlinePlayers()) {
			online.showPlayer(p);
		}

		Murder.playersAmount[arena] -= 1;
		Murder.players[arena][Murder.getPlayerNumber(p, arena)] = null;

		p.getInventory().clear();

		Murder.nameTag.remove(p);

		ResourcePack.resetResourcePack(p);
		despawnLoot(arena);

		p.getInventory().setContents(Murder.InventoryContent.get(p.getName()));
		p.getInventory().setArmorContents(Murder.InventoryArmorContent.get(p.getName()));
		p.teleport(Murder.prevLocation.get(p.getName()));
		p.setGameMode(Murder.prevGamemode.get(p.getName()));
		Murder.playersInLobby.remove(p.getName());
		Murder.playerInLobby.remove(p.getName());
		Murder.playersInGame.remove(p.getName());
		Murder.playersInSpectate.remove(p.getName());
		Murder.Murderers.remove(p.getName());
		Murder.Bystanders.remove(p.getName());

		p.getInventory().clear();
		p.getInventory().setContents(Murder.InventoryContent.get(p.getName()));
		p.getInventory().setArmorContents(Murder.InventoryArmorContent.get(p.getName()));
		p.teleport(Murder.prevLocation.get(p.getName()));
		p.setGameMode(Murder.prevGamemode.get(p.getName()));
		Murder.playersInLobby.remove(p.getName());
		Murder.playerInLobby.remove(p.getName());
		Murder.playersInGame.remove(p.getName());
		Murder.playersInSpectate.remove(p.getName());
		Murder.Murderers.remove(p.getName());
		Murder.Bystanders.remove(p.getName());

		Murder.instance.getServer().getScheduler().cancelTask(loot);

		if (Murder.prevGamemode.get(p.getName()) != GameMode.CREATIVE) {
			p.setAllowFlight(false);
			p.setFlying(false);
		}

		for (Player online : Murder.instance.getServer().getOnlinePlayers()) {
			online.showPlayer(p);
		}

		File playerFile = new File("plugins/Murder/Players/" + p.getName() + ".yml");
		YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
		if ((PlayerFile.getInt("type") == 2)) {
			Murder.sendArenaMessage(Murder.prefix + "§cThe Murderer left the Arena!", arena);
			Murder.console.sendMessage(Murder.prefix + "§cCancelled Arena §2#" + arena + "§c The Murderer left the Arena");
			Game.stopGameDelayed(arena, 100);
		}
		if ((PlayerFile.getInt("type") == 1)) {
			Murder.sendArenaMessage(Murder.prefix + "§cThe Bystander with the Secret weapon left the Arena!", arena);
			Murder.console.sendMessage(Murder.prefix + "§cCancelled Arena §2#" + arena + "§c The Weapon Bystander left the Arena");
			Game.stopGameDelayed(arena, 100);
		}
		PlayerFile.set("type", 0);
		try {
			PlayerFile.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (((Murder.playersAmount[arena] < Murder.minPlayers) || (Murder.playersAmount[arena] == 1)) && (Murder.playersAmount[arena] != 0)) {
			Murder.instance.getServer().getScheduler().cancelTask(countdownLobby);
			Murder.instance.getServer().getScheduler().cancelTask(countdown);

			Murder.sendArenaMessage(Murder.prefix + "§cNot enough Players!", arena);
			Murder.sendArenaMessage(Murder.prefix + "§cStopping...!", arena);

			removeEffects(arena);
		}
		if (Murder.playersAmount[arena] == 0) {
			Murder.instance.getServer().getScheduler().cancelTask(countdownLobby);
			Murder.instance.getServer().getScheduler().cancelTask(countdown);

			Murder.console.sendMessage(Murder.prefix + "§cCancelled Arena §2#" + arena);

			removeEffects(arena);
			stopGame(arena);
		}

		Murder.sendArenaMessage(Murder.prefix + "§1" + Messages.getMessage("leaveArena").replace("%1$s", p.getName()), arena);
	}

	public static void startDelayed(final int arena) {
		broadcastLobbyCountdown(arena);
		delayedStart = Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				startGame(arena);

			}

		}, 20 * 30);
	}

	public static void startGame(final int arena) {

		if ((Murder.playersAmount[arena] > Murder.minPlayers) || (Murder.playersAmount[arena] == Murder.minPlayers)) {
			Murder.inGame.add("" + arena);
			for (int i = 0; i < Murder.maxPlayers; i++) {
				if (Murder.players[arena][i] != null) {
					Murder.players[arena][i].getWorld().setDifficulty(Difficulty.PEACEFUL);
					Murder.playersInGame.add(Murder.players[arena][i].getName());
					Murder.playersInLobby.remove(Murder.players[arena][i]);
				}
			}

			BystanderSelected[arena] = false;
			MurdererSelected[arena] = false;
			rolesSelected[arena] = false;

			Murder.gameStarted.add(arena);
			preAssign(arena);
			addEffects(arena);
			broadcastCountdown(arena);
			checkRoles(arena);
			Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

				@Override
				public void run() {
					assign(arena);
					removeEffects(arena);
					teleportStart(arena);

					Murder.instance.getServer().getScheduler().cancelTask(countdown);
					Murder.instance.getServer().getScheduler().cancelTask(countdownLobby);
					Murder.instance.getServer().getScheduler().cancelTask(delayedStart);
				}

			}, 600L);

			Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

				@Override
				public void run() {
					Murder.instance.getServer().getScheduler().cancelTask(countdown);
					Murder.instance.getServer().getScheduler().cancelTask(countdownLobby);
					Murder.instance.getServer().getScheduler().cancelTask(delayedStart);
				}

			}, 650L);

			Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

				@Override
				public void run() {
					spawnLoot(arena);

					Murder.instance.getServer().getScheduler().cancelTask(countdown);
					Murder.instance.getServer().getScheduler().cancelTask(countdownLobby);
					Murder.instance.getServer().getScheduler().cancelTask(delayedStart);
				}

			}, 1000L);
		}
	}

	public static void startGame(final int arena, Player p) {
		if (Murder.ArenaExists(arena)) {
			if ((Murder.playersAmount[arena] > Murder.minPlayers) || (Murder.playersAmount[arena] == Murder.minPlayers)) {
				startGame(arena);
			} else
				p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("notEnoughPlayers"));
		} else
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
	}

	public static void broadcastCountdown(final int arena) {
		countdown = Murder.instance.getServer().getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {
			int	time	= 30;

			@Override
			public void run() {
				if (time == 30) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", "" + time), arena);
				}
				if (time == 20) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", "" + time), arena);
				}
				if (time == 10) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", "" + time), arena);
				}
				if (time < 6) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", "" + time), arena);
				}
				if (time < 1) {
					Murder.instance.getServer().getScheduler().cancelTask(countdown);
				}
				time--;
			}

		}, 0, 20);

	}

	public static void broadcastLobbyCountdown(final int arena) {
		countdownLobby = Murder.instance.getServer().getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {
			int	time	= 30;

			@Override
			public void run() {
				if (time == 30) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", "" + time), arena);
				}
				if (time == 20) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", "" + time), arena);
				}
				if (time == 10) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", "" + time), arena);
				}
				if (time < 6) {
					Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", "" + time), arena);
				}
				if (time < 1) {
					Murder.instance.getServer().getScheduler().cancelTask(countdownLobby);
				}
				time--;

			}

		}, 0, 20);

	}

	public static void teleportStart(int arena) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);
		for (int i = 0; i < Murder.maxPlayers; i++) {
			if (Murder.players[arena][i] != null) {
				World world = Murder.instance.getServer().getWorld(ArenaFile.getString("World"));
				double X = ArenaFile.getInt("SpawnPoints.players." + i + ".X");
				double Y = ArenaFile.getInt("SpawnPoints.players." + i + ".Y");
				double Z = ArenaFile.getInt("SpawnPoints.players." + i + ".Z");

				Location loc = new Location(world, X, Y, Z);

				Murder.players[arena][i].teleport(loc);
			}
		}
	}

	public static void preAssign(int arena) {

		BystanderSelected[arena] = false;
		MurdererSelected[arena] = false;
		rolesSelected[arena] = false;

		Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - BystanderSelected: " + BystanderSelected[arena]);
		Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - MurdererSelected: " + MurdererSelected[arena]);
		Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - RolesAssigned: " + rolesSelected[arena]);
		int randomNumber = Murder.rd.nextInt(Murder.playersAmount[arena]);
		if (randomNumber == 0) {
			randomNumber += 1;
		}
		m = randomNumber;
		while (MurdererSelected[arena] == false && Murder.murderers[arena][m] == null) {

			if (Murder.players[arena][m] != null) {

				playerFile = new File("plugins/Murder/Players/" + Murder.players[arena][m].getName() + ".yml");
				YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
				if (((PlayerFile.getInt("type") != 2) && (PlayerFile.getInt("type") != 1)) && (MurdererSelected[arena] == false)) {
					Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - Assigning Murderer: " + Murder.getPlayerName(arena, m));
					PlayerFile.set("type", 2);
					Murder.murderers[arena][m] = Murder.getPlayerName(arena, m);
					Murder.Murderers.add(Murder.getPlayerName(arena, m).getName());
					try {
						PlayerFile.save(playerFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (Murder.murderers[arena][m] != null) {
						MurdererSelected[arena] = true;
					}
					if (m <= Murder.playersAmount[arena]) {
						m++;
					} else
						m--;
					preAssign2(arena);
					break;
				}
			} else if (m <= Murder.playersAmount[arena]) {
				m++;
				return;
			} else {
				m--;
				return;
			}
			if (m <= Murder.playersAmount[arena]) {
				m++;
				return;
			} else {
				m--;
				return;
			}
		}

		hideTags(arena);

	}

	public static void preAssign2(int arena) {
		while (BystanderSelected[arena] == false && MurdererSelected[arena] == true && Murder.weaponBystanders[arena][m] == null) {
			if (Murder.players[arena][m] != null) {

				playerFile2 = new File("plugins/Murder/Players/" + Murder.players[arena][m].getName() + ".yml");
				YamlConfiguration PlayerFile1 = YamlConfiguration.loadConfiguration(playerFile2);
				if (((PlayerFile1.getInt("type") != 2) && (PlayerFile1.getInt("type") != 1)) && (Murder.murderers[arena][m] != Murder.players[arena][m]) && (BystanderSelected[arena] == false)) {

					Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - Assigning WeaponBystander: " + Murder.getPlayerName(arena, m));
					PlayerFile1.set("type", 1);
					Murder.weaponBystanders[arena][m] = Murder.getPlayerName(arena, m);
					Murder.Bystanders.add(Murder.getPlayerName(arena, m).getName());
					try {
						PlayerFile1.save(playerFile2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					BystanderSelected[arena] = true;
					break;
				}
			} else if (m <= Murder.playersAmount[arena]) {
				m++;
			} else {
				m--;
			}
			if (m <= Murder.playersAmount[arena]) {
				m++;
				return;
			} else {
				m--;
				return;
			}

		}
	}

	public static void checkRoles(int arena) {
		if ((BystanderSelected[arena] = true) && (MurdererSelected[arena] = true)) {
			rolesSelected[arena] = true;
		}

		Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - BystanderSelected: " + BystanderSelected[arena]);
		Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - MurdererSelected: " + MurdererSelected[arena]);
		Murder.console.sendMessage(Murder.prefix + "§3Arena §2#" + arena + "§3 - RolesAssigned: " + rolesSelected[arena]);
	}

	public static void assign(int arena) {

		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				String pn = Murder.players[arena][m].getName();

				playerFile = new File("plugins/Murder/Players/" + Murder.players[arena][m].getName() + ".yml");
				YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);

				if ((PlayerFile.getInt("type") == 1)) {
					Murder.Bystanders.add(pn);
					Murder.playersInLobby.remove(pn);
					Murder.playerInGame.put(pn, arena);

					Murder.players[arena][m].sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystanderWeapon"));

					Murder.players[arena][m].getInventory().setItem(4, Murder.Gun());
					Murder.players[arena][m].getInventory().setItem(8, Murder.Bullet());
					Murder.players[arena][m].getInventory().setItem(0, Murder.NameInfo(Murder.players[arena][m]));

					Murder.playerType.put(pn, 1);
					PlayerFile.set("type", 1);
					Murder.bystanderAmount[arena] += 1;

					try {
						PlayerFile.save(playerFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else

				if ((PlayerFile.getInt("type") == 2)) {
					Murder.Murderers.add(pn);
					Murder.playersInLobby.remove(pn);
					Murder.playerInGame.put(pn, arena);

					Murder.players[arena][m].sendMessage(Murder.prefix + "§c§l" + Messages.getMessage("murderer"));

					Murder.players[arena][m].getInventory().setItem(4, Murder.Knife());
					Murder.players[arena][m].getInventory().setItem(0, Murder.NameInfo(Murder.players[arena][m]));

					Murder.playerType.put(pn, 2);
					PlayerFile.set("type", 2);

					try {
						PlayerFile.save(playerFile);
					} catch (IOException e) {
						e.printStackTrace();
					}

					Murder.console.sendMessage(Murder.prefix + "§7" + Murder.players[arena][m].getName() + " §8is Murderer in Arena #" + arena);
				} else if ((PlayerFile.getInt("type") == 0)) {
					/* 285 */Murder.Bystanders.add(pn);
					Murder.playersInLobby.remove(pn);
					Murder.playerInGame.put(pn, arena);

					Murder.players[arena][m].sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystander"));
					Murder.bystanders[arena][m] = Murder.players[arena][m];

					Murder.players[arena][m].getInventory().setItem(0, Murder.NameInfo(Murder.players[arena][m]));

					Murder.playerType.put(pn, 0);
					PlayerFile.set("type", 0);
					Murder.bystanders[arena][m] = Murder.getPlayerName(arena, m);

					Murder.bystanderAmount[arena] += 1;
					try {
						PlayerFile.save(playerFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		}

	}

	public static void stopGame(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Player p = Murder.players[arena][m];

				p.getInventory().clear();

				Murder.nameTag.remove(p);
				Murder.invisibleTags.remove(p);

				if (Players.murdererDisguise.contains(p)) {
					Players.murdererDisguise.remove(p);
				}

				ResourcePack.resetResourcePack(p);
				despawnLoot(arena);

				p.getInventory().setContents(Murder.InventoryContent.get(p.getName()));
				p.getInventory().setArmorContents(Murder.InventoryArmorContent.get(p.getName()));
				p.teleport(Murder.prevLocation.get(p.getName()));
				p.setGameMode(Murder.prevGamemode.get(p.getName()));
				Murder.playersInLobby.remove(p.getName());
				Murder.playerInLobby.remove(p.getName());
				Murder.playersInGame.remove(p.getName());
				Murder.playersInSpectate.remove(p.getName());
				Murder.Murderers.remove(p.getName());
				Murder.Bystanders.remove(p.getName());

				try {
					Corpses.despawnCorpse(p);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				for (int l = 0; l < Murder.Items.length; l++) {
					if (Murder.Items[arena][l] != null) {
						Murder.Items[arena][l].remove();
					}
				}

				p.getInventory().clear();
				p.getInventory().setContents(Murder.InventoryContent.get(p.getName()));
				p.getInventory().setArmorContents(Murder.InventoryArmorContent.get(p.getName()));
				p.teleport(Murder.prevLocation.get(p.getName()));
				p.setGameMode(Murder.prevGamemode.get(p.getName()));
				Murder.playersInLobby.remove(p.getName());
				Murder.playerInLobby.remove(p.getName());
				Murder.playersInGame.remove(p.getName());
				Murder.playersInSpectate.remove(p.getName());
				Murder.Murderers.remove(p.getName());
				Murder.Bystanders.remove(p.getName());

				Murder.instance.getServer().getScheduler().cancelTask(loot);

				if (Murder.prevGamemode.get(p.getName()) != GameMode.CREATIVE) {
					p.setAllowFlight(false);
					p.setFlying(false);
				}

				for (Player online : Murder.instance.getServer().getOnlinePlayers()) {
					online.showPlayer(p);
				}

				unHideTags(arena);

				Murder.invisibleTags.remove(p);

				File playerFile = new File("plugins/Murder/Players/" + Murder.players[arena][m].getName() + ".yml");
				YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
				PlayerFile.set("type", 0);
				try {
					PlayerFile.save(playerFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				TagAPI.refreshPlayer(p);

				Murder.inGame.remove("" + arena);
			}
		}
		removeEffects(arena);
		Murder.playersAmount[arena] = 0;
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				for (Player online : Murder.instance.getServer().getOnlinePlayers()) {
					online.showPlayer(Murder.players[arena][m]);
				}
			}

			Murder.players[arena][m] = null;
			Murder.murderers[arena][m] = null;
			Murder.bystanders[arena][m] = null;
			Murder.weaponBystanders[arena][m] = null;
		}

	}

	public static void printScoreboard(int arena) {
		for (int i = 0; i < Murder.playersAmount[arena] + 1; i++) {
			if (Murder.players[arena][i] != null) {
				Player p = Murder.players[arena][i];
				String playerName = p.getName();
				String nameTag = Murder.nameTag.get(p);
				if (nameTag != null) {
					String tagColor = nameTag.substring(0, 2);
					int collectedLoot = p.getLevel();

					String nameSpace = "";
					for (int x = 0; x < 18 - playerName.length(); x++) {
						nameSpace += " ";
					}

					String tagSpace = "";
					for (int x = 0; x < 18 - nameTag.length(); x++) {
						tagSpace += " ";
					}

					String score = "            " + tagColor + playerName + nameSpace + " §7|§r " + nameTag + tagSpace + " §7|§r " + tagColor + collectedLoot;

					Murder.sendArenaMessage(score, arena);
				}
			}
		}
	}

	public static void stopGameDelayed(int a, long delay) {
		final int arena = a;

		printScoreboard(arena);

		unHideTags(arena);
		Murder.getBukkit().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				Game.stopGame(arena);
			}

		}, delay);
	}

	public static void addEffects(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Player pl = Murder.players[arena][m];
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 255));
				pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147000, 255));
				pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147000, 128));
				pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 255));
				pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2147000, 255));
			}
		}
	}

	public static void removeEffects(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Player pl = Murder.players[arena][m];
				for (PotionEffect effect : pl.getActivePotionEffects()) {
					pl.removePotionEffect(effect.getType());
				}
				for (Player online : Murder.instance.getServer().getOnlinePlayers()) {
					online.showPlayer(Murder.players[arena][m]);
				}
			}

		}
	}

	public static void setResourcePack(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Player pl = Murder.players[arena][m];
				pl.setResourcePack("");
			}
		}
	}

	public static void hideTags(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Murder.invisibleTags.add(Murder.players[arena][m]);
				TagAPI.refreshPlayer(Murder.players[arena][m]);
			}
		}
	}

	public static void refreshTags(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Murder.invisibleTags.add(Murder.players[arena][m]);
				TagAPI.refreshPlayer(Murder.players[arena][m]);
			}
		}
	}

	public static void unHideTags(int arena) {
		for (int m = 0; m < Murder.maxPlayers; m++) {
			if (Murder.players[arena][m] != null) {
				Murder.invisibleTags.remove(Murder.players[arena][m]);
				TagAPI.refreshPlayer(Murder.players[arena][m]);
				Murder.hasTag.remove(Murder.players[arena][m]);
			}
		}
	}

	public static void spawnLoot(final int arena) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		final YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		final double randomTime = Math.random() * (60 - 10);

		loot = Murder.instance.getServer().getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {

			int		Number	= 1;
			double	X		= 0;
			double	Y		= 0;
			double	Z		= 0;

			@Override
			public void run() {

				World world = Murder.instance.getServer().getWorld(ArenaFile.getString("World"));
				X = ArenaFile.getDouble("SpawnPoints.loot." + Number + ".X");
				Y = ArenaFile.getDouble("SpawnPoints.loot." + Number + ".Y") + 1;
				Z = ArenaFile.getDouble("SpawnPoints.loot." + Number + ".Z");
				if ((X != 0.0) && (Y != 0.0) && (Z != 0.0)) {
					Murder.Loot[arena][Number] = world.dropItemNaturally(new Location(world, X, Y, Z), Murder.Loot());
					Murder.console.sendMessage(Murder.prefix + "§7Spawning Loot in Arena " + arena + " at " + X + " " + Z + " " + Z);
					Number++;
				} else
					Number = 1;
			}

		}, (int) randomTime * 20, (int) randomTime * 20);
	}

	public static void despawnLoot(int arena) {
		Murder.instance.getServer().getScheduler().cancelTask(loot);
		for (int i = 0; i < 50; i++) {
			try {
				Murder.Loot[arena][i].remove();
			} catch (Exception ex) {

			}
		}
	}

	public static void forceMurderer(int arena, Player p) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		if (arenaFile.exists()) {
			ArenaFile.set("type", 2);
		}
	}

	@SuppressWarnings({ "null" })
	public static void forceMurderer(int arena, String string, Player sender) {
		Player p = Murder.instance.getServer().getPlayerExact(string);
		if (p != null) {
			forceMurderer(arena, p);
		} else
			sender.sendMessage(Murder.prefix + "§cPlayer §4" + p.getName() + " §c is not online!");
	}

	public static void forceWeapon(int arena, Player p) {
		arenaFile = new File("plugins/Murder/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);

		if (arenaFile.exists()) {
			ArenaFile.set("type", 2);
		}
	}

	@SuppressWarnings({ "null" })
	public static void forceWeapon(int arena, String string, Player sender) {
		Player p = Murder.instance.getServer().getPlayerExact(string);
		if (p != null) {
			forceWeapon(arena, p);
		} else
			sender.sendMessage(Murder.prefix + "§cPlayer §4" + p.getName() + " §c is not online!");
	}

	public static void joinArena(String string, Player p) {
		int arena = Integer.parseInt(string);
		joinArena(arena, p);
	}

	public static void leaveArena(String string, Player p) {
		int arena = Integer.parseInt(string);
		leaveArena(arena, p);
	}

	public static void startGame(String string, Player p) {
		int arena = Integer.parseInt(string);
		startGame(arena);
		p.sendMessage(Murder.prefix + "§2started Arena #" + arena);
	}

	@SuppressWarnings("deprecation")
	public static void clearInv(Player p) {
		p.getInventory().clear();
		p.updateInventory();
	}

}
