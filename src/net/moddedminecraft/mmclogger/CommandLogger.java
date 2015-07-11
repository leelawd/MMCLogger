package net.moddedminecraft.mmclogger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

public class CommandLogger implements org.bukkit.event.Listener
{
	private MMCLogger plugin;
	private Notifier notifier;

	public CommandLogger(MMCLogger plugins)
	{
		this.plugin = plugins;
		plugins.getServer().getPluginManager().registerEvents(this, plugins);
	}

	@org.bukkit.event.EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws IOException {
		Player player = event.getPlayer();

		String name = player.getName();

		String command = event.getMessage();

		Location location = player.getLocation();

		int xLocation = (int)location.getX();

		int yLocation = (int)location.getY();

		int zLocation = (int)location.getZ();

		World world = location.getWorld();

		String ipAddress = player.getAddress().getAddress().getHostAddress();

		String worldName = world.getName();

		String date = getDate();

		checkPlayer(name);

		processInformation(player, name, command, xLocation, yLocation, zLocation, worldName, date, ipAddress);
	}

	public void processInformation(Player player, String playerName, String command, int x, int y, int z, String worldName, String date, String ipAddress) {
		boolean data = this.plugin.getConfig().getBoolean("Database.useDatabase");
		boolean playerCommand = this.plugin.getConfig().getBoolean("Log.toggle.playerCommands");
		boolean globalCommand = this.plugin.getConfig().getBoolean("Log.toggle.globalCommands");
		boolean logNotifyCommands = this.plugin.getConfig().getBoolean("Log.toggle.logNotifyCommands");
		boolean inGameNotifications = this.plugin.getConfig().getBoolean("Log.toggle.inGameNotifications");

		File playerFile = new File(plugin.playersFolder, playerName + ".log");



		BukkitTask task;
		if ((globalCommand) && (!commandCheck(command)))
		{
			task = new WriteFile(formatLog(playerName, command, x, y, z, worldName, date, ipAddress), plugin.getCmdFile()).runTaskAsynchronously(this.plugin);
		}
		if ((playerCommand) && (!commandCheck(command)))
		{
			task = new WriteFile(formatLog(playerName, command, x, y, z, worldName, date, ipAddress), playerFile).runTaskAsynchronously(this.plugin);
		}
		if ((checkNotifyList(command)) && (logNotifyCommands)) {
			task = new WriteFile(formatLog(playerName, command, x, y, z, worldName, date, ipAddress), plugin.notifyCommandFile).runTaskAsynchronously(this.plugin);
		}
		if ((checkNotifyList(command)) && (inGameNotifications)) {
			this.plugin.chatNotifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.GOLD + "MMCLogger" + ChatColor.BLUE + "] " + ChatColor.GOLD + playerName + ": " + ChatColor.WHITE + command);
		}
	}

	public String checkApos(String command) {
		String fixed = command.replaceAll("'", "''");
		return fixed;
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
			log = log.replaceAll("%content", Matcher.quoteReplacement(command));
		}

		String[] logArray = { log };
		return logArray;
	}

	public boolean commandCheck(String command) {
		List commands = this.plugin.getConfig().getStringList("Log.commands.blacklist");
		String[] commandsplit = command.split(" ");
		String commandconvert = commandsplit[0];
		for (int i = 0; i < commands.size(); i++) {
			if (commandconvert.matches((String)commands.get(i))) {
				return true;
			}
		}
		return false;
	}

	public boolean checkNotifyList(String command) {
		List commands = this.plugin.getConfig().getStringList("Log.notifications.commands");
		String[] commandsplit = command.split(" ");
		String commandconvert = commandsplit[0];
		for (int i = 0; i < commands.size(); i++) {
			if (commandconvert.matches((String)commands.get(i))) {
				return true;
			}
		}
		return false;
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

}
