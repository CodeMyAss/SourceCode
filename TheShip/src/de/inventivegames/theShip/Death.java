package de.inventivegames.theShip;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Death implements Listener {

	public static int[] cd0 = new int[29];

	static KillLogger[]			kLogger			= new KillLogger[29];
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Player killer = p.getKiller();
		final ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
		ShipPlayer ksp = ShipPlayer.getShipPlayer(killer, false);

		if (((sp != null) && (ksp != null)) && ((sp.playing()) && (ksp.playing()))) {
			ShipPlayer kquarry = ksp.getQuarry();
			ShipPlayer khunter = ksp.getHunter();
			ShipPlayer quarry = sp.getQuarry();
			ShipPlayer hunter = sp.getHunter();

			p.sendMessage(TheShip.prefix + "§aYou are now a Spectator.");
			sp.setInSpectate();

			e.setDeathMessage(null);
			
			kLogger[ksp.getArena()] = new KillLogger(ksp.getArena());
			
			kLogger[ksp.getArena()].logKill(ksp, sp, killer.getItemInHand().getType());
			
			

			// p.setGameMode(GameMode.SPECTATOR);
			p.setHealth(20);
//			e.getDrops().clear();


			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 255));

			for (Player online : TheShip.instance.getServer().getOnlinePlayers()) {
				online.hidePlayer(p);
			}
			
	
			TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {
				
				@Override
				public void run() {
					sp.respawn();
				}
			}, 20 * 10);

			if ((sp.hasFirstHit())) {
				p.sendMessage(TheShip.prefix + "§2" + killer.getName() + " killed you in Self-Defense!");
				return;
			}
			if ((quarry == ksp) && (sp.hasFirstHit())) {
				killer.sendMessage(TheShip.prefix + "§2Well Done - You Stopped your Murderous Hunter!");
				return;
			}

			if ((kquarry == sp) && (hunter == ksp)) {
				killer.sendMessage(TheShip.prefix + "§2Excelllent! You successfully Murdered Your Quarry " + p.getName() + "!");
				p.sendMessage("§c" + killer.getName() + " Hunted you Down and Murdered you!");
				
				SBoard.getBoard(killer).setQuarry(null, ksp.getQuarry().getPlayer().getName());
				ksp.resetQuarry();

				
				return;
			}
			
			if(!(kquarry == sp)) {
				killer.sendMessage("§cClearly, You've been overcome by a raging Blood-Lust!");
				p.sendMessage("§cYou were Slaughtered in Cold Blood by " + killer.getName());
				return;
			}



		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			DamageCause cause = e.getCause();
			ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);

			if ((sp != null) && sp.playing()) {

			}
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				ShipPlayer sp = ShipPlayer.getShipPlayer(p, false);
				final ShipPlayer dsp = ShipPlayer.getShipPlayer(damager, false);
				boolean arrested = false;

				if (((sp != null) && (dsp != null)) && ((sp.playing()) && (dsp.playing()))) {
					if (!(dsp.holdingWeapon())) {
						e.setCancelled(true);
					}
					if (!(sp.hasFirstHit())) {
						dsp.setFirstHit();
					}
					if((dsp.holdingWeapon()) && (dsp.isObserved())) {
						e.setCancelled(true);
						dsp.setFrozen(true);
						p.getInventory().clear();
						if(!arrested) {
							cd0[sp.getArena()] = TheShip.instance.getServer().getScheduler().scheduleSyncDelayedTask(TheShip.instance, new Runnable() {
								
								@Override
								public void run() {
									Prison.arrest(dsp);
									dsp.setFrozen(false);
								}
							}, 20 * 2);
						}
						arrested = true;
					}
				}
			}
		}
	}

}
