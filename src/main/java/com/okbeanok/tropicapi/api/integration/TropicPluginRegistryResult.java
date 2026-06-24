package com.okbeanok.tropicapi.api.integration;

import com.okbeanok.tropicapi.internal.integration.TropicPluginRegistryEntry;

public record TropicPluginRegistryResult(
		TropicPluginRegistryEntry entry,
		boolean loaded,
		boolean enabled,
		String version
) {
}