package com.okbeanok.tropicapi.api.integration;

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

	public static Optional<TropicPluginInfo> getInfo(String pluginName) {
		return s().getInfo(pluginName);
	}

	public static Set<TropicPluginInfo> getAll() {
		return s().getAllTropicPlugins();
	}
}
