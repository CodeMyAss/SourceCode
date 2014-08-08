package de.inventivegames.murder;

public enum SpawnType {

	PLAYERS, LOBBY, LOOT;

	public boolean isValid(String s) {
		return s.equalsIgnoreCase(SpawnType.LOBBY.toString()) || s.equalsIgnoreCase(SpawnType.LOOT.toString()) || s.equalsIgnoreCase(SpawnType.PLAYERS.toString());
	}

}
