package com.okbeanok.tropicapi.api.player;

import java.time.Instant;
import java.util.UUID;

public interface PlayerProfile {

	UUID getUniqueId();

	String getLastKnownName();

	Instant getFirstJoin();

	Instant getLastJoin();

	boolean isBanned();

	boolean isMuted();

	boolean hasFlag(String flagKey);
}