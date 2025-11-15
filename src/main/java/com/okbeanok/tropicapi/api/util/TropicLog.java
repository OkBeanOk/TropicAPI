package com.okbeanok.tropicapi.api.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Structured logger with module names.
 */
public final class TropicLog {

	private static Logger baseLogger;

	private TropicLog() {
	}

	public static void init(Logger logger) {
		baseLogger = logger;
	}

	private static Logger logger() {
		if (baseLogger == null) {
			throw new IllegalStateException("TropicLog not initialized. Is TropicAPI enabled?");
		}
		return baseLogger;
	}

	public static void info(String module, String message) {
		log(Level.INFO, module, message);
	}

	public static void warn(String module, String message) {
		log(Level.WARNING, module, message);
	}

	public static void error(String module, String message) {
		log(Level.SEVERE, module, message);
	}

	private static void log(Level level, String module, String message) {
		logger().log(level, "[{0}] {1}", new Object[]{module, message});
	}
}
