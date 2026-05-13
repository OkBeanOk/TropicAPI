package com.okbeanok.tropicapi.internal.gui;

import com.okbeanok.tropicapi.api.gui.GUIClickEvent;
import com.okbeanok.tropicapi.api.gui.GUIInteractionMode;
import com.okbeanok.tropicapi.api.gui.GUIService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Default implementation of GUIService.
 * Maintains a WeakHashMap of open GUIs and their click handlers.
 */
public final class GUIServiceImpl implements GUIService, Listener {

	private final Map<Inventory, RegisteredGui> guis = new WeakHashMap<>();

	private record RegisteredGui(
			Consumer<GUIClickEvent> handler,
			GUIInteractionMode interactionMode
	) {
	}

	public GUIServiceImpl(org.bukkit.plugin.Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public Inventory createGui(String title, int rows) {
		int clampedRows = Math.max(1, Math.min(6, rows));
		int size = clampedRows * 9;
		return Bukkit.createInventory(null, size, title);
	}

	@Override
	public void openGui(Player player, Inventory inventory) {
		if (player == null || inventory == null) return;
		player.openInventory(inventory);
	}

	@Override
	public void registerClickHandler(Inventory inventory, Consumer<GUIClickEvent> handler) {
		registerClickHandler(inventory, handler, GUIInteractionMode.LOCKED);
	}

	@Override
	public void registerClickHandler(Inventory inventory, Consumer<GUIClickEvent> handler, GUIInteractionMode interactionMode) {
		if (inventory == null || handler == null) return;
		guis.put(inventory, new RegisteredGui(handler, interactionMode == null ? GUIInteractionMode.LOCKED : interactionMode));
	}

	@Override
	public void unregisterGui(Inventory inventory) {
		guis.remove(inventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		RegisteredGui gui = guis.get(inventory);
		if (gui == null) return;

		if (!(event.getWhoClicked() instanceof Player player)) return;

		if (gui.interactionMode() == GUIInteractionMode.LOCKED) {
			event.setCancelled(true);
		}

		GUIClickEvent guiClick = new GUIClickEvent(
				player,
				inventory,
				event.getSlot(),
				event.getClick(),
				event.getCurrentItem(),
				event
		);

		gui.handler().accept(guiClick);
	}
}
