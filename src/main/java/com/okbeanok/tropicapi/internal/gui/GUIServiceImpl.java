package com.okbeanok.tropicapi.internal.gui;

import com.okbeanok.tropicapi.api.gui.GUIClickEvent;
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

	private final Map<Inventory, Consumer<GUIClickEvent>> handlers = new WeakHashMap<>();

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
		if (inventory == null || handler == null) return;
		handlers.put(inventory, handler);
	}

	@Override
	public void unregisterGui(Inventory inventory) {
		handlers.remove(inventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Consumer<GUIClickEvent> handler = handlers.get(inventory);
		if (handler == null) return;

		if (!(event.getWhoClicked() instanceof Player player)) return;

		// Prevent item movement; GUI is "click-only" by default.
		event.setCancelled(true);

		GUIClickEvent guiClick = new GUIClickEvent(
				player,
				inventory,
				event.getSlot(),
				event.getClick(),
				event.getCurrentItem()
		);

		handler.accept(guiClick);
	}
}
