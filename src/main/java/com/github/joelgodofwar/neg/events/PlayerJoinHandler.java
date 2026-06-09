package com.github.joelgodofwar.neg.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.joelgodofwar.neg.NoEndermanGrief;

public class PlayerJoinHandler implements Listener {

    private final NoEndermanGrief plugin;

    public PlayerJoinHandler(NoEndermanGrief plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.UpdateAvailable && (player.isOp() || player.hasPermission("noendermangrief.showUpdateAvailable") || player.hasPermission("noendermangrief.admin"))) {
            String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
            links = links.replace("<DownloadLink>", plugin.DownloadLink).replace("<Download>", plugin.get("neg.version.download"))
                    .replace("<Donate>", plugin.get("neg.version.donate")).replace("<please_update>", plugin.get("neg.version.please_update"))
                    .replace("<Donate_msg>", plugin.get("neg.version.donate.message")).replace("<Notes>", plugin.get("neg.version.notes"))
                    .replace("<Notes_msg>", plugin.get("neg.version.notes.message"));
            String versions = ChatColor.GRAY + plugin.get("neg.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + plugin.get("neg.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
            player.sendMessage(ChatColor.GRAY + plugin.get("neg.version.message").replace("<MyPlugin>", ChatColor.GOLD + NoEndermanGrief.THIS_NAME + ChatColor.GRAY));
            plugin.jsonMessageUtils.sendJsonMessage(player, links);
            player.sendMessage(versions.replace("{nVers}", plugin.UCnewVers).replace("{oVers}", plugin.UColdVers));
        }

        if (player.getDisplayName().equals("JoelYahwehOfWar") || player.getDisplayName().equals("JoelGodOfWar")) {
            player.sendMessage(NoEndermanGrief.THIS_NAME + " " + NoEndermanGrief.THIS_VERSION + " Hello father!");
        }
    }
}