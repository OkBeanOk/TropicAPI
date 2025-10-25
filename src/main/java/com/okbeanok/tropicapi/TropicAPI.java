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
		LOGGER.info("[TropicChatCore] Starting initialization...");
		LOGGER.info("[TropicChatCore] Version: " + getDescription().getVersion());
		LOGGER.info("[TropicChatCore] Server: " + Bukkit.getVersion());
		LOGGER.info("========================================");

		// Check for dependent plugins
		pluginChecker();

		try {
			// Initialize GUI System
			if (!initializeGUISystem()) {
				LOGGER.severe("[TropicChatCore] ✗ GUI system initialization failed! Disabling plugin...");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}

			long endTime = System.currentTimeMillis();
			LOGGER.info("========================================");
			LOGGER.info("[TropicChatCore] ✓ Plugin enabled successfully!");
			LOGGER.info("[TropicChatCore] Initialization took " + (endTime - startTime) + "ms");
			LOGGER.info("========================================");

		} catch (Exception e) {
			LOGGER.severe("========================================");
			LOGGER.severe("[TropicChatCore] ✗ CRITICAL ERROR during initialization!");
			LOGGER.severe("[TropicChatCore] " + e.getMessage());
			e.printStackTrace();
			LOGGER.severe("========================================");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

    @Override
    public void onDisable() {

		LOGGER.info("========================================");
		LOGGER.info("[TropicChatCore] Starting shutdown sequence...");
		LOGGER.info("========================================");

		if (guiManager != null) {
			try {
				LOGGER.info("[TropicChatCore] Closing all open GUIs...");
				for (Player player : getServer().getOnlinePlayers()) {
					if (player.getOpenInventory() != null) {
						player.closeInventory();
					}
				}
				LOGGER.info("[TropicChatCore] ✓ All GUIs closed");
			} catch (Exception e) {
				LOGGER.warning("[TropicChatCore] ✗ Failed to close GUIs: " + e.getMessage());
			}
		}

		try {
			LOGGER.info("[TropicChatCore] Clearing references...");
			guiManager = null;
		}  catch (Exception e) {
			LOGGER.warning("[TropicChatCore] ✗ Failed to clear references: " + e.getMessage());
		}

		LOGGER.info("========================================");
		LOGGER.info("[TropicChatCore] Shutdown complete!");
		LOGGER.info("========================================");
    }

	boolean pluginExists(String pluginName) {
		Server server = getServer();
		return server.getPluginManager().getPlugin(pluginName) != null;
	}

	public void pluginChecker() {
		LOGGER.info("[TropicChatCore] Running plugin checker...");

		Server server = getServer();
		server.getPluginManager().getPlugins().equals("TropicChatCore");
		server.getPluginManager().getPlugins().equals("TropicModeration");
		server.getPluginManager().getPlugins().equals("TropicAuctions");

		boolean isTropicAuctionsPresent = pluginExists("TropicAuctions");
		if (isTropicAuctionsPresent) {
			LOGGER.info("[TropicAuctions] ✓ TropicAuctions is present.");
			boolean isTropicAuctions = true;
		}else {
			LOGGER.warning("[TropicAuctions] ✗ TropicAuctions is NOT present.");
			boolean isTropicAuctions = false;
		}

		boolean isTropicModerationPresent = pluginExists("TropicModeration");
		if (isTropicModerationPresent) {
			LOGGER.info("[TropicModeration] ✓ TropicModeration is present.");
			boolean isTropicModeration = true;
		}
		else {
			LOGGER.warning("[TropicModeration] ✗ TropicModeration is NOT present.");
			boolean isTropicModeration = false;
		}

		boolean isTropicChatCorePresent = pluginExists("TropicChatCore");
		if (isTropicChatCorePresent) {
			LOGGER.info("[TropicChatCore] ✓ TropicChatCore is present.");
			boolean isTropicChatCore = true;
		}else {
			LOGGER.warning("[TropicChatCore] ✗ TropicChatCore is NOT present.");
			boolean isTropicChatCore = false;
		}

		if (!isTropicAuctionsPresent || !isTropicModerationPresent || !isTropicChatCorePresent) {
			LOGGER.warning("[TropicChatCore] One or more dependent plugins are missing. Please make sure a required plugin is installed.");
			server.shutdown();
		} else {
			LOGGER.info("[TropicChatCore] Dependancy check complete.");
		}
	}

	private boolean initializeGUISystem() {
		try {
			LOGGER.info("[TropicChatCore] [1/1] Initializing GUI system...");

			guiManager = new GUIManager();
			getServer().getPluginManager().registerEvents(guiManager, this);

			LOGGER.info("[TropicChatCore]   ✓ GUI system initialized");
			return true;

		} catch (Exception e) {
			LOGGER.severe("[TropicChatCore]   ✗ GUI system initialization error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
