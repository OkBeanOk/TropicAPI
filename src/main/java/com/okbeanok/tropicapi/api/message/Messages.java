package com.okbeanok.tropicapi.api.message;

import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Map;

public final class Messages {

	private static MessageService service;

	private Messages() {
	}

	public static void init(MessageService messageService) {
		service = messageService;
	}

	private static MessageService s() {
		if (service == null) {
			throw new IllegalStateException("Messages not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	public static String getRaw(String key, Locale locale) {
		return s().getRaw(key, locale);
	}

	public static String getRaw(CommandSender sender, String key) {
		return s().getRaw(sender, key);
	}

	public static String format(String template, Map<String, Object> placeholders) {
		return s().format(template, placeholders);
	}

	public static String resolve(String key, Locale locale, Map<String, Object> placeholders) {
		return s().resolve(key, locale, placeholders);
	}

	public static void info(CommandSender sender, String message) {
		s().sendInfo(sender, message);
	}

	public static void success(CommandSender sender, String message) {
		s().sendSuccess(sender, message);
	}

	public static void error(CommandSender sender, String message) {
		s().sendError(sender, message);
	}
}