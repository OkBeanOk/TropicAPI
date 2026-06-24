package com.okbeanok.tropicapi.internal.integration;

import com.okbeanok.tropicapi.api.integration.TropicPluginInfo;
import com.okbeanok.tropicapi.api.integration.TropicPluginRegistryResult;
import com.okbeanok.tropicapi.api.integration.TropicPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TropicPluginsImpl implements TropicPlugins {

	private static final List<TropicPluginRegistryEntry> REGISTRY = List.of(
			new TropicPluginRegistryEntry(
					"TropicAPI",
					Material.LIME_CONCRETE,
					"Required API for colors and shared utilities.",
					true,
					10
			),
			new TropicPluginRegistryEntry(
					"LuckPerms",
					Material.NAME_TAG,
					"Required permissions provider.",
					true,
					12
			),
			new TropicPluginRegistryEntry(
					"Vault",
					Material.GOLD_INGOT,
					"Optional economy and permission bridge.",
					true,
					14
			),
			new TropicPluginRegistryEntry(
					"ItemsAdder",
					Material.ITEM_FRAME,
					"Optional custom item/resource integration.",
					false,
					16
			),
			new TropicPluginRegistryEntry(
					"TropicChat",
					Material.WRITABLE_BOOK,
					"Optional chat-related moderation integration.",
					false,
					19
			),
			new TropicPluginRegistryEntry(
					"TropicaAuctions",
					Material.CHEST,
					"Optional auction-related moderation integration.",
					false,
					21
			),
			new TropicPluginRegistryEntry(
					"TropicWarps",
					Material.ENDER_PEARL,
					"Optional warp-related moderation integration.",
					false,
					23
			),
			new TropicPluginRegistryEntry(
					"TropicSpawners",
					Material.SPAWNER,
					"Optional spawner-related moderation integration.",
					false,
					25
			),
			new TropicPluginRegistryEntry(
					"TropicFishing",
					Material.FISHING_ROD,
					"Optional fishing-related moderation integration.",
					false,
					29
			),
			new TropicPluginRegistryEntry(
					"TropicCore",
					Material.NETHER_STAR,
					"Optional core systems integration.",
					false,
					31
			)
	);

	@Override
	public boolean isPresent(String pluginName) {
		return Bukkit.getPluginManager().getPlugin(pluginName) != null;
	}

	@Override
	public boolean isEnabled(String pluginName) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		return plugin != null && plugin.isEnabled();
	}

	@Override
	public Optional<TropicPluginInfo> getInfo(String pluginName) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin == null) {
			return Optional.empty();
		}

		return Optional.of(fromPlugin(plugin));
	}

	@Override
	public Optional<TropicPluginRegistryEntry> getRegistryEntry(String pluginName) {
		return REGISTRY.stream()
				.filter(entry -> entry.pluginName().equalsIgnoreCase(pluginName))
				.findFirst();
	}

	@Override
	public Optional<TropicPluginRegistryResult> getRegistryResult(String pluginName) {
		return getRegistryEntry(pluginName).map(this::toRegistryResult);
	}

	@Override
	public List<TropicPluginRegistryEntry> getRegistryEntries() {
		return REGISTRY;
	}

	@Override
	public List<TropicPluginRegistryResult> getRegistryResults() {
		return REGISTRY.stream()
				.map(this::toRegistryResult)
				.toList();
	}

	@Override
	public Set<TropicPluginInfo> getAllTropicPlugins() {
		return java.util.Arrays.stream(Bukkit.getPluginManager().getPlugins())
				.filter(p -> p.getName().startsWith("Tropic"))
				.map(this::fromPlugin)
				.collect(Collectors.toSet());
	}

	private TropicPluginRegistryResult toRegistryResult(TropicPluginRegistryEntry entry) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(entry.pluginName());

		return new TropicPluginRegistryResult(
				entry,
				plugin != null,
				plugin != null && plugin.isEnabled(),
				plugin != null ? plugin.getDescription().getVersion() : null
		);
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