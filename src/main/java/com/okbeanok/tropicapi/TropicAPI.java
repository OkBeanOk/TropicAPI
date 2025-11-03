package com.okbeanok.tropicapi;

import com.okbeanok.tropicapi.api.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class TropicAPI extends JavaPlugin {
	public static Logger LOGGER = Bukkit.getLogger();

	private GUIManager guiManager;

	@Override
	public void onLoad() {
		// Plugin load logic
	}

    @Override
    public void onEnable() {
		long startTime = System.currentTimeMillis();

		LOGGER.info("========================================");
		LOGGER.info("[TropicAPI] Starting initialization...");
		LOGGER.info("[TropicAPI] Version: " + getDescription().getVersion());
		LOGGER.info("[TropicAPI] Server: " + Bukkit.getVersion());
		LOGGER.info("========================================");

		// Check for dependent plugins
		pluginChecker();

		try {
			// Initialize GUI System
			if (!initializeGUISystem()) {
				LOGGER.severe("[TropicAPI] ✗ GUI system initialization failed! Disabling plugin...");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}

			long endTime = System.currentTimeMillis();
			LOGGER.info("========================================");
			LOGGER.info("[TropicAPI] ✓ Plugin enabled successfully!");
			LOGGER.info("[TropicAPI] Initialization took " + (endTime - startTime) + "ms");
			LOGGER.info("========================================");

		} catch (Exception e) {
			LOGGER.severe("========================================");
			LOGGER.severe("[TropicAPI] ✗ CRITICAL ERROR during initialization!");
			LOGGER.severe("[TropicAPI] " + e.getMessage());
			e.printStackTrace();
			LOGGER.severe("========================================");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

    @Override
    public void onDisable() {

		LOGGER.info("========================================");
		LOGGER.info("[TropicAPI] Starting shutdown sequence...");
		LOGGER.info("========================================");

		if (guiManager != null) {
			try {
				LOGGER.info("[TropicAPI] Closing all open GUIs...");
				for (Player player : getServer().getOnlinePlayers()) {
					if (player.getOpenInventory() != null) {
						player.closeInventory();
					}
				}
				LOGGER.info("[TropicAPI] ✓ All GUIs closed");
			} catch (Exception e) {
				LOGGER.warning("[TropicAPI] ✗ Failed to close GUIs: " + e.getMessage());
			}
		}

		try {
			LOGGER.info("[TropicAPI] Clearing references...");
			guiManager = null;
		}  catch (Exception e) {
			LOGGER.warning("[TropicAPI] ✗ Failed to clear references: " + e.getMessage());
		}

		LOGGER.info("========================================");
		LOGGER.info(" Shutdown complete!");
		LOGGER.info("========================================");
    }

	boolean pluginExists(String pluginName) {
		Server server = getServer();
		return server.getPluginManager().getPlugin(pluginName) != null;
	}

	private void pluginChecker() {
		LOGGER.info(" Running plugin checker...");

		Server server = getServer();

		boolean isTropicAuctionsPresent = pluginExists("TropicAuctions");
		boolean isTropicModerationPresent = pluginExists("TropicModeration");
		boolean isTropicChatCorePresent = pluginExists("TropicChatCore");

		// shut down only if *none* of the required plugins are present
		if (!isTropicAuctionsPresent && !isTropicModerationPresent && !isTropicChatCorePresent) {
			LOGGER.warning(" One or more dependent plugins are missing. Please make sure a required plugin is installed.");
			server.shutdown();
			return;
		}

		// Log presence/absence per plugin
		if (isTropicAuctionsPresent) {
			LOGGER.info(" ✓ TropicAuctions is present.");
		} else {
			LOGGER.warning(" ✗ TropicAuctions is NOT present.");
		}

		if (isTropicModerationPresent) {
			LOGGER.info(" ✓ TropicModeration is present.");
		} else {
			LOGGER.warning(" ✗ TropicModeration is NOT present.");
		}

		if (isTropicChatCorePresent) {
			LOGGER.info(" ✓ TropicChatCore is present.");
		} else {
			LOGGER.warning(" ✗ TropicChatCore is NOT present.");
		}

		LOGGER.info(" Dependency check complete.");
	}

	private boolean initializeGUISystem() {
		try {
			LOGGER.info(" [1/1] Initializing GUI system...");

			guiManager = new GUIManager();
			getServer().getPluginManager().registerEvents(guiManager, this);

			LOGGER.info("   ✓ GUI system initialized");
			return true;

		} catch (Exception e) {
			LOGGER.severe("   ✗ GUI system initialization error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
