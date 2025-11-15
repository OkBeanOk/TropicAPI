package com.okbeanok.tropicapi.api.player;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple per-player context storage shared across Tropic plugins.
 */
public final class PlayerContext {

	private static final Map<Player, Map<String, Object>> DATA = new ConcurrentHashMap<>();

	private PlayerContext() {
	}

	public static void set(Player player, String key, Object value) {
		DATA.computeIfAbsent(player, p -> new ConcurrentHashMap<>()).put(key, value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Player player, String key, Class<T> type) {
		Map<String, Object> map = DATA.get(player);
		if (map == null) return null;
		Object value = map.get(key);
		if (value == null) return null;
		if (!type.isInstance(value)) return null;
		return (T) value;
	}

	public static void remove(Player player, String key) {
		Map<String, Object> map = DATA.get(player);
		if (map != null) {
			map.remove(key);
			if (map.isEmpty()) {
				DATA.remove(player);
			}
		}
	}

	public static void clear(Player player) {
		DATA.remove(player);
	}
}