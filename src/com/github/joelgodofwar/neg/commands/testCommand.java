package com.github.joelgodofwar.neg.commands;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.joelgodofwar.neg.NoEndermanGrief;
import com.github.joelgodofwar.neg.util.VersionChecker;

public class testCommand {
	private static NoEndermanGrief plugin;
	
	public testCommand(NoEndermanGrief plugin) {
        testCommand.plugin = plugin;
    }
	
	public static boolean execute(CommandSender sender, String[] args) {
		try {
			Bukkit.getConsoleSender().sendMessage("Checking for updates...");
			VersionChecker updater = new VersionChecker(plugin, plugin.projectID, plugin.updateurl);
			if(updater.checkForUpdates2()) {
				plugin.UpdateAvailable = true; // TODO: Update Checker
				plugin.UColdVers = updater.oldVersion();
				plugin.UCnewVers = updater.newVersion();
				
				log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				log(Level.WARNING, "* " + plugin.get("neg.version.message").toString().replace("<MyPlugin>", plugin.getName()) );
				log(Level.WARNING, "* " + plugin.get("neg.version.old_vers") + ChatColor.RED + " " + plugin.UColdVers );
				log(Level.WARNING, "* " + plugin.get("neg.version.new_vers") + ChatColor.GREEN + " " + plugin.UCnewVers );
				log(Level.WARNING, "* " + plugin.get("neg.version.notes") + ": " + updater.newVersionNotes());
				log(Level.WARNING, "* " + plugin.get("neg.version.please_update") );
				log(Level.WARNING, "*");
				log(Level.WARNING, "* " + plugin.get("neg.version.download") + ": " + updater.getDownloadLink());
				log(Level.WARNING, "* " + plugin.get("neg.version.donate") + ": https://ko-fi.com/joelgodofwar");
				log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
				//Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
			}else{
				log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				log(Level.WARNING, "* " + updater.getRecommendedVersion());
				log(Level.WARNING, "* " + plugin.get("neg.version.donate") + ": https://ko-fi.com/joelgodofwar");
				log(Level.WARNING, "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				plugin.UpdateAvailable = false;
			}
			return true;
		}catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
			e.printStackTrace();
			return false;
		}
		
	}
	public static void log(Level lvl, String string) {
		plugin.log(lvl, string);
	}
}
