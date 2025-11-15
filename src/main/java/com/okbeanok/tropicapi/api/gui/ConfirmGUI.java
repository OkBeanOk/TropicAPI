package com.okbeanok.tropicapi.api.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmGUI extends GUI {

	private final Runnable onConfirm;
	private final Runnable onCancel;

	public ConfirmGUI(Player player, String title, Runnable onConfirm, Runnable onCancel) {
		super(player, title, 9);
		this.onConfirm = onConfirm;
		this.onCancel = onCancel;
	}

	@Override
	public void build() {
		ItemStack yes = new ItemStack(Material.LIME_WOOL);
		ItemStack no = new ItemStack(Material.RED_WOOL);

		setItem(3, yes);
		setItem(5, no);
	}

	@Override
	public void onClick(GUIClickEvent event) {
		event.setCancelled(true);

		if (event.getSlot() == 3) {
			if (onConfirm != null) onConfirm.run();
			getPlayer().closeInventory();
		} else if (event.getSlot() == 5) {
			if (onCancel != null) onCancel.run();
			getPlayer().closeInventory();
		}
	}

	public static void open(Player player, String title, Runnable onConfirm, Runnable onCancel) {
		new ConfirmGUI(player, title, onConfirm, onCancel).open();
	}
}
