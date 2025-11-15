package com.okbeanok.tropicapi.internal.config;

import com.okbeanok.tropicapi.api.config.ConfigHandle;
import com.okbeanok.tropicapi.api.config.ConfigService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigServiceImpl implements ConfigService {

	@Override
	public ConfigHandle loadConfig(Plugin plugin, String fileName) {
		File folder = plugin.getDataFolder();
		if (!folder.exists() && !folder.mkdirs()) {
			throw new IllegalStateException("Could not create data folder for " + plugin.getName());
		}
		File file = new File(folder, fileName);
		if (!file.exists()) {
			plugin.saveResource(fileName, false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		return new ConfigHandle() {
			@Override
			public File getFile() {
				return file;
			}

			@Override
			public FileConfiguration getConfiguration() {
				return config;
			}

			@Override
			public void save() {
				try {
					config.save(file);
				} catch (IOException e) {
					throw new RuntimeException("Could not save config " + file.getName(), e);
				}
			}

			@Override
			public void reload() {
				config.setDefaults(YamlConfiguration.loadConfiguration(file));
			}
		};
	}
}