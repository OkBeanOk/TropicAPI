package com.okbeanok.tropicapi.api.integration;

import java.util.Optional;
import java.util.Set;

/**
 * Helper for working with other Tropic ecosystem plugins.
 */
public interface TropicPlugins {

	boolean isPresent(String pluginName);

	Optional<TropicPluginInfo> getInfo(String pluginName);

	/**
	 * Returns the known Tropic plugins (by naming convention or config).
	 */
	Set<TropicPluginInfo> getAllTropicPlugins();
}
