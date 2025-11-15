package com.okbeanok.tropicapi.api.gui;

import com.okbeanok.tropicapi.api.color.ColorAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Base GUI class for all Tropic plugins.
 *
 * Usage:
 *   public class ExampleGUI extends GUI {
 *       public ExampleGUI(Player player) {
 *           super(player, "&aExample GUI", 3);
 *       }
 *
 *       @Override
 *       protected void build() {
 *           setItem(13, someItemStack);
 *       }
 *
 *       @Override
 *       protected void onClick(int slot) {
 *           if (slot == 13) {
 *               // handle click
 *           }
 *       }
 *   }
 */
public abstract class GUI {

	private final Player player;
	private final String title;
	private final int rows;
	private final Inventory inventory;
	private boolean built = false;

	/**
	 * @param player viewer
	 * @param title  uncolored or &/hex colored title (ColorAPI is applied automatically)
	 * @param rows   number of rows (1–6)
	 */
	protected GUI(Player player, String title, int rows) {
		this.player = player;
		this.rows = Math.max(1, Math.min(6, rows));
		this.title = title;

		String coloredTitle = ColorAPI.color(title);
		this.inventory = GUIAPI.create(coloredTitle, this.rows);

		// Register click handler once per GUI instance
		GUIAPI.onClick(this.inventory, event -> {
			// only handle clicks from this gui's player
			if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
				return;
			}
			handleClick(event);
		});
	}

	/**
	 * Build your inventory contents here.
	 * This is called lazily the first time open() is invoked.
	 */
	protected abstract void build();

	/**
	 * Simple click handler by raw slot index.
	 * Override this in your GUIs if you don't care about ClickType / ItemStack.
	 */
	protected void onClick(int slot) {
		// default no-op
	}

	/**
	 * Advanced click handler with full event.
	 * Default implementation delegates to onClick(int slot).
	 * Override this instead if you need click type, item, etc.
	 */
	protected void onClick(GUIClickEvent event) {
		onClick(event.getSlot());
	}

	/**
	 * Called internally by the click handler to dispatch the event.
	 */
	private void handleClick(GUIClickEvent event) {
		onClick(event);
	}

	/**
	 * Opens this GUI for the player.
	 */
	public void open() {
		if (!built) {
			build();
			built = true;
		}
		GUIAPI.open(player, inventory);
	}

	/**
	 * Closes the GUI and unregisters click handling for this inventory.
	 */
	public void close() {
		if (player.getOpenInventory().getTopInventory().equals(inventory)) {
			player.closeInventory();
		}
		GUIAPI.unregister(inventory);
	}

	/**
	 * Rebuilds the GUI contents and reopens it.
	 * Can be called by subclasses when data changes (e.g. list updates).
	 */
	public void refresh() {
		inventory.clear();
		build();
		built = true;
		GUIAPI.open(player, inventory);
	}
	// ---- Helper methods for subclasses ----



	public Player getPlayer() {
		return player;
	}

	public String getTitle() {
		return title;
	}

	public int getRows() {
		return rows;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setItem(int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}

	public ItemStack getItem(int slot) {
		return inventory.getItem(slot);
	}

	public boolean isBuilt() {
		return built;
	}


}