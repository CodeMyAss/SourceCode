package de.inventivegames.Murder;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Signs implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if ((e.getLine(0).equalsIgnoreCase("[Murder]")) && ((e.getPlayer().hasPermission("murder.admin.createsign")) || (e.getPlayer().isOp()))) {
			for (int i = 0; i <= 3; i++) {
				String line = e.getLine(i);

				line = line.replace("&", "§");
				line = line.replace("&", "§");
				e.setLine(i, line);
			}
			e.setLine(0, "§1[§cMurder§1]");
			if (e.getLine(1).equalsIgnoreCase("join")) {
				e.setLine(1, "§2Join");
				e.setLine(3, Murder.getStatus(Integer.valueOf(e.getLine(2))));
			} else if (e.getLine(1).equalsIgnoreCase("leave")) {
				e.setLine(1, "§cLeave");
			}
			e.getPlayer().sendMessage(Murder.prefix + "§2" + Messages.getMessage("createdSign"));
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = e.getClickedBlock();
			Player p = e.getPlayer();
			BlockState state = b.getState();

			if (state instanceof Sign) {
				final Sign sign = (Sign) b.getState();

				if (sign.getLine(0).equals("§1[§cMurder§1]")) {
					if ((sign.getLine(1).equals("§2Join")) && (p.hasPermission("murder.player.join"))) {
						if (!(Murder.playersInGame.contains(p.getName()))) {
							if (!(Murder.inGame.contains("" + sign.getLine(2)))) {
								Game.joinArena(sign.getLine(2), p);

								Murder.instance.getServer().getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {

									@Override
									public void run() {
										sign.setLine(3, Murder.getStatus(Integer.valueOf(sign.getLine(2))));
										sign.update();
									}
								}, 10);

								Murder.instance.getServer().getScheduler().scheduleSyncRepeatingTask(Murder.instance, new Runnable() {

									@Override
									public void run() {
										sign.setLine(3, Murder.getStatus(Integer.valueOf(sign.getLine(2))));
										sign.update();
									}
								}, 0, 20 * 10);

								return;
							} else
								p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("arenaIngame"));
							return;
						} else
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerIngame"));
						return;
					} else if ((sign.getLine(1).equals("§cLeave")) && (p.hasPermission("murder.player.leave"))) {
						if (Murder.playersInGame.contains(p.getName())) {
							int arena = Murder.getArena(p);
							Game.leaveArena(arena, p);
							return;
						} else
							p.sendMessage(Murder.prefix + "§c" + Messages.getMessage("playerNotIngame"));
						return;
					}

				}
			}
		}
	}

}
