package com.okbeanok.tropicapi.internal.message;

import com.okbeanok.tropicapi.api.color.ColorService;
import com.okbeanok.tropicapi.api.message.CenteredMessagesService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Default implementation of CenteredMessagesService using a
 * pixel-width approximation for Minecraft chat.
 */
public final class CenteredMessagesServiceImpl implements CenteredMessagesService {

	// Commonly used approximate center in pixels
	private static final int CENTER_PX = 154;

	private final ColorService colorService;

	public CenteredMessagesServiceImpl(ColorService colorService) {
		this.colorService = colorService;
	}

	@Override
	public void sendCentered(CommandSender sender, String message) {
		if (sender == null || message == null) return;
		sender.sendMessage(toCenteredLine(message));
	}

	@Override
	public void sendCentered(Player player, String message) {
		sendCentered((CommandSender) player, message);
	}

	@Override
	public String toCenteredLine(String message) {
		String colored = colorService.colorize(message);
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : colored.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
				} else if (c == 'r' || c == 'R') {
					isBold = false;
				}
				continue;
			}

			messagePxSize += getCharWidth(c, isBold);
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spacePx = getCharWidth(' ', false);
		int spaces = toCompensate / spacePx;

		if (spaces <= 0) {
			return colored;
		}

		return " ".repeat(spaces) + colored;
	}

	private int getCharWidth(char c, boolean bold) {
		// very rough widths; can be improved
		int width = switch (c) {
			case 'i', 'l', '.', ',', ':', ';', '!' -> 2;
			case ' ', '\t' -> 4;
			default -> 6;
		};
		if (bold) width++;
		return width;
	}
}