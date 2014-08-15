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
					p.sendMessage(Murder.prefix + "§6" + Messages.getMessage("rpMessage1"));
					p.setResourcePack("https://dl.dropboxusercontent.com/s/i1u2a3dsbxjm2hv/MurderResourcePack%20256x%20%28V2.2%29.zip?dl=1");
					p.sendMessage("§6" + Messages.getMessage("rpMessage2"));
					try {
						mp.sendRawMessage(String.format("{\"text\":\"\",\"extra\":[{\"text\":\"%s \",\"color\":\"gold\"},{\"translate\":\"options.serverTextures\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7%s\"}},{\"text\":\" %s.\",\"color\":\"gold\"}]}", new Object[] { Messages.getMessage("rpMessage3").split(";")[0], Messages.getMessage("rpMessage3").split(";")[1], Messages.getMessage("rpMessage3").split(";")[2] }));
					} catch (final Exception e) {
					}
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
