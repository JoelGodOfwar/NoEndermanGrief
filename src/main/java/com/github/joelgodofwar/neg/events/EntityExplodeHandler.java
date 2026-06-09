package com.github.joelgodofwar.neg.events;

import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.Report;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.joelgodofwar.neg.NoEndermanGrief;

public class EntityExplodeHandler implements Listener {

    private final NoEndermanGrief plugin;

    public EntityExplodeHandler(NoEndermanGrief plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent entity) {
        try {
            if (entity.getEntity().getType() == EntityType.CREEPER) {
                entity.setCancelled(plugin.config.getBoolean("creeper_grief", true));

                plugin.LOGGER.debug(plugin.get("neg.entity.creeper.explode") + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockZ());
                return;
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_CREEPER_GRIEF).error(exception));
        }
        try {
            if ((entity.getEntity().getType() == EntityType.FIREBALL) && (((Fireball) entity.getEntity()).getShooter() instanceof Ghast)) {
                if (plugin.config.getBoolean("ghast_grief", true)) {
                    Entity fireball = entity.getEntity();
                    ((Fireball) fireball).setIsIncendiary(false);
                    ((Fireball) fireball).setYield(0F);
                    entity.setCancelled(true);
                }
                plugin.LOGGER.debug(plugin.get("neg.entity.ghast.explode") + entity.getLocation().getBlockX() + ", " + entity.getLocation().getBlockZ());
                return;
            }
        } catch (Exception exception) {
            NoEndermanGrief.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_GHAST_GRIEF).error(exception));
        }
    }
}