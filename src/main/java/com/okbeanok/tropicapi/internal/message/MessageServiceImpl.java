package com.okbeanok.tropicapi.internal.message;

import com.okbeanok.tropicapi.api.color.ColorAPI;
import com.okbeanok.tropicapi.api.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public class MessageServiceImpl implements MessageService {

	private final Locale defaultLocale = Locale.ENGLISH;
	private final String prefixInfo = "&7[&bTropic&7] ";
	private final String prefixSuccess = "&7[&a✓&7] ";
	private final String prefixError = "&7[&c✗&7] ";

	@Override
	public Locale getLocale(Player player) {
		try {
			// Paper exposes this; if not available, just use default
			return player.locale();
		} catch (NoSuchMethodError ignored) {
			return getDefaultLocale();
		}
	}

	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	@Override
	public String getRaw(String key, Locale locale) {
		// Minimal implementation: just return the key for now.
		// Later you can back this with ResourceBundle or YAML.
		return key;
	}

	@Override
	public String format(String template, Map<String, Object> placeholders) {
		if (template == null) return "";
		if (placeholders == null || placeholders.isEmpty()) return template;

		String result = template;
		for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
			String placeholder = "{" + entry.getKey() + "}";
			String value = entry.getValue() == null ? "null" : entry.getValue().toString();
			result = result.replace(placeholder, value);
		}
		return result;
	}

	@Override
	public void sendInfo(CommandSender sender, String message) {
		sendPrefixed(sender, prefixInfo, message);
	}

	@Override
	public void sendSuccess(CommandSender sender, String message) {
		sendPrefixed(sender, prefixSuccess, message);
	}

	@Override
	public void sendError(CommandSender sender, String message) {
		sendPrefixed(sender, prefixError, message);
	}

	private void sendPrefixed(CommandSender sender, String prefix, String message) {
		String raw = prefix + (message == null ? "" : message);
		String colored = ColorAPI.color(raw);
		sender.sendMessage(colored);
	}
}