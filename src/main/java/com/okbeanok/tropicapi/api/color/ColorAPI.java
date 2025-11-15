package com.okbeanok.tropicapi.api.color;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Static entry point for all Tropic plugins to use color utilities.
 * Backed by an internal ColorService implementation set by TropicAPI onEnable().
 */
public final class ColorAPI {

	private static ColorService service;

	private ColorAPI() {
		// utility
	}

	// Called from TropicAPI.onEnable()
	public static void init(ColorService colorService) {
		service = colorService;
	}

	private static ColorService s() {
		if (service == null) {
			throw new IllegalStateException("ColorAPI not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	public static String color(String input) {
		return s().colorize(input);
	}

	public static String[] color(String... lines) {
		return s().colorize(lines);
	}

	public static String strip(String input) {
		return s().stripColors(input);
	}

	public static void send(CommandSender sender, String message) {
		s().sendColored(sender, message);
	}

	public static void send(Player player, String message) {
		s().sendColored(player, message);
	}
}