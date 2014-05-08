package de.inventivegames.LetterAPICommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Class to help with the TabCompletion for Bukkit.
 * <p>
 * Can be used by anybody, as long as you provide credit for it.
 * 
 * @author D4rKDeagle
 */
public class TabCompletionHelper {

	public static List<String> getPossibleCompletionsForGivenArgs(String[] args, String[] possibilitiesOfCompletion) {
		String argumentToFindCompletionFor = args[args.length - 1];

		List<String> listOfPossibleCompletions = new ArrayList<String>();

		for (int i = 0; i < possibilitiesOfCompletion.length; i++) {
			String[] foundString = possibilitiesOfCompletion;

			try {
				if (foundString[i] != null) {
					if (foundString[i].regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
						if (foundString[i] != null) {
							listOfPossibleCompletions.add(foundString[i]);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return listOfPossibleCompletions;
	}

	public static String[] getOnlinePlayerNames() {
		Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
		String[] onlinePlayerNames = new String[onlinePlayers.length + 1];

		for (int i = 0; i < onlinePlayers.length; i++) {
			onlinePlayerNames[i] = onlinePlayers[i].getName();
		}
		return onlinePlayerNames;
	}

	public static String[] getMaterialNames() {
		Material[] mats = Material.values();
		String[] materialNames = new String[Material.values().length];
		for (int i = 0; i < Material.values().length; i++) {
			if (mats[i].isBlock()) {
				materialNames[i] = mats[i].toString();
			}
		}
		return materialNames;
	}

	public static String[] getAllIntegers() {
		String[] ints = new String[50];

		for (int i = 0; i < 50; i++) {
			ints[i] = "" + i;
		}
		return ints;
	}

}
