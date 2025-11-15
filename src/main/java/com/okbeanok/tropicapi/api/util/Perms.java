package com.okbeanok.tropicapi.api.util;

import org.bukkit.command.CommandSender;

public final class Perms {

	private Perms() {
	}

	public static boolean has(CommandSender sender, String permission) {
		return sender.hasPermission(permission);
	}

	public static void require(CommandSender sender, String permission,
							   Runnable onGranted, Runnable onDenied) {
		if (sender.hasPermission(permission)) {
			if (onGranted != null) onGranted.run();
		} else {
			if (onDenied != null) onDenied.run();
		}
	}

	public static void ifHas(CommandSender sender, String permission, Runnable action) {
		require(sender, permission, action, null);
	}
}
