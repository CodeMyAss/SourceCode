package de.inventivegames.murder.loggers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.inventivegames.murder.Arena;
import de.inventivegames.murder.MurderPlayer;

public class WorldChangeLogger {

	private final ArrayList<BlockState>	states	= new ArrayList<BlockState>();

	public WorldChangeLogger(Arena arena) {
	}

	public void onInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		final Block b = e.getClickedBlock();
		if (!e.isCancelled()) {
			if (mp.inGame()) {
				logModifiedBlock(b);
			}
		}
	}

	// public void onPhysics(BlockPhysicsEvent e) {
	// final Block b = e.getBlock();
	// if (!e.isCancelled()) {
	// if (ArenaManager.getByWorld(b.getWorld()) != null) {
	// logModifiedBlock(b);
	// }
	// }
	// }

	public void logModifiedBlock(Block b) {
		if (!states.contains(b.getState())) {
			states.add(b.getState());
		}
	}

	public void resetModifiedBlocks() {
		final List<BlockState> states = new ArrayList<BlockState>(this.states);
		for (final BlockState state : states) {
			// if(!loc.getBlock().equals(template.getBlock())) {

			state.update(true, false);

			this.states.remove(state);
			// loc.getBlock().setType(template.getType());

			// System.out.println(loc.getBlock().getData());
			// System.out.println(template.getData());
			// loc.getBlock().setData(template.getBlock().getData());

			// loc.getBlock().getState().setData(template.getData());
			// loc.getBlock().getState().update(true, false);
			// }else
			// System.out.println("Same!");
		}
	}
}
