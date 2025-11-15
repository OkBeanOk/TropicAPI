package com.okbeanok.tropicapi.api.config;

import org.bukkit.plugin.Plugin;

public interface ConfigService {

	/**
	 * Loads (or creates) a config file relative to the plugin data folder.
	 */
	ConfigHandle loadConfig(Plugin plugin, String fileName);

	/**
	 * Short-hand for "config.yml".
	 */
	default ConfigHandle mainConfig(Plugin plugin) {
		return loadConfig(plugin, "config.yml");
	}
}
