package de.inventivegames.murder.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import de.inventivegames.murder.ArenaManager;
import de.inventivegames.murder.Corpses;
import de.inventivegames.murder.Items;
import de.inventivegames.murder.Messages;
import de.inventivegames.murder.Murder;
import de.inventivegames.murder.MurderPlayer;
import de.inventivegames.murder.ParticleEffect;
import de.inventivegames.murder.ParticleEffects;
import de.inventivegames.murder.ResourcePack;
import de.inventivegames.murder.Spectate;
import de.inventivegames.utils.LocationUtils;
import de.inventivegames.utils.xptimer.XPTimer;

public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

			@Override
			public void run() {
				if (p.isOnline()) {
					Spectate.addFace(p);
				}
			}
		}, 20L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		ResourcePack.resetResourcePack(p);
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		mp.leaveArena(true);
		Murder.instance.getServer().getPluginManager().callEvent(new PlayerDeathEvent(p, new ArrayList<ItemStack>(), 0, ""));
	}

	@EventHandler
	public void ItemDrop(PlayerDropItemEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final int slot = p.getInventory().getHeldItemSlot();
		final ItemStack item = e.getItemDrop().getItemStack();
		if (mp.playing()) {
			e.getItemDrop().remove();
			p.getInventory().remove(item);
			p.getInventory().setItem(slot, item);
		}
	}

	@EventHandler
	public static void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.playing()) {
			e.setCancelled(true);
			p.closeInventory();
		}
	}

	@EventHandler
	public void pickupItem(PlayerPickupItemEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Item item = e.getItem();
		if (mp.cantPickup()) {
			p.getInventory().remove(Items.Gun());
			p.updateInventory();
			e.setCancelled(true);
			return;
		}
		if (mp.inSpectate()) {
			e.setCancelled(true);
			return;
		}
		if (mp.playing()) {
			if (mp.isBystander()) {
				if (item.getItemStack().getType().equals(Items.Gun().getType())) {
					if (!mp.cantPickup()) {
						e.getItem().remove();
						p.getInventory().setItem(4, null);
						p.getInventory().setItem(4, Items.Gun());
						mp.setBystander(false);
						mp.setWeaponBystander(true);
						mp.getArena().addWeaponBystander(mp);
						if (!p.getInventory().contains(Items.Bullet().getType())) {
							p.getInventory().setItem(8, Items.Bullet());
						}
						e.setCancelled(true);
					} else {
						e.setCancelled(true);
					}
				} else {
					e.setCancelled(true);
				}
			} else if (mp.isMurderer()) {
				if (item.getItemStack().getType().equals(Items.Knife().getType())) {
					Bukkit.getScheduler().cancelTask(mp.getArena().trailTask);
					Bukkit.getScheduler().cancelTask(mp.getArena().knifeTimer);
					if (mp.getArena().droppedKnife != null) {
						mp.getArena().droppedKnife.remove();
					}
					mp.getArena().despawnKnifes();
					mp.getArena().droppedKnife = null;
					p.getInventory().setItem(4, null);
					p.getInventory().setItem(4, Items.Knife());
					e.getItem().remove();
					e.setCancelled(true);
				} else {
					e.setCancelled(true);
				}
			}
			if ((mp.isBystander() || mp.isMurderer()) && item.getItemStack().getType().equals(Items.Loot().getType())) {
				item.remove();
				if (p.getLevel() < 0) {
					p.setLevel(0);
				}
				p.setLevel(p.getLevel() + e.getItem().getItemStack().getAmount());
				p.sendMessage(Murder.prefix + "§2" + Messages.getMessage("lootCollected").replace("%1$s", new StringBuilder().append(e.getItem().getItemStack().getAmount()).toString()));
				if (p.getLevel() >= 5 && !mp.isMurderer()) {
					p.getInventory().setItem(4, Items.Gun());
					p.getInventory().setItem(8, Items.Bullet());
					mp.setWeaponBystander(true);
					mp.setBystander(false);
					mp.getArena().addWeaponBystander(mp);
				}
				e.setCancelled(true);
			}
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent e) {
		final Entity ent = e.getEntity();

		if (ent instanceof Player) {
			if (MurderPlayer.getPlayer((Player) e.getEntity()).playing() && e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
				if (e.getDamager() instanceof Arrow) {
					System.out.println("MurderMetadata:" + ((Projectile) e.getDamager()).hasMetadata("Murder"));
					if (((Projectile) e.getDamager()).hasMetadata("Murder")) {
						System.out.println("MurderMetadataValue:" + ((Projectile) e.getDamager()).getMetadata("Murder").get(0).value());
						if (((Projectile) e.getDamager()).getMetadata("Murder").get(0).value().equals("ITEMS_KNIFE") || ((Projectile) e.getDamager()).getMetadata("Murder").get(0).value().equals("ITEMS_BULLET")) {
							if (MurderPlayer.getPlayer((Player) ent).inGame()) {
								((Player) ent).setHealth(2.0D);
								e.setDamage(20.0D);
							}
						}
					}
				}
			}
		}

		if (ent instanceof Player && e.getDamager() instanceof Player) {
			final Player p = (Player) e.getDamager();
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.playing()) {
				if (!p.getItemInHand().getType().equals(Items.Knife().getType())) {
					e.setCancelled(true);
					return;
				}
				final MurderPlayer mpEnt = MurderPlayer.getPlayer((Player) ent);
				if (mp.inSpectate() || mpEnt.inSpectate()) {
					e.setCancelled(true);
					return;
				}

				for (double d = 0.0; d < 2.0; d += 0.5) {
					// e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation().add(0D,
					// d, 0D), Effect.STEP_SOUND,
					// Material.REDSTONE_BLOCK.getId());
					ParticleEffects.displayBlockDust(e.getEntity().getLocation().add(0, d, 0), Material.REDSTONE_BLOCK.getId(), (byte) 0, 0, 0, 0, 0.1f, 5);
				}
				//
				// e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation(),
				// Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
				// e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation().add(0D,
				// 0.5D, 0D), Effect.STEP_SOUND,
				// Material.REDSTONE_BLOCK.getId());
				// e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation().add(0D,
				// 1.0D, 0D), Effect.STEP_SOUND,
				// Material.REDSTONE_BLOCK.getId());
				// e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation().add(0D,
				// 1.5D, 0D), Effect.STEP_SOUND,
				// Material.REDSTONE_BLOCK.getId());
			}
		}

		if (e.getEntityType().equals(EntityType.ZOMBIE) && Corpses.zombieMap.containsValue(ent)) {
			ent.setFireTicks(0);
			e.setDamage(0.0D);
			e.setCancelled(true);
		}
		if (e.getDamager().getType().equals(EntityType.ZOMBIE) && Corpses.zombieMap.containsValue(e.getDamager())) {
			ent.setFireTicks(0);
			e.setDamage(0.0D);
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onOtherDamage(EntityDamageEvent e) {
		final Entity ent = e.getEntity();
		if (ent instanceof Player) {
			final Player p = (Player) ent;
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.inSpectate() || mp.inLobby()) {
				e.setDamage(0.0D);
				e.setCancelled(true);
			}
			if (mp.playing() && (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.DROWNING || e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)) {
				e.setCancelled(true);
			}
		}
		if (e.getEntityType().equals(EntityType.ZOMBIE) && Corpses.zombieMap.containsValue(ent)) {
			ent.setFireTicks(0);
			e.setDamage(0.0D);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = e.getEntity();
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.playing()) {
				mp.getArena().game.onPlayerDeath(e);
			}
		}
	}

	// public static void removeArrowsInPlayer(Player p) {
	// final WrappedDataWatcher data = new WrappedDataWatcher();
	// data.setEntity(p);
	// data.setObject(9, Byte.valueOf((byte) 0));
	// }

	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if (e.getEntity() instanceof Zombie && Corpses.zombieMap.containsValue(e.getEntity()) && e.getTarget() instanceof Player) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onArrowBlock(EntityDamageByEntityEvent event) {
		final Entity entityDamager = event.getDamager();
		final Entity entityDamaged = event.getEntity();
		if (entityDamager instanceof Arrow && entityDamaged instanceof Player && ((Arrow) entityDamager).getShooter() instanceof Player) {
			final Arrow arrow = (Arrow) entityDamager;

			final List<MetadataValue> prevMeta = arrow.getMetadata("Murder");

			final Vector velocity = arrow.getVelocity();

			final Player shooter = (Player) arrow.getShooter();
			final MurderPlayer mpShooter = MurderPlayer.getPlayer(shooter);
			final Player damaged = (Player) entityDamaged;
			final MurderPlayer mpDamaged = MurderPlayer.getPlayer(damaged);
			final Entity vehicle;
			if (damaged.isInsideVehicle()) {
				vehicle = damaged.getVehicle();
			} else {
				vehicle = null;
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					if (vehicle != null) {
						vehicle.setPassenger(damaged);
					}
				}
			}, 2L);
			if (mpDamaged.playing() && mpShooter.playing()) {
				if (shooter == damaged) {
					event.setCancelled(true);
				}
			}
			if (mpDamaged.inSpectate()) {
				arrowTeleport(damaged);
				try {
					damaged.setFlying(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}

				final Arrow newArrow = shooter.launchProjectile(Arrow.class);
				newArrow.setShooter(shooter);
				newArrow.setVelocity(velocity);
				newArrow.setBounce(false);
				for (int i = 0; i < prevMeta.size(); i++) {
					newArrow.setMetadata("Murder", prevMeta.get(i));
				}

				event.setCancelled(true);
				arrow.remove();
				if (mpShooter.isMurderer()) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
						@Override
						public void run() {
							final Item item = newArrow.getLocation().getWorld().dropItemNaturally(newArrow.getLocation().add(0.0D, 1.0D, 0.0D), Items.Knife());
							// item.setVelocity(Utils.calculateKnifeItemVelocity(shooter,
							// item));
							mpShooter.getArena().addItem(item);
							if(newArrow != null) {
							if (newArrow.isOnGround()) {
								newArrow.remove();
							}
							}
						}
					}, 10L);
				}
			}
		}
	}

	public static void arrowTeleport(Player p) {
		double x = 0.0D;
		double y = 0.0D;
		double z = 0.0D;
		if (p.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType().equals(Material.AIR)) {
			y = 2.0D;
		}
		if (p.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().getType().equals(Material.AIR)) {
			x = 1.0D;
		}
		if (p.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().getType().equals(Material.AIR)) {
			z = 1.0D;
		}
		if (p.getLocation().add(-1.0D, 0.0D, 0.0D).getBlock().getType().equals(Material.AIR)) {
			x = -1.0D;
		}
		if (p.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().getType().equals(Material.AIR)) {
			z = -1.0D;
		}
		p.teleport(p.getLocation().add(x, y, z));
	}

	@EventHandler
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.inSpectate()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		for (final Entity ent : p.getNearbyEntities(0.75D, 0.75D, 0.75D)) {
			if (ent instanceof Zombie) {
				final Zombie z = (Zombie) ent;
				if (Corpses.zombieMap.containsValue(z) && mp.inSpectate()) {
					p.setVelocity(calculateVelocity(p, z));
				}
			}
		}
	}

	public Vector calculateVelocity(Player p, Entity e) {
		final Location ploc = p.getLocation();
		final Location eloc = e.getLocation();

		final double px = ploc.getX();
		final double py = ploc.getY();
		final double pz = ploc.getZ();
		final double ex = eloc.getX();
		final double ey = eloc.getY();
		final double ez = eloc.getZ();

		double x = 0.0D;
		double y = 0.0D;
		double z = 0.0D;
		if (px > ex) {
			x = 0.5D;
		} else if (px < ex) {
			x = -0.5D;
		}
		if (py > ey) {
			y = 0.25D;
		} else if (py < ey) {
			y = -0.25D;
		}
		if (pz > ez) {
			z = 0.5D;
		} else if (pz < ez) {
			z = -0.5D;
		}
		return new Vector(x, y, z);
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			final MurderPlayer mp = MurderPlayer.getPlayer(p);
			if (mp.playing()) {
				e.setFoodLevel(20);
				e.setCancelled(true);
			}
		}
	}

	private final ArrayList<Material>	mats	= new ArrayList<Material>(Arrays.asList(Material.BED, Material.BED_BLOCK, Material.DIODE, Material.REDSTONE_COMPARATOR));

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (ArenaManager.getByWorld(e.getPlayer().getWorld()) == null) return;
		ArenaManager.getByWorld(e.getPlayer().getWorld()).worldLogger.onInteract(e);

		if (MurderPlayer.getPlayer(e.getPlayer()).playing()) {
			if (e.getClickedBlock() != null) {
				if (mats.contains(e.getClickedBlock().getType())) {
					e.setCancelled(true);
				}
			}
		}
		if(MurderPlayer.getPlayer(e.getPlayer()).inSpectate()) {
			e.setCancelled(true);
		}
	}

	int	gunTrail;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onGunUse(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Action a = e.getAction();
		final ItemStack is = p.getInventory().getItemInHand();
		if (mp.inSpectate()) {
			e.setCancelled(true);
			return;
		}
		if (mp.playing() && a == Action.RIGHT_CLICK_AIR && is.getType().equals(Items.Gun().getType())) {
			if (p.getInventory().contains(Items.Bullet().getType())) {
				p.getInventory().remove(Items.Bullet().getType());
				p.getInventory().removeItem(new ItemStack[] { Items.Bullet() });
				p.getInventory().setItem(8, null);
				p.updateInventory();

				final double vX = p.getLocation().getDirection().getX() * 10D;
				final double vY = p.getLocation().getDirection().getY() * 10D;
				final double vZ = p.getLocation().getDirection().getZ() * 10D;
				final Arrow arrow;
				if (Murder.serverVersion.contains("1.7.2")) {
					arrow = p.launchProjectile(Arrow.class);
					arrow.setVelocity(new Vector(vX, vY, vZ));
				} else {
					arrow = p.launchProjectile(Arrow.class, new Vector(vX, vY, vZ));
					arrow.setCritical(true);
				}
				arrow.setVelocity(new Vector(vX, vY, vZ));
				arrow.setShooter(p);

				arrow.setMetadata("Murder", new FixedMetadataValue(Murder.instance, String.valueOf("ITEMS_BULLET")));

				// final ItemProjectile projectile = new ItemProjectile("Gun",
				// p, Items.Bullet(), 20);
				// projectile.setInvulnerable(true);

				final ParticleEffect effect = new ParticleEffect(arrow.getLocation(), ParticleEffects.FIREWORKS_SPARK).setCount(1).setOffset(0, 0, 0).setSpeed(0);
				gunTrail = Bukkit.getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {

					@Override
					public void run() {
						if (arrow != null) {
							if (mp.getArena() != null && !mp.getArena().getPlayers().isEmpty()) {
								effect.setLocation(arrow.getLocation()).sendToPlayers(mp.getArena().getPlayers());
								// Location currLoc = arrow.getLocation();
								Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

									@Override
									public void run() {
										if (arrow.isOnGround() || arrow.isDead() || arrow.getLocation().distance(p.getLocation()) > 100) {
											Bukkit.getScheduler().cancelTask(gunTrail);
											arrow.remove();
										}
									}

								}, 5);
							}
						}
					}
				}, 0, 1);

				final long reloadTime = 3;

				final XPTimer relTimer = new XPTimer(Murder.utils).forPlayer(p).withInterval(20).withTime(reloadTime);
				relTimer.start();

				mp.getArena().relTimer = relTimer;

				mp.getArena().reloadTimer = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
					@Override
					public void run() {
						if (mp.getArena() != null) {
							Bukkit.getScheduler().cancelTask(mp.getArena().reloadTimer);
						}
						if (mp.playing() && p.getInventory().contains(Items.Gun())) {
							p.getInventory().setItem(8, Items.Bullet());
							p.getInventory().setItem(4, Items.Gun());
						}
					}
				}, reloadTime * 20L);
			} else {
				p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("reloadNotification"));
			}
		}
	}

	@EventHandler
	public void onSpeedBoost(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Action a = e.getAction();
		final ItemStack is = p.getInventory().getItemInHand();
		if (mp.playing() && (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) && is.getType().equals(Items.SpeedBoost().getType())) {
			// p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100,
			// 1, false));
			// p.setWalkSpeed(0.3F);
			p.setFoodLevel(20);
			p.setSprinting(true);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

				@Override
				public void run() {
					p.setFoodLevel(6);

				}
			}, (5 + Murder.rd.nextInt(5)) * 20);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					p.getInventory().remove(Items.SpeedBoost().getType());
					p.getInventory().remove(Items.SpeedBoost());
					p.getInventory().setItem(8, null);
					p.getItemInHand().setType(Material.AIR);
					p.getInventory().getItemInHand().setType(Material.AIR);
					p.getInventory().getItemInHand().setAmount(0);
					p.updateInventory();

					mp.getArena().speedTimer = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
						@Override
						public void run() {
							if (mp.inGame()) {
								if (!mp.isMurderer() && !mp.isWeaponBystander()) {
									if (!p.getInventory().contains(Items.Gun())) {
										p.getInventory().setItem(8, Items.SpeedBoost());
									}
								}
							}
						}
					}, 20 * 20);
				}
			}, 1L);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKnifeThrow(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Action a = e.getAction();
		final ItemStack is = p.getInventory().getItemInHand();
		if (mp.playing() && a == Action.RIGHT_CLICK_AIR && is.getType().equals(Items.Knife().getType())) {
			mp.getArena().despawnAllArrows();
			mp.getArena().despawnKnifes();
			// ===================================================================================================================================================================
			final int r = Murder.rd.nextInt(8) + 3;
			final double vX = p.getLocation().getDirection().getX() * r;
			final double vY = p.getLocation().getDirection().getY() * r;
			final double vZ = p.getLocation().getDirection().getZ() * r;
			final Arrow arrow;
			if (Murder.serverVersion.contains("1.7.2")) {
				arrow = p.launchProjectile(Arrow.class);
				arrow.setVelocity(new Vector(vX, vY, vZ));
			} else {
				arrow = p.launchProjectile(Arrow.class, new Vector(vX, vY, vZ));
				arrow.setCritical(true);
			}
			arrow.setVelocity(new Vector(vX, vY, vZ));
			arrow.setShooter(p);

			arrow.setMetadata("Murder", new FixedMetadataValue(Murder.instance, String.valueOf("ITEMS_KNIFE")));

			final ParticleEffect effect = new ParticleEffect(arrow.getLocation(), ParticleEffects.FIREWORKS_SPARK).setCount(5).setOffset(0, 0.01F, 0).setSpeed(0);
			mp.getArena().trailTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {

				@Override
				public void run() {
					if (mp.getArena() != null && mp.getArena().getPlayers() != null && !mp.getArena().getPlayers().isEmpty()) {
						if (!arrow.isDead()) {
							effect.setLocation(arrow.getLocation()).sendToPlayers(mp.getArena().getPlayers());
						}
					}
					final Location currLoc = arrow.getLocation();
					if (arrow.isDead()) {
						if (mp.getArena() != null) {
							if (mp.getArena().droppedKnife != null) {
								if (!mp.getArena().getPlayers().isEmpty()) {
									new ParticleEffect(mp.getArena().droppedKnife.getLocation(), ParticleEffects.FLAME).setCount(5).setOffset(0, 0, 0).setSpeed(0.01F).sendToPlayers(mp.getArena().getPlayers());
								} else {
									Bukkit.getScheduler().cancelTask(mp.getArena().trailTask);
								}
							} else {
								Bukkit.getScheduler().cancelTask(mp.getArena().trailTask);
							}

						}
					}
					if (arrow.isOnGround() || arrow.isDead() || arrow.getVelocity().equals(new Vector(0, 0, 0))) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

							@Override
							public void run() {
								if (!p.getInventory().contains(Items.Knife())) {
									if (!isKnifeSpawned(arrow)) {
										if (mp.getArena() != null) {
											final Item item = arrow.getLocation().getWorld().dropItemNaturally(currLoc.add(0.0D, 0.5D, 0.0D), Items.Knife());
											item.setVelocity(new Vector(0, 0.25, 0));
											mp.getArena().droppedKnife = item;
											// item.setVelocity(Utils.calculateKnifeItemVelocity(p,
											// item));
											mp.getArena().addItem(item);
										}
									}
								}
								arrow.remove();

							}
						}, 5);

					}
				}
			}, 0, 1);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					p.getInventory().remove(Items.Knife().getType());
					p.getInventory().remove(Items.Knife());
					p.getInventory().setItem(4, null);
					p.getItemInHand().setType(Material.AIR);
					p.getInventory().getItemInHand().setType(Material.AIR);
					p.getInventory().getItemInHand().setAmount(0);
					p.updateInventory();
				}
			}, 1L);

			mp.getArena().knifeTimer = Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					if (mp.inGame()) {
						for (final Item it : mp.getArena().getItems()) {
							if (it.getType().equals(Items.Knife().getType())) {
								it.remove();
							}
						}
						if (mp.isMurderer()) {
							p.getInventory().setItem(4, Items.Knife());
							if (mp.getArena().droppedKnife != null) {
								mp.getArena().droppedKnife.remove();
							}
						}
						Bukkit.getScheduler().cancelTask(mp.getArena().trailTask);
					}
				}
			}, 1200L);
		}
	}

	private static boolean isKnifeSpawned(Entity entity) {
		for (final Entity ent : entity.getNearbyEntities(10, 10, 10)) {
			if (ent.getType().equals(EntityType.DROPPED_ITEM)) {
				if (((Item) ent).getItemStack().equals(Items.Knife())) return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		final Projectile projectile = e.getEntity();
		if (projectile.hasMetadata("Murder")) {
			if (projectile.getMetadata("Murder").get(0).value().equals("ITEMS_KNIFE") || projectile.getMetadata("Murder").get(0).value().equals("ITEMS_BULLET")) {

				final List<Entity> ents = getNearbyPlayers(projectile, 1, 2, 1);
				if (ents != null && !ents.isEmpty()) {
					final Entity ent = ents.get(0);
					// for(Entity ent : ents) {
					// System.out.println(1);
					if (ent instanceof Player) {
						// System.out.println(2);
						if (projectile.getShooter() != ent) {
							final Player p = (Player) ent;
							if (!MurderPlayer.getPlayer(p).inSpectate()) {
								p.setHealth(1.0D);
								p.damage(20.0D, projectile.getShooter());
								p.damage(20.0D, projectile.getShooter());
							}
						}
					}
					// }
				}

			}
		}
	}

	private static List<Entity> getNearbyPlayers(Entity ent, double x, double y, double z) {
		final List<Entity> players = new ArrayList<Entity>();
		for (final Entity e : ent.getNearbyEntities(x, y, z)) {
			if (e instanceof Player) {
				players.add(e);
			}
		}
		return players;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void footSteps(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Location loc = p.getLocation();
		final ParticleEffects effect = ParticleEffects.FOOTSTEP;
		if (mp.playing() && mp.getArena().inGame() && p != null && p.isOnGround() && !mp.inSpectate() && new LocationUtils(Murder.utils).moved(e.getFrom(), e.getTo())) {
			try {
				final float x = 0.0F;
				final float y = 0.0F;
				final float z = 0.0F;
				final float speed = 0.0F;
				final int count = 1;
				if (mp.getArena().getMurderer() == null) return;
				final Player murderer = mp.getArena().getMurderer().player();
				if (murderer != null) {
					// effect.sendToPlayer(murderer, loc.add(0.0D, 0.1D, 0.0D),
					// x, y, z, speed, count);
					effect.display(loc.add(0.0D, 0.1D, 0.0D), x, y, z, speed, count, murderer);
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@EventHandler
	public static void onSprint(PlayerToggleSprintEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.inGame() && e.isSprinting() && mp.isBystander() && mp.getArena().inGame()) {
			// p.setFoodLevel(6);
			// e.setCancelled(true);
		}
	}

	@EventHandler
	public static void smokeTrail(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		if (mp.inGame()) {
			if (mp.isMurderer() && !mp.inSpectate()) {
				if (mp.getArena().smoke) {
					final Location loc = p.getLocation();
					final ParticleEffects effect = ParticleEffects.LARGE_SMOKE;
					try {
						final float x = 0;
						final float y = (float) 0.5;
						final float z = 0;
						final float speed = 0;
						final int count = 1;
						for (final Player receiver : Murder.instance.getServer().getOnlinePlayers()) {
							// effect.sendToPlayer(receiver, loc, x, y, z,
							// speed, count);
							effect.display(loc.add(0.0D, 0.1D, 0.0D), x, y, z, speed, count, receiver);
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

}
