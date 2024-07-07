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
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.joelgodofwar.neg.commands.testCommand;
import com.github.joelgodofwar.neg.common.MinecraftVersion;
import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.PluginLogger;
import com.github.joelgodofwar.neg.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.neg.common.error.Report;
import com.github.joelgodofwar.neg.gui.GUIMoveItem;
import com.github.joelgodofwar.neg.i18n.Translator;
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
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_20_R1;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_20_R2;
import com.github.joelgodofwar.neg.nms.SettingsBook_v1_20_R3;
import com.github.joelgodofwar.neg.util.Metrics;
import com.github.joelgodofwar.neg.util.PluginUtils;
import com.github.joelgodofwar.neg.util.Utils;
import com.github.joelgodofwar.neg.util.VersionChecker;
import com.github.joelgodofwar.neg.util.YmlConfiguration;

@SuppressWarnings("unused")
public class NoEndermanGrief extends JavaPlugin implements Listener{
	/** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	//public final static Logger logger = Logger.getLogger("Minecraft");
	static String THIS_NAME;
	static String THIS_VERSION;
	/** update checker variables */
	public int projectID = 71236; // https://spigotmc.org/resources/71236
	public String githubURL = "https://github.com/JoelGodOfwar/NoEndermanGrief/raw/master/versions/1.14/versions.xml";
	public boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public static boolean UpdateCheck;
	public String DownloadLink = "https://www.spigotmc.org/resources/no-enderman-grief2.71236";
	/** end update checker variables */
	public static String daLang;
	public static boolean cancelbroadcast;
	public boolean debug;
	File langFile;
	public FileConfiguration lang;
	YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	boolean colorful_console;
	private SettingsBook setingsbook;
	private boolean correctVersion = true;
	String configVersion = "1.0.6";
	//String langVersion = "1.0.6";
	String pluginName = THIS_NAME;
	Translator lang2;
	public String jarfilename = this.getFile().getAbsoluteFile().toString();
	public static DetailedErrorReporter reporter;
	public PluginLogger LOGGER;

