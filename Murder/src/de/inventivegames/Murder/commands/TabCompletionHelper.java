package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import de.inventivegames.murder.ArenaManager;

/**
 * Class to help with the TabCompletion for Bukkit.
 *
 * @author D4rKDeagle
 */

public class TabCompletionHelper {
	public static List<String> getPossibleCompletionsForGivenArgs(String[] args, String[] possibilitiesOfCompletion) {
		final String argumentToFindCompletionFor = args[args.length - 1];

		final List<String> listOfPossibleCompletions = new ArrayList<String>();
		for (int i = 0; i < possibilitiesOfCompletion.length; i++) {
			final String[] foundString = possibilitiesOfCompletion;
			try {
				if (foundString[i] != null && foundString[i].regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
					listOfPossibleCompletions.add(foundString[i]);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		Collections.sort(listOfPossibleCompletions);

		return listOfPossibleCompletions;
	}

	public static String[] getOnlinePlayerNames() {
		final String[] onlinePlayerNames = new String[Bukkit.getServer().getOnlinePlayers().length];
		for (int i = 0; i < Bukkit.getServer().getOnlinePlayers().length; i++) {
			onlinePlayerNames[i] = Bukkit.getServer().getOnlinePlayers()[i].getName();
		}

		return onlinePlayerNames;
	}

	public static String[] getMaterialNames() {
		final Material[] mats = Material.values();
		final String[] materialNames = new String[Material.values().length];
		for (int i = 0; i < Material.values().length; i++) {
			if (mats[i].isBlock()) {
				materialNames[i] = mats[i].toString();
			}
		}
		return materialNames;
	}

	public static String[] getAllIntegers() {
		final String[] ints = new String[25];
		for (int i = 1; i < 30; i++) {
			ints[i] = "" + i;
		}
		return ints;
	}

	public static String[] getAvailableArenaIDs() {
		final String[] ints = new String[ArenaManager.getArenas().size()];
		for (int i = 1; i <= ArenaManager.getArenas().size(); i++) {
			if (ArenaManager.getArenas().get(i - 1) != null) {
				ints[i - 1] = "" + i;
			}
		}
		return ints;
	}
}
