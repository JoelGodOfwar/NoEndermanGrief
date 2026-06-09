package com.github.joelgodofwar.neg.events;

import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.Report;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.joelgodofwar.neg.NoEndermanGrief; // Adjust the import path if the main class is in a different package (e.g., com.github.joelgodofwar.neg)
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlockHandler implements Listener {

    private final NoEndermanGrief plugin;

    public EntityChangeBlockHandler(NoEndermanGrief plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // if (!tr.isrunworld(ac.getName(), e.getEntity().getLocation().getWorld().getName()) )
        //return;
        try {
            if (event.getEntity() == null) {
                return;
            }
            if (event.getEntity().getType() == EntityType.ENDERMAN) {
                Block block = event.getBlock();
                plugin.LOGGER.debug("ECBE Block = " + block.getType().toString());
                BlockData blockData = event.getBlockData();
                plugin.LOGGER.debug("ECBE BlockData = " + blockData.getAsString());

                event.setCancelled(plugin.config.getBoolean("enderman_grief", true));

                plugin.LOGGER.debug(plugin.get("neg.entity.enderman.pickup") + event.getBlock().getType() + " at " + event.getBlock().getLocation());
                return;
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_ENDERMAN_GRIEF).error(exception));
        }
    }
}