package de.inventivegames.LetterAPI;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class LetterAPI extends JavaPlugin implements Listener {

	public static Location	Loc;
	public static String	world	= "world";

	public void onEnable() {
		getLogger().info("===================================");
		getLogger().info("===================================");
		getLogger().info("========Activating LetterAPI=======");
		getLogger().info("===================================");
		getLogger().info("=======www.InventiveGames.de=======");
		getLogger().info("===================================");
		getLogger().info("===================================");

		setupMetrics();
	}

	public void setupMetrics() {
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

	/**
	 * @param text
	 *            Text to Display
	 * @param loc
	 *            Location of the beginning/center of the Text
	 * @param dir
	 *            Facing Direction
	 * @param centered
	 *            if the text should be centered
	 * @param material
	 *            Material of the Text
	 * @param data
	 *            Material data of the Text
	 */
	public static void createBlockText(String text, Location loc, Direction dir, Boolean centered, Material material, byte data) {
		if (!centered) {
			Letter.drawString(text, material, data, loc, dir);
		} else {
			Letter.centreString(text, material, data, loc, dir);
		}
	}

	/**
	 * @param text
	 *            Text to Display
	 * @param loc
	 *            Location of the beginning/center of the Text
	 * @param dir
	 *            Facing Direction
	 * @param centered
	 *            if the text should be centered
	 * @param effect
	 *            Particle effect
	 */
	public static void createParticleText(String text, Location loc, Direction dir, Boolean centered, ParticleEffect effect) {
		if (!centered) {
			Letter.drawString(text, effect, loc, dir);
		} else {
			Letter.centreString(text, effect, loc, dir);
		}
	}

	/**
	 * @param text
	 *            Text to Display
	 * @param loc
	 *            Location of the beginning/center of the Text
	 * @param dir
	 *            Facing Direction
	 * @param centered
	 *            if the text should be centered
	 * @param effect
	 *            Particle effect
	 * @param player
	 *            Player that can see the Blocks
	 */
	public static void createPlayerSpecificText(String text, Location loc, Direction dir, Boolean centered, Material material, byte data, Player player) {
		if (!centered) {
			Letter.drawString(text, material, data, loc, dir, player);
		} else {
			Letter.centreString(text, material, data, loc, dir, player);
		}
	}

	/**
	 * @param text
	 *            Text to Display
	 * @param loc
	 *            Location of the beginning/center of the Text
	 * @param dir
	 *            Facing Direction
	 * @param centered
	 *            if the text should be centered
	 * @param material
	 *            Material of the Text
	 * @param data
	 *            Material data of the Text
	 */
	public static void resetBlockText(String text, Location loc, Direction dir, Boolean centered) {
		if (!centered) {
			Letter.clearString(text, loc, dir);
		} else {
			Letter.clearStringCentred(text, loc, dir);
		}
	}

	/**
	 * @param text
	 *            Text to Display
	 * @param loc
	 *            Location of the beginning/center of the Text
	 * @param dir
	 *            Facing Direction
	 * @param centered
	 *            if the text should be centered
	 * @param player
	 *            Player that can see the Blocks
	 */
	public static void resetPlayerSpecificText(String text, Location loc, Direction dir, Boolean centered, Player player) {
		if (!centered) {
			Letter.clearString(text, loc, dir, player);
		} else {
			Letter.clearStringCentred(text, loc, dir, player);
		}
	}
}
