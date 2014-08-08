package de.inventivegames.murder.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.inventivegames.murder.Murder;
import de.inventivegames.utils.debug.Debug;

public class ReportCommand implements CommandInterface {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (player.isOp()) {
			reportCmd(player);
		}
		return false;
	}

	@Override
	public String permission() {
		return "JHwpwmg.NySCWgZKt.auNFaJPc.AS.sPCeCmMYA.hjGVHzx.DGxpYyGM.cGvRJVJa.Jmem.xS";
	}

	@Override
	public List<String> getCompletions(String[] args) {
		final List<String> list = new ArrayList<String>();

		return list;
	}

	@Override
	public String getUsage() {
		return "§areport";
	}

	private boolean	reportActive	= false;

	public void reportCmd(Player p) {
		if (!reportActive) {
			p.sendMessage("§7==========");
			p.sendMessage("§2This command will collect your Server Data and send it to pastebin.");
			p.sendMessage("§2Type §a/murder report §2again to continue. This option will only be available for 10 seconds.");
			reportActive = true;
			p.sendMessage("§7==========");

			Bukkit.getScheduler().scheduleSyncDelayedTask(Murder.instance, new Runnable() {
				@Override
				public void run() {
					reportActive = false;
				}
			}, 200L);
		} else {
			p.sendMessage("§2Reporting...");
			final Debug debug = Murder.utils.getDebug();
			p.sendMessage(debug.send());
			Murder.utils.getChatUtils().sendRawMessage(p, "{\"text\":\"\",\"extra\":[{\"text\":\"Click here\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + debug.getReportURL() + "\"}},{\"text\":\" to create a Report.\",\"color\":\"dark_green\"}]}");
			reportActive = false;
		}
	}

}
