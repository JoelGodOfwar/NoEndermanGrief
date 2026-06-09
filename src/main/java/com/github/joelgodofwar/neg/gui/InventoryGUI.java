package com.github.joelgodofwar.neg.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.joelgodofwar.neg.NoEndermanGrief;

public class InventoryGUI implements Listener {
	protected final String title;
	protected final List<ItemStack> items;
	private final ItemStack senderHead;
	private final Map<ItemStack, Runnable> choices;
	private final BiConsumer<Player, ItemStack> callback;
	protected final Map<Player, Integer> playerPages = new HashMap<>();
	private final Map<Player, BiConsumer<Player, ItemStack>> playerCallbacks = new HashMap<>();
	protected final Map<Player, Boolean> isNavigating = new HashMap<>();
	@SuppressWarnings("unused") private final String instanceId = java.util.UUID.randomUUID().toString();
	protected static final int ITEMS_PER_PAGE = 45;
	private boolean forcePreviousButton = false;
	private Map<ItemStack, Integer> slotAssignments = new HashMap<>();

	public InventoryGUI(String title, List<ItemStack> items, BiConsumer<Player, ItemStack> callback) {
		this.title = title;
		this.items = new ArrayList<>();
		for (ItemStack item : items) {
			ItemStack clonedItem = item.clone();
			ItemMeta meta = clonedItem.getItemMeta();
			if (meta != null) {
				clonedItem.setItemMeta(meta);
			}
			this.items.add(clonedItem);
		}
		this.senderHead = null;
		this.choices = null;
		this.callback = callback;
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("InventoryGUI created with title: %s, items: %d", title, items.size()));
		}
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("NoEndermanGrief"));
	}

	public InventoryGUI(String title, List<ItemStack> items, ItemStack senderHead) {
		this.title = title;
		this.items = new ArrayList<>();
		for (ItemStack item : items) {
			ItemStack clonedItem = item.clone();
			ItemMeta meta = clonedItem.getItemMeta();
			if (meta != null) {
				clonedItem.setItemMeta(meta);
			}
			this.items.add(clonedItem);
		}
		this.senderHead = senderHead != null ? senderHead.clone() : null;
		this.choices = null;
		this.callback = null;
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("InventoryGUI created with title: %s, senderHead: %b, items: %d", title, senderHead != null, items.size()));
		}
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("NoEndermanGrief"));
	}

	public InventoryGUI(String title, Map<ItemStack, Runnable> choices) {
		this.title = title;
		this.items = new ArrayList<>(choices.keySet());
		this.senderHead = null;
		this.choices = new HashMap<>(choices);
		this.callback = null;
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("InventoryGUI created with title: %s, choices: %d", title, choices.size()));
		}
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("NoEndermanGrief"));
	}

	public void setForcePreviousButton(boolean force) {
		this.forcePreviousButton = force;
	}

	public void open(Player player, BiConsumer<Player, ItemStack> callbackOverride) {
		playerPages.put(player, 0);
		playerCallbacks.put(player, callbackOverride != null ? callbackOverride : callback);
		updateInventory(player);
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("InventoryGUI opened for player: %s, title: %s", player.getName(), title));
		}
	}

	public void openWithSlots(Player player, Map<ItemStack, Integer> slotAssignments, BiConsumer<Player, ItemStack> callbackOverride) {
		playerPages.put(player, 0);
		playerCallbacks.put(player, callbackOverride != null ? callbackOverride : callback);
		this.slotAssignments.clear();
		this.slotAssignments.putAll(slotAssignments);
		this.items.clear();
		this.items.addAll(slotAssignments.keySet());
		updateInventory(player);
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("InventoryGUI opened with slots for player: %s, title: %s, items: %d", player.getName(), title, slotAssignments.size()));
		}
	}

	protected void updateInventory(Player player) {
		int page = playerPages.getOrDefault(player, 0);
		int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
		if ((page < 0) || (page >= totalPages)) {
			page = 0;
			playerPages.put(player, page);
		}

		isNavigating.put(player, true);

		Inventory inventory = Bukkit.createInventory(player, 54, title + " - Page " + (page + 1) + "/" + totalPages);
		/**
		 * Added to define startIndex and endIndex outside the conditional block.
		 * Ensures these variables are available for the log message at the end.
		 */
		int startIndex = page * ITEMS_PER_PAGE;
		int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());
		/**
		 * Modified to clear the inventory with null before placing items.
		 * Applies to both open and openWithSlots cases for consistency.
		 */
		for (int i = 0; i < 54; i++) {
			inventory.setItem(i, null);
		}
		/**
		 * Modified to handle slot assignments when available, falling back to sequential placement.
		 * Moved startIndex and endIndex definition outside to fix the unresolved variable error.
		 */
		if (!slotAssignments.isEmpty()) {
			for (Map.Entry<ItemStack, Integer> entry : slotAssignments.entrySet()) {
				int slot = entry.getValue();
				if ((slot >= 0) && (slot < 54)) {
					inventory.setItem(slot, entry.getKey());
					NoEndermanGrief plugin = NoEndermanGrief.getInstance();
					if ((plugin != null) && (plugin.LOGGER != null)) {
						plugin.LOGGER.debug(String.format("Placed %s in slot %d for player: %s",
								entry.getKey().getItemMeta().getDisplayName(), slot, player.getName()));
					}
				}
			}
		} else {
			for (int i = startIndex; i < endIndex; i++) {
				inventory.setItem(i - startIndex, items.get(i));
			}
		}

		// If conditions add previous button
		// Can we not add a condition that can force adding this?
		// Like conditions or this=true?
		if (forcePreviousButton || ((totalPages > 1) && (page > 0))) {
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			prevMeta.setDisplayName("§ePrevious Page");
			prevButton.setItemMeta(prevMeta);
			inventory.setItem(45, prevButton);
		}
		// If conditions add next button
		if ((totalPages > 1) && (page < (totalPages - 1))) {
			ItemStack nextButton = new ItemStack(Material.ARROW);
			ItemMeta nextMeta = nextButton.getItemMeta();
			nextMeta.setDisplayName("§eNext Page");
			nextButton.setItemMeta(nextMeta);
			inventory.setItem(53, nextButton);
		}

		// Only add Close if not overridden
		if (items.stream().noneMatch(item -> (item != null) && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("§cClose"))) {
			ItemStack closeButton = new ItemStack(Material.BARRIER);
			ItemMeta closeMeta = closeButton.getItemMeta();
			closeMeta.setDisplayName("§cClose");
			closeButton.setItemMeta(closeMeta);
			inventory.setItem(49, closeButton);
			NoEndermanGrief plugin = NoEndermanGrief.getInstance();
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Set default Close in slot 49 for player: %s", player.getName()));
			}
		}

		if (senderHead != null) {
			inventory.setItem(46, senderHead);
		}

		player.openInventory(inventory);
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("Inventory updated for player: %s, page: %d/%d, items: %d", player.getName(), page + 1, totalPages,
					slotAssignments.isEmpty() ? (endIndex - startIndex) : slotAssignments.size()));
		}

		isNavigating.put(player, false);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			NoEndermanGrief plugin = NoEndermanGrief.getInstance();
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug("Click event from non-player, ignoring");
			}
			return;
		}
		Player player = (Player) event.getWhoClicked();
		String viewTitle = event.getView().getTitle();
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("Click event received, title: %s, slot: %d", viewTitle, event.getSlot()));
		}
		if (!viewTitle.startsWith(title + " - Page ") || !isManagedInventory(player, viewTitle)) {
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Click ignored, not managed by this instance: %s", viewTitle));
			}
			return;
		}

		event.setCancelled(true);
		ItemStack clickedItem = event.getCurrentItem();
		if ((clickedItem == null) || !clickedItem.hasItemMeta()) {
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Click on null or meta-less item, slot: %d", event.getSlot()));
			}
			return;
		}

		String displayName = clickedItem.getItemMeta().getDisplayName();
		if (displayName.equals("§ePrevious Page")) {
			playerPages.put(player, playerPages.getOrDefault(player, 0) - 1);
			updateInventory(player);
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("IGUI - Previous page clicked for player: %s", player.getName()));
			}
		} else if (displayName.equals("§eNext Page")) {
			playerPages.put(player, playerPages.getOrDefault(player, 0) + 1);
			updateInventory(player);
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Next page clicked for player: %s", player.getName()));
			}
		} else if (displayName.equals("§cClose")) {
			player.closeInventory();
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Close clicked for player: %s, title: %s", player.getName(), viewTitle));
			}
		} else if (choices != null) {
			// Try finding the item in the choices map
			boolean found = false;
			for (Map.Entry<ItemStack, Runnable> entry : choices.entrySet()) {
				ItemStack key = entry.getKey();
				if (key.isSimilar(clickedItem)) {
					entry.getValue().run();
					if ((plugin != null) && (plugin.LOGGER != null)) {
						plugin.LOGGER.debug(String.format("Choice executed for player: %s, item: %s", player.getName(), clickedItem.getType().name()));
					}
					found = true;
					break;
				}
			}
			if (!found && (plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("No matching choice found for player: %s, item: %s", player.getName(), clickedItem.getType().name()));
			}
		} else {
			BiConsumer<Player, ItemStack> callback = playerCallbacks.get(player);
			if (callback != null) {
				player.closeInventory();
				callback.accept(player, clickedItem);
				if ((plugin != null) && (plugin.LOGGER != null)) {
					plugin.LOGGER.debug(String.format("Callback triggered for player: %s, item: %s", player.getName(), clickedItem.getType().name()));
				}
			} else if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("No callback for player: %s, item: %s", player.getName(), clickedItem.getType().name()));
			}
		}
	}

	protected boolean isManagedInventory(Player player, String viewTitle) {
		int page = playerPages.getOrDefault(player, 0);
		int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
		String expectedTitle = title + " - Page " + (page + 1) + "/" + totalPages;
		boolean isManaged = viewTitle.equals(expectedTitle);
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("Checking managed inventory, expected: %s, actual: %s, result: %b", expectedTitle, viewTitle, isManaged));
		}
		return isManaged;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			NoEndermanGrief plugin = NoEndermanGrief.getInstance();
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug("Close event from non-player, ignoring");
			}
			return;
		}
		Player player = (Player) event.getPlayer();
		String viewTitle = event.getView().getTitle();
		NoEndermanGrief plugin = NoEndermanGrief.getInstance();
		if (!viewTitle.startsWith(title + " - Page ")) {
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Close event ignored, title: %s", viewTitle));
			}
			return;
		}

		if (!isNavigating.getOrDefault(player, false)) {
			playerPages.remove(player);
			playerCallbacks.remove(player);
			isNavigating.remove(player);
			HandlerList.unregisterAll(this);
			if ((plugin != null) && (plugin.LOGGER != null)) {
				plugin.LOGGER.debug(String.format("Inventory closed and unregistered for player: %s, title: %s", player.getName(), viewTitle));
			}
		} else if ((plugin != null) && (plugin.LOGGER != null)) {
			plugin.LOGGER.debug(String.format("Inventory close during navigation, skipping unregister for player: %s", player.getName()));
		}
	}
}