package com.okbeanok.tropicapi.api.config;

import org.bukkit.plugin.Plugin;

public final class Configs {

	private static ConfigService service;

	private Configs() {
	}

	public static void init(ConfigService configService) {
		service = configService;
	}

	private static ConfigService s() {
		if (service == null) {
			throw new IllegalStateException("Configs not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	public static ConfigHandle mainConfig(Plugin plugin) {
		return s().mainConfig(plugin);
	}

	public static ConfigHandle load(Plugin plugin, String fileName) {
		return s().loadConfig(plugin, fileName);
	}
}