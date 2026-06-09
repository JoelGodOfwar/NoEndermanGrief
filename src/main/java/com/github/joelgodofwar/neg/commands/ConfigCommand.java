package com.github.joelgodofwar.neg.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.joelgodofwar.neg.NoEndermanGrief;

import com.github.joelgodofwar.neg.gui.InventoryGUI;
import com.github.joelgodofwar.neg.gui.Language;
import com.github.joelgodofwar.neg.gui.Mob;

public class ConfigCommand {
	private final NoEndermanGrief neg;
	private PluginLogger LOGGER;

	public ConfigCommand(NoEndermanGrief plugin) {
		this.neg = plugin;
		this.LOGGER = neg.LOGGER;
	}

	/**
	 * Executes the config command, opening a GUI for the player to manage settings.
	 *
	 * @param player the player executing the command
	 * @param args   command arguments (ignored for this GUI)
	 * @return true if the command was executed successfully, false otherwise
	 */
	public boolean execute(Player player, String[] args) {
		// Load config.yml directly from file on each command execution
		File configFile = new File(neg.getDataFolder(), "config.yml");
		try {
			neg.config.load(configFile); // Load custom config from file every time
			// Log loaded values to verify
			LOGGER.debug("Loaded config.yml");
			LOGGER.debug("auto_update_check: " + neg.config.getBoolean("auto_update_check", true));
			LOGGER.debug("debug: " + neg.config.getBoolean("debug", false));
			LOGGER.debug("lang: " + neg.config.getString("lang", "en_US"));
			LOGGER.debug("console.colorful_console: " + neg.config.getBoolean("console.colorful_console", true));
			LOGGER.debug("console.longpluginname: " + neg.config.getBoolean("console.longpluginname", true));
			LOGGER.debug("enderman_grief: " + neg.config.getBoolean("enderman_grief", true));
			LOGGER.debug("skeleton_horse_spawn: " + neg.config.getBoolean("skeleton_horse_spawn", true));
			LOGGER.debug("creeper_grief: " + neg.config.getBoolean("creeper_grief", true));
			LOGGER.debug("wandering_trader_spawn: " + neg.config.getBoolean("wandering_trader_spawn", true));
			LOGGER.debug("ghast_grief: " + neg.config.getBoolean("ghast_grief", true));
			LOGGER.debug("phantom_spawn: " + neg.config.getBoolean("phantom_spawn", true));
			LOGGER.debug("pillager_patrol_spawn: " + neg.config.getBoolean("pillager_patrol_spawn", true));
		} catch (Exception e) {
			neg.getLogger().severe("Failed to load config.yml: " + e.getMessage());
			player.sendMessage(ChatColor.RED + "Error loading config.yml. Check server logs.");
			return false;
		}

		// Initialize pending changes for Save/Cancel
		Map<String, Object> pendingChanges = new HashMap<>();

		// Create and open main GUI
		BiConsumer<Player, ItemStack> mainCallback = createMainCallback(pendingChanges);
		InventoryGUI gui = new InventoryGUI("NoEndermanGrief Settings", new ArrayList<>(createSlotAssignments(pendingChanges).keySet()), mainCallback);
		gui.setForcePreviousButton(false); // Single page
		gui.openWithSlots(player, createSlotAssignments(pendingChanges), mainCallback);

		return true;
	}

