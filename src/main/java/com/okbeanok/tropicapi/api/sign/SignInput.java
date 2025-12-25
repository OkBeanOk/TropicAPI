package com.okbeanok.tropicapi.api.sign;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility for sign-based text input.
 *
 * Usage:
 *   SignInput.open(
 *       plugin,
 *       player,
 *       lines -> { ... handle completed input ... },
 *       () -> { ... handle cancel ... }
 *   );
 *
 * Notes:
 * - Uses a temporary sign at the player's feet.
 * - Restores the block after input or cancel.
 */
public final class SignInput implements Listener {

	private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();

	private final Plugin plugin;

	public SignInput(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Open a sign editor for the player.
	 *
	 * @param plugin   owning plugin instance (for scheduling/restoration)
	 * @param player   target player
	 * @param handler  called with the 4 sign lines when the player finishes editing
	 * @param onCancel called if the session is cancelled (quit, error, etc.)
	 */
	public static void open(Plugin plugin,
							Player player,
							Consumer<String[]> handler,
							Runnable onCancel) {

		// Cancel any existing session for this player
		cancel(player.getUniqueId(), false);

		Location loc = player.getLocation().getBlock().getLocation();
		Block block = loc.getBlock();
		Material oldType = block.getType();
		var oldData = block.getBlockData().clone();

		block.setType(Material.OAK_SIGN, false);
		Sign sign = (Sign) block.getState();

		UUID uuid = player.getUniqueId();
		SESSIONS.put(uuid, new Session(loc, oldType, oldData, handler, onCancel));

		// Open sign editor next tick to ensure block is updated
		Bukkit.getScheduler().runTask(plugin, () -> player.openSign(sign));
	}

	/**
	 * Explicitly cancel a session for a player, if present.
	 */
	public static void cancel(UUID playerId, boolean callOnCancel) {
		Session session = SESSIONS.remove(playerId);
		if (session != null) {
			session.restoreBlock();
			if (callOnCancel && session.onCancel != null) {
				session.onCancel.run();
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Session session = SESSIONS.remove(uuid);
		if (session == null) return;

		// Only accept changes for the exact temporary sign location
		if (!event.getBlock().getLocation().equals(session.location())) {
			// put back; it's not our sign
			SESSIONS.put(uuid, session);
			return;
		}

		session.restoreBlock();

		String[] lines = new String[4];
		for (int i = 0; i < 4; i++) {
			lines[i] = event.getLine(i) == null ? "" : event.getLine(i);
		}

		if (session.handler != null) {
			session.handler.accept(lines);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		cancel(event.getPlayer().getUniqueId(), true);
	}

	private record Session(
			Location location,
			Material oldType,
			org.bukkit.block.data.BlockData oldData,
			Consumer<String[]> handler,
			Runnable onCancel
	) {
		void restoreBlock() {
			Block block = location.getBlock();
			block.setType(oldType, false);
			block.setBlockData(oldData, false);
		}
	}
}