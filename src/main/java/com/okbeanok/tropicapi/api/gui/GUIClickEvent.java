package com.okbeanok.tropicapi.api.gui;

import lombok.Value;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Simple wrapper around an inventory click, used by GUIService.
 */
@Value
public class GUIClickEvent {
	Player player;
	Inventory inventory;
	int slot;
	ClickType clickType;
	ItemStack clickedItem;

	public void setCancelled(boolean b) {

	}
}
