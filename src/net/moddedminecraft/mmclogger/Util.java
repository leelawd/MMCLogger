package net.moddedminecraft.mmclogger;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Util {

	@SuppressWarnings("unused")
	private static MMCLogger plugin;
	static int playersOnline;

	public Util(MMCLogger instance) {
		plugin = instance;
	}

	public static void broadcastMessage(String message) {
		Bukkit.getServer().broadcastMessage(processColours(message));
	}

	public static void sendMessage(CommandSender sender, String message) {
		if(sender instanceof Player) {
			sender.sendMessage(processColours(message));
		} else {
			sender.sendMessage(stripColours(message));
		}
	}

	public static void sendMessage(Player sender, String message) {
		sender.sendMessage(stripColours(message));
	}

	public static String processColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}

	public static String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "");
	}

	public static String reason(String[] s, int start, int end)
	{
		String[] args = (String[])Arrays.copyOfRange(s, start, end);
		return StringUtils.join(args, " ");
	}

}
