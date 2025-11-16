package com.okbeanok.tropicapi;

import com.okbeanok.tropicapi.api.color.ColorAPI;
import com.okbeanok.tropicapi.api.color.ColorService;
import com.okbeanok.tropicapi.api.config.ConfigService;
import com.okbeanok.tropicapi.api.config.Configs;
import com.okbeanok.tropicapi.api.event.TropicEvents;
import com.okbeanok.tropicapi.api.gui.GUIAPI;
import com.okbeanok.tropicapi.api.gui.GUIService;
import com.okbeanok.tropicapi.api.integration.TropicPluginsAPI;
import com.okbeanok.tropicapi.api.message.CenteredMessagesAPI;
import com.okbeanok.tropicapi.api.message.CenteredMessagesService;
import com.okbeanok.tropicapi.api.message.MessageService;
import com.okbeanok.tropicapi.api.message.Messages;
import com.okbeanok.tropicapi.api.player.PlayerProfileService;
import com.okbeanok.tropicapi.api.task.TaskService;
import com.okbeanok.tropicapi.api.task.Tasks;
import com.okbeanok.tropicapi.api.util.TropicLog;
import com.okbeanok.tropicapi.internal.color.ColorServiceImpl;
import com.okbeanok.tropicapi.internal.config.ConfigServiceImpl;
import com.okbeanok.tropicapi.internal.event.TropicEventBusImpl;
import com.okbeanok.tropicapi.internal.gui.GUIServiceImpl;
import com.okbeanok.tropicapi.internal.integration.TropicPluginsImpl;
import com.okbeanok.tropicapi.internal.message.CenteredMessagesServiceImpl;
import com.okbeanok.tropicapi.internal.message.MessageServiceImpl;
import com.okbeanok.tropicapi.internal.player.PlayerProfileServiceImpl;
import com.okbeanok.tropicapi.internal.task.TaskServiceImpl;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TropicAPI extends JavaPlugin {

	public static Logger LOGGER = Bukkit.getLogger();

	private static TropicAPI instance;

	private ColorService colorService;
	private CenteredMessagesService centeredMessagesService;
	private GUIService guiService;

	private MessageService messageService;
	private TaskService taskService;
	private ConfigService configService;
	private PlayerProfileService playerProfileService;

	public static TropicAPI getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		// Plugin load logic
	}

	@Override
	public void onEnable() {
		instance = this;

		long startTime = System.currentTimeMillis();

		TropicLog.init(LOGGER);

		LOGGER.info("========================================");
		LOGGER.info(" Starting initialization...");
		LOGGER.info(" Version: " + getDescription().getVersion());
		LOGGER.info(" Server: " + Bukkit.getVersion());
		LOGGER.info("========================================");

		// Check for dependent plugins
		pluginChecker();

		try {
			// Core services
			initializeCoreServices();

			// Initialize GUI System (existing GUIManager if you still need it)
			if (!initializeGUISystem()) {
				LOGGER.severe(" ✗ GUI system initialization failed! Disabling plugin...");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}

			long endTime = System.currentTimeMillis();
			LOGGER.info("========================================");
			LOGGER.info(" ✓ Plugin enabled successfully!");
			LOGGER.info(" Initialization took " + (endTime - startTime) + "ms");
			LOGGER.info("========================================");

		} catch (Exception e) {
			LOGGER.severe("========================================");
			LOGGER.severe(" ✗ CRITICAL ERROR during initialization!");
			LOGGER.severe(" " + e.getMessage());
			e.printStackTrace();
			LOGGER.severe("========================================");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {

		LOGGER.info("========================================");
		LOGGER.info(" Starting shutdown sequence...");
		LOGGER.info("========================================");


		try {
			LOGGER.info(" Clearing references...");
			colorService = null;
			centeredMessagesService = null;
			guiService = null;
		} catch (Exception e) {
			LOGGER.warning(" ✗ Failed to clear references: " + e.getMessage());
		}

		LOGGER.info("========================================");
		LOGGER.info(" Shutdown complete!");
		LOGGER.info("========================================");
	}

	private void initializeCoreServices() {
		LOGGER.info("[TropicAPI] [1/2] Initializing core services...");

		// Color
		this.colorService = new ColorServiceImpl();
		ColorAPI.init(colorService);
		LOGGER.info("[TropicAPI]   ✓ ColorService initialized");

		// Centered messages
		this.centeredMessagesService = new CenteredMessagesServiceImpl(colorService);
		CenteredMessagesAPI.init(centeredMessagesService);
		LOGGER.info("[TropicAPI]   ✓ CenteredMessagesService initialized");

		// GUI
		this.guiService = new GUIServiceImpl(this);
		GUIAPI.init(guiService);
		LOGGER.info("[TropicAPI]   ✓ GUIService initialized");

		// Messaging
		this.messageService = new MessageServiceImpl();
		Messages.init(messageService);
		LOGGER.info("[TropicAPI]   ✓ MessageService initialized");

		// Tasks
		this.taskService = new TaskServiceImpl(this);
		Tasks.init(taskService);
		LOGGER.info("[TropicAPI]   ✓ TaskService initialized");

		// Config
		this.configService = new ConfigServiceImpl();
		Configs.init(configService);
		LOGGER.info("[TropicAPI]   ✓ ConfigService initialized");

		// Player profiles
		this.playerProfileService = new PlayerProfileServiceImpl();
		LOGGER.info("[TropicAPI]   ✓ PlayerProfileService initialized");

		// Tropic plugin integration
		TropicPluginsAPI.init(new TropicPluginsImpl());
		LOGGER.info("[TropicAPI]   ✓ TropicPlugins integration initialized");

		// Internal event bus
		TropicEvents.init(new TropicEventBusImpl());
		LOGGER.info("[TropicAPI]   ✓ TropicEventBus initialized");

		LOGGER.info("[TropicAPI] [1/2] Core services ready.");
	}

	boolean pluginExists(String pluginName) {
		Server server = getServer();
		return server.getPluginManager().getPlugin(pluginName) != null;
	}

	private void pluginChecker() {
		LOGGER.info(" Running plugin checker...");

		Server server = getServer();

		boolean isTropicaFarmingPresent = pluginExists("TropicaFarming");
		boolean isTropicAuctionsPresent = pluginExists("TropicaAuctions");
		boolean isTropicModerationPresent = pluginExists("TropicModeration");
		boolean isTropicChatCorePresent = pluginExists("TropicChatCore");


		// shut down only if *none* of the required plugins are present
		if (!isTropicAuctionsPresent && !isTropicModerationPresent && !isTropicChatCorePresent) {
			LOGGER.warning(" One or more dependent plugins are missing. Please make sure a required plugin is installed.");
			server.shutdown();
			return;
		}

		if (isTropicaFarmingPresent) {
			LOGGER.info(" ✓ TropicaFarming is present.");
		} else {
			LOGGER.warning(" ✗ TropicaFarming is NOT present.");
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
			LOGGER.info(" [2/2] Initializing GUI manager...");

			LOGGER.info("   ✓ GUI manager initialized");
			return true;

		} catch (Exception e) {
			LOGGER.severe("   ✗ GUI system initialization error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
