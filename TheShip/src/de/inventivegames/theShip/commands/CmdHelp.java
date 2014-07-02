package de.inventivegames.theShip.commands;

import org.bukkit.entity.Player;

public class CmdHelp implements CommandInterface {

	@Override
	public boolean onCommand(Player p, String[] args) {
		p.sendMessage("§2==== TheShip MiniGame by inventivetalent  ====");
		p.sendMessage("§2=============== Commands ===============");
		p.sendMessage("§2/TheShip help");
		p.sendMessage("§2/TheShip join <ArenaNumber>");
		p.sendMessage("§2/TheShip leave");
		if (p.hasPermission("TheShip.admin") || p.isOp()) {
			p.sendMessage("§2/TheShip start <ArenaNumber>");
			p.sendMessage("§2/TheShip addarena <ArenaNumber>");
			p.sendMessage("§2/TheShip removearena <ArenaNumber>");
			p.sendMessage("§2/TheShip addspawn <ArenaNumber> lobby");
			p.sendMessage("§2/TheShip addspawn <ArenaNumber> players <SpawnNumber>");
			p.sendMessage("§2/TheShip addspawn <ArenaNumber> prison <SpawnNumber>");
		}
		p.sendMessage("§2=============== Enjoy! =================");
		p.sendMessage("§2========= www.InventiveGames.de ==========");
		return false;
	}

	@Override
	public String permission() {
		return "murder.players";
	}

}
