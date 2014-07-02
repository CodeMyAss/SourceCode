package de.inventivegames.theShip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Game {

	private static File			arenaFile;
	private static File			playerFile;

	static int[]				countdown		= new int[29];
	static int[]				countdownLobby	= new int[29];
	static int[]				delayedStart	= new int[29];

	static int[]				cd0				= new int[29];
	static int[]				cd1				= new int[29];
	static int[]				cd2				= new int[29];
	static int[]				cd3				= new int[29];

	static int[]				gameTimer		= new int[29];

	static ArrayList<Integer>	aNums			= new ArrayList<Integer>(Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30));
	static ArrayList<Integer>	bNums			= new ArrayList<Integer>(Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29));

	public static void joinArena(Player p, int arena) {
		Damageable dp = (Damageable) p;
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);

		if (Arenas.exists(arena)) {
			if (TheShip.playersAmount[arena] < TheShip.maxPlayers) {
				arenaFile = new File("plugins/TheShip/Arenas/" + arena + "/arena.yml");
				YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);
				if (ArenaFile.get("SpawnPoints.lobby.1") != null) {
					TheShip.InventoryContent.put(p, p.getInventory().getContents());
					p.getInventory().clear();
					TheShip.InventoryArmorContent.put(p, p.getInventory().getArmorContents());
					p.getInventory().setArmorContents(null);
					TheShip.prevLocation.put(p, p.getLocation());
					TheShip.prevGamemode.put(p, p.getGameMode());
					p.setGameMode(GameMode.ADVENTURE);
					TheShip.prevLevel.put(p, p.getLevel());
					p.setLevel(0);
					TheShip.prevExp.put(p, p.getExp());
					p.setExp(0);
					TheShip.prevHealth.put(p, dp.getHealth());
					dp.setHealth(20D);
					TheShip.prevFood.put(p, p.getFoodLevel());
					p.setFoodLevel(20);

					ResourcePack.setResourcePack(p, TheShip.serverVersion);

					sp.setArena(arena);
					sp.setInLobby();

					TheShip.playersAmount[arena] += 1;

					TheShip.players[arena][TheShip.playersAmount[arena]] = p;

					sp.setPlayerNumber(TheShip.playersAmount[arena]);

					Location loc = new Location((TheShip.instance.getServer().getWorld(ArenaFile.getString("World"))), (ArenaFile.getDouble("SpawnPoints.lobby.1.X")), (ArenaFile.getDouble("SpawnPoints.lobby.1.Y")), (ArenaFile.getDouble("SpawnPoints.lobby.1.Z")));

					p.teleport(loc);

					TheShip.sendArenaMessage(TheShip.prefix + "§1" + p.getName() + "§2 joined the Arena. " + TheShip.playersAmount[arena] + "/" + TheShip.maxPlayers, arena);

					if (TheShip.playersAmount[arena] == TheShip.minPlayers) {
						startDelayed(arena);
					}

					Signs.updateSigns(arena);
				}
			} else
				p.sendMessage("Arena is full");
		} else
			p.sendMessage("Arena doesn't exist");
	}

	public static void leaveArena(Player p, int arena) {
		ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		sp.setLeft();

		p.getInventory().setContents((ItemStack[]) TheShip.InventoryContent.get(p));
		p.getInventory().setArmorContents((ItemStack[]) TheShip.InventoryArmorContent.get(p));
		p.teleport((Location) TheShip.prevLocation.get(p));
		p.setGameMode((GameMode) TheShip.prevGamemode.get(p));
		p.setLevel((int) TheShip.prevLevel.get(p));
		p.setExp((float) TheShip.prevExp.get(p));
		p.setHealth((double) TheShip.prevHealth.get(p));
		p.setFoodLevel((int) TheShip.prevFood.get(p));

		p.getInventory().clear();
		p.getInventory().setContents((ItemStack[]) TheShip.InventoryContent.get(p));
		p.getInventory().setArmorContents((ItemStack[]) TheShip.InventoryArmorContent.get(p));
		p.teleport((Location) TheShip.prevLocation.get(p));
		p.setGameMode((GameMode) TheShip.prevGamemode.get(p));

		TheShip.prevExp.remove(p);
		TheShip.prevFood.remove(p);
		TheShip.prevGamemode.remove(p);
		TheShip.prevHealth.remove(p);
		TheShip.prevLevel.remove(p);
		TheShip.prevLocation.remove(p);

		if (TheShip.prevGamemode.get(p) != GameMode.CREATIVE) {
			p.setAllowFlight(false);
			p.setFlying(false);
		}

		for (Player online : TheShip.instance.getServer().getOnlinePlayers()) {
			online.showPlayer(p);
		}
	}

	public static void startDelayed(final int arena) {
		broadcastLobbyCountdown(arena);
		delayedStart[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

			@Override
			public void run() {
				startGame(arena);

			}

		}, 20 * TheShip.lobbyCountdown);
	}

	public static void startGame(final int arena) {
		if ((TheShip.playersAmount[arena] > TheShip.minPlayers) || (TheShip.playersAmount[arena] == TheShip.minPlayers)) {
			TheShip.inGame.add("" + arena);
			assign(arena);
			broadcastCountdown(arena);

			for (int i = 0; i < TheShip.playersAmount[arena] + 1; i++) {
				if (TheShip.players[arena][i] != null) {
					ShipPlayer sp = ShipPlayer.getShipPlayer(TheShip.players[arena][i], false);

					sp.setFrozen(true);
					sp.setBlind(true);
					sp.setInvisible(true);
				}
			}

			cd0[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

				@Override
				public void run() {
					teleportStart(arena);

				}
			}, 20 * (TheShip.countdown / 2));

			cd1[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < TheShip.playersAmount[arena] + 1; i++) {
						if (TheShip.players[arena][i] != null) {
							ShipPlayer sp = ShipPlayer.getShipPlayer(TheShip.players[arena][i], false);

							sp.setFrozen(false);
							sp.setBlind(false);
							sp.setInvisible(false);
						}
					}

					start(arena);

					TheShip.instance.getServer().getScheduler().cancelTask(countdown[arena]);
					TheShip.instance.getServer().getScheduler().cancelTask(countdownLobby[arena]);
					TheShip.instance.getServer().getScheduler().cancelTask(delayedStart[arena]);
				}

			}, 20 * TheShip.countdown);

			cd2[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

				@Override
				public void run() {
					TheShip.instance.getServer().getScheduler().cancelTask(countdown[arena]);
					TheShip.instance.getServer().getScheduler().cancelTask(countdownLobby[arena]);
					TheShip.instance.getServer().getScheduler().cancelTask(delayedStart[arena]);
				}

			}, (20 * TheShip.countdown) + 50);

			// cd3[arena] =
			// TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance,
			// new Runnable() {
			//
			// @Override
			// public void run() {
			// TheShip.instance.getServer().getScheduler().cancelTask(countdown[arena]);
			// TheShip.instance.getServer().getScheduler().cancelTask(countdownLobby[arena]);
			// TheShip.instance.getServer().getScheduler().cancelTask(delayedStart[arena]);
			// }
			//
			// }, (20 * TheShip.countdown) + 100);
		}
	}

	public static void assign(int arena) {
		int prev = -1;
		for (int i = 0; i < TheShip.playersAmount[arena] + 1; i++) {
//			System.out.println(" 0");
			if (TheShip.players[arena][i] != null) {
				Player p = TheShip.players[arena][i];
				ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
				int r = TheShip.rd.nextInt(TheShip.playersAmount[arena]) + 1;
				if (r == 0) {
					r += 1;
				}
				while ((!sp.hasQuarry())) {
//					System.out.println(" 01");
					if (i != r) {
//						System.out.println(" 001");
						if (TheShip.players[arena][r] != null) {
//							System.out.println(" 1");
							ShipPlayer quarry = ShipPlayer.getShipPlayer(TheShip.players[arena][r], false);

							if (checkAmount(TheShip.playersAmount[arena], prev, r)) {
//								System.out.println(" 2");

								if (((!quarry.hasHunter()))) {

//									System.out.println(" 3 ");
									sp.setQuarry(quarry);
									quarry.setHunter(sp);

									prev = i;

									break;
								} else {
									r = TheShip.rd.nextInt(TheShip.playersAmount[arena]) + 1;
								}
							} else {
								r = TheShip.rd.nextInt(TheShip.playersAmount[arena]) + 1;
							}

						} else {
							r = TheShip.rd.nextInt(TheShip.playersAmount[arena]) + 1;
						}
					} else {
						r = TheShip.rd.nextInt(TheShip.playersAmount[arena]) + 1;
					}
				}
			}
		}
	}

	public static boolean checkAmount(int amount, int prev, int i) {
		if (aNums.contains(amount)) {
//			System.out.println("1 true - " + amount + "   " + prev + "  " + i);
			return true;
		} else if (bNums.contains(amount)) {
			if (prev == i) {
//				System.out.println("2 false - " + amount + "   " + prev + "  " + i);
				return false;
			} else {
//				System.out.println("2 true - " + amount + "   " +  prev + "  " + i);
				return true;
			}
		}
		return false;
	}

	public static void start(int arena) {
		for (int i = 0; i < TheShip.playersAmount[arena] + 1; i++) {
			if (TheShip.players[arena][i] != null) {
				Player p = TheShip.players[arena][i];
				ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);

				sp.setInGame();

				SBoard sb = SBoard.getBoard(p);
				sb.setQuarry(sp.getQuarry(), null);
				sb.setStatus(false);
				sb.startTimer(TheShip.gameTimer);
				sb.updateMoney(sp.getMoney());
				sb.updateDisplay();

			}
		}
		Weapons.spawnWeapons(arena);
		gameTimer(arena);
	}

	public static void gameTimer(final int arena) {
		gameTimer[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

			@Override
			public void run() {
				stopDelayed(arena);
			}
		}, TheShip.gameTimer);
	}

	public static void stopDelayed(final int arena) {
		TheShip.sendArenaMessage(TheShip.prefix + "§2The game has ended.", arena);

		TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {

			@Override
			public void run() {
				stopGame(arena);
				for (int i = 0; i < TheShip.playersAmount[arena] + 1; i++) {
					if (TheShip.players[arena][i] != null) {
						Player p = TheShip.players[arena][i];
						ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);

						SBoard sb = SBoard.getBoard(p);

						sb.resetBoard();

					}
				}

				Weapons.despawn(arena);
			}
		}, 20 * 5);
	}

	public static void stopGame(int arena) {
		for (int m = 0; m < TheShip.maxPlayers; m++) {
			if (TheShip.players[arena][m] != null) {
				Player p = TheShip.players[arena][m];
				ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);

				TheShip.inGame.remove("" + arena);
				
				p.getInventory().clear();

				ResourcePack.resetResourcePack(p);

				p.getInventory().clear();
				p.getInventory().setContents((ItemStack[]) TheShip.InventoryContent.get(p));
				p.getInventory().setArmorContents((ItemStack[]) TheShip.InventoryArmorContent.get(p));
				p.teleport((Location) TheShip.prevLocation.get(p));
				p.setGameMode((GameMode) TheShip.prevGamemode.get(p));
				p.setLevel((int) TheShip.prevLevel.get(p));
				p.setExp((float) TheShip.prevExp.get(p));

				sp.setLeft();
				sp.saveMoneyToFile();

				SBoard sb = SBoard.getBoard(p);
				sb.resetBoard();

				TheShip.instance.getServer().getScheduler().cancelTask(Prison.cd0[arena]);
				TheShip.instance.getServer().getScheduler().cancelTask(Death.cd0[arena]);

				if (TheShip.prevGamemode.get(p) != GameMode.CREATIVE) {
					p.setAllowFlight(false);
					p.setFlying(false);
				}

				for (Player online : TheShip.instance.getServer().getOnlinePlayers()) {
					online.showPlayer(p);
				}

				TheShip.players[arena][m] = null;

				Death.kLogger[arena] = null;
			}
		}

		TheShip.playersAmount[arena] = 0;

		Signs.updateSigns(arena);
	}

	public static void broadcastCountdown(final int arena) {
		countdown[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncRepeatingTask(TheShip.instance, new Runnable() {
			int	time	= TheShip.countdown;

			@Override
			public void run() {
				if (time == 30) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Game starts in " + time + "s", arena);
				}
				if (time == 20) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Game starts in " + time + "s", arena);
				}
				if (time == 10) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Game starts in " + time + "s", arena);
				}
				if (time < 6) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Game starts in " + time + "s", arena);
				}
				if (time <= 1) {
					TheShip.instance.getServer().getScheduler().cancelTask(countdown[arena]);
				}
				time--;
			}

		}, 0, 20);

	}

	public static void broadcastLobbyCountdown(final int arena) {
		countdownLobby[arena] = TheShip.instance.getServer().getScheduler().scheduleSyncRepeatingTask(TheShip.instance, new Runnable() {
			int	time	= TheShip.lobbyCountdown;

			@Override
			public void run() {
				if (time == 30) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Lobby ends in " + time + "s", arena);
				}
				if (time == 20) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Lobby ends in " + time + "s", arena);
				}
				if (time == 10) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Lobby ends in " + time + "s", arena);
				}
				if (time < 6) {
					TheShip.sendArenaMessage(TheShip.prefix + "§2Lobby ends in " + time + "s", arena);
				}
				if (time <= 1) {
					TheShip.instance.getServer().getScheduler().cancelTask(countdownLobby[arena]);
				}
				time--;

			}

		}, 0, 20);

	}

	public static void teleportStart(int arena) {
		arenaFile = new File("plugins/TheShip/Arenas/" + arena + "/arena.yml");
		YamlConfiguration ArenaFile = YamlConfiguration.loadConfiguration(arenaFile);
		for (int i = 0; i < TheShip.maxPlayers; i++) {
			if (TheShip.players[arena][i] != null) {
				World world = TheShip.instance.getServer().getWorld(ArenaFile.getString("World"));
				double X = ArenaFile.getInt("SpawnPoints.players." + i + ".X");
				double Y = ArenaFile.getInt("SpawnPoints.players." + i + ".Y");
				double Z = ArenaFile.getInt("SpawnPoints.players." + i + ".Z");

				Location loc = new Location(world, X, Y, Z);

				TheShip.players[arena][i].teleport(loc);
			}
		}

	}

}
