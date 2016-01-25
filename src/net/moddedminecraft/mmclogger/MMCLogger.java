package net.moddedminecraft.mmclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import mineverse.Aust1n46.chat.MineverseChat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;





public class MMCLogger extends JavaPlugin implements Listener {

	public MineverseChat MineverseChat;
	Logger log = Logger.getLogger("Minecraft");
	private FileConfiguration config;
	private File configFile;
	private CommandLogger commandLogger;
	private ChatLogger chatLogger;
	private LoginLogger loginLogger;
	public Notifier chatNotifier;
	public boolean data;
	public String dataType;

	//Date now = new Date();
	//SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	
	public File logFolder1 = new File(getDataFolder(), "logs/");
	public File clogFolder1 = new File(getDataFolder(), "commandlogs/");

	public File playersFolder = new File(getDataFolder(), "players");
	public File logFolder = new File(logFolder1, getFileDate());
	public File clogFolder = new File(clogFolder1, getFileDate());
	
	public File chatFile;
	public File commandFile;
	
	public File notifyChatFile = new File(getDataFolder(), "notifyChat.log");
	public File notifyCommandFile = new File(getDataFolder(), "notifyCommands.log");

	public void onDisable()
	{
		log.info("[MMCLogger] plugin disabled");
	}

	public void onEnable()
	{
		this.commandLogger = new CommandLogger(this);
		this.chatLogger = new ChatLogger(this);
		this.loginLogger = new LoginLogger(this);
		this.chatNotifier = new Notifier(this);
		configCheck();
		folderCheck();
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				checkDate();
			}
		}, 20L, 20L);

		log.info("[MMCLogger] plugin enabled.");
	}
	
	public static String getFileDate() {
		DateFormat dateFormat = new SimpleDateFormat("MMMMMMMMM");
		Date date = new Date();
		return dateFormat.format(date);
	} 
	
	public File getChatFile() {
		File chatFile = new File(logFolder, ChatLogger.getFileDate() + "-chat.log");
		return chatFile;
	}
	public File getCmdFile() {
		File commandFile = new File(clogFolder, ChatLogger.getFileDate() + "-cmd.log");
		return commandFile;
	}
	
	public void checkDate() {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	}

	public void folderCheck() {
		if (!clogFolder1.exists()) {
			clogFolder1.mkdir();
		}
		if (!logFolder1.exists()) {
			logFolder1.mkdir();
		}
		if (!playersFolder.exists()) {
			playersFolder.mkdir();
		}
		if (!clogFolder.exists()) {
			clogFolder.mkdir();
		}
		if (!logFolder.exists()) {
			logFolder.mkdir();
		}
	}

	public void configCheck()
	{
		String[] blacklist = { 
				"/help", 
				"/who", 
				"/home" };
		String[] commandNotifyList = { 
				"/pl", 
				"/item", 
				"/give",
				"/plugins", 
				"/version", 
				"/ver", 
				"/op" };
		String[] chatNotifyList = { 
				"ddos",
				"hack",
				"flymod",
				"dupe",
				"duplicate", 
				"duplication",
				"homo",
				"faggot" };

		File dataFolder = getDataFolder();

		try {
			this.config = getConfig();
			this.configFile = new File(dataFolder, "config.yml");
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}
			if (!this.configFile.exists()) {
				this.configFile.createNewFile();
			}
			this.config.options().header(defaultFormat());

			if (!this.config.contains("Log.toggle.globalCommands")) {
				this.config.set("Log.toggle.globalCommands", true);
			}
			if (!this.config.contains("Log.toggle.globalChat")) {
				this.config.set("Log.toggle.globalChat", true);
			}
			if (!this.config.contains("Log.toggle.playerCommands")) {
				this.config.set("Log.toggle.playerCommands", true);
			}
			if (!this.config.contains("Log.toggle.playerChat")) {
				this.config.set("Log.toggle.playerChat", true);
			}
			if (!this.config.contains("Log.toggle.logNotifyChat")) {
				this.config.set("Log.toggle.logNotifyChat", true);
			}
			if (!this.config.contains("Log.toggle.inGameNotifications")) {
				this.config.set("Log.toggle.inGameNotifications", true);
			}
			if (!this.config.contains("Log.toggle.logNotifyCommands")) {
				this.config.set("Log.toggle.logNotifyCommands", true);
			}
			if (!this.config.contains("Log.toggle.playerLogin")) {
				this.config.set("Log.toggle.playerLogin", true);
			}
			if (!this.config.contains("Log.toggle.globalLogin")) {
				this.config.set("Log.toggle.globalLogin", true);
			}

			if (!this.config.contains("Log.commands.blacklist")) {
				this.config.addDefault("Log.commands.blacklist", Arrays.asList(blacklist));
			}

			if (!this.config.contains("Log.logFormat")) {
				this.config.addDefault("Log.logFormat", "[%date] %name: %content");
			}
			if (!this.config.contains("Log.notifications.chat")) {
				this.config.addDefault("Log.notifications.chat", Arrays.asList(chatNotifyList));
			}
			if (!this.config.contains("Log.notifications.commands")) {
				this.config.addDefault("Log.notifications.commands", Arrays.asList(commandNotifyList));
			}
			this.config.options().copyDefaults(true);
			saveConfig();
		}
		catch (Exception e) {}
	}

	public String defaultFormat() {
		String dateFormat = "%date = The date and time when the content is logged.\n";
		String worldFormat = "%world = The world the player is in when the content is logged.\n";
		String xCoord = "%x = The x coordinate of player when the content is logged.\n";
		String yCoord = "%y = The y coordinate of player when the content is logged.\n";
		String zCoord = "%z = The z coordinate of player when the content is logged.\n";
		String nameFormat = "%name = The name of the player that created the content that is logged.\n";
		String contentFormat = "%content = The content that is logged.\n";
		String format = dateFormat + worldFormat + xCoord + yCoord + zCoord + nameFormat + contentFormat;
		return format;
	}


	public void ccreload(CommandSender sender)
	{
		reloadConfig();
		sender.sendMessage(ChatColor.BLUE + "[MMCLogger] configuration reloaded.");
	}


	public void deleteDirectory(File file)
			throws IOException
	{
		if (file.isDirectory())
		{
			if (file.list().length == 0)
			{
				file.delete();
			}
			else
			{
				String[] files = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);

					deleteDirectory(fileDelete);
				}

				if (file.list().length == 0) {
					file.delete();
				}
			}
		}
		else {
			file.delete();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		boolean data = getConfig().getBoolean("Database.useDatabase");
		if (cmd.getName().equalsIgnoreCase("clog")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				ccreload(sender);
				return true;
			}
			return true;
		}

		Util.sendMessage(sender, "&cInvalid command usage! Type /Reboot help");
		return true;
	}
}
