package com.okbeanok.tropicapi.api.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

/**
 * Simple wrapper so plugins can share a unified config pattern.
 */
public interface ConfigHandle {

	File getFile();

	FileConfiguration getConfiguration();

	void save();

	void reload();
}