	/**
	 * Creates the slot assignments for the main GUI based on the current pending changes.
	 *
	 * @param pendingChanges the map of pending configuration changes
	 * @return a map of ItemStacks to their slot positions
	 */
	private Map<ItemStack, Integer> createSlotAssignments(Map<String, Object> pendingChanges) {
		Map<String, String[]> settingComments = new HashMap<>();
		settingComments.put("auto_update_check", new String[]{"Change to false to stop", "auto-update-check"});
		settingComments.put("debug", new String[]{"Set to true before sending a log", "about an issue.", "Logs trace data required to", "pinpoint where errors are."});
		settingComments.put("lang", new String[]{"Click to select a language", "from available options"});
		settingComments.put("console.colorful_console", new String[]{"Enables fancy ANSI colors in", "console. (Disable if you're", "getting weird characters in", "the console)"});
		settingComments.put("console.longpluginname", new String[]{"Should NEG use its full name", "or NEG in console messages?"});
		settingComments.put("enderman_grief", new String[]{"Set to true to prevent", "Enderman from picking up", "blocks, false to allow"});
		settingComments.put("skeleton_horse_spawn", new String[]{"Set to true to prevent", "Skeleton Horses from", "spawning, false to allow"});
		settingComments.put("creeper_grief", new String[]{"Set to true to prevent", "Creeper explosions from", "destroying blocks, false to", "allow"});
		settingComments.put("wandering_trader_spawn", new String[]{"Set to true to prevent", "Wandering Traders from", "spawning, false to allow"});
		settingComments.put("ghast_grief", new String[]{"Set to true to prevent Ghast", "fireball explosions from", "destroying blocks, false to", "allow"});
		settingComments.put("phantom_spawn", new String[]{"Set to true to prevent", "Phantoms from spawning,", "false to allow"});
		settingComments.put("pillager_patrol_spawn", new String[]{"Set to true to prevent", "Pillager Patrols from", "spawning, false to allow"});

		Map<ItemStack, Integer> slotAssignments = new HashMap<>();

		// Top row: auto_update_check, debug, lang
		slotAssignments.put(createSettingItem("auto_update_check", settingComments.get("auto_update_check"), pendingChanges), 0);
		slotAssignments.put(createSettingItem("debug", settingComments.get("debug"), pendingChanges), 1);
		slotAssignments.put(createLangItem("lang", settingComments.get("lang"), pendingChanges), 2);

		// Second row: console settings
		slotAssignments.put(createSettingItem("console.colorful_console", settingComments.get("console.colorful_console"), pendingChanges), 9);
		slotAssignments.put(createSettingItem("console.longpluginname", settingComments.get("console.longpluginname"), pendingChanges), 10);

		// Mob settings with heads and panes
		slotAssignments.put(Mob.CREEPER.getHead(), 18);
		slotAssignments.put(createSettingItem("creeper_grief", settingComments.get("creeper_grief"), pendingChanges), 19);
		slotAssignments.put(Mob.ENDERMAN.getHead(), 20);
		slotAssignments.put(createSettingItem("enderman_grief", settingComments.get("enderman_grief"), pendingChanges), 21);
		slotAssignments.put(Mob.GHAST.getHead(), 22);
		slotAssignments.put(createSettingItem("ghast_grief", settingComments.get("ghast_grief"), pendingChanges), 23);
		slotAssignments.put(Mob.PHANTOM.getHead(), 24);
		slotAssignments.put(createSettingItem("phantom_spawn", settingComments.get("phantom_spawn"), pendingChanges), 25);
		slotAssignments.put(Mob.PILLAGER.getHead(), 26);
		slotAssignments.put(createSettingItem("pillager_patrol_spawn", settingComments.get("pillager_patrol_spawn"), pendingChanges), 27);
		slotAssignments.put(Mob.SKELETON_HORSE.getHead(), 28);
		slotAssignments.put(createSettingItem("skeleton_horse_spawn", settingComments.get("skeleton_horse_spawn"), pendingChanges), 29);
		slotAssignments.put(Mob.WANDERING_TRADER.getHead(), 30);
		slotAssignments.put(createSettingItem("wandering_trader_spawn", settingComments.get("wandering_trader_spawn"), pendingChanges), 31);

		// Add Save button (moved to slot 51)
		ItemStack saveButton = new ItemStack(Material.SLIME_BLOCK);
		ItemMeta saveMeta = saveButton.getItemMeta();
		saveMeta.setDisplayName(ChatColor.GREEN + "Save");
		saveMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Save changes to config.yml"));
		saveButton.setItemMeta(saveMeta);
		slotAssignments.put(saveButton, 51);

		return slotAssignments;
	}

