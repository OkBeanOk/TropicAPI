package com.okbeanok.tropicapi.api.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Simple per-player cooldown manager.
 */
public final class Cooldowns {

	private static final Map<String, Map<UUID, Long>> COOLDOWNS = new ConcurrentHashMap<>();

	private Cooldowns() {
	}

	/**
	 * @param key    unique key per action, e.g. "auction#create"
	 * @param uuid   player UUID
	 * @param period cooldown period in millis
	 * @return remaining cooldown millis, or 0 if no cooldown / expired
	 */
	public static long getRemaining(String key, UUID uuid, long period) {
		long now = System.currentTimeMillis();
		long expiresAt = COOLDOWNS
				.getOrDefault(key, Map.of())
				.getOrDefault(uuid, 0L);
		long remaining = expiresAt - now;
		return Math.max(remaining, 0);
	}

	public static boolean checkAndApply(String key, UUID uuid, long period, TimeUnit unit) {
		long millis = unit.toMillis(period);
		long remaining = getRemaining(key, uuid, millis);
		if (remaining > 0) return false;

		long now = System.currentTimeMillis();
		COOLDOWNS
				.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
				.put(uuid, now + millis);
		return true;
	}

	public static boolean checkAndApply(String key, Player player, long period, TimeUnit unit) {
		return checkAndApply(key, player.getUniqueId(), period, unit);
	}
}