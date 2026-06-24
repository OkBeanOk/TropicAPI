package com.okbeanok.tropicapi;

import com.okbeanok.tropicapi.api.color.ColorAPI;
import com.okbeanok.tropicapi.api.color.ColorService;
import com.okbeanok.tropicapi.api.config.ConfigService;
import com.okbeanok.tropicapi.api.config.Configs;
import com.okbeanok.tropicapi.api.event.TropicEvents;
import com.okbeanok.tropicapi.api.gui.GUIAPI;
import com.okbeanok.tropicapi.api.gui.GUIService;
import com.okbeanok.tropicapi.api.integration.TropicPluginRegistryResult;
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
import com.okbeanok.tropicapi.internal.commands.TropicAPICommand;
import com.okbeanok.tropicapi.internal.config.ConfigServiceImpl;
import com.okbeanok.tropicapi.internal.event.TropicEventBusImpl;
import com.okbeanok.tropicapi.internal.gui.GUIServiceImpl;
import com.okbeanok.tropicapi.internal.integration.TropicPluginsImpl;
import com.okbeanok.tropicapi.internal.message.CenteredMessagesServiceImpl;
import com.okbeanok.tropicapi.internal.message.MessageServiceImpl;
import com.okbeanok.tropicapi.internal.player.PlayerProfileServiceImpl;
import com.okbeanok.tropicapi.internal.task.TaskServiceImpl;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TropicAPI extends JavaPlugin {

	public static final Logger LOGGER = Bukkit.getLogger();

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
		instance = this;
	}

	@Override
	public void onEnable() {
		instance = this;

		long startTime = System.currentTimeMillis();

		TropicLog.init(LOGGER);

		logStartupHeader();

		try {
			initializeCoreServices();
			registerCommands();
			logPluginRegistry();

			if (!initializeGUISystem()) {
				LOGGER.severe("[TropicAPI] GUI system initialization failed. Disabling plugin...");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}

			logStartupComplete(startTime);
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
			clearServices();
			LOGGER.info("[TropicAPI] ✓ References cleared");
		} catch (Exception e) {
			LOGGER.warning("[TropicAPI] ✗ Failed to clear references: " + e.getMessage());
		}

		LOGGER.info("========================================");
		LOGGER.info(" Shutdown complete!");
		LOGGER.info("========================================");
	}

	private void initializeCoreServices() {
		LOGGER.info("[TropicAPI] [1/2] Initializing core services...");

		initializeColorService();
		initializeCenteredMessagesService();
		initializeGUIService();
		initializeMessageService();
		initializeTaskService();
		initializeConfigService();
		initializePlayerProfileService();
		initializePluginIntegrationService();
		initializeEventBus();

		LOGGER.info("[TropicAPI] [1/2] Core services ready.");
	}

	private void initializeColorService() {
		this.colorService = new ColorServiceImpl();
		ColorAPI.init(colorService);
		LOGGER.info("[TropicAPI]   ✓ ColorService initialized");
	}

	private void initializeCenteredMessagesService() {
		this.centeredMessagesService = new CenteredMessagesServiceImpl(colorService);
		CenteredMessagesAPI.init(centeredMessagesService);
		LOGGER.info("[TropicAPI]   ✓ CenteredMessagesService initialized");
	}

	private void initializeGUIService() {
		this.guiService = new GUIServiceImpl(this);
		GUIAPI.init(guiService);
		LOGGER.info("[TropicAPI]   ✓ GUIService initialized");
	}

	private void initializeMessageService() {
		this.messageService = new MessageServiceImpl();
		Messages.init(messageService);
		LOGGER.info("[TropicAPI]   ✓ MessageService initialized");
	}

	private void initializeTaskService() {
		this.taskService = new TaskServiceImpl(this);
		Tasks.init(taskService);
		LOGGER.info("[TropicAPI]   ✓ TaskService initialized");
	}

	private void initializeConfigService() {
		this.configService = new ConfigServiceImpl();
		Configs.init(configService);
		LOGGER.info("[TropicAPI]   ✓ ConfigService initialized");
	}

	private void initializePlayerProfileService() {
		this.playerProfileService = new PlayerProfileServiceImpl();
		LOGGER.info("[TropicAPI]   ✓ PlayerProfileService initialized");
	}

	private void initializePluginIntegrationService() {
		TropicPluginsAPI.init(new TropicPluginsImpl());
		LOGGER.info("[TropicAPI]   ✓ TropicPlugins integration initialized");
	}

	private void initializeEventBus() {
		TropicEvents.init(new TropicEventBusImpl());
		LOGGER.info("[TropicAPI]   ✓ TropicEventBus initialized");
	}

	private void registerCommands() {
		PluginCommand command = getCommand("tropicapi");

		if (command == null) {
			LOGGER.warning("[TropicAPI]   ✗ Failed to register /tropicapi command. Is it listed in plugin.yml?");
			return;
		}

		command.setExecutor(new TropicAPICommand());
		LOGGER.info("[TropicAPI]   ✓ Commands registered");
	}

	private void logPluginRegistry() {
		LOGGER.info("[TropicAPI] Running plugin registry check...");

		for (TropicPluginRegistryResult result : TropicPluginsAPI.getRegistryResults()) {
			String pluginName = result.entry().pluginName();

			if (result.enabled()) {
				LOGGER.info("[TropicAPI]   ✓ " + pluginName + " is enabled. Version: " + result.version());
			} else if (result.loaded()) {
				LOGGER.warning("[TropicAPI]   ! " + pluginName + " is loaded but disabled.");
			} else if (result.entry().required()) {
				LOGGER.warning("[TropicAPI]   ✗ Required plugin " + pluginName + " is NOT loaded.");
			} else {
				LOGGER.info("[TropicAPI]   - Optional plugin " + pluginName + " is not loaded.");
			}
		}

		LOGGER.info("[TropicAPI] Plugin registry check complete.");
	}

	private boolean initializeGUISystem() {
		try {
			LOGGER.info("[TropicAPI] [2/2] Initializing GUI manager...");
			LOGGER.info("[TropicAPI]   ✓ GUI manager initialized");
			return true;
		} catch (Exception e) {
			LOGGER.severe("[TropicAPI]   ✗ GUI system initialization error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private void clearServices() {
		colorService = null;
		centeredMessagesService = null;
		guiService = null;
		messageService = null;
		taskService = null;
		configService = null;
		playerProfileService = null;
	}

	private void logStartupHeader() {
		LOGGER.info("========================================");
		LOGGER.info(" Starting initialization...");
		LOGGER.info(" Version: " + getDescription().getVersion());
		LOGGER.info(" Server: " + Bukkit.getVersion());
		LOGGER.info("========================================");
	}

	private void logStartupComplete(long startTime) {
		long endTime = System.currentTimeMillis();

		LOGGER.info("========================================");
		LOGGER.info(" ✓ Plugin enabled successfully!");
		LOGGER.info(" Initialization took " + (endTime - startTime) + "ms");
		LOGGER.info("========================================");
	}
}