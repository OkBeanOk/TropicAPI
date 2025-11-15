package com.okbeanok.tropicapi.internal.integration;

import com.okbeanok.tropicapi.api.integration.TropicPluginInfo;
import com.okbeanok.tropicapi.api.integration.TropicPlugins;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.stream.Collectors;

public class TropicPluginsImpl implements TropicPlugins {

	@Override
	public boolean isPresent(String pluginName) {
		return Bukkit.getPluginManager().getPlugin(pluginName) != null;
	}

	@Override
	public java.util.Optional<TropicPluginInfo> getInfo(String pluginName) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin == null) return java.util.Optional.empty();

		return java.util.Optional.of(fromPlugin(plugin));
	}

	@Override
	public Set<TropicPluginInfo> getAllTropicPlugins() {
		return java.util.Arrays.stream(Bukkit.getPluginManager().getPlugins())
				.filter(p -> p.getName().startsWith("Tropic"))
				.map(this::fromPlugin)
				.collect(Collectors.toSet());
	}

	private TropicPluginInfo fromPlugin(Plugin plugin) {
		return new TropicPluginInfo() {
			@Override
			public String getName() {
				return plugin.getName();
			}

			@Override
			public String getVersion() {
				return plugin.getDescription().getVersion();
			}

			@Override
			public boolean isEnabled() {
				return plugin.isEnabled();
			}
		};
	}
}
