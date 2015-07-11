package net.moddedminecraft.mmclogger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import mineverse.Aust1n46.chat.*;
import mineverse.Aust1n46.chat.alias.*;

public class ChatLogger implements org.bukkit.event.Listener
{
	private MMCLogger plugin;


	public ChatLogger(MMCLogger plugins)
	{
		this.plugin = plugins;
		plugins.getServer().getPluginManager().registerEvents(this, plugins);
	}

	@org.bukkit.event.EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) throws IOException
	{
		Player player = event.getPlayer();
		String name = player.getName();
		String message = event.getMessage();
		Location location = player.getLocation();
		int xLocation = (int)location.getX();
		int yLocation = (int)location.getY();
		int zLocation = (int)location.getZ();
		World world = location.getWorld();
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		String worldName = world.getName();
		String date = getDate();
		checkPlayer(name);

		processInformation(player, name, message, xLocation, yLocation, zLocation, worldName, date, ipAddress);
	}

	public void processInformation(Player player, String playerName, String content, int x, int y, int z, String worldName, String date, String ipAddress) {
		boolean globalChat = this.plugin.getConfig().getBoolean("Log.toggle.globalChat");
		boolean playerChat = this.plugin.getConfig().getBoolean("Log.toggle.playerChat");
		boolean logNotifyChat = this.plugin.getConfig().getBoolean("Log.toggle.logNotifyChat");
		boolean inGameNotifications = this.plugin.getConfig().getBoolean("Log.toggle.inGameNotifications");

		File playerFile = new File(plugin.playersFolder, playerName + ".log");

		BukkitTask task;
		if (globalChat)
			task = new WriteFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), plugin.getChatFile()).runTaskAsynchronously(this.plugin);
		if (playerChat)
			task = new WriteFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), playerFile).runTaskAsynchronously(this.plugin);
		if ((checkNotifyList(content)) && (logNotifyChat)) {
			task = new WriteFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), plugin.notifyChatFile).runTaskAsynchronously(this.plugin);
		}
		if ((checkNotifyList(content)) && (inGameNotifications)) {
			this.plugin.chatNotifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.GOLD + "MMCLogger" + ChatColor.BLUE + "] " + ChatColor.GOLD + playerName + ": " + ChatColor.WHITE + content);
		}
	}

	public String checkApos(String content) {
		String fixed = content.replaceAll("'", "''");
		return fixed;
	}

	public void checkPlayer(String name) throws IOException
	{
		File file = new File(plugin.playersFolder, name + ".log");
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public String[] formatLog(String playerName, String command, int x, int y, int z, String worldName, String date, String ipAddress) {
		String format = this.plugin.getConfig().getString("Log.logFormat");
		String log = format;
		if (log.contains("%ip")) {
			log = log.replaceAll("%ip", ipAddress);
		}
		if (log.contains("%date")) {
			log = log.replaceAll("%date", date);
		}
		if (log.contains("%world")) {
			log = log.replaceAll("%world", worldName);
		}
		if (log.contains("%x")) {
			log = log.replaceAll("%x", Integer.toString(x));
		}
		if (log.contains("%y")) {
			log = log.replaceAll("%y", Integer.toString(y));
		}
		if (log.contains("%z")) {
			log = log.replaceAll("%z", Integer.toString(z));
		}
		if (log.contains("%name")) {
			log = log.replaceAll("%name", playerName);
		}
		if (log.contains("%content")) {
			log = log.replaceAll("%content", java.util.regex.Matcher.quoteReplacement(command));
		}

		String[] logArray = { log };
		return logArray;
	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getFileDate() {
		DateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	} 

	public boolean checkNotifyList(String message) {
		List messageList = this.plugin.getConfig().getStringList("Log.notifications.chat");
		for (int i = 0; i < messageList.size(); i++) {
			if (message.toLowerCase().contains((CharSequence)messageList.get(i))) {
				return true;
			}
		}
		return false;
	}
}
