package com.okbeanok.tropicapi.api.util;

import java.util.concurrent.TimeUnit;

public final class Durations {

	private Durations() {
	}

	public static String formatMillis(long millis) {
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		return formatSeconds(seconds);
	}

	public static String formatSeconds(long seconds) {
		long days = seconds / 86400;
		seconds %= 86400;
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		seconds %= 60;

		StringBuilder sb = new StringBuilder();
		if (days > 0) sb.append(days).append("d ");
		if (hours > 0) sb.append(hours).append("h ");
		if (minutes > 0) sb.append(minutes).append("m ");
		if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");
		return sb.toString().trim();
	}
}
