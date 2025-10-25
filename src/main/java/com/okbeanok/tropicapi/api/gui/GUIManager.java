package com.okbeanok.tropicapi.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all GUI instances and handles their events.
 */
public class GUIManager implements Listener {
	private final Map<UUID, GUI> openGUIs;

	public GUIManager() {
		this.openGUIs = new HashMap<>();
	}

	public void registerGUI(Player player, GUI gui) {
		openGUIs.put(player.getUniqueId(), gui);
	}

	public void unregisterGUI(Player player) {
		openGUIs.remove(player.getUniqueId());
	}

	public GUI getGUI(Player player) {
		return openGUIs.get(player.getUniqueId());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) {
			return;
		}

		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof GUI gui) {
			gui.onClick(event);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player player)) {
			return;
		}

		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof GUI gui) {
			gui.onClose(event);
			unregisterGUI(player);
		}
	}
}
