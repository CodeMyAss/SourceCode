package de.inventivegames.murder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResourcePack {
	public static void setResourcePack(final Player p) {
		final MurderPlayer mp = MurderPlayer.getPlayer(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
			@Override
			public void run() {
				try {
					p.sendMessage(Murder.prefix + "§6Downloading Custom Resources...");
					p.setResourcePack("https://dl.dropboxusercontent.com/s/i1u2a3dsbxjm2hv/MurderResourcePack%20256x%20%28V2.2%29.zip?dl=1");
					p.sendMessage("§6If the ResourcePack isn't downloading,");
					mp.sendRawMessage("{\"text\":\"\",\"extra\":[{\"text\":\"make sure you have \",\"color\":\"gold\"},{\"translate\":\"options.serverTextures\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Options/Video Settings/Server Textures\"}},{\"text\":\" enabled.\",\"color\":\"gold\"}]}");
					return;
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}, 5L);
	}

	public static void resetResourcePack(Player p) {
		p.setResourcePack("https://dl.dropboxusercontent.com/s/99udm9shqq98hlc/MurderDefault.zip?dl=1");
	}
}
