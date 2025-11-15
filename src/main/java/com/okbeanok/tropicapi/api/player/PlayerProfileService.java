package com.okbeanok.tropicapi.api.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerProfileService {

	/**
	 * Gets a profile for an online player (should be fast).
	 */
	PlayerProfile getProfile(Player player);

	/**
	 * Gets a profile for an offline player if immediately available.
	 */
	Optional<PlayerProfile> getProfile(OfflinePlayer player);

	/**
	 * Async lookup by UUID (for database-backed profiles).
	 */
	CompletableFuture<PlayerProfile> loadProfile(UUID uuid);
}