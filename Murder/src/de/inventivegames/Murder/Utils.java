package de.inventivegames.Murder;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Utils {

	public static Vector calculateKnifeItemVelocity(Player p, Item e) {
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

		if (px < ex) {
			x = 1;
		} else if (px > ex) {
			x = -1;
		}

		if (py < ey) {
			y = 0.1;
		} else if (py > ey) {
			y = -0.1;
		}

		if (pz < ez) {
			z = 1;
		} else if (pz > ez) {
			z = -1;
		}

		return new Vector(x, y, z);
	}

}
