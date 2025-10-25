package com.okbeanok.tropicapi.api.gui;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a clickable button in a GUI.
 */
public class GUIButton {
	@Getter
	private final ItemStack item;
	private final ClickAction action;

	public GUIButton(ItemStack item, ClickAction action) {
		this.item = item;
		this.action = action;
	}

	public GUIButton(ItemStack item) {
		this(item, null);
	}

	public void onClick(InventoryClickEvent event) {
		if (action != null) {
			action.execute(event);
		}
	}

	@FunctionalInterface
	public interface ClickAction {
		void execute(InventoryClickEvent event);
	}
}
