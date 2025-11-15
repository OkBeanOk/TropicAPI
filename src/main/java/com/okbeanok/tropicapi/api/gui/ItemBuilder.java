package com.okbeanok.tropicapi.api.gui;

import org.bukkit.Material;
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

	public ItemBuilder name(String displayName) {
		meta.setDisplayName(displayName);
		return this;
	}

	public ItemBuilder lore(String... lines) {
		lore.addAll(Arrays.asList(lines));
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

	public ItemStack build() {
		if (!lore.isEmpty()) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
}
