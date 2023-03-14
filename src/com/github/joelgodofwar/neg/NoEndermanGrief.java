package com.github.joelgodofwar.neg;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.joelgodofwar.neg.gui.GUIMoveItem;
//import com.github.joelgodofwar.jgowlib.utils.bukkit.Metrics;
import com.github.joelgodofwar.neg.nms.SettingsBook;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_14_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_15_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_16_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_16_R2;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_16_R3;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_17_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_18_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_19_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_19_R2;
import com.github.joelgodofwar.neg.util.Ansi;
import com.github.joelgodofwar.neg.util.Metrics;
import com.github.joelgodofwar.neg.util.PluginUtils;
import com.github.joelgodofwar.neg.util.UpdateChecker;
import com.github.joelgodofwar.neg.util.Utils;
import com.github.joelgodofwar.neg.util.VersionChecker;
import com.github.joelgodofwar.neg.util.YmlConfiguration;


@SuppressWarnings("unused")
public class NoEndermanGrief extends JavaPlugin implements Listener{
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static String daLang;
	public static boolean cancelbroadcast;
	public static boolean debug;
	public static boolean allowSpawnSH;
	public static boolean allowSpawnWT;
	public static boolean allowPickup;
	public static boolean allowExplode;
	File langFile;
	FileConfiguration lang;
	/** update checker variables */
	public String updateurl = "https://github.com/JoelGodOfwar/NoEndermanGrief/raw/master/versions/{vers}/version.txt";
	public String newVerMsg;// = Ansi.YELLOW + this.getName() + Ansi.MAGENTA + " v{oVer}" + Ansi.RESET + " " + lang.get("newvers") + Ansi.GREEN + " v{nVer}" + Ansi.RESET;
	public int updateVersion = 71236; // https://spigotmc.org/resources/71236
	boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public static boolean UpdateCheck;
	public String thisName = this.getName();
	public String thisVersion = this.getDescription().getVersion();
	/** end update checker variables */
	YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	boolean colorful_console;
	private SettingsBook setingsbook;
	private boolean correctVersion = true;
	String configVersion = "1.0.6";
	String langVersion = "1.0.6";
	String pluginName = this.getDescription().getName();
	
	@Override // TODO: onEnable
	public void onEnable(){
		UpdateCheck = getConfig().getBoolean("auto_update_check", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		config = new YmlConfiguration();
		colorful_console = getConfig().getBoolean("console.colorful_console", true);
		if(!getConfig().getBoolean("console.longpluginname", true)) {
			pluginName = "NEG";
		}
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.YELLOW + "**************************************" + Ansi.RESET);
		logger.info(Ansi.GREEN + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.RESET + " Loading...");
		
		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			log("jarfile contains dev, debug set to true.");
		}else {
			logger.info("This is not a DEV version.");
		}
		
