package com.github.joelgodofwar.neg.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.github.joelgodofwar.neg.NoEndermanGrief;

public class ConfigAPI  {

	@SuppressWarnings("unused")
	public static  void CheckForConfig(Plugin plugin){
		try{
			PluginDescriptionFile pdfFile = plugin.getDescription();
			if(!plugin.getDataFolder().exists()){
				log(": Data Folder doesn't exist", plugin);
				log(": Creating Data Folder", plugin);
				plugin.getDataFolder().mkdirs();
				log(": Data Folder Created at " + plugin.getDataFolder(), plugin);
			}
			File  file = new File(plugin.getDataFolder(), "config.yml");
			plugin.getLogger().info("" + file);
			if(!file.exists()){
				log(": config.yml not found, creating!", plugin);
				plugin.saveResource("config.yml", true);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void Reloadconfig(Plugin plugin, CommandSender sender){
		// Load config.
		FileConfiguration config = plugin.getConfig();
		String daString = config.getString("debug").replace("'", "") + ",";
		
		if(daString.contains("true")){
			NoEndermanGrief.debug = true;
			//log("debug=true", plugin);
		}else{
			NoEndermanGrief.debug = false;
			//log("debug=false", plugin);
		}
		String daString2 = config.getString("auto_update_check").replace("'", "") + ",";
		if(daString2.contains("true")){
			//NoEndermanGrief.UpdateCheck = true;
		}else{
			//NoEndermanGrief.UpdateCheck = false;
		}
		String daString3 = config.getString("skeleton_horse_spawn").replace("'", "") + ",";
		if(daString3.contains("true")){
			//NoEndermanGrief.allowSpawnSH = true;
		}else{
			//NoEndermanGrief.allowSpawnSH = false;
		}
		String daString5 = config.getString("wandering_trader").replace("'", "") + ",";
		if(daString5.contains("true")){
			//NoEndermanGrief.allowSpawnWT = true;
		}else{
			//NoEndermanGrief.allowSpawnWT = false;
		}
		String daString4 = config.getString("enderman_grief").replace("'", "") + ",";
		if(daString4.contains("true")){
			//NoEndermanGrief.allowPickup = true;
		}else{
			//NoEndermanGrief.allowPickup = false;
		}
		String daString6 = config.getString("enderman_grief").replace("'", "") + ",";
		if(daString6.contains("true")){
			//NoEndermanGrief.allowExplode = true;
		}else{
			//NoEndermanGrief.allowExplode = false;
		}
		String daString7 = config.getString("lang", "en_US").replace("'", "");
		NoEndermanGrief.daLang = daString7;
		if(NoEndermanGrief.debug){log("UpdateCheck = " + NoEndermanGrief.UpdateCheck, plugin);} //TODO: Logger
		if(sender != null){
			sender.sendMessage(ChatColor.YELLOW + plugin.getName() + ChatColor.WHITE + " Configs Reloaded");
		}
	}
	public static  void log(String dalog, Plugin plugin){
		NoEndermanGrief.logger.info(Ansi.YELLOW + "" + plugin.getName() + Ansi.RESET + " " + dalog + Ansi.RESET);
	}
	public  void logDebug(String dalog, Plugin plugin){
		log(" " + plugin.getDescription().getVersion() + Ansi.RED + Ansi.BOLD + " [DEBUG] " + Ansi.RESET + dalog, plugin);
	}
	/*
     * this copy(); method copies the specified file from your jar
     *     to your /plugins/<pluginName>/ folder
     */
    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
