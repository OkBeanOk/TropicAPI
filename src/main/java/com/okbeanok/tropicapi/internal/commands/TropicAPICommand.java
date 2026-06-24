package com.okbeanok.tropicapi.internal.commands;

import com.okbeanok.tropicapi.api.color.ColorAPI;
import com.okbeanok.tropicapi.api.integration.TropicPluginRegistryResult;
import com.okbeanok.tropicapi.api.integration.TropicPluginsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Optional;

public class TropicAPICommand implements CommandExecutor {

	private static final String PREFIX = "&#00d4ff&lTropicAPI &8» &7";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sendHelp(sender, label);
			return true;
		}

		switch (args[0].toLowerCase(Locale.ROOT)) {
			case "plugins", "integrations" -> sendPlugins(sender);
			case "plugin", "integration" -> sendPlugin(sender, label, args);
			default -> sendHelp(sender, label);
		}

		return true;
	}

	private void sendHelp(CommandSender sender, String label) {
		send(sender, "");
		send(sender, "&#00d4ff&lTropicAPI");
		send(sender, "&7Shared core API for the Tropic plugin ecosystem.");
		send(sender, "");
		send(sender, "&#ffaa00/" + label + " plugins &8- &7View registered plugin integrations.");
		send(sender, "&#ffaa00/" + label + " plugin <name> &8- &7View one plugin integration.");
		send(sender, "");
	}

	private void sendPlugins(CommandSender sender) {
		send(sender, "");
		send(sender, "&#00d4ff&lRegistered Plugin Integrations");
		send(sender, "&7Status colors: &#00ff00Hooked &8/ &#ff4444Not detected");
		send(sender, "");

		for (TropicPluginRegistryResult result : TropicPluginsAPI.getRegistryResults()) {
			String pluginName = result.entry().pluginName();
			String required = result.entry().required() ? "&#ffaa00Required" : "&#aaaaaaOptional";
			String status = result.enabled() ? "&#00ff00Hooked" : "&#ff4444Not detected";
			String version = result.version() == null ? "&#ff4444Not loaded" : "&f" + result.version();

			send(sender, "&8- &#00d4ff" + pluginName
					+ " &8| " + status
					+ " &8| " + required
					+ " &8| &7Version: " + version);
		}

		send(sender, "");
	}

	private void sendPlugin(CommandSender sender, String label, String[] args) {
		if (args.length < 2) {
			send(sender, PREFIX + "Usage: &#ffaa00/" + label + " plugin <name>");
			return;
		}

		String pluginName = args[1];
		Optional<TropicPluginRegistryResult> optionalResult = TropicPluginsAPI.getRegistryResult(pluginName);

		if (optionalResult.isEmpty()) {
			send(sender, PREFIX + "No registered integration found for &#ff4444" + pluginName + "&7.");
			return;
		}

		TropicPluginRegistryResult result = optionalResult.get();

		send(sender, "");
		send(sender, "&#00d4ff&l" + result.entry().pluginName());
		send(sender, "&7Description: &f" + result.entry().description());
		send(sender, "&7Required: " + (result.entry().required() ? "&#ffaa00Yes" : "&#aaaaaaNo"));
		send(sender, "&7Loaded: " + (result.loaded() ? "&#00ff00Yes" : "&#ff4444No"));
		send(sender, "&7Enabled: " + (result.enabled() ? "&#00ff00Yes" : "&#ff4444No"));
		send(sender, "&7Version: " + (result.version() == null ? "&#ff4444Not loaded" : "&f" + result.version()));
		send(sender, "");
	}

	private void send(CommandSender sender, String message) {
		sender.sendMessage(ColorAPI.color(message));
	}
}