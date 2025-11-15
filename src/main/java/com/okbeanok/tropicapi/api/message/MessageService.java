package com.okbeanok.tropicapi.api.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public interface MessageService {

	Locale getLocale(Player player);

	Locale getDefaultLocale();

	String getRaw(String key, Locale locale);

	default String getRaw(CommandSender sender, String key) {
		if (sender instanceof Player player) {
			return getRaw(key, getLocale(player));
		}
		return getRaw(key, getDefaultLocale());
	}

	String format(String template, Map<String, Object> placeholders);

	default String resolve(String key, Locale locale, Map<String, Object> placeholders) {
		return format(getRaw(key, locale), placeholders);
	}

	void sendInfo(CommandSender sender, String message);

	void sendSuccess(CommandSender sender, String message);

	void sendError(CommandSender sender, String message);
}