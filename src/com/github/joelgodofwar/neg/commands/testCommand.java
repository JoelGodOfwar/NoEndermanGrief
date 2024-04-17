package com.github.joelgodofwar.neg.commands;

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
			VersionChecker updater = new VersionChecker(plugin, plugin.projectID, plugin.githubURL);
			if(updater.checkForUpdates()) { // TODO: Update Checker
				/** Update available */
				plugin.UpdateAvailable = true;
				plugin.UColdVers = updater.oldVersion();
				plugin.UCnewVers = updater.newVersion();

				log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				log("* " + plugin.get("neg.version.message").toString().replace("<MyPlugin>", plugin.getName()) );
				log("* " + plugin.get("neg.version.old_vers") + ChatColor.RED + " " + plugin.UColdVers );
				log("* " + plugin.get("neg.version.new_vers") + ChatColor.GREEN + " " + plugin.UCnewVers );
				log("* " + plugin.get("neg.version.notes") + ": " + updater.newVersionNotes());
				log("* " + plugin.get("neg.version.please_update") );
				log("*");
				log("* " + plugin.get("neg.version.download") + ": " + updater.getDownloadLink());
				log("* " + plugin.get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
				log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				//Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
				//Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
			}else{
				/** Up to date */
				log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				log("* " + updater.getRecommendedVersion());
				log("* " + plugin.get("neg.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
				log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				plugin.UpdateAvailable = false;
			}
			return true;
		}catch(Exception e) {
			/** Error */
			log(ChatColor.RED + "Could not process update check");
			e.printStackTrace();
			return false;
		}


	}
	public static void log(String string) {
		plugin.LOGGER.log(string);
	}
}
