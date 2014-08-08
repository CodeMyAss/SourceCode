package de.inventivegames.murder;

import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Spawnpoint {

	private final Location	loc;

	public Spawnpoint(Location loc) {
		this.loc = loc;
	}

	public Spawnpoint(World world, double X, double Y, double Z) {
		loc = new Location(world, X, Y, Z);
	}

	public World getWorld() {
		return loc.getWorld();
	}

	public double getX() {
		return loc.getX();
	}

	public double getY() {
		return loc.getY();
	}

	public double getZ() {
		return loc.getZ();
	}

	public Location toLocation() {
		return loc;
	}

	// ////////////

	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		final JSONObject object = new JSONObject();
		object.put("world", loc.getWorld().getName());
		object.put("x", loc.getX());
		object.put("y", loc.getY());
		object.put("z", loc.getZ());
		return object.toString();
	}

	public static Spawnpoint fromString(String s) {
		JSONObject property = null;
		try {
			property = (JSONObject) new JSONParser().parse(s);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		World world = null;
		double X = 0;
		double Y = 0;
		double Z = 0;

		final String wn = (String) property.get("world");
		world = Murder.instance.getServer().getWorld(wn);
		X = (double) property.get("x");
		Y = (double) property.get("y");
		Z = (double) property.get("z");

		return new Spawnpoint(world, X, Y, Z);
	}

}
