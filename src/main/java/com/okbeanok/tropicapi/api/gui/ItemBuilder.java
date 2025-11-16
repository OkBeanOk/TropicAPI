package com.okbeanok.tropicapi.api.gui;

import com.okbeanok.tropicapi.api.color.ColorAPI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ItemBuilder {

	private final ItemStack item;
	private final ItemMeta meta;
	private final List<String> lore = new ArrayList<>();

	private ItemBuilder(Material material) {
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
	}

	public static ItemBuilder of(Material material) {
		return new ItemBuilder(material);
	}

	/**
	 * Sets the display name, automatically colorizing (&, hex, gradients).
	 */
	public ItemBuilder name(String displayName) {
		if (displayName != null) {
			meta.setDisplayName(ColorAPI.color(displayName));
		}
		return this;
	}

	/**
	 * Sets the display name as-is, without automatic colorizing.
	 */
	public ItemBuilder nameRaw(String displayName) {
		meta.setDisplayName(displayName);
		return this;
	}

	/**
	 * Adds lore lines, automatically colorizing each one.
	 */
	public ItemBuilder lore(String... lines) {
		if (lines != null) {
			lore.addAll(Arrays.asList(ColorAPI.color(lines)));
		}
		return this;
	}

	/**
	 * Adds lore lines, automatically colorizing each one.
	 */
	public ItemBuilder lore(List<String> lines) {
		if (lines != null && !lines.isEmpty()) {
			lore.addAll(Arrays.asList(ColorAPI.color(lines.toArray(new String[0]))));
		}
		return this;
	}

	/**
	 * Adds lore lines without automatic colorizing.
	 */
	public ItemBuilder loreRaw(String... lines) {
		if (lines != null) {
			lore.addAll(Arrays.asList(lines));
		}
		return this;
	}

	public ItemBuilder addFlag(ItemFlag flag) {
		meta.addItemFlags(flag);
		return this;
	}

	public ItemBuilder unbreakable(boolean unbreakable) {
		meta.setUnbreakable(unbreakable);
		return this;
	}

	public ItemBuilder glow() {
		meta.addEnchant(Enchantment.UNBREAKING, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public ItemStack build() {
		if (!lore.isEmpty()) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
}
