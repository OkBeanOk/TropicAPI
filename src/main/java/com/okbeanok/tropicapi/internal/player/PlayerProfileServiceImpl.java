package com.okbeanok.tropicapi.internal.player;

import com.okbeanok.tropicapi.api.player.PlayerProfile;
import com.okbeanok.tropicapi.api.player.PlayerProfileService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerProfileServiceImpl implements PlayerProfileService {

	@Override
	public PlayerProfile getProfile(Player player) {
		return fromOffline(player);
	}

	@Override
	public Optional<PlayerProfile> getProfile(OfflinePlayer player) {
		return Optional.of(fromOffline(player));
	}

	@Override
	public CompletableFuture<PlayerProfile> loadProfile(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
			return fromOffline(off);
		});
	}

	private PlayerProfile fromOffline(OfflinePlayer player) {
		return new PlayerProfile() {
			@Override
			public UUID getUniqueId() {
				return player.getUniqueId();
			}

			@Override
			public String getLastKnownName() {
				return player.getName();
			}

			@Override
			public Instant getFirstJoin() {
				long first = player.getFirstPlayed();
				return first > 0 ? Instant.ofEpochMilli(first) : Instant.EPOCH;
			}

			@Override
			public Instant getLastJoin() {
				long last = player.getLastPlayed();
				return last > 0 ? Instant.ofEpochMilli(last) : Instant.EPOCH;
			}

			@Override
			public boolean isBanned() {
				return player.isBanned();
			}

			@Override
			public boolean isMuted() {
				// No built-in mute concept; always false by default.
				return false;
			}

			@Override
			public boolean hasFlag(String flagKey) {
				// Placeholder: no flags by default.
				return false;
			}
		};
	}
}