	/**
	 * Creates the callback for the main GUI, handling clicks and updates.
	 *
	 * @param pendingChanges the map of pending configuration changes
	 * @return the BiConsumer callback for the main GUI
	 */
	private BiConsumer<Player, ItemStack> createMainCallback(Map<String, Object> pendingChanges) {
		return (p, item) -> {
			if ((item == null) || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
				return;
			}
			String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			if (displayName.equals("Save")) {
				// Apply pending changes to neg.config
				pendingChanges.forEach((key, value) -> {
					if (key.contains(".")) {
						// Handle nested keys like console.colorful_console
						neg.config.set(key, value);
					} else {
						neg.config.set(key, value);
					}
				});
				try {
					File configFile = new File(neg.getDataFolder(), "config.yml");
					neg.config.save(configFile); // Use custom config to preserve comments
					p.sendMessage(ChatColor.GREEN + "Configuration saved!");
				} catch (Exception e) {
					neg.getLogger().severe("Failed to save config: " + e.getMessage());
				}
				p.closeInventory();
			} else if (displayName.startsWith("lang:")) {
				// Open language sub-GUI
				openLanguageSubGUI(p, pendingChanges);
			} else {
				String settingKey = getSettingKeyFromDisplayName(displayName);
				if ((settingKey != null) && !settingKey.equals("lang")) {
					// Toggle boolean setting
					boolean currentValue = (boolean) pendingChanges.getOrDefault(settingKey, neg.config.getBoolean(settingKey, false));
					pendingChanges.put(settingKey, !currentValue);
					p.sendMessage(ChatColor.GREEN + settingKey + " set to: " + !currentValue + " (click Save to apply)");
					// Update main GUI with new slot assignments
					InventoryGUI gui = new InventoryGUI("NoEndermanGrief Settings", new ArrayList<>(createSlotAssignments(pendingChanges).keySet()), createMainCallback(pendingChanges));
					gui.setForcePreviousButton(false);
					gui.openWithSlots(p, createSlotAssignments(pendingChanges), createMainCallback(pendingChanges));
				}
			}
		};
	}

