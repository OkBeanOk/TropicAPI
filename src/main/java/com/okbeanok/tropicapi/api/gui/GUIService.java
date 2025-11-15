package com.okbeanok.tropicapi.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

/**
 * Service for creating and managing simple inventory GUIs.
 */
public interface GUIService {

	/**
	 * Creates a chest-style GUI.
	 *
	 * @param title Inventory title (pass already-colored string if you want).
	 * @param rows  number of rows (1–6).
	 */
	Inventory createGui(String title, int rows);

	void openGui(Player player, Inventory inventory);

	/**
	 * Registers a click handler for the given inventory.
	 * Handler is called on every click inside that GUI.
	 */
	void registerClickHandler(Inventory inventory, Consumer<GUIClickEvent> handler);

	/**
	 * Unregisters any handler for the given inventory.
	 */
	void unregisterGui(Inventory inventory);
}