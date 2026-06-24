package com.okbeanok.tropicapi.api.integration;

import com.okbeanok.tropicapi.internal.integration.TropicPluginRegistryEntry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Helper for working with other Tropic ecosystem plugins.
 */
public interface TropicPlugins {

	boolean isPresent(String pluginName);

	boolean isEnabled(String pluginName);
	
	Optional<TropicPluginInfo> getInfo(String pluginName);
	Optional<TropicPluginRegistryEntry> getRegistryEntry(String pluginName);
	Optional<TropicPluginRegistryResult> getRegistryResult(String pluginName);
	
	List<TropicPluginRegistryEntry> getRegistryEntries();
	List<TropicPluginRegistryResult> getRegistryResults();

	/**
	 * Returns the known Tropic plugins by naming convention.
	 */
	Set<TropicPluginInfo> getAllTropicPlugins();
}
