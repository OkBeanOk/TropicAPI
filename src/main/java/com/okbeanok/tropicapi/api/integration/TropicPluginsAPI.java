package com.okbeanok.tropicapi.api.integration;

import com.okbeanok.tropicapi.internal.integration.TropicPluginRegistryEntry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class TropicPluginsAPI {

	private static TropicPlugins service;

	private TropicPluginsAPI() {
	}

	public static void init(TropicPlugins tropicPlugins) {
		service = tropicPlugins;
	}

	private static TropicPlugins s() {
		if (service == null) {
			throw new IllegalStateException("TropicPluginsAPI not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	public static boolean isPresent(String pluginName) {
		return s().isPresent(pluginName);
	}

	public static boolean isEnabled(String pluginName) {
		return s().isEnabled(pluginName);
	}

	public static Optional<TropicPluginInfo> getInfo(String pluginName) {
		return s().getInfo(pluginName);
	}

	public static Optional<TropicPluginRegistryEntry> getRegistryEntry(String pluginName) {
		return s().getRegistryEntry(pluginName);
	}

	public static Optional<TropicPluginRegistryResult> getRegistryResult(String pluginName) {
		return s().getRegistryResult(pluginName);
	}

	public static List<TropicPluginRegistryEntry> getRegistryEntries() {
		return s().getRegistryEntries();
	}

	public static List<TropicPluginRegistryResult> getRegistryResults() {
		return s().getRegistryResults();
	}

	public static Set<TropicPluginInfo> getAll() {
		return s().getAllTropicPlugins();
	}
}
