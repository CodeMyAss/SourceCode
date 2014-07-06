package de.inventivegames.Murder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

public class Players implements Listener {

	private static File					playerFile;

	static int[]						reloadTimer					= new int[29];
	static int[]						knifeTimer					= new int[29];

	private static ArrayList<Player>	cantPickup					= new ArrayList<Player>();

	private static ArrayList<Player>	murderDisguiseNoticeDelay	= new ArrayList<Player>();

	public static boolean[]				smoke						= new boolean[29];

	public static String[]				murdererDisguiseTag			= new String[25];

	public static ArrayList<Player>		murdererDisguise			= new ArrayList<Player>();

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Murder.playersInGame.remove(p);
		playerFile = new File("plugins/Murder/Players/" + p.getName() + ".yml");
		YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);
		try {
			PlayerFile.addDefault("type", 0);
			PlayerFile.options().copyDefaults(true);
			PlayerFile.save(playerFile);
		} catch (IOException ex) {
			Murder.console.sendMessage(Murder.prefix + "§cCould not create Player File for Player §2" + p.getName());
			ex.printStackTrace();
		}
		PlayerFile.set("type", 0);
		try {
			PlayerFile.save(playerFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Murder.playerType.remove(p);

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Murder.playersInGame.contains(p)) {
			Game.leaveArena(Murder.getArena(p), p);
		}
	}

	@EventHandler
	public void ItemDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (Murder.playersInGame.contains(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public static void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (Murder.playersInGame.contains(p)) {
			e.setCancelled(true);
			p.closeInventory();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void pickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		Item item = e.getItem();
		if (cantPickup.contains(p)) {
			p.getInventory().remove(Murder.Gun());
			p.updateInventory();
			e.setCancelled(true);
			return;
		}
		if (Murder.playersInSpectate.contains(p)) {
			p.getInventory().clear();
			p.updateInventory();
			e.setCancelled(true);
			return;
		}
		if ((Murder.playersInGame.contains(p)) || (Murder.playersInLobby.contains(p))) {
			if (Murder.Bystanders.contains(p)) {
				if (item.getItemStack().getType().equals(Material.DIAMOND_HOE)) {
					if (!cantPickup.contains(p)) {
						p.getInventory().setItem(4, null);
						p.getInventory().setItem(4, Murder.Gun());
						if (!(p.getInventory().contains(Material.ARROW))) {
							p.getInventory().setItem(8, Murder.Bullet());
						}
						e.getItem().remove();
						e.setCancelled(true);
					} else {
						e.setCancelled(true);
					}
				} else {
					e.setCancelled(true);
				}
			} else if (Murder.Murderers.contains(p)) {
				if (item.getItemStack().getType().equals(Material.IRON_SWORD)) {
					p.getInventory().setItem(4, null);
					p.getInventory().setItem(4, Murder.Knife());
					e.getItem().remove();
					e.setCancelled(true);
				} else {
					e.setCancelled(true);
				}
			}
			if ((Murder.Bystanders.contains(p)) || (Murder.Murderers.contains(p))) {
				if (item.getItemStack().getType().equals(Material.DIAMOND)) {
					item.remove();
					p.setLevel(p.getLevel() + 1);
					p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("lootCollected").replace("%1$s", "1"));
					if ((p.getLevel() == 5) && (!(Murder.Murderers.contains(p)))) {
						p.getInventory().setItem(4, Murder.Gun());
						p.getInventory().setItem(8, Murder.Bullet());
					}
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();
		if ((ent instanceof Player) && (e.getDamager() instanceof Player)) {
			Player p = (Player) e.getDamager();
			if ((Murder.playersInGame.contains(p)) || (Murder.playerInLobby.containsKey(p))) {
				if (!((p.getItemInHand().getType().equals(Material.IRON_SWORD)) || (p.getItemInHand().getType().equals(Material.BOW)))) {
					e.setCancelled(true);
				}
				if (Murder.playersInSpectate.contains(p)) {
					e.setCancelled(true);
				}
			}

		}

		if (e.getEntityType().equals(EntityType.ZOMBIE)) {
			if (Murder.zombieMap.containsValue(ent)) {
				ent.setFireTicks(0);
				e.setDamage(0D);
				e.setCancelled(true);
			}
		}

		if (e.getDamager().getType().equals(EntityType.ZOMBIE)) {
			if (Murder.zombieMap.containsValue(e.getDamager())) {
				ent.setFireTicks(0);
				e.setDamage(0D);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onOtherDamage(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
			if (Murder.playersInGame.contains(p)) {
				if ((e.getCause() == DamageCause.FALL) || (e.getCause() == DamageCause.FIRE_TICK)) {
					e.setCancelled(true);
				}
			}
			if (Murder.playersInSpectate.contains(p)) {
				e.setDamage(0D);
				e.setCancelled(true);
			}
		}
		if (e.getEntityType().equals(EntityType.ZOMBIE)) {
			if (Murder.zombieMap.containsValue(ent)) {
				ent.setFireTicks(0);
				e.setDamage(0D);
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {

		if (e.getEntity() instanceof Player) {
			if (e.getEntity().getKiller() instanceof Player) {
				if (Murder.playersInGame.contains(e.getEntity())) {
					final Player p = (Player) e.getEntity();

					Murder.playersInSpectate.add(p);
					p.sendMessage(Murder.prefix + "§aYou are now a Spectator.");

					e.setDeathMessage(null);

					final Player killer = (Player) e.getEntity().getKiller();

					// p.setGameMode(GameMode.SPECTATOR);
					p.setHealth(20);
					e.getDrops().clear();
					p.setAllowFlight(true);
					p.setFlying(true);

					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 255));

					for (Player online : Murder.instance.getServer().getOnlinePlayers()) {
						online.hidePlayer(p);
					}

					if (Murder.isMurderer(killer)) {

						Murder.bystanderAmount[Murder.getArena(p)] -= 1;

						Item item = p.getLocation().getWorld().dropItemNaturally(p.getLocation().add(0, 1, 0), Murder.Knife());
						Murder.Items[Murder.getArena(p)][Murder.ItemAmount[Murder.getArena(p)]] = item;
						Murder.ItemAmount[Murder.getArena(p)]++;

						if (Murder.weaponBystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] == p) {
							Murder.weaponBystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] = null;
						} else if (Murder.bystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] == p) {
							Murder.bystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] = null;
						}

						System.out.println("BystandersAmount = " + Murder.bystanderAmount[Murder.getArena(p)]);
						System.out.println("BystandersAmount = " + Murder.bystanderAmount[Murder.getArena(killer)]);

						if (Murder.bystanderAmount[Murder.getArena(p)] <= 0) {
							Murder.sendArenaMessage(Murder.prefix + "§c" + Messages.getMessage("murdererWin1"), Murder.getArena(p));
							Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("murdererWin2").replace("%1$s", Murder.getNameTag(killer)).replace("%2$s", killer.getName()), Murder.getArena(killer));

							Game.stopGameDelayed(Murder.getArena(p), 10 * 20);

							Rewards.addMoney(killer, Murder.deposit);
						}

						if (p.getInventory().contains(Murder.Gun())) {
							Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

								@Override
								public void run() {
									Item gun = p.getWorld().dropItemNaturally(p.getLocation(), Murder.Gun());
									Murder.Items[Murder.getArena(p)][Murder.ItemAmount[Murder.getArena(p)]] = gun;
									Murder.ItemAmount[Murder.getArena(p)]++;
								}
							}, 2);
						}

					} else if (!Murder.isMurderer(killer)) {
						if (Murder.Bystanders.contains(p)) {

							Murder.bystanderAmount[Murder.getArena(p)] -= 1;

							Murder.sendArenaMessage(Murder.prefix + "§1" + Messages.getMessage("killedInnocent").replace("%1$s", killer.getName()), Murder.getArena(p));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 1));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 1));

							Rewards.removeMoney(killer, Murder.withdraw);

							cantPickup.add(killer);

							final ItemStack nameTag = killer.getInventory().getItem(0);

							Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

								@Override
								public void run() {
									killer.getInventory().clear();
									killer.getInventory().remove(Material.DIAMOND_HOE);
									killer.getInventory().remove(Murder.Gun());
									killer.getInventory().setItem(4, null);
									killer.getItemInHand().setType(Material.AIR);
									killer.getInventory().getItemInHand().setType(Material.AIR);
									killer.getInventory().getItemInHand().setAmount(0);
									killer.updateInventory();

									killer.getInventory().setItem(0, nameTag);
								}
							}, 1);

							Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

								@Override
								public void run() {
									Item gun = killer.getWorld().dropItemNaturally(killer.getLocation(), Murder.Gun());
									Murder.Items[Murder.getArena(killer)][Murder.ItemAmount[Murder.getArena(killer)]] = gun;
									Murder.ItemAmount[Murder.getArena(killer)]++;
								}
							}, 2);

							killer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147000, 1));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147000, 1));

							Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

								@Override
								public void run() {
									Player pl = killer;
									for (PotionEffect effect : pl.getActivePotionEffects()) {
										pl.removePotionEffect(effect.getType());
									}
								}

							}, 20 * 10);

							Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

								@Override
								public void run() {
									cantPickup.remove(killer);
								}

							}, 20 * 30);

							if (Murder.weaponBystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] == p) {
								Murder.weaponBystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] = null;
							} else if (Murder.bystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] == p) {
								Murder.bystanders[Murder.getArena(p)][Murder.getPlayerNumber(p, Murder.getArena(p))] = null;
							}

							if (Murder.bystanderAmount[Murder.getArena(p)] <= 0) {
								Murder.sendArenaMessage(Murder.prefix + "§c" + Messages.getMessage("murdererWin1"), Murder.getArena(p));
								Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("murdererWin2").replace("%1$s", Murder.getNameTag(killer)).replace("%2$s", killer.getName()), Murder.getArena(p));
								Game.stopGameDelayed(Murder.getArena(p), 10 * 20);

								Rewards.addMoney(killer, Murder.deposit);

							}
						} else if (Murder.isMurderer(p)) {
							Murder.sendArenaMessage(Murder.prefix + "§1" + Messages.getMessage("killedMurderer").replace("%1$s", killer.getName()), Murder.getArena(p));
							Murder.sendArenaMessage(Murder.prefix + "§1" + Messages.getMessage("bystanderWin1"), Murder.getArena(p));
							Murder.sendArenaMessage(Murder.prefix + "§2" + Messages.getMessage("bystanderWin2").replace("%1$s", Murder.getNameTag(killer)).replace("%2$s", killer.getName()), Murder.getArena(p));

							Rewards.addMoney(killer, Murder.deposit);

							Game.stopGameDelayed(Murder.getArena(p), 10 * 20);

						}

					}

					try {
						Corpses.spawnCorpse(p.getLocation(), p);
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					playerFile = new File("plugins/Murder/Players/" + p.getName() + ".yml");
					YamlConfiguration PlayerFile = YamlConfiguration.loadConfiguration(playerFile);

					Murder.playerType.remove(p);
					PlayerFile.set("type", 0);
					try {
						PlayerFile.save(playerFile);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					p.teleport(e.getEntity().getLocation());

				}
			}
		}
	}

	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if (e.getEntity() instanceof Zombie) {
			if (Murder.zombieMap.containsValue(e.getEntity())) {
				if (e.getTarget() instanceof Player) {
					e.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onArrowBlock(EntityDamageByEntityEvent event) {
		Entity entityDamager = event.getDamager();
		Entity entityDamaged = event.getEntity();

		if (entityDamager instanceof Arrow) {
			if (entityDamaged instanceof Player && ((Arrow) entityDamager).getShooter() instanceof Player) {
				Arrow arrow = (Arrow) entityDamager;

				Vector velocity = arrow.getVelocity();

				final Player shooter = (Player) arrow.getShooter();
				Player damaged = (Player) entityDamaged;

				if (Murder.isSpectator(damaged)) {
					damaged.teleport(entityDamaged.getLocation().add(0, 5, 0));
					damaged.setFlying(true);

					final Arrow newArrow = shooter.launchProjectile(Arrow.class);
					newArrow.setShooter(shooter);
					newArrow.setVelocity(velocity);
					newArrow.setBounce(false);

					event.setCancelled(true);
					arrow.remove();

					if (Murder.isMurderer(shooter)) {
						Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

							@Override
							public void run() {
								Item item = newArrow.getLocation().getWorld().dropItemNaturally(newArrow.getLocation().add(0, 1, 0), Murder.Knife());
								item.setVelocity(Utils.calculateKnifeItemVelocity(shooter, item));

								Murder.Items[Murder.getArena(shooter)][Murder.ItemAmount[Murder.getArena(shooter)]] = item;
								Murder.ItemAmount[Murder.getArena(shooter)]++;

								if (newArrow.isOnGround()) {
									newArrow.remove();
								}
							}
						}, 10);
					}
				}
			}
		}
	}

	@EventHandler
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent e) {
		if (e.getEntity() instanceof Player) {
			if (Murder.playersInSpectate.contains((Player) e.getEntity())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		for (Entity ent : p.getNearbyEntities(0.75, 0.75, 0.75)) {
			if (ent instanceof Zombie) {
				Zombie z = (Zombie) ent;

				if (Murder.zombieMap.containsValue(z)) {
					if (Murder.isSpectator(p)) {
						p.setVelocity(calculateVelocity(p, z));
					}
				}
			}
		}
	}

	public Vector calculateVelocity(Player p, Entity e) {
		Location ploc = p.getLocation();
		Location eloc = e.getLocation();

		double px = ploc.getX();
		double py = ploc.getY();
		double pz = ploc.getZ();
		double ex = eloc.getX();
		double ey = eloc.getY();
		double ez = eloc.getZ();

		double x = 0;
		double y = 0;
		double z = 0;

		if (px > ex) {
			x = 0.5;
		} else if (px < ex) {
			x = -0.5;
		}

		if (py > ey) {
			y = 0.25;
		} else if (py < ey) {
			y = -0.25;
		}

		if (pz > ez) {
			z = 0.5;
		} else if (pz < ez) {
			z = -0.5;
		}

		return new Vector(x, y, z);
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (Murder.playersInGame.contains(p)) {
				e.setFoodLevel(20);
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onGunUse(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action a = e.getAction();
		ItemStack is = p.getInventory().getItemInHand();
		if (Murder.playersInGame.contains(p)) {
			if (a == Action.RIGHT_CLICK_AIR) {
				if (is.getType().equals(Material.DIAMOND_HOE)) {
					if (p.getInventory().contains(Material.ARROW)) {
						p.getInventory().remove(Material.ARROW);
						p.getInventory().removeItem(Murder.Bullet());
						p.getInventory().setItem(8, null);
						p.updateInventory();

						double vX = p.getLocation().getDirection().getX() * 5;
						double vY = p.getLocation().getDirection().getY() * 5;
						double vZ = p.getLocation().getDirection().getZ() * 5;

						Arrow arrow;
						Snowball snowball;

						if (Murder.serverVersion.contains("1.7.2")) {
							arrow = p.launchProjectile(Arrow.class);
							arrow.setVelocity(new Vector(vX, vY, vZ));
							snowball = p.launchProjectile(Snowball.class);
							snowball.setVelocity(new Vector(vX, vY, vZ));
						} else {
							arrow = p.launchProjectile(Arrow.class, new Vector(vX, vY, vZ));
							snowball = p.launchProjectile(Snowball.class, new Vector(vX, vY, vZ));
							arrow.setCritical(true);
						}
						arrow.setVelocity(new Vector(vX * 5, vY * 5, vZ * 5));
						snowball.setVelocity(new Vector(vX * 5, vY * 5, vZ * 5));
						arrow.setShooter(p);

						p.playSound(p.getEyeLocation(), Sound.SHOOT_ARROW, 10, 1);

						reloadTimer[Murder.getArena(p)] = Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

							@Override
							public void run() {
								Murder.instance.getServer().getScheduler().cancelTask(reloadTimer[Murder.getArena(p)]);
								if (Murder.isPlaying(p)) {
									if (p.getInventory().contains(Murder.Gun())) {
										p.getInventory().setItem(8, Murder.Bullet());
										p.getInventory().setItem(4, Murder.Gun());
									}
								}
							}

						}, 20 * 4L);

					} else {
						p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("reloadNotification"));
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKnifeThrow(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action a = e.getAction();
		ItemStack is = p.getInventory().getItemInHand();
		if (Murder.playersInGame.contains(p)) {
			if (a == Action.RIGHT_CLICK_AIR) {
				if (is.getType().equals(Material.IRON_SWORD)) {

					double vX = p.getLocation().getDirection().getX() * 5;
					double vY = p.getLocation().getDirection().getY() * 5;
					double vZ = p.getLocation().getDirection().getZ() * 5;

					final Arrow arrow;
					Snowball snowball;

					if (Murder.serverVersion.contains("1.7.2")) {
						arrow = p.launchProjectile(Arrow.class);
						arrow.setVelocity(new Vector(vX, vY, vZ));
						snowball = p.launchProjectile(Snowball.class);
						snowball.setVelocity(new Vector(vX, vY, vZ));
					} else {
						arrow = p.launchProjectile(Arrow.class, new Vector(vX, vY, vZ));
						snowball = p.launchProjectile(Snowball.class, new Vector(vX, vY, vZ));
						arrow.setCritical(true);
					}

					arrow.setVelocity(new Vector(vX * 5, vY * 5, vZ * 5));
					snowball.setVelocity(new Vector(vX * 5, vY * 5, vZ * 5));
					arrow.setShooter(p);

					p.playSound(p.getEyeLocation(), Sound.SHOOT_ARROW, 10, 1);

					Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

						@Override
						public void run() {
							p.getInventory().remove(Material.IRON_SWORD);
							p.getInventory().remove(Murder.Knife());
							p.getInventory().setItem(4, null);
							p.getItemInHand().setType(Material.AIR);
							p.getInventory().getItemInHand().setType(Material.AIR);
							p.getInventory().getItemInHand().setAmount(0);
							p.updateInventory();
						}
					}, 1);

					Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

						@Override
						public void run() {
							Item item = arrow.getLocation().getWorld().dropItemNaturally(arrow.getLocation().add(0, 1, 0), Murder.Knife());
							item.setVelocity(Utils.calculateKnifeItemVelocity(p, item));

							Murder.Items[Murder.getArena(p)][Murder.ItemAmount[Murder.getArena(p)]] = item;
							Murder.ItemAmount[Murder.getArena(p)]++;

							if (arrow.isOnGround()) {
								arrow.remove();
							}
						}
					}, 10);

					knifeTimer[Murder.getArena(p)] = Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

						@Override
						public void run() {
							if (Murder.isPlaying(p)) {
								p.getInventory().setItem(4, Murder.Knife());
							}
						}

					}, 20 * 60L);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void footSteps(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		ParticleEffects effect = ParticleEffects.FOOTSTEP;
		if ((Murder.playersInGame.contains(p)) && (Murder.inGame.contains("" + Murder.getArena(p))) && (p != null)) {
			if ((p.isOnGround()) && (!(Murder.isSpectator(p)))) {
				if (Murder.utils.getLocationUtils().moved(e.getFrom(), e.getTo())) {
					try {
						float x = (float) 0;
						float y = (float) 0;
						float z = (float) 0;
						float speed = 0;
						int count = 1;
						Player murderer = Murder.getMurderer(Murder.getArena(p));
						if (murderer != null) {
							effect.sendToPlayer(murderer, loc.add(0, 0.1, 0), x, y, z, speed, count);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

	}

	@EventHandler
	public static void onSprint(PlayerToggleSprintEvent e) {
		Player p = e.getPlayer();
		if (Murder.playersInGame.contains(p)) {
			if (e.isSprinting()) {
				if ((Murder.isBystander(p) || Murder.isWeaponBystander(p)) && (Murder.inGame.contains("" + Murder.getArena(p)))) {
					p.setFoodLevel(6);
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public static void smokeTrail(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		int arena = Murder.getArena(p);
		if (Murder.playersInGame.contains(p)) {
			if ((Murder.isMurderer(p)) && (!(Murder.isSpectator(p)))) {
				if (smoke[arena]) {
					Location loc = p.getLocation();
					ParticleEffects effect = ParticleEffects.LARGE_SMOKE;
					try {
						float x = (float) 0;
						float y = (float) 0.5;
						float z = (float) 0;
						float speed = 0;
						int count = 1;
						for (Player receiver : Murder.instance.getServer().getOnlinePlayers()) {
							effect.sendToPlayer(receiver, loc, x, y, z, speed, count);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public static void disguiseMurderer(final Player p, String name) {
		murdererDisguise.add(p);
		murdererDisguiseTag[Murder.getArena(p)] = name;
		TagAPI.refreshPlayer(p);

		p.sendMessage(Murder.prefix + "§2You Disguised as " + name);

	}

	@EventHandler
	public void onTagMurderChange(final AsyncPlayerReceiveNameTagEvent event) {
		Player player = event.getNamedPlayer();

		if ((Murder.isMurderer(player)) && (murdererDisguise.contains(player))) {
			final String tag = murdererDisguiseTag[Murder.getArena(player)];
			event.setTag(tag);
			Murder.console.sendMessage(Murder.prefix + "§2Disguising Murderer §a" + player + "§2: §r" + tag);

			Murder.getBukkit().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

				@Override
				public void run() {
					event.setTag(tag);
				}
			}, 10);
		}
	}

	@EventHandler
	public void onTagChange(AsyncPlayerReceiveNameTagEvent event) {
		Player player = event.getNamedPlayer();
		int Name = Murder.rd.nextInt(25);
		int Color = Murder.rd.nextInt(13);

		String color = Murder.colorCode[Color];
		String name = Murder.nameTags[Name];
		if ((Murder.invisibleTags.contains(player)) && (!(Murder.hasTag.contains(player))) && (!(murdererDisguise.contains(player)))) {
			event.setTag(color + name);
			Murder.nameTag.put(player, color + name);
			Murder.hasTag.add(player);
			Murder.console.sendMessage(Murder.prefix + "§2Assigning Tag for Player §a" + player + "§2: §r" + color + name);
		}
	}

	@EventHandler
	public void onTagChangeUpdate(AsyncPlayerReceiveNameTagEvent event) {
		Player player = event.getNamedPlayer();
		if (Murder.nameTag.get(player) != null) {
			if (Murder.invisibleTags.contains(player)) {
				event.setTag(Murder.nameTag.get(player));
			}
		}
	}

}