		logger.info("Checking lang files...");
		/** Lang file check */
		if(debug){logDebug("datafolder=" + getDataFolder());}
		langFile = new File(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar, daLang + ".yml");//\
		if(debug){logDebug("langFilePath=" + langFile.getPath());}
		if(!langFile.exists()){                                  // checks if the yaml does not exist
			langFile.getParentFile().mkdirs();                  // creates the /plugins/<pluginName>/ directory if not found
			saveResource("lang" + File.separatorChar + "cs_CZ.yml", true);
			saveResource("lang" + File.separatorChar + "de_DE.yml", true);
			saveResource("lang" + File.separatorChar + "en_US.yml", true);
			saveResource("lang" + File.separatorChar + "fr_FR.yml", true);
			saveResource("lang" + File.separatorChar + "lol_US.yml", true);
			saveResource("lang" + File.separatorChar + "nl_NL.yml", true);
			saveResource("lang" + File.separatorChar + "pt_BR.yml", true);
			log("lang file not found! copied cs_CZ.yml, de_DE.yml, en_US.yml, es_MX.yml, fr_FR.yml, nl_NL.yml, and pt_BR.yml to "
			+ getDataFolder() + "" + File.separatorChar + "lang");
			//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
        }
		lang = new YamlConfiguration();
		try {
			lang.load(langFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		logger.info("Checking lang files version...");
		String checklangversion = lang.getString("version", "1.0.0");
		if(checklangversion != null){
			if(!checklangversion.equalsIgnoreCase(langVersion)){
				saveResource("lang" + File.separatorChar + "cs_CZ.yml", true);
				saveResource("lang" + File.separatorChar + "de_DE.yml", true);
				saveResource("lang" + File.separatorChar + "en_US.yml", true);
				saveResource("lang" + File.separatorChar + "fr_FR.yml", true);
				saveResource("lang" + File.separatorChar + "lol_US.yml", true);
				saveResource("lang" + File.separatorChar + "nl_NL.yml", true);
				saveResource("lang" + File.separatorChar + "pt_BR.yml", true);
				log("lang file outdated! copied cs_CZ.yml, de_DE.yml, en_US.yml, es_MX.yml, fr_FR.yml, nl_NL.yml, and pt_BR.yml to "
						+ getDataFolder() + "" + File.separatorChar + "lang");
			}
		}else{
			saveResource("lang" + File.separatorChar + "cs_CZ.yml", true);
			saveResource("lang" + File.separatorChar + "de_DE.yml", true);
			saveResource("lang" + File.separatorChar + "en_US.yml", true);
			saveResource("lang" + File.separatorChar + "fr_FR.yml", true);
			saveResource("lang" + File.separatorChar + "lol_US.yml", true);
			saveResource("lang" + File.separatorChar + "nl_NL.yml", true);
			saveResource("lang" + File.separatorChar + "pt_BR.yml", true);
			log("lang file not found! copied cs_CZ.yml, de_DE.yml, en_US.yml, es_MX.yml, fr_FR.yml, nl_NL.yml, and pt_BR.yml to "
					+ getDataFolder() + "" + File.separatorChar + "lang");
		}
		/** Lang file check */
		if( !(Double.parseDouble( getMCVersion().substring(0, 4) ) >= 1.14) ){
			logger.info(Ansi.RED + "WARNING!" + Ansi.GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + Ansi.RESET);
			logger.info(Ansi.RED + "WARNING! " + Ansi.YELLOW + lang.get("server_not_version") + Ansi.RESET);
			logger.info(Ansi.RED + "WARNING! " + Ansi.YELLOW + this.getName() + " v" + this.getDescription().getVersion() + " disabling." + Ansi.RESET);
			logger.info(Ansi.RED + "WARNING!" + Ansi.GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + Ansi.RESET);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		logger.info("Checking config file...");
		/**  Check for config */
		try{
			if(!getDataFolder().exists()){
				log("Data Folder doesn't exist");
				log("Creating Data Folder");
				getDataFolder().mkdirs();
				log("Data Folder Created at " + getDataFolder());
			}
			File  file = new File(getDataFolder(), "config.yml");
			log("" + file);
			if(!file.exists()){
				log("config.yml not found, creating!");
				saveResource("config.yml", true);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.info("Checking config file version...");
		String checkconfigversion = getConfig().getString("version", "1.0.0");
		logger.info("Config file version=" + checkconfigversion + " expected=" + configVersion);
		if(checkconfigversion != null){
			if(!checkconfigversion.equalsIgnoreCase(configVersion)){
				try {
					copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					oldconfig.load(new File(getDataFolder(), "config.yml"));
				} catch (IOException | InvalidConfigurationException e2) {
					logWarn("Could not load config.yml");
					e2.printStackTrace();
				}
				saveResource("config.yml", true);
				try {
					config.load(new File(getDataFolder(), "config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					logWarn("Could not load config.yml");
					e1.printStackTrace();
				}
				try {
					oldconfig.load(new File(getDataFolder(), "old_config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					e1.printStackTrace();
				}
				config.set("auto_update_check", oldconfig.get("auto_update_check", true));
				config.set("debug", oldconfig.get("debug", false));
				config.set("lang", oldconfig.get("lang", "en_US"));
				config.set("console.colorful_console", oldconfig.get("colorful_console", true));
				config.set("console.longpluginname", oldconfig.get("console.longpluginname", true));
				config.set("enderman_grief", oldconfig.get("enderman_grief", false));
				config.set("skeleton_horse_spawn", oldconfig.get("skeleton_horse_spawn", false));
				config.set("creeper_grief", oldconfig.get("creeper_grief", false));
				config.set("wandering_trader_spawn", oldconfig.get("wandering_trader", false));
				config.set("ghast_grief", oldconfig.get("ghast_grief", false));
				config.set("phantom_spawn", oldconfig.get("phantom_spawn", false));
				config.set("pillager_patrol_spawn", oldconfig.get("pillager_patrol_spawn", false));
				try {
					config.save(new File(getDataFolder(), "config.yml"));
				} catch (IOException e) {
					logWarn("Could not save old settings to config.yml");
					e.printStackTrace();
				}
				log("config.yml has been updated");
			}
		}
		/** end config check */
		try {
			config.load(new File(getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			logWarn("Could not load config.yml");
			e1.printStackTrace();
		}

		allowSpawnSH = getConfig().getBoolean("skeleton_horse_spawn");
		allowSpawnWT = getConfig().getBoolean("wandering_trader_spawn");
		allowPickup = getConfig().getBoolean("enderman_grief");
		allowExplode = getConfig().getBoolean("creeper_grief");
		
		if(debug){
			logDebug("Config.yml dump");
			logDebug("auto_update_check=" + getConfig().getBoolean("auto_update_check"));
			logDebug("debug=" + getConfig().getBoolean("debug"));
			logDebug("lang=" + getConfig().getString("lang"));
			
			logDebug("console.colorful_console=" + getConfig().getBoolean("console.colorful_console"));
			logDebug("console.longpluginname=" + getConfig().getBoolean("console.longpluginname"));
			logDebug("enderman_grief=" + getConfig().getBoolean("enderman_grief"));
			logDebug("skeleton_horse_spawn=" + getConfig().getBoolean("skeleton_horse_spawn"));
			logDebug("creeper_grief=" + getConfig().getBoolean("creeper_grief"));
			logDebug("wandering_trader_spawn=" + getConfig().getBoolean("wandering_trader_spawn"));
			logDebug("ghast_grief=" + getConfig().getBoolean("ghast_grief"));
			logDebug("phantom_spawn=" + getConfig().getBoolean("phantom_spawn"));
			logDebug("pillager_patrol_spawn=" + getConfig().getBoolean("pillager_patrol_spawn"));
		}
		
		/** Update Checker */
		newVerMsg = Ansi.YELLOW + this.getName() + Ansi.MAGENTA + " v{oVer}" + Ansi.RESET + " " + lang.get("newvers") + Ansi.GREEN + " v{nVer}" + Ansi.RESET;
		if(UpdateCheck){
			try {
				Bukkit.getConsoleSender().sendMessage("Checking for updates...");
				UpdateChecker updater = new UpdateChecker(this, updateVersion, updateurl);
				if(updater.checkForUpdates()) {
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();
					
					log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					log(Level.WARNING, "* " + lang.get("newvers.message").toString().replace("<MyPlugin>", this.getName()) );
					log(Level.WARNING, "* " + lang.get("newvers.old_vers") + ChatColor.RED + UColdVers );
					log(Level.WARNING, "* " + lang.get("newvers.new_vers") + ChatColor.GREEN + UCnewVers );
					log(Level.WARNING, "*");
					log(Level.WARNING, "* " + lang.get("newvers.please_update") );
					log(Level.WARNING, "*");
					log(Level.WARNING, "* " + lang.get("newvers.download") + ": https://www.spigotmc.org/resources/no-enderman-grief2.71236/history");
					log(Level.WARNING, "* " + lang.get("newvers.donate") + ": https://ko-fi.com/joelgodofwar");
					log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
					//Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
				}else{
					UpdateAvailable = false;
				}
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
				e.printStackTrace();
			}
		}
		/** end update checker */
		
		try {
			getConfig().load(new File(getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		
		logDebug("lang=" + daLang);
		getServer().getPluginManager().registerEvents(this, this);
		//getServer().getPluginManager().registerEvents(new GUIMoveItem(), this);  // Comment out for release versions, un comment for Dev builds.
		consoleInfo(Ansi.BOLD + "ENABLED" + Ansi.RESET);
		
		
		this.correctVersion = this.setupBook();
		
		try {
			Metrics metrics  = new Metrics(this, 6004);
			// TODO:
			metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> call() throws Exception {
					Map<String, Integer> valueMap = new HashMap<>();
					if(getServer().getPluginManager().getPlugin("DragonDropElytra") != null){valueMap.put("DragonDropElytra", 1);}
		    		//if(getServer().getPluginManager().getPlugin("NoEndermanGrief") != null){valueMap.put("NoEndermanGrief", 1);}
		    		if(getServer().getPluginManager().getPlugin("PortalHelper") != null){valueMap.put("PortalHelper", 1);}
		    		if(getServer().getPluginManager().getPlugin("ShulkerRespawner") != null){valueMap.put("ShulkerRespawner", 1);}
		    		if(getServer().getPluginManager().getPlugin("MoreMobHeads") != null){valueMap.put("MoreMobHeads", 1);}
		    		if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){valueMap.put("SilenceMobs", 1);}
		    		if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
					if(getServer().getPluginManager().getPlugin("VillagerWorkstationHighlights") != null){valueMap.put("VillagerWorkstationHighlights", 1);}
					if(getServer().getPluginManager().getPlugin("RotationalWrench") != null){valueMap.put("RotationalWrench", 1);}
					return valueMap;
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("auto_update_check", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("auto_update_check").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_debug", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("debug").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("enderman_grief", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("enderman_grief").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("skeleton_horse_spawn", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("skeleton_horse_spawn").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("creeper_grief", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("creeper_grief").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("wandering_trader", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("wandering_trader_spawn").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_lang", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("lang").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("ghast_grief", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("ghast_grief").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("phantom_spawn", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("phantom_spawn").toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("pillager_patrol_spawn", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "" + getConfig().getString("pillager_patrol_spawn").toUpperCase();
				}
			}));
		}catch (Exception e){
			// Failed to submit the stats
		}
	}
	
	@Override // TODO: onDisable
	public void onDisable(){
		//saveConfig();
		config = null;
		consoleInfo(Ansi.BOLD + "DISABLED" + Ansi.RESET);
	}
	
	public void consoleInfo(String state) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.YELLOW + "**************************************" + Ansi.RESET);
		logger.info(Ansi.GREEN + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.RESET + " is " + state);
		logger.info(Ansi.YELLOW + "**************************************" + Ansi.RESET);
	}
	
	public void log2(String dalog) {
		logger.info("NEG  " + dalog);
	}
	public void log(String dalog){
		logger.info(Ansi.YELLOW + "" + pluginName + Ansi.RESET + " " + dalog + Ansi.RESET);
	}
	public void log(Level lvl, String dalog){
    	logger.log(lvl, dalog);
    }
	public void logDebug(String dalog){
		log(" " + this.getDescription().getVersion() + Ansi.RED + Ansi.BOLD + " [DEBUG] " + Ansi.RESET + dalog);
	}
	public void logWarn(String dalog){
		log(" " + this.getDescription().getVersion() + Ansi.RED + Ansi.BOLD + " [WARNING] " + Ansi.RESET + dalog);
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		//if (!tr.isrunworld(ac.getName(), e.getEntity().getLocation().getWorld().getName()) )
			//return;

		if (e.getEntity() == null)
			return;

		if (e.getEntity().getType() == EntityType.ENDERMAN) {
			if(!getConfig().getBoolean("enderman_grief", false)){
				e.setCancelled(true);
			}
			if(debug){log("" + lang.get("enderman_pickup") + e.getBlock().getType() + " at " + e.getBlock().getLocation());} //
			return;
		}
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityExplodeEvent e) {
		if (e.getEntity().getType() == EntityType.CREEPER) {
			if(!getConfig().getBoolean("creeper_grief", false)){
				e.setCancelled(true);
			}
			if(debug){log("" + lang.get("creeper_explode") + e.getLocation().getBlockX() + ", " + e.getLocation().getBlockZ());} //
			return;
		}
		if (e.getEntity().getType() == EntityType.FIREBALL  && ((Fireball) e.getEntity()).getShooter() instanceof Ghast) {
			if(!getConfig().getBoolean("ghast_grief", false)){
				Entity fireball = e.getEntity();
				((Fireball) fireball).setIsIncendiary(false);
				((Fireball) fireball).setYield(0F);
				e.setCancelled(true);
			}
			if(debug){log("" + lang.get("ghast_explode") + e.getLocation().getBlockX() + ", " + e.getLocation().getBlockZ());} //
			return;
		}
		
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if( UpdateAvailable && ( player.isOp() || player.hasPermission("noendermangrief.showUpdateAvailable") || player.hasPermission("noendermangrief.admin") ) ){
			String links = "[\"\",{\"text\":\"Download\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/no-enderman-grief2.71236/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Download the latest version.\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Download the latest version.\"}},{\"text\":\"| \"},{\"text\":\"Donate\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Donate to the plugin maker.\"}},{\"text\":\" | \"},{\"text\":\"ChangeLog\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/no-enderman-grief2.71236/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"View the ChangeLog.\"}}]";
			String versions = "" + ChatColor.GRAY + lang.get("newvers.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + lang.get("newvers.old_vers") + ": " + ChatColor.RED + "{oVers}";
			player.sendMessage("" + ChatColor.GRAY + lang.get("newvers.message").toString().replace("<MyPlugin>", ChatColor.GOLD + this.getName() + ChatColor.GRAY) );
			Utils.sendJson(player, links);
			player.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
			//p.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("newvers") + 
					//" \n" + ChatColor.GREEN + UpdateChecker.getResourceUrl() + ChatColor.RESET);
			//player.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " v" + UColdVers + ChatColor.RESET + " " + lang.get("newvers") + ChatColor.GREEN + " v" + UCnewVers + ChatColor.RESET + "\n" + ChatColor.GREEN +					UpdateChecker.getResourceUrl() + ChatColor.RESET);
		}
		
		if( player.getDisplayName().equals("JoelYahwehOfWar") ||  player.getDisplayName().equals("JoelGodOfWar") ){
			player.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
		}
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e){ //onEntitySpawn(EntitySpawnEvent e) {
		Entity entity = e.getEntity();
		if (entity instanceof SkeletonHorse){
			if(!getConfig().getBoolean("skeleton_horse_spawn", false)){
				if(debug){log("" + lang.get("skeleton_horse") + e.getLocation());} //
				e.setCancelled(true);
			}
		}
		if (entity instanceof WanderingTrader){
			if(!getConfig().getBoolean("wandering_trader_spawn", false)){
				if(debug){log("" + lang.get("wandering_trader") + e.getLocation());} //
				e.setCancelled(true);
			}
		}
		if (entity instanceof Phantom){
			if(!getConfig().getBoolean("phantom_spawn", false)){
				if(debug){log("" + lang.get("phantom") + e.getLocation());} //
				e.setCancelled(true);
			}
		}
		if(e.getSpawnReason() == SpawnReason.PATROL) {
			if(!getConfig().getBoolean("pillager_patrol_spawn", false)){
				if(debug){log("" + lang.get("pillager_patrol") + e.getLocation());} //
				e.setCancelled(true);
			}
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){ // TODO: Commands
		//Player p = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("NEG")){
			if (args.length == 0)
			{
				/** Check if sender has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") || sender.isOp() ) {
						/** Command code */
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
						sender.sendMessage(ChatColor.GOLD + " ");
						sender.sendMessage(ChatColor.GOLD + " " + lang.get("newvers.donate") + ": https://ko-fi.com/joelgodofwar");
						sender.sendMessage(ChatColor.GOLD + " ");
						if( sender.isOp()||sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
							sender.sendMessage(ChatColor.WHITE + " -<[" + ChatColor.GOLD + " OP Commands " + ChatColor.WHITE + "}>-");
							sender.sendMessage(ChatColor.GOLD + " /NEG update - " + lang.get("help_update"));//Check for update.");
							sender.sendMessage(ChatColor.GOLD + " /NEG reload - " + lang.get("help_reload") );//Reload config file.");
							if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") || 
									!(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ){
								sender.sendMessage(ChatColor.GOLD + " /NEG toggledebug - " + lang.get("debuguse"));
							}
						}
						if( sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ){
							sender.sendMessage(ChatColor.WHITE + " -<[" + ChatColor.GOLD + " Admin Commands " + ChatColor.WHITE + "}>-");
							if( sender instanceof Player ) {
								sender.sendMessage(ChatColor.GOLD + " /NEG BOOK - " + lang.get("help_book"));
							}
							sender.sendMessage(ChatColor.GOLD + " /NEG EG true/false - " + lang.get("help_endermen"));
							sender.sendMessage(ChatColor.GOLD + " /NEG SH true/false - " + lang.get("help_skeleton_horse"));
							sender.sendMessage(ChatColor.GOLD + " /NEG CG true/false - " + lang.get("help_creeper"));
							sender.sendMessage(ChatColor.GOLD + " /NEG WT true/false - " + lang.get("help_wandering_trader"));
							sender.sendMessage(ChatColor.GOLD + " /NEG GG true/false - " + lang.get("help_ghast"));
							sender.sendMessage(ChatColor.GOLD + " /NEG PG true/false - " + lang.get("help_phantom"));
							sender.sendMessage(ChatColor.GOLD + " /NEG PP true/false - " + lang.get("help_pillager_patrol"));
						}
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
						return true;
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("no_perm"));
						return false;
					}
			}
			if(args[0].equalsIgnoreCase("dumpinfo") || args[0].equalsIgnoreCase("di")){
				if( sender.isOp() || sender.hasPermission("noendermangrief.op") 
						|| sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player)) {
					Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
					//StringBuilder messageBuilder = new StringBuilder();
					
					log2("" + Ansi.yellowBold + "Please copy from this line until the second line of dashes." + Ansi.ANSI_RESET);
					log2("" + Ansi.yellowBold + "------------------------------------------------------------" + Ansi.ANSI_RESET);
					log2("Config.yml dump");
					log2("auto_update_check=" + getConfig().getBoolean("auto_update_check"));
					log2("debug=" + getConfig().getBoolean("debug"));
					log2("lang=" + getConfig().getString("lang"));
	
					log2("console.colorful_console=" + getConfig().getBoolean("console.colorful_console"));
					log2("console.longpluginname=" + getConfig().getBoolean("console.longpluginname"));
					log2("enderman_grief=" + getConfig().getBoolean("enderman_grief"));
					log2("skeleton_horse_spawn=" + getConfig().getBoolean("skeleton_horse_spawn"));
					log2("creeper_grief=" + getConfig().getBoolean("creeper_grief"));
					log2("wandering_trader_spawn=" + getConfig().getBoolean("wandering_trader_spawn"));
					log2("ghast_grief=" + getConfig().getBoolean("ghast_grief"));
					log2("phantom_spawn=" + getConfig().getBoolean("phantom_spawn"));
					log2("pillager_patrol_spawn=" + getConfig().getBoolean("pillager_patrol_spawn"));
					log2("");
					log2("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
					log2("");
					log2("Plugins dump");
					PluginUtils.loadPluginJarNames();
						for (Plugin plugin : plugins) {
							Map<String, Object> info = PluginUtils.getInfo(plugin);
							String pluginName = (String) info.get("Name");
							String pluginVersion = (String) info.get("Version");
	
							log2(String.format("[%s] v%s", pluginName, pluginVersion));
							log2(String.format("  FileName: %s", info.get("FileName")));
							log2(String.format("  Main: %s", info.get("Main")));
							log2(String.format("  Enabled: %b, API-Version: %s", info.get("Enabled"), info.get("API-Version")));
							log2(String.format("  Description: %s", info.get("Description")));
							log2(String.format("  Authors: %s", info.get("Authors")));
							log2(String.format("  Website: %s", info.get("Website")));
							log2(String.format("  Depends: %s", info.get("Depends")));
							log2(String.format("  SoftDepends: %s", info.get("SoftDepends")));
							log2(String.format("  Commands: %s", info.get("Commands")));
							log2(String.format("  Permissions: %s", info.get("Permissions")));
							log2(String.format("  Default Permissions: %s", info.get("Default Permissions")));
							log2(String.format("  Load: %s", info.get("Load")));
							log2(String.format("  LoadBefore: %s", info.get("LoadBefore")));
							log2(String.format("  Provides: %s", info.get("Provides")));
							log2("");
						}
					log2("" + Ansi.yellowBold + "------------------------------------------------------------" + Ansi.ANSI_RESET);
					log2("" + Ansi.yellowBold + "This is the end of the debug dump." + Ansi.ANSI_RESET);
					//sender.sendMessage(messageBuilder.toString());
		            return true;
				}
			}
			if(args[0].equalsIgnoreCase("config")){
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_RED + "This command can not be sent by console");
					return false;
				}
				if( (sender instanceof Player) && sender.hasPermission("noendermangrief.admin") ){
					  Player player = (Player) sender;
					  Inventory gui = Bukkit.createInventory(player, 6*9, "Configurations");
						 
			            //Menu Options(Items)
					  	ItemStack btnSpace = new ItemStack(Material.AIR);
			            ItemStack btnUpdate = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnUpdateTrue = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnUpdateFalse = new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnLang = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnLangCZ = new ItemStack(Material.PAPER);
			            ItemStack btnLangDE = new ItemStack(Material.PAPER);
			            ItemStack btnLangEN = new ItemStack(Material.PAPER);
			            ItemStack btnLangFR = new ItemStack(Material.PAPER);
			            ItemStack btnLangLOL = new ItemStack(Material.PAPER);
			            ItemStack btnLangNL = new ItemStack(Material.PAPER);
			            ItemStack btnLangBR = new ItemStack(Material.PAPER);
			            
			            ItemStack btnDebug =  new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnDebugTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnDebugFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnConsole = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnConsoleTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnConsoleFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnTrader = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnTraderTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnTraderFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnPillager = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnPillagerTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnPillagerFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnEnder = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnEnderTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnEnderFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnGhast = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnGhastTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnGhastFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnHorse = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnHorseTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnHorseFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnPhantom = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnPhantomTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnPhantomFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnCreeper = new ItemStack(Material.WRITABLE_BOOK);
			            ItemStack btnCreeperTrue =  new ItemStack(Material.LIME_STAINED_GLASS_PANE);
			            ItemStack btnCreeperFalse =  new ItemStack(Material.RED_STAINED_GLASS_PANE);
			            
			            ItemStack btnSave = new ItemStack(Material.SLIME_BLOCK);
			            ItemStack btnCancel =  new ItemStack(Material.BARRIER);
			            
			 
			            //Edit the items
			            ItemMeta update_meta = btnUpdate.getItemMeta();
			            update_meta.setDisplayName("auto_update_check");
			            ArrayList<String> update_lore = new ArrayList<>();
			            update_lore.add(ChatColor.AQUA + "Should this plugin ");
			            update_lore.add(ChatColor.GREEN + "automatically" + ChatColor.AQUA + " check");
			            update_lore.add(ChatColor.AQUA + " for updates?");
			            update_meta.setLore(update_lore);
			            btnUpdate.setItemMeta(update_meta);
			            
			            ItemMeta updatetrue_meta = btnUpdateTrue.getItemMeta();
			            updatetrue_meta.setDisplayName("Set auto_update_check to True");
			            ArrayList<String> updatetrue_lore = new ArrayList<>();
			            //updatetrue_lore.add(ChatColor.AQUA + "Set auto_update_check to True");
			            updatetrue_lore.add(ChatColor.YELLOW + "Will check for updates.");
			            updatetrue_meta.setLore(updatetrue_lore);
			            btnUpdateTrue.setItemMeta(updatetrue_meta);
			            
			            ItemMeta updatefalse_meta = btnUpdateTrue.getItemMeta();
			            updatefalse_meta.setDisplayName("Set auto_update_check to False");
			            ArrayList<String> updatefalse_lore = new ArrayList<>();
			            //updatefalse_lore.add(ChatColor.AQUA + "Set auto_update_check to False");
			            updatefalse_lore.add(ChatColor.YELLOW + "Will not check for updates.");
			            updatefalse_meta.setLore(updatefalse_lore);
			            btnUpdateFalse.setItemMeta(updatefalse_meta);
			            
			            
			            
			            ItemMeta lang_meta = btnLang.getItemMeta();
			            lang_meta.setDisplayName("language");
			            ArrayList<String> lang_lore = new ArrayList<>();
			            lang_lore.add(ChatColor.AQUA + "Select your preferred language");
			            lang_meta.setLore(lang_lore);
			            btnLang.setItemMeta(lang_meta);
			            
			            ItemMeta langCZ_meta = btnLangCZ.getItemMeta();
			            langCZ_meta.setDisplayName("čeština (cs-CZ)");
			            ArrayList<String> langCZ_lore = new ArrayList<>();
			            langCZ_lore.add(ChatColor.AQUA + "Jako jazyk vyberte češtinu.");
			            langCZ_meta.setLore(langCZ_lore);
			            btnLangCZ.setItemMeta(langCZ_meta);
			            
			            ItemMeta langDE_meta = btnLangDE.getItemMeta();
			            langDE_meta.setDisplayName("Deutsche (de_DE)");
			            ArrayList<String> langDE_lore = new ArrayList<>();
			            langDE_lore.add(ChatColor.AQUA + "Wählen Sie Deutsch als Sprache aus.");
			            langDE_meta.setLore(langDE_lore);
			            btnLangDE.setItemMeta(langDE_meta);
			            
			            ItemMeta langEN_meta = btnLangEN.getItemMeta();
			            langEN_meta.setDisplayName("English (en_US)");
			            ArrayList<String> langEN_lore = new ArrayList<>();
			            langEN_lore.add(ChatColor.AQUA + "Select English as your language.");
			            langEN_meta.setLore(langEN_lore);
			            btnLangEN.setItemMeta(langEN_meta);
			            
			            ItemMeta langFR_meta = btnLangFR.getItemMeta();
			            langFR_meta.setDisplayName("Français (fr_FR)");
			            ArrayList<String> langFR_lore = new ArrayList<>();
			            langFR_lore.add(ChatColor.AQUA + "Sélectionnez Français comme langue.");
			            langFR_meta.setLore(langFR_lore);
			            btnLangFR.setItemMeta(langFR_meta);
			            
			            ItemMeta langLOL_meta = btnLangLOL.getItemMeta();
			            langLOL_meta.setDisplayName("LoL Cat (lol_US)");
			            ArrayList<String> langLOL_lore = new ArrayList<>();
			            langLOL_lore.add(ChatColor.AQUA + "Select lulz kat az ur language.");
			            langLOL_meta.setLore(langLOL_lore);
			            btnLangLOL.setItemMeta(langLOL_meta);
			            
			            ItemMeta langNL_meta = btnLangNL.getItemMeta();
			            langNL_meta.setDisplayName("Nederlands (nl_NL)");
			            ArrayList<String> langNL_lore = new ArrayList<>();
			            langNL_lore.add(ChatColor.AQUA + "Selecteer Nederlands als je taal.");
			            langNL_meta.setLore(langNL_lore);
			            btnLangNL.setItemMeta(langNL_meta);
			            
			            ItemMeta langBR_meta = btnLangBR.getItemMeta();
			            langBR_meta.setDisplayName("Português (pt_BR)");
			            ArrayList<String> langBR_lore = new ArrayList<>();
			            langBR_lore.add(ChatColor.AQUA + "Selecione Português como seu idioma.");
			            langBR_meta.setLore(langBR_lore);
			            btnLangBR.setItemMeta(langBR_meta);
			            
			            
			 
			            ItemMeta debug_meta = btnDebug.getItemMeta();
			            debug_meta.setDisplayName("debug");
			            ArrayList<String> debug_lore = new ArrayList<>();
			            debug_lore.add(ChatColor.AQUA + "Set to true before.");
			            debug_lore.add(ChatColor.AQUA + "sending a log about");
			            debug_lore.add(ChatColor.AQUA + "an issue.");
			            debug_lore.add(" ");
			            debug_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "Logs trace data");
			            debug_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW   + "required to pinpoint");
			            debug_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "where errors are.");
			            debug_meta.setLore(debug_lore);
			            btnDebug.setItemMeta(debug_meta);

			            ItemMeta debugtrue_meta = btnUpdateTrue.getItemMeta();
			            debugtrue_meta.setDisplayName("Set debug to True");
			            ArrayList<String> debugtrue_lore = new ArrayList<>();
			            //debugtrue_lore.add(ChatColor.AQUA + "Set debug to True");
			            debugtrue_lore.add(ChatColor.YELLOW + "Will log debug information.");
			            debugtrue_meta.setLore(debugtrue_lore);
			            btnDebugTrue.setItemMeta(debugtrue_meta);
			            
			            ItemMeta debugfalse_meta = btnUpdateTrue.getItemMeta();
			            debugfalse_meta.setDisplayName("Set debug to False");
			            ArrayList<String> debugfalse_lore = new ArrayList<>();
			            //debugfalse_lore.add(ChatColor.AQUA + "Set debug to False");
			            debugfalse_lore.add(ChatColor.YELLOW + "Will not log debug information.");
			            debugfalse_meta.setLore(debugfalse_lore);
			            btnDebugFalse.setItemMeta(debugfalse_meta);
			            
			            
			 
			            ItemMeta console_meta = btnConsole.getItemMeta();
			            console_meta.setDisplayName("console.colorful_console");
			            ArrayList<String> console_lore = new ArrayList<>();
			            console_lore.add(ChatColor.AQUA + "Enables fancy ANSI");
			            console_lore.add(ChatColor.AQUA + "colors in console.");
			            console_lore.add(" ");
			            console_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "(Disable if you're");
			            console_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW   + "getting weird characters");
			            console_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "in the console.)");
			            console_meta.setLore(console_lore);
			            btnConsole.setItemMeta(console_meta);
			            
			            ItemMeta consoletrue_meta = btnConsoleTrue.getItemMeta();
			            consoletrue_meta.setDisplayName("Set console.colorful_console to True");
			            ArrayList<String> consoletrue_lore = new ArrayList<>();
			            //consoletrue_lore.add(ChatColor.AQUA + "Set console.colorful_console to True");
			            consoletrue_lore.add(ChatColor.YELLOW + "Will have colorful text in console.");
			            consoletrue_meta.setLore(consoletrue_lore);
			            btnConsoleTrue.setItemMeta(consoletrue_meta);
			            
			            ItemMeta consolefalse_meta = btnConsoleFalse.getItemMeta();
			            consolefalse_meta.setDisplayName("Set console.colorful_console to False");
			            ArrayList<String> consolefalse_lore = new ArrayList<>();
			            //consolefalse_lore.add(ChatColor.AQUA + "Set console.colorful_console to False");
			            consolefalse_lore.add(ChatColor.YELLOW + "Will not have colorful text in console.");
			            consolefalse_meta.setLore(consolefalse_lore);
			            btnConsoleFalse.setItemMeta(consolefalse_meta);
			            
			            
			 
			            ItemMeta trader_meta = btnTrader.getItemMeta();
			            trader_meta.setDisplayName("wandering_trader_spawn");
			            ArrayList<String> trader_lore = new ArrayList<>();
			            trader_lore.add(ChatColor.AQUA + "Set if Wandering");
			            trader_lore.add(ChatColor.AQUA + "Traders should spawn.");
			            trader_lore.add(" ");
			            trader_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no spawn");
			            trader_meta.setLore(trader_lore);
			            btnTrader.setItemMeta(trader_meta);
			            
			            ItemMeta tradertrue_meta = btnTraderTrue.getItemMeta();
			            tradertrue_meta.setDisplayName("Set wandering_trader_spawn to True");
			            ArrayList<String> tradertrue_lore = new ArrayList<>();
			            //tradertrue_lore.add(ChatColor.AQUA + "Set wandering_trader_spawn to True");
			            tradertrue_lore.add(ChatColor.YELLOW + "Wandering Traders will spawn.");
			            tradertrue_meta.setLore(tradertrue_lore);
			            btnTraderTrue.setItemMeta(tradertrue_meta);
			            
			            ItemMeta traderfalse_meta = btnTraderFalse.getItemMeta();
			            traderfalse_meta.setDisplayName("Set wandering_trader_spawn to False");
			            ArrayList<String> traderfalse_lore = new ArrayList<>();
			            //traderfalse_lore.add(ChatColor.AQUA + "Set wandering_trader_spawn to False");
			            traderfalse_lore.add(ChatColor.YELLOW + "Wandering Traders will NOT spawn.");
			            traderfalse_meta.setLore(traderfalse_lore);
			            btnTraderFalse.setItemMeta(traderfalse_meta);
			            
			            
			 
			            ItemMeta pillager_meta = btnPillager.getItemMeta();
			            pillager_meta.setDisplayName("pillager_patrol_spawn");
			            ArrayList<String> pillager_lore = new ArrayList<>();
			            pillager_lore.add(ChatColor.AQUA + "Set if Pillager");
			            pillager_lore.add(ChatColor.AQUA + "Patrols should spawn.");
			            pillager_lore.add(" ");
			            pillager_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no spawn");
			            pillager_meta.setLore(pillager_lore);
			            btnPillager.setItemMeta(pillager_meta);
			            
			            ItemMeta pillagertrue_meta = btnPillagerTrue.getItemMeta();
			            pillagertrue_meta.setDisplayName("Set pillager_patrol_spawn to True");
			            ArrayList<String> pillagertrue_lore = new ArrayList<>();
			            //pillagertrue_lore.add(ChatColor.AQUA + "Set pillager_patrol_spawn to True");
			            pillagertrue_lore.add(ChatColor.YELLOW + "Pillagers will spawn.");
			            pillagertrue_meta.setLore(pillagertrue_lore);
			            btnPillagerTrue.setItemMeta(pillagertrue_meta);
			            
			            ItemMeta pillagerfalse_meta = btnPillagerFalse.getItemMeta();
			            pillagerfalse_meta.setDisplayName("Set pillager_patrol_spawn to False");
			            ArrayList<String> pillagerfalse_lore = new ArrayList<>();
			            //pillagerfalse_lore.add(ChatColor.AQUA + "Set pillager_patrol_spawn to False");
			            pillagerfalse_lore.add(ChatColor.YELLOW + "Pillagers will NOT spawn.");
			            pillagerfalse_meta.setLore(pillagerfalse_lore);
			            btnPillagerFalse.setItemMeta(pillagerfalse_meta);
			            
			            
			 
			            ItemMeta ender_meta = btnEnder.getItemMeta();
			            ender_meta.setDisplayName("enderman_grief");
			            ArrayList<String> ender_lore = new ArrayList<>();
			            ender_lore.add(ChatColor.AQUA + "Set if Endermen can");
			            ender_lore.add(ChatColor.AQUA + "pick up blocks.");
			            ender_lore.add(" ");
			            ender_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no pickup");
			            ender_meta.setLore(ender_lore);
			            btnEnder.setItemMeta(ender_meta);
			            
			            ItemMeta endertrue_meta = btnEnderTrue.getItemMeta();
			            endertrue_meta.setDisplayName("Set enderman_grief to True");
			            ArrayList<String> endertrue_lore = new ArrayList<>();
			            //endertrue_lore.add(ChatColor.AQUA + "Set enderman_grief to True");
			            endertrue_lore.add(ChatColor.YELLOW + "Endermen will pickup blocks.");
			            endertrue_meta.setLore(endertrue_lore);
			            btnEnderTrue.setItemMeta(endertrue_meta);
			            
			            ItemMeta enderfalse_meta = btnEnderFalse.getItemMeta();
			            enderfalse_meta.setDisplayName("Set enderman_grief to False");
			            ArrayList<String> enderfalse_lore = new ArrayList<>();
			            //enderfalse_lore.add(ChatColor.AQUA + "Set enderman_grief to False");
			            enderfalse_lore.add(ChatColor.YELLOW + "Endermen will NOT pickup blocks.");
			            enderfalse_meta.setLore(enderfalse_lore);
			            btnEnderFalse.setItemMeta(enderfalse_meta);
			            
			            
			 
			            ItemMeta ghast_meta = btnGhast.getItemMeta();
			            ghast_meta.setDisplayName("ghast_grief");
			            ArrayList<String> ghast_lore = new ArrayList<>();
			            ghast_lore.add(ChatColor.AQUA + "Set whether Ghast");
			            ghast_lore.add(ChatColor.AQUA + "fireball explosions");
			            ghast_lore.add(ChatColor.AQUA + "can destroy blocks.");
			            ghast_lore.add(" ");
			            ghast_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no grief");
			            ghast_meta.setLore(ghast_lore);
			            btnGhast.setItemMeta(ghast_meta);
			            
			            ItemMeta ghasttrue_meta = btnGhastTrue.getItemMeta();
			            ghasttrue_meta.setDisplayName("Set ghast_grief to True");
			            ArrayList<String> ghasttrue_lore = new ArrayList<>();
			            //ghasttrue_lore.add(ChatColor.AQUA + "Set ghast_grief to True");
			            ghasttrue_lore.add(ChatColor.YELLOW + "Ghast fireballs will destroy blocks.");
			            ghasttrue_meta.setLore(ghasttrue_lore);
			            btnGhastTrue.setItemMeta(ghasttrue_meta);
			            
			            ItemMeta ghastfalse_meta = btnGhastFalse.getItemMeta();
			            ghastfalse_meta.setDisplayName("Set ghast_grief to False");
			            ArrayList<String> ghastfalse_lore = new ArrayList<>();
			            //ghastfalse_lore.add(ChatColor.AQUA + "Set ghast_grief to False");
			            ghastfalse_lore.add(ChatColor.YELLOW + "Ghast fireballs will NOT");
			            ghastfalse_lore.add(ChatColor.YELLOW + "destroy blocks.");
			            ghastfalse_meta.setLore(ghastfalse_lore);
			            btnGhastFalse.setItemMeta(ghastfalse_meta);
			            
			            
			 
			            ItemMeta horse_meta = btnHorse.getItemMeta();
			            horse_meta.setDisplayName("skeleton_horse_spawn");
			            ArrayList<String> horse_lore = new ArrayList<>();
			            horse_lore.add(ChatColor.AQUA + "Set whether Ghast");
			            horse_lore.add(ChatColor.AQUA + "fireball explosions");
			            horse_lore.add(ChatColor.AQUA + "can destroy blocks.");
			            horse_lore.add(" ");
			            horse_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no grief");
			            horse_meta.setLore(horse_lore);
			            btnHorse.setItemMeta(horse_meta);
			            
			            ItemMeta horsetrue_meta = btnHorseTrue.getItemMeta();
			            horsetrue_meta.setDisplayName("Set skeleton_horse_spawn to True");
			            ArrayList<String> horsetrue_lore = new ArrayList<>();
			            //horsetrue_lore.add(ChatColor.AQUA + "Set skeleton_horse_spawn to True");
			            horsetrue_lore.add(ChatColor.YELLOW + "Skeleton Horses will spawn.");
			            horsetrue_meta.setLore(horsetrue_lore);
			            btnHorseTrue.setItemMeta(horsetrue_meta);
			            
			            ItemMeta horsefalse_meta = btnHorseFalse.getItemMeta();
			            horsefalse_meta.setDisplayName("Set skeleton_horse_spawn to False");
			            ArrayList<String> horsefalse_lore = new ArrayList<>();
			            //horsefalse_lore.add(ChatColor.AQUA + "Set skeleton_horse_spawn to False");
			            horsefalse_lore.add(ChatColor.YELLOW + "Skeleton Horses will NOT spawn.");
			            horsefalse_meta.setLore(horsefalse_lore);
			            btnHorseFalse.setItemMeta(horsefalse_meta);
			            
			            
			 
			            ItemMeta phantom_meta = btnPhantom.getItemMeta();
			            phantom_meta.setDisplayName("phantom_spawn");
			            ArrayList<String> phantom_lore = new ArrayList<>();
			            phantom_lore.add(ChatColor.AQUA + "Set whether Ghast");
			            phantom_lore.add(ChatColor.AQUA + "fireball explosions");
			            phantom_lore.add(ChatColor.AQUA + "can destroy blocks.");
			            phantom_lore.add(" ");
			            phantom_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no grief");
			            phantom_meta.setLore(phantom_lore);
			            btnPhantom.setItemMeta(phantom_meta);
			            
			            ItemMeta phantomtrue_meta = btnPhantomTrue.getItemMeta();
			            phantomtrue_meta.setDisplayName("Set phantom_spawn to True");
			            ArrayList<String> phantomtrue_lore = new ArrayList<>();
			            //phantomtrue_lore.add(ChatColor.AQUA + "Set phantom_spawn to True");
			            phantomtrue_lore.add(ChatColor.YELLOW + "Phantoms will spawn.");
			            phantomtrue_meta.setLore(phantomtrue_lore);
			            btnPhantomTrue.setItemMeta(phantomtrue_meta);
			            
			            ItemMeta phantomfalse_meta = btnPhantomFalse.getItemMeta();
			            phantomfalse_meta.setDisplayName("Set phantom_spawn to False");
			            ArrayList<String> phantomfalse_lore = new ArrayList<>();
			            //phantomfalse_lore.add(ChatColor.AQUA + "Set phantom_spawn to False");
			            phantomfalse_lore.add(ChatColor.YELLOW + "Phantoms will NOT spawn.");
			            phantomfalse_meta.setLore(phantomfalse_lore);
			            btnPhantomFalse.setItemMeta(phantomfalse_meta);
			            
			            
			 
			            ItemMeta creeper_meta = btnCreeper.getItemMeta();
			            creeper_meta.setDisplayName("creeper_grief");
			            ArrayList<String> creeper_lore = new ArrayList<>();
			            creeper_lore.add(ChatColor.AQUA + "Set if Creeper");
			            creeper_lore.add(ChatColor.AQUA + "explosions can");
			            creeper_lore.add(ChatColor.AQUA + "destroy blocks.");
			            creeper_lore.add(" ");
			            creeper_lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW  + "false = no grief");
			            creeper_meta.setLore(creeper_lore);
			            btnCreeper.setItemMeta(creeper_meta);
			            
			            ItemMeta creepertrue_meta = btnCreeperTrue.getItemMeta();
			            creepertrue_meta.setDisplayName("Set creeper_grief to True");
			            ArrayList<String> creepertrue_lore = new ArrayList<>();
			            //creepertrue_lore.add(ChatColor.AQUA + "Set creeper_grief to True");
			            creepertrue_lore.add(ChatColor.YELLOW + "Creeper Explosions will destroy blocks.");
			            creepertrue_meta.setLore(creepertrue_lore);
			            btnCreeperTrue.setItemMeta(creepertrue_meta);
			            
			            ItemMeta creeperfalse_meta = btnCreeperFalse.getItemMeta();
			            creeperfalse_meta.setDisplayName("Set creeper_grief to False");
			            ArrayList<String> creeperfalse_lore = new ArrayList<>();
			            //creeperfalse_lore.add(ChatColor.AQUA + "Set creeper_grief to False");
			            creeperfalse_lore.add(ChatColor.YELLOW + "Creeper Explosions will NOT destroy blocks.");
			            creeperfalse_meta.setLore(creeperfalse_lore);
			            btnCreeperFalse.setItemMeta(creeperfalse_meta);
			            

			            
			            ItemMeta save_meta = btnSave.getItemMeta();
			            save_meta.setDisplayName("Set configs and save.");
			            ArrayList<String> save_lore = new ArrayList<>();
			            //save_lore.add(ChatColor.AQUA + "Set configs and save.");
			            save_lore.add(ChatColor.YELLOW + "Your current changes");
			            save_lore.add(ChatColor.YELLOW + "will be set and saved");
			            save_lore.add(ChatColor.YELLOW + "to config.yml");
			            save_meta.setLore(save_lore);
			            btnSave.setItemMeta(save_meta);
			            
			            ItemMeta cancel_meta = btnCancel.getItemMeta();
			            cancel_meta.setDisplayName("Cancel");
			            ArrayList<String> cancel_lore = new ArrayList<>();
			            cancel_lore.add(ChatColor.AQUA + "Cancel without setting");
			            cancel_lore.add(ChatColor.AQUA + "or saving.");
			            save_lore.add(ChatColor.YELLOW + "Your current changes");
			            save_lore.add(ChatColor.YELLOW + "will be lost.");
			            cancel_meta.setLore(cancel_lore);
			            btnCancel.setItemMeta(cancel_meta);
			            
			            
			            
			            //
			            
			            //Put the items in the inventory
			            ItemStack[] menu_items = {btnUpdate, btnUpdateTrue, btnUpdateFalse, btnLang, btnLangCZ, btnLangDE, btnLangEN, btnLangFR, btnSpace,
			            		btnDebug, btnDebugTrue, btnDebugFalse, btnSpace, btnLangLOL, btnLangNL, btnLangBR, btnSpace, btnSpace,
			            		btnConsole, btnConsoleTrue, btnConsoleFalse, btnTrader, btnTraderTrue, btnTraderFalse, btnPillager, btnPillagerTrue, btnPillagerFalse,
			            		btnEnder, btnEnderTrue, btnEnderFalse, btnGhast, btnGhastTrue, btnGhastFalse, btnSpace, btnSpace, btnSpace,
			            		btnHorse, btnHorseTrue, btnHorseFalse, btnPhantom, btnPhantomTrue, btnPhantomFalse, btnSpace, btnSpace, btnSpace,
			            		btnCreeper, btnCreeperTrue, btnCreeperFalse, btnSpace, btnSpace, btnSpace, btnSave, btnSpace, btnCancel};
			            gui.setContents(menu_items);
			            player.openInventory(gui);
					  return true;
				  }else if(!sender.hasPermission("noendermangrief.admin")){
					  sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm"));
					  return false;
				  }
			}
			
			if(args[0].equalsIgnoreCase("book")){
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_RED + "This command can not be sent by console");
					return false;
				}
				if( sender.hasPermission("noendermangrief.admin") ){
					Player player = (Player) sender;
					//int a = player.getStatistic(Statistic.TIME_SINCE_REST);
					player.getWorld().dropItemNaturally(player.getLocation(), setingsbook.giveBook());
					return true;
				}else if( !sender.hasPermission("noendermangrief.admin") ){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm"));
					return false;
				}
			}
			if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
				  if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") || !(sender instanceof Player) ||
						  sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
					  debug = !debug;
					  sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("debugtrue", "Debug set to boolean.").toString().replace("boolean", "" + debug));
					  return true;
				  }else{
					  sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm"));
					  return false;
				  }
			}

			if(args[0].equalsIgnoreCase("reload")){
				/** Check if player has permission */
				if ( sender.hasPermission("noendermangrief.op") || sender.isOp() || sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ) {
					/** Command code */
					oldconfig = new YamlConfiguration();
					log("Checking config file version...");
					try {
						oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
					} catch (Exception e2) {
						logWarn("Could not load config.yml");
						e2.printStackTrace();
					}
					String checkconfigversion = oldconfig.getString("version", "1.0.0");
					if(checkconfigversion != null){
						if(!checkconfigversion.equalsIgnoreCase(configVersion)){
							try {
								copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
							} catch (IOException e) {

							}
							saveResource("config.yml", true);
							
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (IOException | InvalidConfigurationException e1) {
								logWarn("Could not load config.yml");
								e1.printStackTrace();
							}
							try {
								oldconfig.load(new File(getDataFolder(), "old_config.yml"));
							} catch (IOException | InvalidConfigurationException e1) {
								logWarn("Could not load old_config.yml");
								e1.printStackTrace();
							}
							config.set("auto_update_check", oldconfig.get("auto_update_check", true));
							config.set("debug", oldconfig.get("debug", false));
							config.set("lang", oldconfig.get("lang", "en_US"));
							config.set("console.colorful_console", oldconfig.get("console.colorful_console", true));
							config.set("enderman_grief", oldconfig.get("enderman_grief", false));
							config.set("skeleton_horse_spawn", oldconfig.get("skeleton_horse_spawn", false));
							config.set("creeper_grief", oldconfig.get("creeper_grief", false));
							config.set("wandering_trader_spawn", oldconfig.get("wandering_trader_spawn", false));
							config.set("ghast_grief", oldconfig.get("ghast_grief", false));
							config.set("phantom_spawn", oldconfig.get("phantom_spawn", false));
							config.set("pillager_patrol_spawn", oldconfig.get("pillager_patrol_spawn", false));
							
							try {
								config.save(new File(getDataFolder(), "config.yml"));
							} catch (IOException e) {
								logWarn("Could not save old settings to config.yml");
								e.printStackTrace();
							}
							
							log("config.yml Updated! old config saved as old_config.yml");
							log("chance_config.yml saved.");
						}else{
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (IOException | InvalidConfigurationException e1) {
								logWarn("Could not load config.yml");
								e1.printStackTrace();
							}
						}
						oldconfig = null;
					}
					log("Loading config file...");
					try {
						getConfig().load(new File(getDataFolder(), "config.yml"));
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
					try {
						config.load(new File(getDataFolder(), "config.yml"));
					} catch (IOException | InvalidConfigurationException e1) {
						logWarn("Could not load config.yml");
						e1.printStackTrace();
					}
					
					debug = getConfig().getBoolean("debug", false);
					daLang = getConfig().getString("lang", "en_US");
					reloadConfig();
					
					sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " has been " + ChatColor.WHITE + "reloaded");
					return true;
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("no_perm"));
					return false;
				}
			}
			if(args[0].equalsIgnoreCase("test")){
				if(!(sender instanceof Player)) {
					if(UpdateCheck){
						try {
							Bukkit.getConsoleSender().sendMessage("Checking for updates...");
							VersionChecker updater = new VersionChecker(this, updateVersion, updateurl);
							if(updater.checkForUpdates2()) {
								UpdateAvailable = true; // TODO: Update Checker
								UColdVers = updater.oldVersion();
								UCnewVers = updater.newVersion();
								
								log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								log(Level.WARNING, "* " + lang.get("newvers.message").toString().replace("<MyPlugin>", this.getName()) );
								log(Level.WARNING, "* " + lang.get("newvers.old_vers") + ChatColor.RED + " " + UColdVers );
								log(Level.WARNING, "* " + lang.get("newvers.new_vers") + ChatColor.GREEN + " " + UCnewVers );
								log(Level.WARNING, "* " + lang.get("newvers.notes") + ": " + updater.newVersionNotes());
								log(Level.WARNING, "* " + lang.get("newvers.please_update") );
								log(Level.WARNING, "*");
								log(Level.WARNING, "* " + lang.get("newvers.download") + ": " + updater.getDownloadLink());
								log(Level.WARNING, "* " + lang.get("newvers.donate") + ": https://ko-fi.com/joelgodofwar");
								log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
								//Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
							}else{
								log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								log(Level.WARNING, "* " + updater.getRecommendedVersion());
								log(Level.WARNING, "* " + lang.get("newvers.donate") + ": https://ko-fi.com/joelgodofwar");
								log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								UpdateAvailable = false;
							}
							return true;
						}catch(Exception e) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
							e.printStackTrace();
							return false;
						}
					}
				}
			}
			if(args[0].equalsIgnoreCase("update")){ // TODO: Command Update
			  // Player must be OP and auto_update_check must be true
				if(!(sender instanceof Player)) {
					/** Update Checker */
					newVerMsg = Ansi.YELLOW + this.getName() + Ansi.MAGENTA + " v{oVer}" + Ansi.RESET + " " + lang.get("newvers") + Ansi.GREEN + " v{nVer}" + Ansi.RESET;
					if(UpdateCheck){
						try {
							Bukkit.getConsoleSender().sendMessage("Checking for updates...");
							UpdateChecker updater = new UpdateChecker(this, updateVersion, updateurl);
							if(updater.checkForUpdates()) {
								UpdateAvailable = true; // TODO: Update Checker
								UColdVers = updater.oldVersion();
								UCnewVers = updater.newVersion();
								
								log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								log(Level.WARNING, "* " + lang.get("newvers.message").toString().replace("<MyPlugin>", this.getName()) );
								log(Level.WARNING, "* " + lang.get("newvers.old_vers") + ChatColor.RED + UColdVers );
								log(Level.WARNING, "* " + lang.get("newvers.new_vers") + ChatColor.GREEN + UCnewVers );
								log(Level.WARNING, "*");
								log(Level.WARNING, "* " + lang.get("newvers.please_update") );
								log(Level.WARNING, "*");
								log(Level.WARNING, "* " + lang.get("newvers.download") + ": https://www.spigotmc.org/resources/no-enderman-grief2.71236/history");
								log(Level.WARNING, "* " + lang.get("newvers.donate") + ": https://ko-fi.com/joelgodofwar");
								log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
								//Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
							}else{
								UpdateAvailable = false;
							}
						}catch(Exception e) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
							e.printStackTrace();
						}
					}
					/** end update checker */
					return true;
				}
				/** Check if player has permission */
				if( sender.isOp() || sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.showUpdateAvailable") 
						|| sender.hasPermission("noendermangrief.admin") ){
					String pluginName = this.getName();
					@SuppressWarnings("unused")
					BukkitTask updateTask = this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {

						public void run() {
							try {
								Bukkit.getConsoleSender().sendMessage("Checking for updates...");
								UpdateChecker updater = new UpdateChecker(thisVersion, updateVersion, updateurl);
								if(updater.checkForUpdates()) {
									UpdateAvailable = true;
									UColdVers = updater.oldVersion();
									UCnewVers = updater.newVersion();
									String links = "[\"\",{\"text\":\"Download\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/no-enderman-grief2.71236/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Download the latest version.\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Download the latest version.\"}},{\"text\":\"| \"},{\"text\":\"Donate\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Donate to the plugin maker.\"}},{\"text\":\" | \"},{\"text\":\"ChangeLog\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/no-enderman-grief2.71236/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"View the ChangeLog.\"}}]";
									//String links = "{\"text\":\"Donate\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Donate to the plugin maker.\"}}";
									String versions = "" + ChatColor.GRAY + lang.get("newvers.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + lang.get("newvers.old_vers") + ": " + ChatColor.RED + "{oVers}";
									sender.sendMessage("" + ChatColor.GRAY + lang.get("newvers.message").toString().replace("<MyPlugin>", ChatColor.GOLD + pluginName + ChatColor.GRAY) );
									Utils.sendJson(sender, links);
									sender.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
								}else{
									String links = "{\"text\":\"Donate\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Donate to the plugin maker.\"}}";
									Utils.sendJson(sender, links);
									sender.sendMessage(ChatColor.YELLOW + thisName + ChatColor.RED + " v" + thisVersion + ChatColor.RESET + " Up to date." + ChatColor.RESET);
									UpdateAvailable = false;
								}
							}catch(Exception e) {
								sender.sendMessage(ChatColor.RED + "Could not process update check");
								Bukkit.getConsoleSender().sendMessage(Ansi.RED + "Could not process update check");
								e.printStackTrace();
							}
						}
						
					});
					return true;
				}else{
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + lang.get("no_perm"));
					return false;
				}
			}
			if( args[0].equalsIgnoreCase("eg") || args[0].equalsIgnoreCase("endermangrief") ){
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("enderman_grief_current" + 
							"").toString().replace("[setting]", "" + getConfig().getBoolean("enderman_grief", false)));
					return true;
				}
				/** Check if player has permission */
				if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg eg True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("enderman_grief", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("enderman_grief_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("enderman_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("enderman_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			  
			}
			if( args[0].equalsIgnoreCase("sh") || args[0].equalsIgnoreCase("skeletonhorse") ){
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("skeleton_horse_current" + 
							"").toString().replace("[setting]", "" + getConfig().getBoolean("skeleton_horse_spawn", false)));
					return true;
				}
				/** Check if player has permission */
				if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg sh True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("skeleton_horse_spawn", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("skeleton_horse_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("skeleton_horse_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("skeleton_horse_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			}
			if( args[0].equalsIgnoreCase("wt") || args[0].equalsIgnoreCase("wanderingtrader") ){
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("wandering_trader_current" + 
							"").toString().replace("[setting]", "" + getConfig().getBoolean("wandering_trader_spawn", false)));
					return true;
				}
				log("args.length=" + args.length);
				/** Check if player has permission */
				if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg sh True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("wandering_trader_spawn", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("wandering_trader_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("wandering_trader_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("wandering_trader_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			}
			if( args[0].equalsIgnoreCase("cg") || args[0].equalsIgnoreCase("creepergrief") ){
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("creeper_current" + 
							"").toString().replace("[setting]", "" + getConfig().getBoolean("creeper_grief", false)));
					return true;
				}
				/** Check if player has permission */
				if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg eg True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("creeper_grief", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("creeper_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("creeper_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("creeper_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			}
			// Ghast Grief
			if( args[0].equalsIgnoreCase("gg") || args[0].equalsIgnoreCase("ghastgrief") ){
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("ghast_current")
					.toString().replace("[setting]", "" + getConfig().getBoolean("ghast_grief", false)));
					return true;
				}
				/** Check if player has permission */
				if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg eg True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("ghast_grief", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("ghast_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("ghast_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("ghast_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			}
			if( args[0].equalsIgnoreCase("pg") || args[0].equalsIgnoreCase("phantomgrief") ){ // Phantom Grief
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("phantom_current")
					.toString().replace("[setting]", "" + getConfig().getBoolean("phantom_spawn", false)));
					return true;
				}
				/** Check if player has permission */
				if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg eg True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("phantom_spawn", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("phantom_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("phantom_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("phantom_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			}
			if( args[0].equalsIgnoreCase("pp") || args[0].equalsIgnoreCase("pillagerpatrol") ){ // Pillager Patrol Grief
				if(args.length <= 1){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("pillager_patrol_current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("pillager_patrol_spawn", false)));
					return true;
				}
				/** Check if player has permission */
				if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
					/** Command code */
					if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.RED + lang.get("boolean") + ": /neg eg True/False");
						return false;
					}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
						config.set("pillager_patrol_spawn", Boolean.parseBoolean(args[1]));
						saveConfig();
						
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("pillager_patrol_set") + " " + args[1]);
						if(args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("pillager_patrol_wont") );
						}else if(args[1].equalsIgnoreCase("true")){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + lang.get("pillager_patrol_will") );
						}
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							e1.printStackTrace();
						}
						config = new YmlConfiguration();
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						return true;
					}
				}else {
					sender.sendMessage(ChatColor.DARK_RED + "" + lang.get("noperm", "Lang file error: noperm"));
					return false;
				}
			}
		}
		return true;
	}
	@Override 
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		if (command.getName().equalsIgnoreCase("NEG")) {
			List<String> autoCompletes = new ArrayList<>(); //create a new string list for tab completion
			if (args.length == 1) { // reload, toggledebug, playerheads, customtrader, headfix
				autoCompletes.add("Update");
				autoCompletes.add("Reload");
				autoCompletes.add("ToggleDebug");
				autoCompletes.add("Book");
				autoCompletes.add("EndermanGrief");
				autoCompletes.add("SkeletonHorse");
				autoCompletes.add("CreeperGrief");
				autoCompletes.add("WanderingTrader");
				autoCompletes.add("GhastGrief");
				autoCompletes.add("PhantomGrief");
				autoCompletes.add("PillagerPatrol");
				return autoCompletes; // then return the list
			}
			if(args.length > 1) {
				if( args[0].equalsIgnoreCase("EndermanGrief") || args[0].equalsIgnoreCase("eg") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("SkeletonHorse") || args[0].equalsIgnoreCase("sh") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("CreeperGrief") || args[0].equalsIgnoreCase("cg") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("WanderingTrader") || args[0].equalsIgnoreCase("wt") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("GhastGrief") || args[0].equalsIgnoreCase("gg") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("PhantomGrief") || args[0].equalsIgnoreCase("pg") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("PillagerPatrol") || args[0].equalsIgnoreCase("pp") ) {
					autoCompletes.add("true");
					autoCompletes.add("false");
					return autoCompletes; // then return the list
				}
			}
		}
		return null;
	}
	public static String getMCVersion() {
		String strVersion = Bukkit.getVersion();
		strVersion = strVersion.substring(strVersion.indexOf("MC: "), strVersion.length());
		strVersion = strVersion.replace("MC: ", "").replace(")", "");
		return strVersion;
	}
	public boolean makeBoolean(String args){
		if(args.contains("true")){
			return true;
		}
		else if(args.contains("false")){
			return false;
		}
		return false;
	}
	
	public static void copyFile_Java7(String origin, String destination) throws IOException {
		Path FROM = Paths.get(origin);
		Path TO = Paths.get(destination);
		//overwrite the destination file if it exists, and copy
		// the file attributes, including the rwx permissions
		CopyOption[] options = new CopyOption[]{
			StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES
		}; 
		Files.copy(FROM, TO, options);
	}
	
	@Override
	public void saveConfig(){
		try {
			config.save(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			logWarn("Could not save old settings to config.yml");
			e.printStackTrace();
		}
	}
	
	public boolean isCorrectVersion(){
		return correctVersion;
	}
	public boolean setupBook(){
		String packageName = this.getServer().getClass().getPackage().getName();
		String version = packageName.substring(packageName.lastIndexOf('.') + 1);

		if (version.equals("v1_14_R1")) {
			setingsbook = new SettingsBook_v1_14_R1();
		}else if (version.equals("v1_15_R1")) {
			setingsbook = new SettingsBook_v1_15_R1();
		}else if (version.equals("v1_16_R1")) {
			setingsbook = new SettingsBook_v1_16_R1();
		}else if (version.equals("v1_16_R2")) {
			setingsbook = new SettingsBook_v1_16_R2();
		}else if (version.equals("v1_16_R3")) {
			setingsbook = new SettingsBook_v1_16_R3();
		}else if (version.equals("v1_17_R1")) {
			setingsbook = new SettingsBook_v1_17_R1();
		}else if (version.equals("v1_18_R1")) {
			setingsbook = new SettingsBook_v1_18_R1();
		}else if (version.equals("v1_19_R1")) {
			setingsbook = new SettingsBook_v1_19_R1();
		}else if (version.equals("v1_19_R2")) {
			setingsbook = new SettingsBook_v1_19_R2();
		}
		// This will return true if the server version was compatible with one of our NMS classes
		// because if it is, our actionbar would not be null
		return setingsbook != null;
	}
	
	public boolean saveConfig(boolean update, boolean Debug, boolean Console, boolean Trader, boolean Pillager, boolean Ender, boolean Ghast, boolean Horse,
			boolean Phantom, boolean Creeper, String Lang) {
		config.set("auto_update_check", update);
		config.set("debug", Debug);
		config.set("lang", Lang);
		config.set("console.colorful_console", Console);
		config.set("enderman_grief", Ender);
		config.set("skeleton_horse_spawn", Horse);
		config.set("creeper_grief", Creeper);
		config.set("wandering_trader_spawn", Trader);
		config.set("ghast_grief", Ghast);
		config.set("phantom_spawn", Phantom);
		config.set("pillager_patrol_spawn", Pillager);
		try {
			config.save(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			logWarn("Could not save settings to config.yml");
			e.printStackTrace();
			return false;
		}
		log("config.yml has been updated");
		return true;
	}
	
	
}
