package com.github.joelgodofwar.neg.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public enum Mob {
	CREEPER(
			"creeper",	"Creeper",
			"c66c91fd-6fb5-414f-b70e-39c19edf3d28",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVhZjg1MmMyYjczOGU5ODY0OThlZGFlYmZlMGZmZWE5ZTkxM2ZkNGJhZmIyNWJlYmZlZDVhYjNmZGFmYWY3NCJ9fX0=",
			new ArrayList<>(),
			"entity.creeper.primed"
			),
	ENDERMAN(
			"enderman",	"Enderman",
			"e229ba57-ec25-4501-87cb-af52d6ee7497",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=",
			new ArrayList<>(),
			"entity.enderman.ambient"
			),
	GHAST(
			"ghast",	"Ghast",
			"7af3876e-0427-45c5-97ae-7119688cdecf",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUzZGUzMWEyZDAwNDFhNmVmNzViZjdhNmM4NDY4NDY0ZGIxYWFhNjIwMWViYjFhNjAxM2VkYjIyNDVjNzYwNyJ9fX0=",
			new ArrayList<>(),
			"entity.ghast.ambient"
			),
	PHANTOM(
			"phantom",	"Phantom",
			"385f1bf3-12d8-4246-87fc-2622a415a312",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQ2ODMwZGE1ZjgzYTNhYWVkODM4YTk5MTU2YWQ3ODFhNzg5Y2ZjZjEzZTI1YmVlZjdmNTRhODZlNGZhNCJ9fX0=",
			new ArrayList<>(),
			"entity.phantom.ambient"
			),
	PILLAGER(
			"pillager",	"Pillager",
			"c64b8af5-b547-4a15-abf9-12d3eb052f37",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIyNWYwYjQ5YzUyOTUwNDhhNDA5YzljNjAxY2NhNzlhYThlYjUyYWZmNWUyMDMzZWJiODY1ZjQzNjdlZjQzZSJ9fX0=",
			new ArrayList<>(),
			"entity.pillager.ambient"
			),
	SKELETON_HORSE(
			"skeleton_horse",	"Skeleton Horse",
			"3d84a760-800d-4f87-a537-18446aad8623",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUyMjY3MDViZDJhOWU3YmI4ZDZiMGY0ZGFhOTY5YjllMTJkNGFlNWM2NmRhNjkzYmI1ZjRhNGExZTZhYTI5NiJ9fX0=",
			new ArrayList<>(),
			"entity.skeleton_horse.ambient"
			),
	WANDERING_TRADER(
			"wandering_trader",	"Wandering Trader",
			"70aaec20-d989-4e06-857d-285ad2dca337",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=",
			new ArrayList<>(),
			"entity.wandering_trader.ambient"
			);

	private final String langName;
	private final String displayName;
	private final String uuid;
	private final String texture;
	private final ArrayList<String> lore;
	private final String noteblockSound;
	private static final Map<String, Mob> BY_LANG_NAME = new HashMap<>();

	// Static block to populate the lookup map
	static {
		for (Mob mob : values()) {
			BY_LANG_NAME.put(mob.langName.toUpperCase(), mob);
		}
	}

	Mob(String langName, String displayName, String uuid, String texture, ArrayList<String> lore, String noteblockSound) {
		this.langName = langName;
		this.displayName = displayName;
		this.uuid = uuid;
		this.texture = texture;
		this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
		this.noteblockSound = noteblockSound;
	}

	public String getLangName() {
		return langName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getUuid() {
		return uuid;
	}

	public String getTexture() {
		return texture;
	}

	public ArrayList<String> getLore() {
		return new ArrayList<>(lore);
	}

	public String getNoteblockSound() {
		return noteblockSound;
	}

	/**
	 * Returns an ItemStack representing the custom player head for this mob.
	 *
	 * @return the configured ItemStack with the mob's head texture
	 */
	public ItemStack getHead() {
		return HeadUtils.makeHead(displayName, texture, uuid, new ArrayList<>(lore), noteblockSound);
	}

	/**
	 * Looks up a Mob by its langName.
	 *
	 * @param langName the language name (e.g., "creeper") to look up
	 * @return the corresponding Mob enum, or null if not found
	 */
	public static Mob getByLangName(String langName) {
		if (langName == null) {
			return null;
		}
		return BY_LANG_NAME.get(langName.toUpperCase());
	}
}