	/**
	 * Opens a sub-GUI for language selection with 20 language heads, sorted by English name.
	 *
	 * @param player the player to open the GUI for
	 * @param pendingChanges the map of pending changes to pass through
	 */
	private void openLanguageSubGUI(Player player, Map<String, Object> pendingChanges) {
		Map<ItemStack, Integer> langSlotAssignments = new HashMap<>();
		// Sort languages by langNameInEnglish
		List<Language> sortedLanguages = new ArrayList<>(Arrays.asList(Language.values()));
		sortedLanguages.sort(Comparator.comparing(Language::getLangNameInEnglish));
		// Alternative: Sort by langNameInLang
		// sortedLanguages.sort(Comparator.comparing(Language::getLangNameInLang));

		for (int i = 0; i < sortedLanguages.size(); i++) {
			Language lang = sortedLanguages.get(i);
			ItemStack head = lang.getHead();
			ItemMeta meta = head.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + lang.getLangNameInEnglish());
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.AQUA + "Language: " + lang.getLangNameInLang());
			lore.add(ChatColor.YELLOW + "Click to select");
			meta.setLore(lore);
			head.setItemMeta(meta);
			langSlotAssignments.put(head, i); // Slots 0-19 for 20 languages
		}

		// Add Previous button (moved to slot 47)
		ItemStack previousButton = new ItemStack(Material.ARROW);
		ItemMeta previousMeta = previousButton.getItemMeta();
		previousMeta.setDisplayName(ChatColor.YELLOW + "Previous");
		previousMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Return to main menu"));
		previousButton.setItemMeta(previousMeta);
		langSlotAssignments.put(previousButton, 47);

		BiConsumer<Player, ItemStack> langCallback = (p, item) -> {
			if ((item == null) || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
				return;
			}
			String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			if (displayName.equals("Previous")) {
				// Return to main GUI with existing pendingChanges
				InventoryGUI gui = new InventoryGUI("NoEndermanGrief Settings", new ArrayList<>(createSlotAssignments(pendingChanges).keySet()), createMainCallback(pendingChanges));
				gui.setForcePreviousButton(false);
				gui.openWithSlots(p, createSlotAssignments(pendingChanges), createMainCallback(pendingChanges));
			} else {
				// Find the language by display name
				for (Language lang : Language.values()) {
					if (lang.getLangNameInEnglish().equals(displayName)) {
						pendingChanges.put("lang", lang.getLangCode());
						p.sendMessage(ChatColor.GREEN + "Language set to: " + lang.getLangNameInEnglish() + " (click Save in main menu)");
						// Return to main GUI with updated pendingChanges
						InventoryGUI gui = new InventoryGUI("NoEndermanGrief Settings", new ArrayList<>(createSlotAssignments(pendingChanges).keySet()), createMainCallback(pendingChanges));
						gui.setForcePreviousButton(false);
						gui.openWithSlots(p, createSlotAssignments(pendingChanges), createMainCallback(pendingChanges));
						return;
					}
				}
			}
		};

		InventoryGUI langGui = new InventoryGUI("Select Language", new ArrayList<>(langSlotAssignments.keySet()), langCallback);
		langGui.setForcePreviousButton(false); // Single page
		langGui.openWithSlots(player, langSlotAssignments, langCallback);
	}

	/**
	 * Creates an ItemStack for a boolean setting, using glass panes to indicate state.
	 *
	 * @param key     the config key (e.g., "auto_update_check")
	 * @param comments the YAML comments to use as lore
	 * @param pendingChanges the map of pending changes
	 * @return the ItemStack with appropriate material and lore
	 */
	private ItemStack createSettingItem(String key, String[] comments, Map<String, Object> pendingChanges) {
		boolean value = (boolean) pendingChanges.getOrDefault(key, neg.config.getBoolean(key, false));
		boolean isSet = pendingChanges.containsKey(key) || neg.config.isSet(key);
		Material material = isSet ? (value ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE) : Material.GRAY_STAINED_GLASS_PANE;
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + key + ": " + (isSet ? value : "Unset"));
		List<String> lore = new ArrayList<>();
		for (String comment : comments) {
			if (comment.contains("true =") || comment.contains("false =")) {
				lore.add(ChatColor.UNDERLINE + "" + ChatColor.YELLOW + comment);
			} else {
				lore.add(ChatColor.AQUA + comment);
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Creates an ItemStack for the lang setting, using a book to open the sub-GUI.
	 *
	 * @param key     the config key ("lang")
	 * @param comments the YAML comments to use as lore
	 * @param pendingChanges the map of pending changes
	 * @return the ItemStack with book material and lore
	 */
	private ItemStack createLangItem(String key, String[] comments, Map<String, Object> pendingChanges) {
		String value = (String) pendingChanges.getOrDefault(key, neg.config.getString(key, "en_US"));
		boolean isSet = pendingChanges.containsKey(key) || neg.config.isSet(key);
		ItemStack item = new ItemStack(Material.BOOK);
		ItemMeta meta = item.getItemMeta();
		Language lang = Language.getByLangCode(value);
		String display = lang != null ? lang.getLangNameInEnglish() : value;
		meta.setDisplayName(ChatColor.YELLOW + key + ": " + (isSet ? display : "Unset"));
		List<String> lore = new ArrayList<>();
		for (String comment : comments) {
			lore.add(ChatColor.AQUA + comment);
		}
		if (lang != null) {
			lore.add(ChatColor.AQUA + "Current: " + lang.getLangNameInLang());
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Maps display name to config key for callback handling.
	 *
	 * @param displayName the display name of the clicked item
	 * @return the corresponding config key, or null if not found
	 */
	private String getSettingKeyFromDisplayName(String displayName) {
		if (displayName.contains(":")) {
			return displayName.substring(0, displayName.indexOf(':')).trim();
		}
		return null;
	}
}