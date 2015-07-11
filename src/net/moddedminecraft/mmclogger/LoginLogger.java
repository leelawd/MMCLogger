package net.moddedminecraft.mmclogger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class LoginLogger implements org.bukkit.event.Listener
{
	private MMCLogger plugin;

	public LoginLogger(MMCLogger plugins)
	{
		this.plugin = plugins;
		plugins.getServer().getPluginManager().registerEvents(this, plugins);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) throws IOException
	{
		Player player = event.getPlayer();
		String name = player.getName();
		Location location = player.getLocation();
		int x = (int)location.getX();
		int y = (int)location.getY();
		int z = (int)location.getZ();
		World world = location.getWorld();
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		String worldName = world.getName();
		String date = getDate();
		String login = "1";
		checkPlayer(name);
		processInformationJoin(player, name, login, x, y, z, worldName, date, ipAddress);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) throws IOException {
		Player player = event.getPlayer();
		String name = player.getName();
		Location location = player.getLocation();
		int x = (int)location.getX();
		int y = (int)location.getY();
		int z = (int)location.getZ();
		World world = location.getWorld();
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		String worldName = world.getName();
		String date = getDate();
		String login = "0";
		checkPlayer(name);
		processInformationQuit(player, name, login, x, y, z, worldName, date, ipAddress);
	}

	public void processInformationJoin(Player player, String playerName, String login, int x, int y, int z, String worldName, String date, String ipAddress) {
		boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
		boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
		File playerFile = new File(plugin.playersFolder, playerName + ".log");
		String log = " logged in.";
		String[] content = formatLog(playerName, log, x, y, z, worldName, date, ipAddress);

		BukkitTask task;

		if (globalLogin) {
			task = new WriteFile(content, plugin.getChatFile()).runTaskAsynchronously(this.plugin);
		}
		if (playerLogin) {
			task = new WriteFile(content, playerFile).runTaskAsynchronously(this.plugin);
		}
	}

	public void processInformationQuit(Player player, String playerName, String login, int x, int y, int z, String worldName, String date, String ipAddress)
	{
		boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
		boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
		File playerFile = new File(plugin.playersFolder, playerName + ".log");
		String log = " logged out.";
		String[] content = formatLog(playerName, log, x, y, z, worldName, date, ipAddress);

		BukkitTask task;
		if (globalLogin) {
			task = new WriteFile(content, plugin.getChatFile()).runTaskAsynchronously(this.plugin);
		}
		if (playerLogin) {
			task = new WriteFile(content, playerFile).runTaskAsynchronously(this.plugin);
		}
	}

	public void checkPlayer(String name) throws IOException {
		File file = new File(plugin.playersFolder, name + ".log");
		
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public String getDate() {
		DateFormat dateFormat = new java.text.SimpleDateFormat("MMM/dd/yyyy hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String[] formatLog(String playerName, String loginString, int x, int y, int z, String worldName, String date, String ipAddress) {
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
			log = log.replaceAll("%content", java.util.regex.Matcher.quoteReplacement(loginString));
		}

		String[] logArray = { log };
		return logArray;
	}
}
