package com.github.joelgodofwar.neg;

import com.github.joelgodofwar.neg.commands.ConfigCommand;
import com.github.joelgodofwar.neg.commands.testCommand;
import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.neg.common.error.Report;
import com.github.joelgodofwar.neg.events.CreatureSpawnHandler;
import com.github.joelgodofwar.neg.events.EntityChangeBlockHandler;
import com.github.joelgodofwar.neg.events.EntityExplodeHandler;
import com.github.joelgodofwar.neg.events.PlayerJoinHandler;
import com.github.joelgodofwar.neg.gui.GUIMoveItem;
import com.github.joelgodofwar.neg.i18n.Translator;
import com.github.joelgodofwar.neg.utils.Metrics;
import com.github.joelgodofwar.neg.utils.PluginUtils;
import lib.github.joelgodofwar.coreutils.CoreUtils;
import lib.github.joelgodofwar.coreutils.util.JsonMessageUtils;
import lib.github.joelgodofwar.coreutils.util.Version;
import lib.github.joelgodofwar.coreutils.util.VersionChecker;
import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class NoEndermanGrief extends JavaPlugin implements Listener{
	/** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	//public final static Logger logger = Logger.getLogger("Minecraft");
	public static String THIS_NAME;
	public static String THIS_VERSION;
	/** update checker variables */
	public int projectID = 71236; // https://spigotmc.org/resources/71236
	public String githubURL = "https://github.com/JoelGodOfwar/NoEndermanGrief/raw/master/versions/1.20/versions.xml";
	public boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public static boolean UpdateCheck;
	public String DownloadLink = "https://dev.bukkit.org/projects/no-enderman-grief2";
	/** end update checker variables */
	Version MINIMUM_MINECRAFT_VERSION = new Version("1.20");
	Version MAXIMUM_MINECRAFT_VERSION = new Version("26.1.2");
	Version CURRENT_MINECRAFT_VERSION = Version.getCurrentVersion();
	public static String daLang;
	public static boolean cancelbroadcast;
	public boolean debug;
	File langFile;
	public FileConfiguration lang;
	public YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	boolean colorful_console;
	private boolean correctVersion = true;
	Version minConfigVersion = new Version("1.0.7");
	//String langVersion = "1.0.6";
	String pluginName = THIS_NAME;
	Translator lang2;
	public String jarfilename = this.getFile().getAbsoluteFile().toString();
	public static DetailedErrorReporter reporter;
	public PluginLogger LOGGER;
	public CoreUtils coreUtils = new CoreUtils(this);
	public JsonMessageUtils jsonMessageUtils;

	@SuppressWarnings("unlikely-arg-type") @Override // TODO: onEnable
	public void onEnable(){
		long startTime = System.currentTimeMillis();
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
		this.LOGGER = new PluginLogger(pluginName, () -> debug);
		CoreUtils.initLogger(LOGGER);

		jsonMessageUtils = coreUtils.jsonMessageUtils;

		// Handle unexpected Minecraft versions
		Version checkVersion = this.verifyMinecraftVersion();

		LOGGER.log(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
		LOGGER.log(ChatColor.GREEN + " v" + THIS_VERSION + ChatColor.RESET + " Loading...");
		LOGGER.log("Server Version: " + getServer().getVersion());

		//MinecraftVersion version = this.verifyMinecraftVersion();

		/* DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			LOGGER.log(ChatColor.RED + "YOU ARE USING A DEV=BUILD, PLEASE REPORT ANY ISSUES." + ChatColor.RESET);
		}

		LOGGER.log("Checking lang files...");

		if( !(CURRENT_MINECRAFT_VERSION.isAtLeast(PluginLibrary.MINIMUM_MINECRAFT_VERSION)) ){
			LOGGER.log(ChatColor.RED + "WARNING!" + ChatColor.GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
			LOGGER.log(ChatColor.RED + "WARNING! " + ChatColor.YELLOW + get("neg.message.server_not_version") + ChatColor.RESET);
			LOGGER.log(ChatColor.RED + "WARNING! " + ChatColor.YELLOW + THIS_NAME + " v" + THIS_VERSION + " disabling." + ChatColor.RESET);
			LOGGER.log(ChatColor.RED + "WARNING!" + ChatColor.GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		LOGGER.log("Checking config file...");
		/*  Check for config */
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
		Version checkconfigversion = Version.fromString(getConfig().getString("version", "1.0.0"));
		LOGGER.log("Config file version=" + checkconfigversion + " expected=" + minConfigVersion);
		if(checkconfigversion != null){
			if(!checkconfigversion.equals(minConfigVersion)){
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
				if(checkconfigversion.isAtMost("1.0.6")){
					// If 1.0.6 or under do this
					config.set("auto_update_check", oldconfig.get("auto_update_check", true));
					config.set("debug", oldconfig.get("debug", false));
					config.set("lang", oldconfig.get("lang", "en_US"));
					config.set("console.colorful_console", oldconfig.get("colorful_console", true));
					config.set("console.longpluginname", oldconfig.get("console.longpluginname", true));
					config.set("enderman_grief", !oldconfig.getBoolean("enderman_grief", false));
					config.set("skeleton_horse_spawn", !oldconfig.getBoolean("skeleton_horse_spawn", false));
					config.set("creeper_grief", !oldconfig.getBoolean("creeper_grief", false));
					config.set("wandering_trader_spawn", !oldconfig.getBoolean("wandering_trader", false));
					config.set("ghast_grief", !oldconfig.getBoolean("ghast_grief", false));
					config.set("phantom_spawn", !oldconfig.getBoolean("phantom_spawn", false));
					config.set("pillager_patrol_spawn", !oldconfig.getBoolean("pillager_patrol_spawn", false));
				}else {
					// If above 1.0.6 do this
					config.set("auto_update_check", oldconfig.get("auto_update_check", true));
					config.set("debug", oldconfig.get("debug", false));
					config.set("lang", oldconfig.get("lang", "en_US"));
					config.set("console.colorful_console", oldconfig.get("colorful_console", true));
					config.set("console.longpluginname", oldconfig.get("console.longpluginname", true));
					config.set("enderman_grief", oldconfig.get("enderman_grief", true));
					config.set("skeleton_horse_spawn", oldconfig.get("skeleton_horse_spawn", true));
					config.set("creeper_grief", oldconfig.get("creeper_grief", true));
					config.set("wandering_trader_spawn", oldconfig.get("wandering_trader", true));
					config.set("ghast_grief", oldconfig.get("ghast_grief", true));
					config.set("phantom_spawn", oldconfig.get("phantom_spawn", true));
					config.set("pillager_patrol_spawn", oldconfig.get("pillager_patrol_spawn", true));
				}
				try {
					config.save(new File(getDataFolder(), "config.yml"));
				} catch (Exception exception) {
					LOGGER.warn("Could not save old settings to config.yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
				}
				LOGGER.log("config.yml has been updated");
			}
		}
		/* end config check */
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

		/* Update Checker */
		if(UpdateCheck){
			try {
				LOGGER.log("Checking for updates...");
				VersionChecker updater = new VersionChecker(this, projectID, githubURL);
				if(updater.checkForUpdates()) {
					/* Update available */
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();

					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					LOGGER.log("* " + get("neg.version.message").replace("<MyPlugin>", THIS_NAME) );
					LOGGER.log("* " + get("neg.version.old_vers") + ChatColor.RED + UColdVers );
					LOGGER.log("* " + get("neg.version.new_vers") + ChatColor.GREEN + UCnewVers );
					LOGGER.log("*");
					LOGGER.log("* " + get("neg.version.please_update") );
					LOGGER.log("*");
					LOGGER.log("* " + get("neg.version.download") + ": " + DownloadLink + "/history");
					LOGGER.log("* " + get("neg.version.donate") + ": https://ko-fi.com/joelgodofwar");
					LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				}else{
					/* Up to date */
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
			/* auto_update_check is false so nag. */
			LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
			LOGGER.log("* " + get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
			LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
		}
		/* end update checker */

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new GUIMoveItem(this), this);  // Comment out for release versions, un comment for Dev builds.
		getServer().getPluginManager().registerEvents(new EntityChangeBlockHandler(this), this);
		getServer().getPluginManager().registerEvents(new EntityExplodeHandler(this), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this), this);
		getServer().getPluginManager().registerEvents(new CreatureSpawnHandler(this), this);
		consoleInfo(ChatColor.BOLD + "ENABLED" + ChatColor.RESET + " - Loading took " + LoadTime(startTime));

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
					return Objects.requireNonNull(getConfig().getString("auto_update_check")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_debug", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("debug")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("enderman_grief", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("enderman_grief")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("skeleton_horse_spawn", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("skeleton_horse_spawn")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("creeper_grief", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("creeper_grief")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("wandering_trader", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("wandering_trader_spawn")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("var_lang", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("lang")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("ghast_grief", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("ghast_grief")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("phantom_spawn", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("phantom_spawn")).toUpperCase();
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("pillager_patrol_spawn", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return Objects.requireNonNull(getConfig().getString("pillager_patrol_spawn")).toUpperCase();
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

	/* @EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		// if (!tr.isrunworld(ac.getName(), e.getEntity().getLocation().getWorld().getName()) )
		//return;
		try {
			if (event.getEntity() == null) {
				return;
			}

			if (event.getEntity().getType() == EntityType.ENDERMAN) {
				Block block = event.getBlock();
				LOGGER.debug("ECBE Block = " + block.getType().toString());
				BlockData blockData = event.getBlockData();
				LOGGER.debug("ECBE BlockData = " + blockData.getAsString());

				event.setCancelled( config.getBoolean("enderman_grief", true) );

				LOGGER.debug(get("neg.entity.enderman.pickup") + event.getBlock().getType() + " at " + event.getBlock().getLocation());
				return;
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_ENDERMAN_GRIEF).error(exception));
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent entity) {
		try {
			if (entity.getEntity().getType() == EntityType.CREEPER) {

				entity.setCancelled( config.getBoolean("creeper_grief", true) );

				LOGGER.debug(get("neg.entity.creeper.explode") + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockZ());
				return;
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_CREEPER_GRIEF).error(exception));
		}
		try {
			if ((entity.getEntity().getType() == EntityType.FIREBALL)  && (((Fireball) entity.getEntity()).getShooter() instanceof Ghast)) {
				if(config.getBoolean("ghast_grief", true)){
					Entity fireball = entity.getEntity();
					((Fireball) fireball).setIsIncendiary(false);
					((Fireball) fireball).setYield(0F);
					entity.setCancelled(true);
				}
				LOGGER.debug(get("neg.entity.ghast.explode") + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockZ());
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
			player.sendMessage("" + ChatColor.GRAY + get("neg.version.message").replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
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
				if(config.getBoolean("skeleton_horse_spawn", true)){
					LOGGER.debug(get("neg.entity.skeleton_horse") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_SKELETON_HORSE_GRIEF).error(exception));
		}
		try {
			if (entity instanceof WanderingTrader){
				if(config.getBoolean("wandering_trader_spawn", true)){
					LOGGER.debug(get("neg.entity.wandering_trader") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_WANDERING_TRADER_GRIEF).error(exception));
		}
		try {
			if (entity instanceof Phantom){
				if(config.getBoolean("phantom_spawn", true)){
					LOGGER.debug(get("neg.entity.phantom") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_PHANTOM_GRIEF).error(exception));
		}
		try {
			if(event.getSpawnReason() == SpawnReason.PATROL) {
				if(config.getBoolean("pillager_patrol_spawn", true)){
					LOGGER.debug(get("neg.entity.pillager_patrol") + event.getLocation());
					event.setCancelled(true);
				}
			}
		}catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_HANDLING_PILLAGER_PATROL_GRIEF).error(exception));
		}
	}// */

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand( CommandSender sender,  Command cmd,  String label,  String[] args){ // TODO: Commands
		try {
			//Player p = (Player)sender;
			if (cmd.getName().equalsIgnoreCase("NEG")){
				if (args.length == 0){
					/* Check if sender has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") || sender.isOp() ) {
						/* Command code */
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
						sender.sendMessage(ChatColor.GOLD + " ");
						sender.sendMessage(ChatColor.RED + " " + get("neg.version.donate.message") + ChatColor.GREEN + ": https://ko-fi.com/joelgodofwar" + ChatColor.RESET);
						sender.sendMessage(ChatColor.GOLD + " ");
						if( sender.isOp()||sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
							sender.sendMessage(ChatColor.WHITE + " -<[" + ChatColor.AQUA + " OP Commands " + ChatColor.WHITE + "}>-");
							sender.sendMessage(ChatColor.GOLD + " /NEG update - " + get("neg.command.update"));//Check for update.
							sender.sendMessage(ChatColor.GOLD + " /NEG reload - " + get("neg.command.reload") );//Reload config file.
							if( sender.hasPermission("noendermangrief.admin") ) {
								sender.sendMessage(ChatColor.GOLD + " /NEG config - " + get("neg.command.config") );
							}
							if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") ||
									!(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ){
								sender.sendMessage(ChatColor.GOLD + " /NEG toggledebug - " + get("neg.message.debuguse") );
							}
						}
						if( sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ){
							sender.sendMessage(ChatColor.WHITE + " -<[" + ChatColor.YELLOW + " Admin Commands " + ChatColor.WHITE + "}>-");
							/* if( sender instanceof Player ) {
								sender.sendMessage(ChatColor.GOLD + " /NEG BOOK - " + get("neg.command.book"));
							}//*/
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.no_perm"));
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
						ConfigCommand configCommand = new ConfigCommand(this);
						return configCommand.execute(player, args);
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
								get("neg.message.debugtrue").replace("<boolean>", get("neg.message.boolean." + String.valueOf(debug).toLowerCase()) ));
						return true;
					}else{
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + ChatColor.RED + " " + get("neg.message.noperm"));
						return false;
					}
				}

				if(args[0].equalsIgnoreCase("reload")){
					/* Check if player has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.isOp() || sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ) {
						/* Command code */
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
							if(!checkconfigversion.equalsIgnoreCase(String.valueOf(minConfigVersion))){
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.no_perm"));
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
						/* Console */
						try {
							Bukkit.getConsoleSender().sendMessage("Checking for updates...");
							VersionChecker updater = new VersionChecker(this, projectID, githubURL);
							if(updater.checkForUpdates()) {
								/* Update available */
								UpdateAvailable = true; // TODO: Update Checker
								UColdVers = updater.oldVersion();
								UCnewVers = updater.newVersion();

								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								LOGGER.log("* " + get("neg.version.message").replace("<MyPlugin>", THIS_NAME) );
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
								/* Up to date */
								UpdateAvailable = false;
								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
								LOGGER.log("* " + ChatColor.YELLOW + THIS_NAME + ChatColor.RESET + " " + get("neg.version.curvers") + ChatColor.RESET );
								LOGGER.log("* " + get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
								LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
							}
						}catch(Exception exception) {
							/* Error */
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + get("neg.version.update.error"));
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
						}
						/* end update checker */
						return true;
					}
					/* Check if player has permission */
					if( sender.isOp() || sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.showUpdateAvailable")
							|| sender.hasPermission("noendermangrief.admin") ){
						BukkitTask updateTask = this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
							@Override public void run() {
								try {
									Bukkit.getConsoleSender().sendMessage("Checking for updates...");
									VersionChecker updater = new VersionChecker(THIS_VERSION, projectID, githubURL,LOGGER);
									if(updater.checkForUpdates()) {
										UpdateAvailable = true;
										UColdVers = updater.oldVersion();
										UCnewVers = updater.newVersion();
										String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>.\"}}]";
										links = links.replace("<DownloadLink>", DownloadLink).replace("<Download>", get("neg.version.download"))
												.replace("<Donate>", get("neg.version.donate")).replace("<please_update>", get("neg.version.please_update"))
												.replace("<Donate_msg>", get("neg.version.donate.message")).replace("<Notes>", get("neg.version.notes"))
												.replace("<Notes_msg>", get("neg.version.notes.message"));
										String versions = ChatColor.GRAY + get("neg.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("neg.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
										sender.sendMessage(ChatColor.GRAY + get("neg.version.message").replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
										jsonMessageUtils.sendJsonMessage((Player) sender, links);
										sender.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
									}else{
										String links = "{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}}";
										links = links.replace("<Donate>", get("neg.version.donate")).replace("<Donate_msg>", get("neg.version.donate.message"));
										jsonMessageUtils.sendJsonMessage((Player) sender, links);
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
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.current").replace("[setting]", "" + getConfig().getBoolean("enderman_grief", false)));
						return true;
					}
					/* Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("enderman_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.enderman.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
						return false;
					}

				}
				if( args[0].equalsIgnoreCase("sh") || args[0].equalsIgnoreCase("skeletonhorse") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.current").replace("[setting]", "" + getConfig().getBoolean("skeleton_horse_spawn", false)));
						return true;
					}
					/* Check if the player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg sh True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("skeleton_horse_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.skeleton_horse.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("wt") || args[0].equalsIgnoreCase("wanderingtrader") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.current").replace("[setting]", "" + getConfig().getBoolean("wandering_trader_spawn", false)));
						return true;
					}
					LOGGER.log("args.length=" + args.length);
					/* Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg sh True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("wandering_trader_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.wandering_trader.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("cg") || args[0].equalsIgnoreCase("creepergrief") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.current").replace("[setting]", "" + getConfig().getBoolean("creeper_grief", false)));
						return true;
					}
					/* Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("creeper_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.creeper.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
						return false;
					}
				}
				// Ghast Grief
				if( args[0].equalsIgnoreCase("gg") || args[0].equalsIgnoreCase("ghastgrief") ){
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.current").replace("[setting]", "" + getConfig().getBoolean("ghast_grief", false)));
						return true;
					}
					/* Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("ghast_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.ghast.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("pg") || args[0].equalsIgnoreCase("phantomgrief") ){ // Phantom Grief
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.current").replace("[setting]", "" + getConfig().getBoolean("phantom_spawn", false)));
						return true;
					}
					/* Check if player has permission */
					if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("phantom_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.phantom.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("pp") || args[0].equalsIgnoreCase("pillagerpatrol") ){ // Pillager Patrol Grief
					if(args.length <= 1){
						sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.current").replace("[setting]", "" + getConfig().getBoolean("pillager_patrol_spawn", false)));
						return true;
					}
					/* Check if player has permission */
					if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/* Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.RED + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							config.set("pillager_patrol_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.will") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(ChatColor.YELLOW + THIS_NAME + " " + ChatColor.WHITE + get("neg.entity.pillager_patrol.wont") );
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
						sender.sendMessage(ChatColor.DARK_RED + get("neg.message.noperm"));
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
					//autoCompletes.add("Book");
					autoCompletes.add("EndermanGrief");
					autoCompletes.add("SkeletonHorse");
					autoCompletes.add("CreeperGrief");
					autoCompletes.add("WanderingTrader");
					autoCompletes.add("GhastGrief");
					autoCompletes.add("PhantomGrief");
					autoCompletes.add("PillagerPatrol");
					if(sender.hasPermission("noendermangrief.admin")) {
						autoCompletes.add("Config");
					}
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

	// Used to check the Minecraft version
	private Version verifyMinecraftVersion() {
		try {
			// We'll just warn the user for now
			if (CURRENT_MINECRAFT_VERSION.compareTo(MINIMUM_MINECRAFT_VERSION) < 0) {
				LOGGER.warn("Version " + CURRENT_MINECRAFT_VERSION + " is lower than the minimum " + MINIMUM_MINECRAFT_VERSION);
			}
			if (CURRENT_MINECRAFT_VERSION.compareTo(MAXIMUM_MINECRAFT_VERSION) > 0) {
				LOGGER.warn("Version " + CURRENT_MINECRAFT_VERSION + " has not yet been tested! Proceed with caution.");
			}
			return CURRENT_MINECRAFT_VERSION;
		} catch (Exception exception) {
			reporter.reportWarning(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_PARSE_MINECRAFT_VERSION).error(exception).messageParam(MAXIMUM_MINECRAFT_VERSION));
			// Unknown version - just assume it is the latest
			return MAXIMUM_MINECRAFT_VERSION;
		}
	}

	public String getJarFileName() {
		return jarfilename;
	}

	public boolean getDebug() {
		return debug;
	}

	static JavaPlugin plugin = getInstance();
	public static NoEndermanGrief getInstance() {
		return (NoEndermanGrief) Bukkit.getPluginManager().getPlugin("NoEndermanGrief");
	}
}
