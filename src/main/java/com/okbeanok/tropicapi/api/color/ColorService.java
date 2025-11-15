package com.okbeanok.tropicapi.api.color;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Core color formatting service.
 * Supports legacy & codes and hex colors like #RRGGBB or &#RRGGBB.
 */
public interface ColorService {

	/**
	 * Colorizes a single string (legacy & + hex).
	 */
	String colorize(String input);

	/**
	 * Colorizes an array of strings.
	 */
	default String[] colorize(String... lines) {
		if (lines == null) return new String[0];
		String[] out = new String[lines.length];
		for (int i = 0; i < lines.length; i++) {
			out[i] = colorize(lines[i]);
		}
		return out;
	}

	/**
	 * Strips all color codes from the string.
	 */
	String stripColors(String input);

	/**
	 * Sends a colored message to a CommandSender.
	 */
	void sendColored(CommandSender sender, String message);

	/**
	 * Sends a colored message to a Player.
	 */
	void sendColored(Player player, String message);
}