package net.moddedminecraft.mmclogger;

import org.bukkit.entity.Player;

public class Notifier
{
	private MMCLogger plugin;

	public Notifier(MMCLogger plugins)
	{
		this.plugin = plugins;
	}

	public void notifyPlayer(String string) {
		for (Player player : org.bukkit.Bukkit.getServer().getOnlinePlayers()) {
			if (player.hasPermission("mmclogger.notify")) {
				Util.sendMessage(player, string);
			}
		}
	}
}
