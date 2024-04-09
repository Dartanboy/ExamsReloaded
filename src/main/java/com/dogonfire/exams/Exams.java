package com.dogonfire.exams;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class Exams extends JavaPlugin
{
	private ExamManager			examManager				= null;
	private StudentManager		studentManager			= null;
	private PermissionsManager	permissionManager		= null;

	private FileConfiguration	config					= null;
	private Commands			commands				= null;

	public boolean				debug					= false;
	public boolean				examPricesEnabled		= true;
	public boolean				examRanksEnabled		= true;

	public String				serverName				= "Your Server";

	public List<String>			endExamMessages			= new ArrayList<>();
	public String				languageFilename		= "english.yml";

	public int					minExamTime				= 60;
	public int 					autoCleanTime			= 8*60;
	public int					requiredExamScore		= 80;
	public boolean				shuffleQuestionOptions	= false;

	static private Exams 		instance;

	static public Exams instance() {
		return instance;
	}

	public static void log(String message)
	{
		instance.getLogger().info(message);
	}

	public static void log(Level level, String message) { instance.getLogger().log(level, message); }

	public static void logDebug(String message)
	{
		if (instance.debug)
		{
			instance.getLogger().info(message);
		}
	}

	public static void sendInfo(Player player, String message)
	{
		player.sendMessage(ChatColor.AQUA + message);
	}

	public static void sendToAll(String message)
	{
		instance.getServer().broadcastMessage(message);
	}

	public static void sendMessage(String playerName, String message)
	{
		Objects.requireNonNull(instance.getServer().getPlayer(playerName)).sendMessage(ChatColor.AQUA + message);
	}

	public void reloadSettings()
	{
		reloadConfig();
		loadSettings();
		examManager.load();
	    studentManager.load();
	}

	public void loadSettings()
	{
		config = getConfig();
		
		serverName = config.getString("ServerName", "Your Server");
		minExamTime = config.getInt("MinExamTime", 60);
		requiredExamScore = config.getInt("RequiredExamScore", 80);
		debug = config.getBoolean("Debug", false);
		shuffleQuestionOptions = config.getBoolean("ShuffleQuestionOptions", false);
		endExamMessages = config.getStringList("EndExamMessages");
	}

	public void saveSettings()
	{
		config.set("ServerName", serverName);
		config.set("MinExamTime", minExamTime);
		config.set("RequiredExamScore", requiredExamScore);
		config.set("Debug", debug);
		config.set("ShuffleQuestionOptions", shuffleQuestionOptions);
		config.set("EndExamMessages", endExamMessages);

		saveConfig();
	}

	public void onEnable()
	{
		instance = this;

		endExamMessages.add("");
		endExamMessages.add("");
		endExamMessages.add("");
		endExamMessages.add("&e------------- Exam done -------------");
		endExamMessages.add("");
		endExamMessages.add("&b Exam Score: &e<score> &bpoints.");
		endExamMessages.add("&b Points Needed: &e<requiredScore> &bpoints.");

		examManager = new ExamManager();
		studentManager = new StudentManager();
		permissionManager = new PermissionsManager();

		this.commands = new Commands();

		loadSettings();
		saveSettings();

		examManager.load();
		studentManager.load();

		getServer().getPluginManager().registerEvents(new BlockListener(), this);
	}

	public void onDisable()
	{
		//reloadSettings();
		this.setEnabled(false);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return commands.onCommand(sender, cmd, label, args);
	}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        return this.commands.onTabComplete(sender, cmd, alias, args);
    }
}