package com.okbeanok.tropicapi.internal.color;

import com.okbeanok.tropicapi.api.color.ColorService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorServiceImpl implements ColorService {

	// Matches #RRGGBB and &#RRGGBB
	private static final Pattern HEX_PATTERN = Pattern.compile("(&?#)([A-Fa-f0-9]{6})");

	@Override
	public String colorize(String input) {
		if (input == null) {
			return null;
		}

		// 1) Expand gradient tags into &#RRGGBB prefixes
		String withGradients = GradientUtil.applyGradients(input);

		// 2) Convert hex forms (#RRGGBB or &#RRGGBB) into §x§R§R§G§G§B§B
		String withHex = applyHexColors(withGradients);

		// 3) Translate legacy & codes (&a, &b, &l, etc.) to §
		return ChatColor.translateAlternateColorCodes('&', withHex);
	}

	private String applyHexColors(String input) {
		Matcher matcher = HEX_PATTERN.matcher(input);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			String prefix = matcher.group(1); // "#" or "&#"
			String hex = matcher.group(2);

			// If it's just "#RRGGBB", we still want to treat it as hex;
			// Strip any leading & (if present) so translateAlternateColorCodes doesn't eat it.
			boolean hadAmp = prefix.startsWith("&");

			StringBuilder replacement = new StringBuilder();
			replacement.append('§').append('x');
			for (char c : hex.toCharArray()) {
				replacement.append('§').append(Character.toLowerCase(c));
			}

			// If the original had an & (e.g., "&#RRGGBB"), we don't want that & to be processed
			// as a legacy color later, so we replace the entire match with the §x... sequence.
			matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement.toString()));
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}

	@Override
	public String stripColors(String input) {
		if (input == null) return null;
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
}