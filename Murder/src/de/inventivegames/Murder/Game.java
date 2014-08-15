package de.inventivegames.murder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.inventivegames.murder.event.GameEndEvent;
import de.inventivegames.murder.event.GameStartEvent;
import de.inventivegames.murder.event.PlayerKillEvent;
import de.inventivegames.utils.xptimer.XPTimer;

public class Game {

	public static void join(String name, Player p) {
		join(ArenaManager.getIDByName(name), p);
	}

	public static void join(int id, Player p) {
		final Arena arena = ArenaManager.getByID(id);
		if (arena == null) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaNotExisting"));
			return;
		}
		if (!(arena.getPlayerAmount() <= Murder.maxPlayers)) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaFull"));
			arena.setStatus(ArenaStatus.FULL);
			return;
		}
		if (!arena.spawnpointExists(SpawnType.LOBBY)) {
			p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("lobbySpawnNotExisting"));
			return;
		}

		arena.join(p);
	}

	public static void leave(int id, Player p, boolean broadcast, boolean v) {
		final Arena arena = ArenaManager.getByID(id);

		arena.leave(p, broadcast, v);
	}

	// ////////////////////////////////////////////////////////////////

	private final Arena	arena;
	@SuppressWarnings("unused")
	private boolean		started		= false;
	// Schedulers
	private int			delayedStart;
	private int			countdown;
	private int			countdownLobby;
	public int			smokeDelay;
	public boolean		stopping	= false;
	public boolean		stopped		= false;
	private int			loot;
	private int			cd0;
	private int			cd1;
	private int			cd2;
	private int			cd3;
	@SuppressWarnings("unused")
	private int			cd4;

	public Game(Arena arena) {
		this.arena = arena;
	}

	public void cancelAllTaks() {
		cancelTaks(delayedStart, countdown, countdownLobby, smokeDelay, loot, cd0, cd1, cd2, cd3, arena.knifeTimer, arena.knifeTimer, arena.reloadTimer, arena.speedTimer);
	}

	private void cancelTaks(int... integers) {
		for (final int i : integers) {
			Bukkit.getScheduler().cancelTask(i);
		}
	}

	@SuppressWarnings("deprecation")
	public void onPlayerDeath(PlayerDeathEvent e) {
		Murder.console.sendMessage(Murder.debugPrefix + "Death");
		if (e.getEntity() instanceof Player) {
			final Player p = e.getEntity();
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.inGame()) {
				Murder.console.sendMessage(Murder.debugPrefix + "Death1");
				e.setDeathMessage(null);

				for (double d = 0.0; d < 2.0; d += 0.5) {
					// e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation().add(0D,
					// d, 0D), Effect.STEP_SOUND,
					// Material.REDSTONE_BLOCK.getId());
					ParticleEffects.displayBlockDust(e.getEntity().getLocation().add(0, d, 0), Material.REDSTONE_BLOCK.getId(), (byte) 0, 0, 0, 0, 0.1f, 5);
				}

				p.getInventory().setHeldItemSlot(0);

				p.setHealth(20);
				e.getDrops().clear();
				p.setAllowFlight(true);
				p.setFlying(true);

				p.getInventory().setHeldItemSlot(0);

				removeArrowsInPlayer(p);

				p.setLevel(0);
				if (p.getLevel() != 0 && p.getLevel() < 5) {
					for (int i = 0; i < p.getLevel(); i++) {
						final Item loot = p.getWorld().dropItemNaturally(p.getLocation(), Items.Loot());
						arena.addItem(loot);
					}
				}
				if (e.getEntity().getKiller() instanceof Player) {

					final Player killer = e.getEntity().getKiller();
					final MurderPlayer mpKiller = MurderPlayer.getPlayer(killer);
					Murder.console.sendMessage(Murder.debugPrefix + mpKiller + " killed " + mp);
					Murder.console.sendMessage(Murder.debugPrefix + "Death2");
					mp.setInSpectate();
					p.sendMessage(Murder.prefix + Messages.getMessage("spectator"));

					for (final Player online : Murder.instance.getServer().getOnlinePlayers()) {
						online.hidePlayer(p);
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
						@Override
						public void run() {
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 255, false));

							p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 255, false));
						}
					}, 5L);

					Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
						@Override
						public void run() {
							p.getInventory().setItem(0, Items.Compass());
							if (!mpKiller.inSpectate()) {
								killer.setPassenger(p);
							}
						}
					}, 20L);
					// if (mp.isBystander())
					// mpKiller.getArena().bystanderAmount--;
					if (mpKiller.isMurderer()) {

						// p.getWorld().dropItemNaturally(p.getLocation(),
						// Items.Knife());
						try {
							if (arena.getWeaponBystanders().contains(mp)) {
								arena.removeWeaponBystander(mp);
							}
						} catch (final Exception e1) {
						}
						Murder.console.sendMessage(Murder.debugPrefix + "BystanderAmount(Death) " + arena.getBystanderAmount());
						if (arena.getBystanderAmount() <= 0) {
							arena.sendMessage(Murder.prefix + "§c" + Messages.getMessage("murdererWin1"));
							arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("murdererWin2").replace("%1$s", mpKiller.getNameTag()).replace("%2$s", killer.getName()));

							Murder.instance.getServer().getPluginManager().callEvent(new PlayerKillEvent(arena, true, "murderer", killer, p, false));

							arena.game.stopDelayed(200L);

						}

						if (p.getInventory().contains(Items.Gun())) {
							Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
								@Override
								public void run() {
									final Item gun = p.getWorld().dropItemNaturally(p.getLocation(), Items.Gun());
									arena.addItem(gun);
								}
							}, 2L);
						}
					} else if (mpKiller.isWeaponBystander()) {
						if (mp.isBystander()) {

							arena.sendMessage(Murder.prefix + "§1" + Messages.getMessage("killedInnocent").replace("%1$s", killer.getName()));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 1, false));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 1, false));

							Murder.instance.getServer().getPluginManager().callEvent(new PlayerKillEvent(arena, false, "bystander", killer, p, true));

							mpKiller.cantPickup(true);
							mpKiller.setWeaponBystander(false);
							arena.removeWeaponBystander(mpKiller);
							mpKiller.setBystander(true);

							final ItemStack nameTag = killer.getInventory().getItem(0);

							Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
								@Override
								public void run() {
									// killer.getInventory().clear();
									killer.getInventory().remove(Items.Gun().getType());
									killer.getInventory().remove(Items.Gun());
									killer.getInventory().setItem(4, null);
									killer.getItemInHand().setType(Material.AIR);
									killer.getInventory().getItemInHand().setType(Material.AIR);
									killer.getInventory().getItemInHand().setAmount(0);
									killer.updateInventory();

									killer.getInventory().setItem(0, nameTag);
								}
							}, 1L);

							Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
								@Override
								public void run() {
									final Item gun = killer.getWorld().dropItemNaturally(killer.getLocation(), Items.Gun());
									arena.addItem(gun);
								}
							}, 2L);

							killer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 1, false));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 1, false));

							Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
								@Override
								public void run() {
									final Player pl = killer;
									for (final PotionEffect effect : pl.getActivePotionEffects()) {
										pl.removePotionEffect(effect.getType());
									}
								}
							}, 200L);

							Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
								@Override
								public void run() {
									mpKiller.cantPickup(false);

								}
							}, 600L);
						} else if (mp.isMurderer()) {
							arena.sendMessage(Murder.prefix + "§1" + Messages.getMessage("killedMurderer").replace("%1$s", killer.getName()));
							arena.sendMessage(Murder.prefix + "§1" + Messages.getMessage("bystanderWin1"));
							arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("bystanderWin2").replace("%1$s", mp.getNameTag()).replace("%2$s", p.getName()));

							Murder.instance.getServer().getPluginManager().callEvent(new PlayerKillEvent(arena, true, "bystander", killer, p, false));

							arena.game.stopDelayed(200L);
						}
					}
					try {
						Corpses.spawnCorpse(p.getLocation(), p);
					} catch (final Exception e2) {
						e2.printStackTrace();
					}
					Murder.instance.getServer().getPluginManager().callEvent(new de.inventivegames.murder.event.PlayerDeathEvent(p, arena, killer));
				} else if (e.getEntity().getKiller() instanceof Arrow) {
					arena.despawnKnifes();
					final Item item = p.getLocation().getWorld().dropItemNaturally(p.getLocation().add(0.0D, 1.0D, 0.0D), Items.Knife());
					arena.addItem(item);
				}
				p.teleport(e.getEntity().getLocation());
			}
		}

	}

	public static void removeArrowsInPlayer(Player p) {
		final WrappedDataWatcher data = new WrappedDataWatcher();
		data.setEntity(p);
		data.setObject(9, Byte.valueOf((byte) 0));
	}

	public void startDelayed() {
		// if(started) return;
		broadcastLobbyCountdown();
		arena.starting(true);
		started = true;
		if (arena.getPlayerAmount() >= Murder.minPlayers) {
			if (!Bukkit.getScheduler().isCurrentlyRunning(delayedStart)) {
				delayedStart = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

					@Override
					public void run() {
						start();
						Bukkit.getScheduler().cancelTask(delayedStart);
						delayedStart = -1;
					}

				}, 20 * Murder.lobbyCountdown);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void start() {
		if (arena.getAlivePlayerAmount() < 2) return;
		if (arena.timer != null) {
			arena.timer.cancel();
		}
		arena.despawnAllArrows();
		arena.despawnKnifes();
		arena.despawnAllItems();

		if (arena.getPlayerAmount() >= Murder.minPlayers) {
			arena.setStatus(ArenaStatus.INGAME);
			arena.starting(false);
			arena.inGame(true);
			for (final Player p : arena.getPlayers()) {
				p.getWorld().setDifficulty(Difficulty.PEACEFUL);
				p.getInventory().clear();
				p.updateInventory();
				// mp.setInGame();
			}
		}
		arena.addEffects();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				assign();
			}
		}, 5L);
		broadcastCountdown();

		if (!Bukkit.getScheduler().isCurrentlyRunning(cd0)) {
			cd0 = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					teleport();
					Bukkit.getScheduler().cancelTask(cd0);
				}
			}, 20 * (Murder.countdown / 2) + 1);
		}

		// Murder.console.sendMessage(Murder.debugPrefix + "EffectTaskRunning:"
		// +
		// Bukkit.getScheduler().isCurrentlyRunning(cd1));
		if (!Bukkit.getScheduler().isCurrentlyRunning(cd1)) {
			cd1 = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					giveItems();
					for (final Player p : arena.getPlayers()) {
						final MurderPlayer mp = MurderPlayer.getPlayer(p);
						mp.removeEffects();
						// Murder.console.sendMessage(Murder.debugPrefix +
						// "Removing " +
						// mp.player().getName() + " Effects");
					}

					startSmoke();

					Bukkit.getScheduler().cancelTask(countdown);
					Bukkit.getScheduler().cancelTask(countdownLobby);
					Bukkit.getScheduler().cancelTask(delayedStart);

					Bukkit.getScheduler().cancelTask(cd1);
				}
			}, 20 * Murder.countdown);
		}

		if (!Bukkit.getScheduler().isCurrentlyRunning(cd2)) {
			cd2 = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					Bukkit.getScheduler().cancelTask(countdown);
					Bukkit.getScheduler().cancelTask(countdownLobby);
					Bukkit.getScheduler().cancelTask(delayedStart);

					Bukkit.getScheduler().cancelTask(cd2);
				}
			}, 20 * Murder.countdown + 50);
		}

		if (!Bukkit.getScheduler().isCurrentlyRunning(cd3)) {
			cd3 = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					spawnLoot();

					Bukkit.getScheduler().cancelTask(countdown);
					Bukkit.getScheduler().cancelTask(countdownLobby);
					Bukkit.getScheduler().cancelTask(delayedStart);

					Bukkit.getScheduler().cancelTask(cd3);
				}
			}, 20 * Murder.countdown + 100);
		}

		Murder.instance.getServer().getPluginManager().callEvent(new GameStartEvent(arena));
		Signs.updateSigns(arena.getID());
	}

	public void stop() {
		if (stopped) return;
		Murder.console.sendMessage(Murder.debugPrefix + "Stop");
		Bukkit.getScheduler().cancelTask(loot);
		Bukkit.getScheduler().cancelTask(arena.knifeTimer);
		Bukkit.getScheduler().cancelTask(arena.reloadTimer);

		Corpses.despawnCorpse(arena);
		String winner = "murderer";

		arena.started = false;

		final List<Player> players = new ArrayList<Player>(arena.getPlayers());

		unDisguisePlayers();

		despawnLoot();

		Corpses.despawnCorpse(arena);

		if (arena.getBystanderAmount() == 0) {
			winner = "murderer";
		} else {
			winner = "bystanders";
		}
		arena.setStatus(ArenaStatus.WAITING);
		Murder.instance.getServer().getPluginManager().callEvent(new GameEndEvent(arena, players, winner));

		for (final Item item : arena.getItems()) {
			if (item != null) {
				item.remove();
			}
		}

		arena.despawnAllItems();

		arena.despawnAllArrows();

		for (final Player p : players) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			mp.leaveArena(false, false);
			mp.setNameTag(null);
			mp.removeEffects();
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				arena.reset();

			}
		}, 1);

		stopped = true;

		arena.setStatus(ArenaStatus.WAITING);
		Signs.updateSigns(arena.getID());

	}

	public void stopDelayed(long l) {
		if (stopping) return;
		printScoreboard();
		stopping = true;
		Bukkit.getScheduler().cancelTask(loot);
		Bukkit.getScheduler().cancelTask(arena.knifeTimer);
		Bukkit.getScheduler().cancelTask(arena.reloadTimer);
		cancelAllTaks();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
			@Override
			public void run() {
				stop();
			}
		}, l);
	}

	public void broadcastLobbyCountdown() {
		for (final Player p : arena.getPlayers()) {
			arena.timer = new XPTimer(Murder.utils).withInterval(20).withTime(Murder.lobbyCountdown).withText();
			arena.timer.forPlayer(p).start();
		}
		if (!Bukkit.getScheduler().isCurrentlyRunning(countdownLobby)) {
			countdownLobby = Bukkit.getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {
				int	time	= Murder.lobbyCountdown;

				@Override
				public void run() {
					if (time == 30) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time == 20) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time == 10) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time < 6) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("lobbyCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time <= 1) {
						Bukkit.getScheduler().cancelTask(countdownLobby);
					}
					time--;
				}
			}, 0L, 20L);
		}
	}

	public void broadcastCountdown() {
		for (final Player p : arena.getPlayers()) {
			arena.timer = new XPTimer(Murder.utils).withInterval(20).withTime(Murder.countdown).withText();
			arena.timer.forPlayer(p).start();
		}
		Bukkit.getScheduler().cancelTask(countdownLobby);
		if (!Bukkit.getScheduler().isCurrentlyRunning(countdown)) {
			countdown = Bukkit.getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {
				int	time	= Murder.countdown;

				@Override
				public void run() {
					if (time == 30) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time == 20) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time == 10) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time < 6) {
						arena.sendMessage(Murder.prefix + "§2" + Messages.getMessage("gameCountdown").replace("%1$s", new StringBuilder().append(time).toString()));
					}
					if (time <= 1) {
						Bukkit.getScheduler().cancelTask(countdown);
					}
					time--;
				}
			}, 0L, 20L);
		}
	}

	public void assign() {
		Murder.console.sendMessage(Murder.debugPrefix + "=== Assign (" + arena.getID() + ") ===");
		final List<Player> temp = new ArrayList<Player>();
		Murder.console.sendMessage(Murder.debugPrefix + "PlayerAmount:" + arena.getPlayerAmount());
		for (final Player p : arena.getPlayers()) {
			if (!arena.getForcedMurderers().contains(p) && !arena.getForcedWeaponBystanders().contains(p)) {
				if (arena.getPlayerAmount() > 3) {
					if (!Murder.murdererBlacklist.contains(p) && !Murder.weaponBlacklist.contains(p)) {
						temp.add(p);
					} else {
						Murder.console.sendMessage(Murder.debugPrefix + "Ignoring " + p.getName() + ", Reason: " + (Murder.murdererBlacklist.contains(p) ? "MurdererBlacklist" : Murder.weaponBlacklist.contains(p) ? "WeaponBystanderBlacklist" : "NONE"));
					}
				} else {
					temp.add(p);
				}
			}
		}

		Player p;
		MurderPlayer mp;

		// /////////

		if (arena.getForcedMurderers().isEmpty()) {
			p = temp.get(Murder.rd.nextInt(temp.size()));
			mp = MurderPlayer.getPlayer(p);

			mp.setMurderer(true);
			arena.setMurderer(mp);

			p.sendMessage(Murder.prefix + "§c§l" + Messages.getMessage("murderer"));
			Murder.console.sendMessage(Murder.debugPrefix + "Murderer: " + p.getName());
			Murder.murdererBlacklist.add(p);
			Murder.weaponBlacklist.remove(p);

			temp.remove(p);
		} else {
			p = arena.getForcedMurderers().get(0);
			mp = MurderPlayer.getPlayer(p);

			mp.setMurderer(true);
			arena.setMurderer(mp);

			p.sendMessage(Murder.prefix + "§c§l" + Messages.getMessage("murderer"));
			Murder.console.sendMessage(Murder.debugPrefix + "Murderer(Forced): " + p.getName());
			Murder.murdererBlacklist.add(p);
			Murder.weaponBlacklist.remove(p);

			Murder.forcedMurderers.remove(p);
			temp.remove(p);
		}

		// ///////////

		if (arena.getForcedWeaponBystanders().isEmpty()) {
			p = temp.get(Murder.rd.nextInt(temp.size()));
			mp = MurderPlayer.getPlayer(p);

			mp.setWeaponBystander(true);
			arena.addWeaponBystander(mp);

			p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystanderWeapon"));
			Murder.console.sendMessage(Murder.debugPrefix + "WeaponBystander: " + p.getName());
			Murder.weaponBlacklist.add(p);
			Murder.murdererBlacklist.remove(p);

			temp.remove(p);
		} else {
			p = arena.getForcedWeaponBystanders().get(0);
			mp = MurderPlayer.getPlayer(p);

			mp.setWeaponBystander(true);
			arena.addWeaponBystander(mp);

			p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystanderWeapon"));
			Murder.console.sendMessage(Murder.debugPrefix + "WeaponBystander(Forced): " + p.getName());
			Murder.weaponBlacklist.add(p);
			Murder.murdererBlacklist.remove(p);

			Murder.forcedWeapons.remove(p);
			temp.remove(p);
		}

		// //////////////

		for (final Player pl : temp) {
			p = pl;
			mp = MurderPlayer.getPlayer(p);

			mp.setBystander(true);

			p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystander"));
			Murder.console.sendMessage(Murder.debugPrefix + "Bystander(Default): " + p.getName());
			Murder.murdererBlacklist.remove(p);
			Murder.weaponBlacklist.remove(p);
		}
		final List<Player> murdererBlacklist = arena.getMurdererBlacklist();
		for (final Player pl : murdererBlacklist) {
			p = pl;
			mp = MurderPlayer.getPlayer(p);
			if (arena.getPlayerAmount() >= 3) {

				if (!mp.hasRole()) {
					mp.setBystander(true);
					p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystander"));
					Murder.console.sendMessage(Murder.debugPrefix + "Bystander(MB): " + p.getName());
					Murder.murdererBlacklist.remove(p);
				}

			} else if (!mp.hasRole()) {
				mp.setBystander(true);
				p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystander"));
				Murder.console.sendMessage(Murder.debugPrefix + "Bystander(MB): " + p.getName());
				Murder.murdererBlacklist.remove(p);
			}

		}
		final List<Player> weaponBlacklist = arena.getWeaponBlacklist();
		for (final Player pl : weaponBlacklist) {
			p = pl;
			mp = MurderPlayer.getPlayer(p);
			// if (!mp.isBystander()) {
			if (arena.getPlayerAmount() >= 3) {
				if (!mp.hasRole()) {
					mp.setBystander(true);
					p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystander"));
					Murder.console.sendMessage(Murder.debugPrefix + "Bystander(WB): " + p.getName());
					Murder.weaponBlacklist.remove(p);
				}

			} else if (!mp.hasRole()) {
				mp.setBystander(true);
				p.sendMessage(Murder.prefix + "§1§l" + Messages.getMessage("bystander"));
				Murder.console.sendMessage(Murder.debugPrefix + "Bystander(WB): " + p.getName());
				Murder.weaponBlacklist.remove(p);
			}
		}

		temp.clear();

		disguisePlayers();

		Signs.updateSigns(arena.getID());
		Murder.console.sendMessage(Murder.debugPrefix + "===================");
	}

	public void forceMurderer(Player p) {

	}

	public void forceWeaponBystander(Player p) {

	}

	@SuppressWarnings("deprecation")
	public void giveItems() {
		for (final Player p : arena.getPlayers()) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			Murder.console.sendMessage(Murder.debugPrefix + p.getName() + " " + mp.getNameTag() + " " + (mp.isWeaponBystander() ? "WB" : mp.isMurderer() ? "M" : "B"));
			if (mp.playing()) {
				if (mp.isMurderer()) {
					p.getInventory().setItem(4, Items.Knife());
					p.getInventory().setItem(0, Items.NameInfo(p));
				} else if (mp.isWeaponBystander()) {
					p.getInventory().setItem(4, Items.Gun());
					p.getInventory().setItem(8, Items.Bullet());
					p.getInventory().setItem(0, Items.NameInfo(p));
				} else if (mp.isBystander()) {
					p.getInventory().setItem(0, Items.NameInfo(p));
					p.getInventory().setItem(8, Items.SpeedBoost());
				} else {
					Murder.console.sendMessage(Murder.debugPrefix + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					Murder.console.sendMessage(Murder.debugPrefix + "Error in \"giveItems()\" - Player=" + mp);
				}
				p.updateInventory();
				mp.setInGame();
			}
		}
	}

	public void teleport() {
		final ArrayList<Spawnpoint> points = (ArrayList<Spawnpoint>) arena.getSpawnpoint(SpawnType.PLAYERS);
		for (int i = 0; i < arena.getPlayerAmount(); i++) {
			final Player p = arena.getPlayers().get(i);
			Location loc = points.get(i).toLocation();
			if (loc.getX() == 0.0D || loc.getY() == 0.0D || loc.getZ() == 0.0D) {
				i = 0;
				loc = points.get(i).toLocation();
			}
			p.teleport(loc);
			p.getInventory().setHeldItemSlot(0);
			Murder.mod.refreshPlayer(p);
		}
		// for(Player p : arena.getPlayers()) {
		// fixLocation(p);
		// }
	}

	// private void fixLocation(Player p) {
	// final WrapperPlayServerEntityHeadRotation rot = new
	// WrapperPlayServerEntityHeadRotation();
	// rot.setEntityId(p.getEntityId());
	// rot.setHeadYaw(p.getLocation().getYaw());
	//
	// final WrapperPlayServerEntityTeleport teleport = new
	// WrapperPlayServerEntityTeleport();
	// teleport.setEntityID(p.getEntityId());
	// teleport.setPitch(p.getLocation().getPitch());
	// teleport.setX(p.getLocation().getX());
	// teleport.setY(p.getLocation().getY());
	// teleport.setZ(p.getLocation().getZ());
	// teleport.setYaw(p.getLocation().getYaw());
	//
	// for (final Player online : arena.getPlayers()) {
	// rot.sendPacket(online);
	// teleport.sendPacket(online);
	// }
	// }

	public void printScoreboard() {
		for (final Player p : arena.getPlayers()) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			final String playerName = p.getName();
			final String nameTag = mp.getNameTag();
			if (nameTag != null && nameTag != "§cNULL") {
				final String tagColor = nameTag.substring(0, 2);
				final int collectedLoot = p.getLevel();

				String nameSpace = "";
				for (int x = 0; x < 18 - playerName.length(); x++) {
					nameSpace = nameSpace + " ";
				}
				String tagSpace = "";
				for (int x = 0; x < 18 - nameTag.length(); x++) {
					tagSpace = tagSpace + " ";
				}
				final String score = "            " + tagColor + playerName + nameSpace + " §7|§r " + tagColor + nameTag + tagSpace + " §7|§r " + tagColor + collectedLoot;

				arena.sendMessage(score);
			}
		}
	}

	// public void printScoreboard() {
	// String lines = "PLAYER----`§7|§r----`NAME----`§7|§r----`LOOT----\n";
	// for (final Player p : arena.getPlayers()) {
	// final MurderPlayer mp = MurderPlayer.getPlayer(p);
	// final String playerName = p.getName();
	// final String nameTag = mp.getNameTag();
	// if (nameTag != null && nameTag != "§cNULL") {
	// final String tagColor = nameTag.substring(0, 2);
	// final int collectedLoot = p.getLevel();
	// lines += tagColor + playerName + "`§7|§r`" + nameTag + "`§7|§r`" +
	// tagColor + collectedLoot +"§r\n";
	// }
	// }
	// lines +="-------`-------`-------`-------";
	// Murder.console.sendMessage(Murder.debugPrefix + lines);
	// TabText text = new TabText(lines);
	// text.setPageHeight(arena.getPlayerAmount());
	// text.setTabs(17, 17, 3);
	// text.sortByFields(-2, 1);
	// arena.sendMessage(text.getPage(0, false));
	// }

	public void disguisePlayers() {
		for (final Player p : arena.getPlayers()) {
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			final int Name = Murder.rd.nextInt(Murder.nameTags.length - 1);
			final int Color = Murder.rd.nextInt(Murder.colorCode.length - 1);

			final String color = Murder.colorCode[Color];
			final String name = Murder.nameTags[Name];

			if (p != null && color != null && name != null) {
				mp.setNameTag(color + name);
				try {
					// Murder.mod.setURL(SKIN_URL.replace("%%var%%",
					// color.replace("§", "").replace(" ", "")));
					Murder.mod/*
							 * .withURL(SKIN_URL.replace("%%var%%",
							 * color.replace("§", "").replace(" ", "")))
							 */.changeDisplay(p, Murder.SKIN_NAME, color + name);
				} catch (final Exception localException) {
				}
			}
		}
	}

	public void unDisguisePlayers() {
		for (final Player p : arena.getPlayers()) {
			Murder.mod.removeChanges(p);
		}
	}

	private double	randomTime	= Murder.rd.nextInt(55) + 15;

	public void spawnLoot() {

		if (!Bukkit.getScheduler().isCurrentlyRunning(loot)) {
			loot = Bukkit.getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {
				int	Number	= 1;

				@Override
				public void run() {

					final List<Spawnpoint> points = arena.getSpawnpoint(SpawnType.LOOT);
					if (points == null) return;
					if (Number >= points.size()) {
						Number = 1;
					}

					final Location loc = points.get(Number).toLocation();
					final World world = loc.getWorld();
					if (loc != null) {
						arena.addLoot(world.dropItemNaturally(loc, Items.Loot()));
						Murder.console.sendMessage(Murder.debugPrefix + "§7Spawning Loot in Arena " + arena.getID() + " at " + loc);
						Number++;
					}
					randomTime = Murder.rd.nextInt(55) + 15;
				}
			}, (int) randomTime * 20, (int) randomTime * 20);
		}
	}

	public void despawnLoot() {
		Bukkit.getScheduler().cancelTask(loot);
		for (final Item item : arena.getLoot()) {
			item.remove();
		}
	}

	public void startSmoke() {
		if (Murder.smokeTimer != -1) {
			if (!Bukkit.getScheduler().isCurrentlyRunning(smokeDelay)) {
				smokeDelay = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
					@Override
					public void run() {
						arena.smoke = true;
						if (arena.getMurderer() != null) {
							arena.getMurderer().player().sendMessage(Murder.prefix + Messages.getMessage("smokeNotification"));
						}
					}
				}, 20 * Murder.smokeTimer);
			}
		}
	}

	public void cancelDelayedStart() {
		Bukkit.getScheduler().cancelTask(delayedStart);
		arena.starting(false);
		Bukkit.getScheduler().cancelTask(countdownLobby);
		arena.timer.cancel();
	}

}
