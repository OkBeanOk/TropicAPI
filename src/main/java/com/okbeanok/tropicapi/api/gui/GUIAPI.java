package com.okbeanok.tropicapi.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

/**
 * Static entry point for GUI utilities.
 *
 * Most plugins will only use the high-level {@link GUI} base class,
 * which itself calls into this API. You can still use GUIAPI directly
 * for highly customized or non-standard GUIs.
 */
public final class GUIAPI {

	private static GUIService service;

	private GUIAPI() {
		// utility
	}

	/**
	 * Called from TropicAPI.onEnable() to wire the internal implementation.
	 */
	public static void init(GUIService guiService) {
		service = guiService;
	}

	private static GUIService s() {
		if (service == null) {
			throw new IllegalStateException("GUIAPI not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	/**
	 * Create a chest-style inventory with the given title and rows.
	 */
	public static Inventory create(String title, int rows) {
		return s().createGui(title, rows);
	}

	/**
	 * Open the given inventory for the player.
	 */
	public static void open(Player player, Inventory inventory) {
		s().openGui(player, inventory);
	}

	/**
	 * Register a click handler for that inventory.
	 * This is what {@link GUI} uses internally to route clicks.
	 */
	public static void onClick(Inventory inventory, Consumer<GUIClickEvent> handler) {
		s().registerClickHandler(inventory, handler);
	}

	/**
	 * Register a click handler for that inventory with an interaction mode.
	 */
	public static void onClick(Inventory inventory, Consumer<GUIClickEvent> handler, GUIInteractionMode interactionMode) {
		s().registerClickHandler(inventory, handler, interactionMode);
	}

	/**
	 * Unregister any click handler for that inventory.
	 */
	public static void unregister(Inventory inventory) {
		s().unregisterGui(inventory);
	}
}