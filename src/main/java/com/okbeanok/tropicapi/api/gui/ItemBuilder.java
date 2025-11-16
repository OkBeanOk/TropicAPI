package com.okbeanok.tropicapi.api.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building ItemStacks with a fluent API.
 */
public class ItemBuilder {
	private final ItemStack item;
	private final ItemMeta meta;

	public ItemBuilder(Material material) {
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
	}

	public ItemBuilder(ItemStack item) {
		this.item = item.clone();
		this.meta = item.getItemMeta();
	}

	public ItemBuilder amount(int amount) {
		item.setAmount(amount);
		return this;
	}

	public ItemBuilder name(String name) {
		if (meta != null) {
			meta.setDisplayName(ColorAPI.process(name));
		}
		return this;
	}

	public ItemBuilder lore(String... lore) {
		if (meta != null) {
			List<String> loreList = new ArrayList<>();
			for (String line : lore) {
				loreList.add(ColorAPI.process(line));
			}
			meta.setLore(loreList);
		}
		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		if (meta != null) {
			List<String> processedLore = new ArrayList<>();
			for (String line : lore) {
				processedLore.add(ColorAPI.process(line));
			}
			meta.setLore(processedLore);
		}
		return this;
	}

	public ItemBuilder addLoreLine(String line) {
		if (meta != null) {
			List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
			lore.add(ColorAPI.process(line));
			meta.setLore(lore);
		}
		return this;
	}

	public ItemBuilder enchant(Enchantment enchantment, int level) {
		if (meta != null) {
			meta.addEnchant(enchantment, level, true);
		}
		return this;
	}

	public ItemBuilder glow() {
		if (meta != null) {
			meta.addEnchant(Enchantment.UNBREAKING, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		return this;
	}

	public ItemBuilder flags(ItemFlag... flags) {
		if (meta != null) {
			meta.addItemFlags(flags);
		}
		return this;
	}

	public ItemBuilder unbreakable() {
		if (meta != null) {
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		return this;
	}

	public ItemStack build() {
		item.setItemMeta(meta);
		return item;
	}
}
