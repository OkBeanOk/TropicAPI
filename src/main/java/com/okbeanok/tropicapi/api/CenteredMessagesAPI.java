package com.okbeanok.tropicapi.api;

import com.okbeanok.tropicapi.api.utils.color.DefaultFontInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CenteredMessagesAPI {
	public void sendCenteredMessagePrivate(Player player, String message){
		if (message == null || message.equals("")){
			player.sendMessage("");
			return;
		}
		message = ColorAPI.process(message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char c : message.toCharArray()){
			if (c == '�') {
				previousCode = true;
			} else if (previousCode) {
				previousCode = false;
				isBold = c == 'l' || c == 'L';
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}
		int CENTER_PX = 154;
		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX -halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate){
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb + message);
	}

	public void sendCenteredMessagePublic(Player player, String message, Event event){
		if (message == null || message.equals("")){
			player.sendMessage("");
		}
	}
}
