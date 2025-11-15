package com.okbeanok.tropicapi.api.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Static entry point for centered messages.
 */
public final class CenteredMessagesAPI {

	private static CenteredMessagesService service;

	private CenteredMessagesAPI() {
		// utility
	}

	// Called from TropicAPI.onEnable()
	public static void init(CenteredMessagesService centeredMessagesService) {
		service = centeredMessagesService;
	}

	private static CenteredMessagesService s() {
		if (service == null) {
			throw new IllegalStateException("CenteredMessagesAPI not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	public static void send(CommandSender sender, String message) {
		s().sendCentered(sender, message);
	}

	public static void send(Player player, String message) {
		s().sendCentered(player, message);
	}

	public static String center(String message) {
		return s().toCenteredLine(message);
	}
}
