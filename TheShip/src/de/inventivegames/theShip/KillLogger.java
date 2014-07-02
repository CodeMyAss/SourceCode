package de.inventivegames.theShip;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KillLogger {

	private int arena;
	private HashMap<ShipPlayer, Material> kills = new HashMap<ShipPlayer, Material>();
	
	
	public KillLogger(int arena) {
		this.arena = arena;
	}
	
	public void logKill(ShipPlayer sp, ShipPlayer killed, Material weapon) {
		if(kills.containsKey(sp)) {
			kills.remove(sp);
		}
		kills.put(sp, weapon);
		
		if(sp.getQuarry() == killed) {
			sp.addMoney(Weapons.getReward(weapon));
		}else
		if(sp.getQuarry() != killed) {
			sp.removeMoney(Weapons.getReward(weapon));
		}
	}
	
	public ArrayList<ItemStack> getWeapons(ShipPlayer sp){

		return null;
	}
	
}
