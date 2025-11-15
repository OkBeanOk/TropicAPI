package com.okbeanok.tropicapi.api.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Service for sending/formatting centered messages.
 */
public interface CenteredMessagesService {

	void sendCentered(CommandSender sender, String message);

	void sendCentered(Player player, String message);

	/**
	 * Returns a single line padded with spaces to look centered in chat.
	 */
	String toCenteredLine(String message);
}
