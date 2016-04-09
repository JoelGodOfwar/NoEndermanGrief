package com.github.joelgodofwar.neg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.github.joelgodofwar.neg.api.ConfigAPI;


public class PluginBase  extends JavaPlugin implements Listener{

	public final static Logger logger = Logger.getLogger("Minecraft");
	public static boolean UpdateCheck;
	public static boolean cancelbroadcast;
	public static boolean debug;
	public static boolean allowSpawn;
	public static boolean allowPickup;
	
	@Override // TODO: onEnable
	public void onEnable(){

		ConfigAPI.CheckForConfig(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info("**************************************************************");
		logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " Has been enabled");
		logger.info("**************************************************************");
		getServer().getPluginManager().registerEvents(this, this);
		
		String varCheck = getConfig().getString("auto-update-check");
		String varCheck2 = getConfig().getString("skeleton-horse-spawn");
		String varCheck3 = getConfig().getString("skeleton-horse-spawn");
		String varCheck4 = getConfig().getString("debug");
		//log("varCheck " + varCheck);
		//log("varCheck2 " + varCheck2);
		//log("varCheck3 " + varCheck3);
		if(varCheck.contains("default")){
			getConfig().set("auto-update-check", true);
		}
		if(varCheck2.contains("default")){
			getConfig().set("skeleton-horse-spawn", false);
		}
		if(varCheck3.contains("default")){
			getConfig().set("enderman-grief", false);
		}
		if(varCheck4.contains("default")){
			getConfig().set("debug", false);
		}
		saveConfig();
		ConfigAPI.Reloadconfig(this, null);
		
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		}catch (Exception e){
			// Failed to submit the stats
		}
	}
	
	@Override // TODO: onDisable
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info("**************************************************************");
		logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " Has been disabled");
		logger.info("**************************************************************");
	}
	
	public static  void log(String dalog){
		Bukkit.getLogger().info(dalog);
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		//if (!tr.isrunworld(ac.getName(), e.getEntity().getLocation().getWorld().getName()) )
			//return;

		if (e.getEntity() == null)
			return;

		if (e.getEntity().getType() == EntityType.ENDERMAN) {
			if(!allowPickup){
				e.setCancelled(true);
			}
			if(debug){log("Enderman attempted to pickup " + e.getBlock().getType() + " at " + e.getBlock().getLocation());} //TODO: Logger
			return;
		}

	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	  {
	    Player p = event.getPlayer();
	    if(p.isOp() && UpdateCheck){	
			try {
			
				URL url = new URL("https://raw.githubusercontent.com/JoelGodOfwar/NoEndermanGrief/master/version.txt");
				final URLConnection conn = url.openConnection();
	            conn.setConnectTimeout(5000);
	            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            final String response = reader.readLine();
	            final String localVersion = this.getDescription().getVersion();
	            if(debug){log("response= ." + response + ".");} //TODO: Logger
	            if(debug){log("localVersion= ." + localVersion + ".");} //TODO: Logger
	            if (!response.equalsIgnoreCase(localVersion)) {
					p.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " New version available!");
				}
			} catch (MalformedURLException e) {
				log("MalformedURLException");
				e.printStackTrace();
			} catch (IOException e) {
				log("IOException");
				e.printStackTrace();
			}catch (Exception e) {
				log("Exception");
				e.printStackTrace();
			}
			
		}
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Horse){
        	Horse h = (Horse) entity;
        	Variant variant = h.getVariant();
        	if(variant.name() == "SKELETON_HORSE"){
        		if(debug){log("Skeleton Horse attempted to spawn at " + e.getLocation());} //TODO: Logger
        		if(!allowSpawn){
        			e.setCancelled(true);
        		}
        	}
        }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		//Player p = (Player)sender;
	    if (cmd.getName().equalsIgnoreCase("NEG"))
	    {
	    	if (args.length == 0)
	    	{
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("noendermangrief.op") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
	            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
			    if(sender.isOp()||sender.hasPermission("sps.op")){
			    	sender.sendMessage(ChatColor.GOLD + " OP Commands");
			    	sender.sendMessage(ChatColor.GOLD + " /NEG EG true/false - Set if endermen grief or not.");
			    	sender.sendMessage(ChatColor.GOLD + " /NEG SH true/false - Set if Skeleton Horses spawn or not.");
			    	sender.sendMessage(ChatColor.GOLD + " /NEG update - Check for update.");//Check for update.");
			    	sender.sendMessage(ChatColor.GOLD + " /NEG reload - Reload Config file." );//Reload config file.");
			    	sender.sendMessage(ChatColor.GOLD + " /NEG check true/false - Set auto update check." );//set auto-update-check to true or false.");
			    }
			    sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + "NoEndermanGrief" + ChatColor.GREEN + "]===============[]");
			    return true;
	    	}
	    
	    	if(args[0].equalsIgnoreCase("check")){
	    		if(args.length< 1){
					return false;
	    		}
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("noendermangrief.op") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
		    	if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " §c" + "Argument must be boolean. Usage: /sps check True/False");
		    	}else if(args[1].contains("true") || args[1].contains("false")){
		    		FileConfiguration config = getConfig();
					config.set("auto-update-check", "" + args[1]);
							
					saveConfig();
					ConfigAPI.Reloadconfig(this, null);
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + " " + args[1]);
					if(args[1].contains("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " Will not check for updates." );
					}else if(args[1].contains("true")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " Will check for updates." );
					}
					reloadConfig();
					return true;
				}
	    	
	    	}
	    	if(args[0].equalsIgnoreCase("reload")){
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("noendermangrief.op") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
	            ConfigAPI.Reloadconfig(this, sender);
	    	}
	    	if(args[0].equalsIgnoreCase("update")){
			  // Player must be OP and auto-update-check must be true
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("noendermangrief.op") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
				try {
					
					URL url = new URL("https://raw.githubusercontent.com/JoelGodOfwar/NoEndermanGrief/master/version.txt");
					final URLConnection conn = url.openConnection();
			        conn.setConnectTimeout(5000);
			        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        final String response = reader.readLine();
			        final String localVersion = this.getDescription().getVersion();
			        if(debug){log("response= ." + response + ".");} //TODO: Logger
			        if(debug){log("localVersion= ." + localVersion + ".");} //TODO: Logger
			        if (!response.equalsIgnoreCase(localVersion)) {
			        	sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " New version available!");
					}else{
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.GREEN + " Version is up to date." );
					}
				} catch (MalformedURLException e) {
					log(this.getName() + " MalformedURLException");
					e.printStackTrace();
				} catch (IOException e) {
					log(this.getName() + " IOException");
					e.printStackTrace();
				}catch (Exception e) {
					log(this.getName() + " Exception");
					e.printStackTrace();
				}
	    	}
	    	if(args[0].equalsIgnoreCase("eg")){
	    		if(args.length< 1){
					return false;
	    		}
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("noendermangrief.op") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
		    	if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " §c" + "Argument must be boolean. Usage: /neg eg True/False");
		    	}else if(args[1].contains("true") || args[1].contains("false")){
		    		FileConfiguration config = getConfig();
					config.set("enderman-grief", "" + args[1]);
						
					saveConfig();
					ConfigAPI.Reloadconfig(this, null);
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + "enderman-grief has been set to " + args[1]);
					if(args[1].contains("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + "Endermen will not pickup blocks." );
					}else if(args[1].contains("true")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + "Endermen will pickup blocks." );
					}
					reloadConfig();
					return true;
				}
	    	  
	    	}
	    	if(args[0].equalsIgnoreCase("sh")){
	    		if(args.length< 1){
					return false;
	    		}
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("noendermangrief.op") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
		    	if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " §c" + "Argument must be boolean. Usage: /neg sh True/False");
		    	}else if(args[1].contains("true") || args[1].contains("false")){
		    		FileConfiguration config = getConfig();
					config.set("skeleton-horse-spawn", "" + args[1]);
							
					saveConfig();
					ConfigAPI.Reloadconfig(this, null);
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + "skeleton-horse-spawn has been set to " + args[1]);
					if(args[1].contains("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + "SKELETON_HORSE will not spawn during thunderstorms." );
					}else if(args[1].contains("true")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + ChatColor.WHITE + "SKELETON_HORSE will spawn during thunderstorms." );
					}
					reloadConfig();
					return true;
				}
	    	}
	    }
	    
	    return true;
	}
}
