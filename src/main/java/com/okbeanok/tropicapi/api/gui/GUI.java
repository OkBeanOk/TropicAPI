package com.okbeanok.tropicapi.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for creating interactive GUIs.
 */
public abstract class GUI implements InventoryHolder {
	protected final Player player;
	protected final String title;
	protected final int size;
	protected final Inventory inventory;
	protected final Map<Integer, GUIButton> buttons;
	protected boolean cancelAllClicks;

	public GUI(Player player, String title, int rows) {
		this.player = player;
		this.title = title;
		this.size = rows * 9;
		this.inventory = Bukkit.createInventory(this, size, title);
		this.buttons = new HashMap<>();
		this.cancelAllClicks = true;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Sets a button at the specified slot.
	 */
	public void setButton(int slot, GUIButton button) {
		if (slot >= 0 && slot < size) {
			buttons.put(slot, button);
			inventory.setItem(slot, button.getItem());
		}
	}

	/**
	 * Sets an item at the specified slot without a button action.
	 */
	public void setItem(int slot, ItemStack item) {
		if (slot >= 0 && slot < size) {
			inventory.setItem(slot, item);
		}
	}

	/**
	 * Fills empty slots with the specified item.
	 */
	public void fillEmpty(ItemStack item) {
		for (int i = 0; i < size; i++) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, item);
			}
		}
	}

	/**
	 * Opens the GUI for the player.
	 */
	public void open() {
		build();
		player.openInventory(inventory);
	}

	/**
	 * Refreshes the GUI content.
	 */
	public void refresh() {
		inventory.clear();
		buttons.clear();
		build();
	}

	/**
	 * Called when the GUI is built. Override to add items and buttons.
	 */
	protected abstract void build();

	/**
	 * Called when a slot is clicked.
	 */
	public void onClick(InventoryClickEvent event) {
		if (cancelAllClicks) {
			event.setCancelled(true);
		}

		int slot = event.getRawSlot();
		if (slot >= 0 && slot < size) {
			GUIButton button = buttons.get(slot);
			if (button != null) {
				button.onClick(event);
			}
		}
	}

	/**
	 * Called when the inventory is closed.
	 */
	public void onClose(InventoryCloseEvent event) {
		// Override if needed
	}

	public Player getPlayer() {
		return player;
	}

	public String getTitle() {
		return title;
	}
}