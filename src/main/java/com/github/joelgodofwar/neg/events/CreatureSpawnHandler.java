package com.github.joelgodofwar.neg.events;

import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.Report;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;


import com.github.joelgodofwar.neg.NoEndermanGrief;

public class CreatureSpawnHandler implements Listener {

    private final NoEndermanGrief plugin;

    public CreatureSpawnHandler(NoEndermanGrief plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) { //onEntitySpawn(EntitySpawnEvent e) {
        Entity entity = event.getEntity();
        try {
            if (entity instanceof SkeletonHorse) {
                if (plugin.config.getBoolean("skeleton_horse_spawn", true)) {
                    plugin.LOGGER.debug(plugin.get("neg.entity.skeleton_horse") + event.getLocation());
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_SKELETON_HORSE_GRIEF).error(exception));
        }
        try {
            if (entity instanceof WanderingTrader) {
                if (plugin.config.getBoolean("wandering_trader_spawn", true)) {
                    plugin.LOGGER.debug(plugin.get("neg.entity.wandering_trader") + event.getLocation());
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_WANDERING_TRADER_GRIEF).error(exception));
        }
        try {
            if (entity instanceof Phantom) {
                if (plugin.config.getBoolean("phantom_spawn", true)) {
                    plugin.LOGGER.debug(plugin.get("neg.entity.phantom") + event.getLocation());
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_PHANTOM_GRIEF).error(exception));
        }
        try {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.PATROL) {
                if (plugin.config.getBoolean("pillager_patrol_spawn", true)) {
                    plugin.LOGGER.debug(plugin.get("neg.entity.pillager_patrol") + event.getLocation());
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_PILLAGER_PATROL_GRIEF).error(exception));
        }
    }
}