	@Override // TODO: onEnable
	public void onEnable(){
		long startTime = System.currentTimeMillis();
		LOGGER = new PluginLogger(this);
		reporter = new DetailedErrorReporter(this);
		UpdateCheck = getConfig().getBoolean("auto_update_check", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		config = new YmlConfiguration();
		colorful_console = getConfig().getBoolean("console.colorful_console", true);
		lang2 = new Translator(daLang, getDataFolder().toString());
		THIS_NAME = this.getDescription().getName();
		THIS_VERSION = this.getDescription().getVersion();
		if(!getConfig().getBoolean("console.longpluginname", true)) {
			pluginName = "NEG";
		}else {
			pluginName = THIS_NAME;
		}

		MinecraftVersion checkVersion = this.verifyMinecraftVersion();
		LOGGER = new PluginLogger(this);

		LOGGER.log(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
		LOGGER.log(ChatColor.GREEN + " v" + THIS_VERSION + ChatColor.RESET + " Loading...");
		LOGGER.log("Server Version: " + getServer().getVersion().toString());

		MinecraftVersion version = this.verifyMinecraftVersion();

		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			LOGGER.log("jarfile contains DEV, debug set to true.");
		}else {
			LOGGER.log("This is not a DEV version.");
		}

		LOGGER.log("Checking lang files...");

		if( !(Double.parseDouble( getMCVersion().substring(0, 4) ) >= 1.14) ){
			LOGGER.log(ChatColor.RED + "WARNING!" + ChatColor.GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
			LOGGER.log(ChatColor.RED + "WARNING! " + ChatColor.YELLOW + get("neg.message.server_not_version") + ChatColor.RESET);
			LOGGER.log(ChatColor.RED + "WARNING! " + ChatColor.YELLOW + THIS_NAME + " v" + THIS_VERSION + " disabling." + ChatColor.RESET);
			LOGGER.log(ChatColor.RED + "WARNING!" + ChatColor.GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		LOGGER.log("Checking config file...");
		/**  Check for config */
		try{
			if(!getDataFolder().exists()){
				LOGGER.log("Data Folder doesn't exist");
				LOGGER.log("Creating Data Folder");
				getDataFolder().mkdirs();
				LOGGER.log("Data Folder Created at " + getDataFolder());
			}
			File  file = new File(getDataFolder(), "config.yml");
			LOGGER.log("" + file);
			if(!file.exists()){
				LOGGER.log("config.yml not found, creating!");
				saveResource("config.yml", true);
			}
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
		LOGGER.log("Checking config file version...");
		String checkconfigversion = getConfig().getString("version", "1.0.0");
		LOGGER.log("Config file version=" + checkconfigversion + " expected=" + configVersion);
		if(checkconfigversion != null){
			if(!checkconfigversion.equalsIgnoreCase(configVersion)){
				try {
					copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				try {
					oldconfig.load(new File(getDataFolder(), "config.yml"));
				} catch (Exception exception) {
					LOGGER.warn("Could not load config.yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				}
				saveResource("config.yml", true);
				try {
					config.load(new File(getDataFolder(), "config.yml"));
				} catch (Exception exception) {
					LOGGER.warn("Could not load config.yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				}
				try {
					oldconfig.load(new File(getDataFolder(), "old_config.yml"));
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
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
				} catch (Exception exception) {
					LOGGER.warn("Could not save old settings to config.yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
				}
				LOGGER.log("config.yml has been updated");
			}
		}
		/** end config check */
		try {
			config.load(new File(getDataFolder(), "config.yml"));
		} catch (Exception exception) {
			LOGGER.warn("Could not load config.yml");
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
		}

		if(debug){
			LOGGER.debug("Config.yml dump");
			LOGGER.debug("auto_update_check=" + getConfig().getBoolean("auto_update_check"));
			LOGGER.debug("debug=" + getConfig().getBoolean("debug"));
			LOGGER.debug("lang=" + getConfig().getString("lang"));

			LOGGER.debug("console.colorful_console=" + getConfig().getBoolean("console.colorful_console"));
			LOGGER.debug("console.longpluginname=" + getConfig().getBoolean("console.longpluginname"));
			LOGGER.debug("enderman_grief=" + getConfig().getBoolean("enderman_grief"));
			LOGGER.debug("skeleton_horse_spawn=" + getConfig().getBoolean("skeleton_horse_spawn"));
			LOGGER.debug("creeper_grief=" + getConfig().getBoolean("creeper_grief"));
			LOGGER.debug("wandering_trader_spawn=" + getConfig().getBoolean("wandering_trader_spawn"));
			LOGGER.debug("ghast_grief=" + getConfig().getBoolean("ghast_grief"));
			LOGGER.debug("phantom_spawn=" + getConfig().getBoolean("phantom_spawn"));
			LOGGER.debug("pillager_patrol_spawn=" + getConfig().getBoolean("pillager_patrol_spawn"));
		}

		/** Update Checker */
		if(UpdateCheck){
			try {
				LOGGER.log("Checking for updates...");
				VersionChecker updater = new VersionChecker(this, projectID, githubURL);
				if(updater.checkForUpdates()) {
					/** Update available */
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();

					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					LOGGER.log("* " + get("neg.version.message").toString().replace("<MyPlugin>", THIS_NAME) );
					LOGGER.log("* " + get("neg.version.old_vers") + ChatColor.RED + UColdVers );
					LOGGER.log("* " + get("neg.version.new_vers") + ChatColor.GREEN + UCnewVers );
					LOGGER.log("*");
					LOGGER.log("* " + get("neg.version.please_update") );
					LOGGER.log("*");
					LOGGER.log("* " + get("neg.version.download") + ": " + DownloadLink + "/history");
					LOGGER.log("* " + get("neg.version.donate") + ": https://ko-fi.com/joelgodofwar");
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				}else{
					/** Up to date */
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					LOGGER.log("* " + get("neg.version.curvers"));
					LOGGER.log("* " + get("neg.version.donate") + ": https://ko-fi.com/joelgodofwar");
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					UpdateAvailable = false;
				}
			}catch(Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
			}
		}else {
			/** auto_update_check is false so nag. */
			LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
			LOGGER.log("* " + get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
			LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
		}
		/** end update checker */

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new GUIMoveItem(this), this);  // Comment out for release versions, un comment for Dev builds.
		consoleInfo(ChatColor.BOLD + "ENABLED" + ChatColor.RESET + " - Loading took " + LoadTime(startTime));

		try {
			this.correctVersion = this.setupBook();
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BOOK_SETUP_ERROR).error(exception));
		}

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
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_METRICS_LOAD_ERROR).error(exception));
		}
	}

	@Override // TODO: onDisable
	public void onDisable(){
		//saveConfig();
		config = null;
		consoleInfo(ChatColor.BOLD + "DISABLED" + ChatColor.RESET);
	}

	public void consoleInfo(String state) {
		//loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
		loading(ChatColor.YELLOW + " v" + THIS_VERSION + ChatColor.RESET + " is " + state  + ChatColor.RESET);
		//loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
	}

	public void loading(String string) {
		if(!colorful_console) {
			string = ChatColor.stripColor(string);
		}
		LOGGER.log(string);
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent entitye) {
		//if (!tr.isrunworld(ac.getName(), e.getEntity().getLocation().getWorld().getName()) )
		//return;
		try {
			if (entitye.getEntity() == null) {
				return;
			}

			if (entitye.getEntity().getType() == EntityType.ENDERMAN) {
				if(!getConfig().getBoolean("enderman_grief", false)){
					entitye.setCancelled(true);
				}
				LOGGER.debug("" + get("neg.entity.enderman.pickup") + entitye.getBlock().getType() + " at " + entitye.getBlock().getLocation());
				return;
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_ENDERMAN_GRIEF).error(exception));
		}
	}

	@EventHandler
	public void onEntityChangeBlock(EntityExplodeEvent entity) {
		try {
			if (entity.getEntity().getType() == EntityType.CREEPER) {
				if(!getConfig().getBoolean("creeper_grief", false)){
					entity.setCancelled(true);
				}
				LOGGER.debug("" + get("neg.entity.creeper.explode") + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockZ());
				return;
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_CREEPER_GRIEF).error(exception));
		}
		try {
			if ((entity.getEntity().getType() == EntityType.FIREBALL)  && (((Fireball) entity.getEntity()).getShooter() instanceof Ghast)) {
				if(!getConfig().getBoolean("ghast_grief", false)){
					Entity fireball = entity.getEntity();
					((Fireball) fireball).setIsIncendiary(false);
					((Fireball) fireball).setYield(0F);
					entity.setCancelled(true);
				}
				LOGGER.debug("" + get("neg.entity.ghast.explode") + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockZ());
				return;
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_GHAST_GRIEF).error(exception));
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if( UpdateAvailable && ( player.isOp() || player.hasPermission("noendermangrief.showUpdateAvailable") || player.hasPermission("noendermangrief.admin") ) ){
			String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
			links = links.replace("<DownloadLink>", DownloadLink).replace("<Download>", get("neg.version.download"))
					.replace("<Donate>", get("neg.version.donate")).replace("<please_update>", get("neg.version.please_update"))
					.replace("<Donate_msg>", get("neg.version.donate.message")).replace("<Notes>", get("neg.version.notes"))
					.replace("<Notes_msg>", get("neg.version.notes.message"));
			String versions = "" + ChatColor.GRAY + get("neg.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("neg.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
			player.sendMessage("" + ChatColor.GRAY + get("neg.version.message").toString().replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
			Utils.sendJson(player, links);
			player.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
		}

		if( player.getDisplayName().equals("JoelYahwehOfWar") ||  player.getDisplayName().equals("JoelGodOfWar") ){
			player.sendMessage(THIS_NAME + " " + THIS_VERSION + " Hello father!");
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event){ //onEntitySpawn(EntitySpawnEvent e) {
		Entity entity = event.getEntity();
		try {
			if (entity instanceof SkeletonHorse){
				if(!getConfig().getBoolean("skeleton_horse_spawn", false)){
					LOGGER.debug("" + get("neg.entity.skeleton_horse") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_SKELETON_HORSE_GRIEF).error(exception));
		}
		try {
			if (entity instanceof WanderingTrader){
				if(!getConfig().getBoolean("wandering_trader_spawn", false)){
					LOGGER.debug("" + get("neg.entity.wandering_trader") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_WANDERING_TRADER_GRIEF).error(exception));
		}
		try {
			if (entity instanceof Phantom){
				if(!getConfig().getBoolean("phantom_spawn", false)){
					LOGGER.debug("" + get("neg.entity.phantom") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_PHANTOM_GRIEF).error(exception));
		}
		try {
			if(event.getSpawnReason() == SpawnReason.PATROL) {
				if(!getConfig().getBoolean("pillager_patrol_spawn", false)){
					LOGGER.debug("" + get("neg.entity.pillager_patrol") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_PILLAGER_PATROL_GRIEF).error(exception));
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){ // TODO: Commands
		try {
			//Player p = (Player)sender;
			if (cmd.getName().equalsIgnoreCase("NEG")){
				if (args.length == 0)
				{
					/** Check if sender has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") || sender.isOp() ) {
						/** Command code */
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
						sender.sendMessage(ChatColor.GOLD + " ");
						sender.sendMessage(ChatColor.RED + " " + get("neg.version.donate.message") + ChatColor.GREEN + ": https://ko-fi.com/joelgodofwar" + ChatColor.RESET);
						sender.sendMessage(ChatColor.GOLD + " ");
						if( sender.isOp()||sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
							sender.sendMessage(ChatColor.WHITE + " -<[" + ChatColor.AQUA + " OP Commands " + ChatColor.WHITE + "}>-");
							sender.sendMessage(ChatColor.GOLD + " /NEG update - " + get("neg.command.update"));//Check for update.");
							sender.sendMessage(ChatColor.GOLD + " /NEG reload - " + get("neg.command.reload") );//Reload config file.");
							if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") ||
									!(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ){
								sender.sendMessage(ChatColor.GOLD + " /NEG toggledebug - " + get("neg.message.debuguse") );
							}
						}
						if( sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ){
							sender.sendMessage(ChatColor.WHITE + " -<[" + ChatColor.YELLOW + " Admin Commands " + ChatColor.WHITE + "}>-");
							if( sender instanceof Player ) {
								sender.sendMessage(ChatColor.GOLD + " /NEG BOOK - " + get("neg.command.book"));
							}
							sender.sendMessage(ChatColor.GOLD + " /NEG EG true/false - " + get("neg.command.endermen"));
							sender.sendMessage(ChatColor.GOLD + " /NEG SH true/false - " + get("neg.command.skeleton_horse"));
							sender.sendMessage(ChatColor.GOLD + " /NEG CG true/false - " + get("neg.command.creeper"));
							sender.sendMessage(ChatColor.GOLD + " /NEG WT true/false - " + get("neg.command.wandering_trader"));
							sender.sendMessage(ChatColor.GOLD + " /NEG GG true/false - " + get("neg.command.ghast"));
							sender.sendMessage(ChatColor.GOLD + " /NEG PG true/false - " + get("neg.command.phantom"));
							sender.sendMessage(ChatColor.GOLD + " /NEG PP true/false - " + get("neg.command.pillager_patrol"));
						}
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
						return true;
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.no_perm"));
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("dumpinfo") || args[0].equalsIgnoreCase("di")){
					if( sender.isOp() || sender.hasPermission("noendermangrief.op")
							|| sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player)) {
						Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
						//StringBuilder messageBuilder = new StringBuilder();

						LOGGER.log("" + ChatColor.YELLOW + ChatColor.BOLD + "Please copy from this line until the second line of dashes." + ChatColor.RESET);
						LOGGER.log("" + ChatColor.YELLOW + ChatColor.BOLD + "------------------------------------------------------------" + ChatColor.RESET);
						LOGGER.log("Config.yml dump");
						LOGGER.log("auto_update_check=" + getConfig().getBoolean("auto_update_check"));
						LOGGER.log("debug=" + getConfig().getBoolean("debug"));
						LOGGER.log("lang=" + getConfig().getString("lang"));

						LOGGER.log("console.colorful_console=" + getConfig().getBoolean("console.colorful_console"));
						LOGGER.log("console.longpluginname=" + getConfig().getBoolean("console.longpluginname"));
						LOGGER.log("enderman_grief=" + getConfig().getBoolean("enderman_grief"));
						LOGGER.log("skeleton_horse_spawn=" + getConfig().getBoolean("skeleton_horse_spawn"));
						LOGGER.log("creeper_grief=" + getConfig().getBoolean("creeper_grief"));
						LOGGER.log("wandering_trader_spawn=" + getConfig().getBoolean("wandering_trader_spawn"));
						LOGGER.log("ghast_grief=" + getConfig().getBoolean("ghast_grief"));
						LOGGER.log("phantom_spawn=" + getConfig().getBoolean("phantom_spawn"));
						LOGGER.log("pillager_patrol_spawn=" + getConfig().getBoolean("pillager_patrol_spawn"));
						LOGGER.log("");
						LOGGER.log("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
						LOGGER.log("");
						LOGGER.log("Plugins dump");
						PluginUtils.loadPluginJarNames();
						for (Plugin plugin : plugins) {
							Map<String, Object> info = PluginUtils.getInfo(plugin);
							String pluginName = (String) info.get("Name");
							String pluginVersion = (String) info.get("Version");

							LOGGER.log(String.format("[%s] v%s", pluginName, pluginVersion));
							LOGGER.log(String.format("  FileName: %s", info.get("FileName")));
							LOGGER.log(String.format("  Main: %s", info.get("Main")));
							LOGGER.log(String.format("  Enabled: %b, API-Version: %s", info.get("Enabled"), info.get("API-Version")));
							LOGGER.log(String.format("  Description: %s", info.get("Description")));
							LOGGER.log(String.format("  Authors: %s", info.get("Authors")));
							LOGGER.log(String.format("  Website: %s", info.get("Website")));
							LOGGER.log(String.format("  Depends: %s", info.get("Depends")));
							LOGGER.log(String.format("  SoftDepends: %s", info.get("SoftDepends")));
							LOGGER.log(String.format("  Commands: %s", info.get("Commands")));
							LOGGER.log(String.format("  Permissions: %s", info.get("Permissions")));
							LOGGER.log(String.format("  Default Permissions: %s", info.get("Default Permissions")));
							LOGGER.log(String.format("  Load: %s", info.get("Load")));
							LOGGER.log(String.format("  LoadBefore: %s", info.get("LoadBefore")));
							LOGGER.log(String.format("  Provides: %s", info.get("Provides")));
							LOGGER.log("");
						}
						LOGGER.log("" + ChatColor.YELLOW + ChatColor.BOLD + "------------------------------------------------------------" + ChatColor.RESET);
						LOGGER.log("" + ChatColor.YELLOW + ChatColor.BOLD + "This is the end of the debug dump." + ChatColor.RESET);
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
						//ItemStack btnUpdateTrue = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
						ItemStack btnUpdateTrue = new ItemStack(getConfig().getBoolean("auto_update_check") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnUpdateFalse = new ItemStack(getConfig().getBoolean("auto_update_check") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnLang = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnLangCZ = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("cs_CZ") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangDE = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("de_DE") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangEN = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("en_US") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangFR = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("fr_FR") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangLOL = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("lol_US") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangNL = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("nl_NL") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangBR = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("pt_BR") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);

						ItemStack btnDebug =  new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnDebugTrue =  new ItemStack(getConfig().getBoolean("debug") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnDebugFalse =  new ItemStack(getConfig().getBoolean("debug") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnConsole = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnConsoleTrue =  new ItemStack(getConfig().getBoolean("console.colorful_console") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnConsoleFalse =  new ItemStack(getConfig().getBoolean("console.colorful_console") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnLongname = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnLongnameTrue =  new ItemStack(getConfig().getBoolean("console.longpluginname") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnLongnameFalse =  new ItemStack(getConfig().getBoolean("console.longpluginname") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);


						ItemStack btnTrader = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnTraderTrue =  new ItemStack(getConfig().getBoolean("wandering_trader_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnTraderFalse =  new ItemStack(getConfig().getBoolean("wandering_trader_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnPillager = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnPillagerTrue =  new ItemStack(getConfig().getBoolean("pillager_patrol_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnPillagerFalse =  new ItemStack(getConfig().getBoolean("pillager_patrol_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnEnder = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnEnderTrue =  new ItemStack(getConfig().getBoolean("enderman_grief") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnEnderFalse =  new ItemStack(getConfig().getBoolean("enderman_grief") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnGhast = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnGhastTrue =  new ItemStack(getConfig().getBoolean("ghast_grief") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnGhastFalse =  new ItemStack(getConfig().getBoolean("ghast_grief") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnHorse = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnHorseTrue =  new ItemStack(getConfig().getBoolean("skeleton_horse_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnHorseFalse =  new ItemStack(getConfig().getBoolean("skeleton_horse_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnPhantom = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnPhantomTrue =  new ItemStack(getConfig().getBoolean("phantom_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnPhantomFalse =  new ItemStack(getConfig().getBoolean("phantom_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnCreeper = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnCreeperTrue =  new ItemStack(getConfig().getBoolean("creeper_grief") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnCreeperFalse =  new ItemStack(getConfig().getBoolean("creeper_grief") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

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
						updatetrue_lore.add(ChatColor.YELLOW + "Will check for updates.");
						updatetrue_meta.setLore(updatetrue_lore);
						btnUpdateTrue.setItemMeta(updatetrue_meta);

						ItemMeta updatefalse_meta = btnUpdateTrue.getItemMeta();
						updatefalse_meta.setDisplayName("Set auto_update_check to False");
						ArrayList<String> updatefalse_lore = new ArrayList<>();
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
						consoletrue_lore.add(ChatColor.YELLOW + "Will have colorful text in console.");
						consoletrue_meta.setLore(consoletrue_lore);
						btnConsoleTrue.setItemMeta(consoletrue_meta);

						ItemMeta consolefalse_meta = btnConsoleFalse.getItemMeta();
						consolefalse_meta.setDisplayName("Set console.colorful_console to False");
						ArrayList<String> consolefalse_lore = new ArrayList<>();
						consolefalse_lore.add(ChatColor.YELLOW + "Will not have colorful text in console.");
						consolefalse_meta.setLore(consolefalse_lore);
						btnConsoleFalse.setItemMeta(consolefalse_meta);


						ItemMeta longname_meta = btnLongname.getItemMeta();
						longname_meta.setDisplayName("console.longpluginname");
						ArrayList<String> longname_lore = new ArrayList<>();
						longname_lore.add(ChatColor.AQUA + "Logs use NoEndermanGrief");
						longname_lore.add(ChatColor.AQUA + "or NEG.");
						longname_meta.setLore(longname_lore);
						btnLongname.setItemMeta(longname_meta);

						ItemMeta longnametrue_meta = btnLongnameTrue.getItemMeta();
						longnametrue_meta.setDisplayName("Set longname.colorful_longname to True");
						ArrayList<String> longnametrue_lore = new ArrayList<>();
						longnametrue_lore.add(ChatColor.YELLOW + "Will have colorful text in longname.");
						longnametrue_meta.setLore(longnametrue_lore);
						btnLongnameTrue.setItemMeta(longnametrue_meta);

						ItemMeta longnamefalse_meta = btnLongnameFalse.getItemMeta();
						longnamefalse_meta.setDisplayName("Set longname.colorful_longname to False");
						ArrayList<String> longnamefalse_lore = new ArrayList<>();
						longnamefalse_lore.add(ChatColor.YELLOW + "Will not have colorful text in longname.");
						longnamefalse_meta.setLore(longnamefalse_lore);
						btnLongnameFalse.setItemMeta(longnamefalse_meta);


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
						ItemStack[] menu_items = {
								btnUpdate, btnUpdateTrue, btnUpdateFalse, btnLang, btnLangCZ, btnLangDE, btnLangEN, btnLangFR, btnSpace,
								btnDebug, btnDebugTrue, btnDebugFalse, btnSpace, btnLangLOL, btnLangNL, btnLangBR, btnSpace, btnSpace,
								btnConsole, btnConsoleTrue, btnConsoleFalse, btnTrader, btnTraderTrue, btnTraderFalse, btnPillager, btnPillagerTrue, btnPillagerFalse,
								btnEnder, btnEnderTrue, btnEnderFalse, btnGhast, btnGhastTrue, btnGhastFalse, btnSpace, btnSpace, btnSpace,
								btnHorse, btnHorseTrue, btnHorseFalse, btnPhantom, btnPhantomTrue, btnPhantomFalse, btnSpace, btnSpace, btnSpace,
								btnCreeper, btnCreeperTrue, btnCreeperFalse, btnLongname, btnLongnameTrue, btnLongnameFalse, btnSpace, btnSave, btnCancel};
						gui.setContents(menu_items);
						player.openInventory(gui);
						return true;
					}else if(!sender.hasPermission("noendermangrief.admin")){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " " + get("neg.message.noperm"));
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
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " " + get("neg.message.noperm"));
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
					if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") || !(sender instanceof Player) ||
							sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
						debug = !debug;
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " " +
								get("neg.message.debugtrue").toString().replace("<boolean>", get("neg.message.boolean." + String.valueOf(debug).toLowerCase()) ));
						return true;
					}else{
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " " + get("neg.message.noperm"));
						return false;
					}
				}

				if(args[0].equalsIgnoreCase("reload")){
					/** Check if player has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.isOp() || sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ) {
						/** Command code */
						oldconfig = new YamlConfiguration();
						LOGGER.log("Checking config file version...");
						try {
							oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
						} catch (Exception exception) {
							LOGGER.warn("Could not load config.yml");
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
						}
						String checkconfigversion = oldconfig.getString("version", "1.0.0");
						if(checkconfigversion != null){
							if(!checkconfigversion.equalsIgnoreCase(configVersion)){
								try {
									copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
								} catch (Exception exception) {
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
								saveResource("config.yml", true);

								try {
									config.load(new File(getDataFolder(), "config.yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not load config.yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
								try {
									oldconfig.load(new File(getDataFolder(), "old_config.yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not load old_config.yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
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
								} catch (Exception exception) {
									LOGGER.warn("Could not save old settings to config.yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}

								LOGGER.log("config.yml Updated! old config saved as old_config.yml");
								LOGGER.log("chance_config.yml saved.");
							}else{
								try {
									config.load(new File(getDataFolder(), "config.yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not load config.yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
							}
							oldconfig = null;
						}
						LOGGER.log("Loading config file...");
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (Exception exception) {
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
						}
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (Exception exception) {
							LOGGER.warn("Could not load config.yml");
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
						}

						debug = getConfig().getBoolean("debug", false);
						daLang = getConfig().getString("lang", "en_US");
						reloadConfig();

						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " has been " + ChatColor.WHITE + "reloaded");
						return true;
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.no_perm"));
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("test")){
					if(!(sender instanceof Player)) {
						if(UpdateCheck){
							return new testCommand(this).execute(sender, args);
						}
					}
				}
				if(args[0].equalsIgnoreCase("update")){ // TODO: Command Update
					// Player must be OP
					if(!(sender instanceof Player)) {
						/** Console */
						try {
							Bukkit.getConsoleSender().sendMessage("Checking for updates...");
							VersionChecker updater = new VersionChecker(this, projectID, githubURL);
							if(updater.checkForUpdates()) {
								/** Update available */
								UpdateAvailable = true; // TODO: Update Checker
								UColdVers = updater.oldVersion();
								UCnewVers = updater.newVersion();

								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								LOGGER.log("* " + get("neg.version.message").toString().replace("<MyPlugin>", THIS_NAME) );
								LOGGER.log("* " + get("neg.version.old_vers") + ChatColor.RED + UColdVers );
								LOGGER.log("* " + get("neg.version.new_vers") + ChatColor.GREEN + UCnewVers );
								LOGGER.log("*");
								LOGGER.log("* " + get("neg.version.please_update") );
								LOGGER.log("*");
								LOGGER.log("* " + get("neg.version.download") + ": " + DownloadLink + "/history");
								LOGGER.log("* " + get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
								//Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + UpdateChecker.getResourceUrl() + ChatColor.RESET);
							}else{
								/** Up to date */
								UpdateAvailable = false;
								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								LOGGER.log("* " + ChatColor.YELLOW + THIS_NAME + ChatColor.RESET + " " + get("neg.version.curvers") + ChatColor.RESET );
								LOGGER.log("* " + get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
							}
						}catch(Exception exception) {
							/** Error */
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + get("neg.version.update.error"));
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
						}
						/** end update checker */
						return true;
					}
					/** Check if player has permission */
					if( sender.isOp() || sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.showUpdateAvailable")
							|| sender.hasPermission("noendermangrief.admin") ){
						BukkitTask updateTask = this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
							@Override public void run() {
								try {
									Bukkit.getConsoleSender().sendMessage("Checking for updates...");
									VersionChecker updater = new VersionChecker(THIS_VERSION, projectID, githubURL);
									if(updater.checkForUpdates()) {
										UpdateAvailable = true;
										UColdVers = updater.oldVersion();
										UCnewVers = updater.newVersion();
										String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>.\"}}]";
										links = links.replace("<DownloadLink>", DownloadLink).replace("<Download>", get("neg.version.download"))
												.replace("<Donate>", get("neg.version.donate")).replace("<please_update>", get("neg.version.please_update"))
												.replace("<Donate_msg>", get("neg.version.donate.message")).replace("<Notes>", get("neg.version.notes"))
												.replace("<Notes_msg>", get("neg.version.notes.message"));
										String versions = "" + ChatColor.GRAY + get("neg.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("neg.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
										sender.sendMessage("" + ChatColor.GRAY + get("neg.version.message").toString().replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
										Utils.sendJson(sender, links);
										sender.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
									}else{
										String links = "{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}}";
										links = links.replace("<Donate>", get("neg.version.donate")).replace("<Donate_msg>", get("neg.version.donate.message"));
										Utils.sendJson(sender, links);
										sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " v" + THIS_VERSION + ChatColor.RESET + " " + get("neg.version.curvers") + ChatColor.RESET);
										UpdateAvailable = false;
									}
								}catch(Exception exception) {
									sender.sendMessage(ChatColor.RED + get("neg.version.update.error"));
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
								}
							}
						});
						return true;
					}else{
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("eg") || args[0].equalsIgnoreCase("endermangrief") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("enderman_grief", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("enderman_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}

				}
				if( args[0].equalsIgnoreCase("sh") || args[0].equalsIgnoreCase("skeletonhorse") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("skeleton_horse_spawn", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg sh True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("skeleton_horse_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("wt") || args[0].equalsIgnoreCase("wanderingtrader") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("wandering_trader_spawn", false)));
						return true;
					}
					LOGGER.log("args.length=" + args.length);
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg sh True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("wandering_trader_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("cg") || args[0].equalsIgnoreCase("creepergrief") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("creeper_grief", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("creeper_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}
				}
				// Ghast Grief
				if( args[0].equalsIgnoreCase("gg") || args[0].equalsIgnoreCase("ghastgrief") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("ghast_grief", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("ghast_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("pg") || args[0].equalsIgnoreCase("phantomgrief") ){ // Phantom Grief
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("phantom_spawn", false)));
						return true;
					}
					/** Check if player has permission */
					if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("phantom_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("pp") || args[0].equalsIgnoreCase("pillagerpatrol") ){ // Pillager Patrol Grief
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("pillager_patrol_spawn", false)));
						return true;
					}
					/** Check if player has permission */
					if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("pillager_patrol_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.will") );
							}
							try {
								config.load(new File(getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load config.yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.DARK_RED + "" + get("neg.message.noperm"));
						return false;
					}
				}
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_COMMAND_ERROR).error(exception));
			// ERROR_RUNNING_DRAGON_DEATH_COMMAND "Error running command after dragon death."
		}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		try {
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
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_TAB_COMPLETE_ERROR).error(exception));
			// ERROR_RUNNING_DRAGON_DEATH_COMMAND "Error running command after dragon death."
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

	public void copyFile_Java7(String origin, String destination) throws IOException {
		try {
			Path FROM = Paths.get(origin);
			Path TO = Paths.get(destination);
			//overwrite the destination file if it exists, and copy
			// the file attributes, including the rwx permissions
			CopyOption[] options = new CopyOption[]{
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			Files.copy(FROM, TO, options);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
		}
	}

	@Override
	public void saveConfig(){
		try {
			config.save(new File(getDataFolder(), "config.yml"));
		} catch (Exception exception) {
			LOGGER.warn("Could not save old settings to config.yml");
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
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
		}else if (version.equals("v1_20_R1")) {
			setingsbook = new SettingsBook_v1_20_R1();
		}else if (version.equals("v1_20_R2")) {
			setingsbook = new SettingsBook_v1_20_R2();
		}else if (version.equals("v1_20_R3")) {
			setingsbook = new SettingsBook_v1_20_R3();
		}
		// This will return true if the server version was compatible with one of our NMS classes
		// because if it is, our actionbar would not be null
		return setingsbook != null;
	}

	public boolean saveConfig(boolean update, boolean Debug, boolean Console, boolean Longname, boolean Trader, boolean Pillager, boolean Ender, boolean Ghast, boolean Horse,
			boolean Phantom, boolean Creeper, String Lang) {
		// UpdateCheck	debug	daLang	colorful_console
		UpdateCheck = update;
		debug = Debug;
		daLang = Lang;
		colorful_console = Console;
		if(!Longname) {
			pluginName = "NEG";
		}else {
			pluginName = THIS_NAME;
		}
		config.set("auto_update_check", update);
		config.set("debug", Debug);
		config.set("lang", Lang);
		config.set("console.colorful_console", Console);
		config.set("console.longpluginname", Longname);
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
			LOGGER.warn("Could not save settings to config.yml");
			e.printStackTrace();
			return false;
		}
		LOGGER.log("config.yml has been updated");
		return true;
	}

	public String LoadTime(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
		long milliseconds = elapsedTime % 1000;

		if (minutes > 0) {
			return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
		} else if (seconds > 0) {
			return String.format("%d s %d ms.", seconds, milliseconds);
		} else {
			return String.format("%d ms.", elapsedTime);
		}
	}

	@SuppressWarnings("static-access")
	public String get(String key, String... defaultValue) {
		return lang2.get(key, defaultValue);
	}

	// Used to check Minecraft version
	private MinecraftVersion verifyMinecraftVersion() {
		MinecraftVersion minimum = new MinecraftVersion(PluginLibrary.MINIMUM_MINECRAFT_VERSION);
		MinecraftVersion maximum = new MinecraftVersion(PluginLibrary.MAXIMUM_MINECRAFT_VERSION);

		try {
			MinecraftVersion current = new MinecraftVersion(this.getServer());

			// We'll just warn the user for now
			if (current.compareTo(minimum) < 0) {
				LOGGER.warn("Version " + current + " is lower than the minimum " + minimum);
			}
			if (current.compareTo(maximum) > 0) {
				LOGGER.warn("Version " + current + " has not yet been tested! Proceed with caution.");
			}

			return current;
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_PARSE_MINECRAFT_VERSION).error(exception).messageParam(maximum));

			// Unknown version - just assume it is the latest
			return maximum;
		}
	}

	public String getjarfilename() {
		return jarfilename;
	}

	public boolean getDebug() {
		return debug;
	}

}
