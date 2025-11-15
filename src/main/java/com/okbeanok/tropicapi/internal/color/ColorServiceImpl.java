package com.okbeanok.tropicapi.internal.color;

import com.okbeanok.tropicapi.api.color.ColorService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of ColorService.
 * Handles:
 *  - &x style legacy codes via ChatColor.translateAlternateColorCodes('&', ...)
 *  - hex colors in forms:
 *      #RRGGBB
 *      &#RRGGBB
 *  which are converted to §x§R§R§G§G§B§B.
 */
public final class ColorServiceImpl implements ColorService {

	// Matches #RRGGBB or &#RRGGBB (case-insensitive)
	private static final Pattern HEX_PATTERN = Pattern.compile("(?i)&?#([0-9A-F]{6})");

	@Override
	public String colorize(String input) {
		if (input == null) return "";

		// 1) Replace hex codes with §x§R§R§G§G§B§B
		String processed = applyHexColors(input);

		// 2) Translate legacy & codes
		return ChatColor.translateAlternateColorCodes('&', processed);
	}

	@Override
	public String stripColors(String input) {
		if (input == null) return "";
		return ChatColor.stripColor(colorize(input));
	}

	@Override
	public void sendColored(CommandSender sender, String message) {
		if (sender == null || message == null) return;
		sender.sendMessage(colorize(message));
	}

	@Override
	public void sendColored(Player player, String message) {
		sendColored((CommandSender) player, message);
	}

	private String applyHexColors(String input) {
		Matcher matcher = HEX_PATTERN.matcher(input);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String hex = matcher.group(1); // RRGGBB
			StringBuilder replacement = new StringBuilder("§x");
			for (char c : hex.toCharArray()) {
				replacement.append('§').append(c);
			}
			matcher.appendReplacement(sb, replacement.toString());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}