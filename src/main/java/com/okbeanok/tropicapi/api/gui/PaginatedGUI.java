package com.okbeanok.tropicapi.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;

public abstract class PaginatedGUI<T> extends GUI {

	private int page = 0;
	private List<T> items = Collections.emptyList();
	private final int pageSize;

	protected PaginatedGUI(Player player, String title, int size, int pageSize) {
		super(player, title, size);
		this.pageSize = pageSize;
	}

	public void setItems(List<T> items) {
		this.items = items == null ? Collections.emptyList() : items;
	}

	protected int getPage() {
		return page;
	}

	protected int getPageSize() {
		return pageSize;
	}

	protected List<T> getItemsOnPage() {
		int from = page * pageSize;
		int to = Math.min(from + pageSize, items.size());
		if (from >= to) return Collections.emptyList();
		return items.subList(from, to);
	}

	protected boolean hasNextPage() {
		return (page + 1) * pageSize < items.size();
	}

	protected boolean hasPreviousPage() {
		return page > 0;
	}

	protected void nextPage() {
		if (hasNextPage()) {
			page++;
			rebuild();
		}
	}

	protected void previousPage() {
		if (hasPreviousPage()) {
			page--;
			rebuild();
		}
	}

	protected abstract void rebuild();

	@Override
	public void open() {
		super.open();
		rebuild();
	}

	protected Inventory getInventoryInternal() {
		return getInventory();
	}
}