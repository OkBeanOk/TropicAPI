package com.okbeanok.tropicapi.api.color;

import com.google.common.collect.ImmutableMap;
import com.okbeanok.tropicapi.api.utils.color.GradientPattern;
import com.okbeanok.tropicapi.api.utils.color.Pattern;
import com.okbeanok.tropicapi.api.utils.color.RainbowPattern;
import com.okbeanok.tropicapi.api.utils.color.SolidPattern;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Advanced color processing API with support for RGB, gradients, and rainbow effects.
 * Provides backward compatibility with legacy color codes while supporting modern RGB colors.
 *
 * @author IridiumColorAPI Contributors
 * @version 2.0.0
 * @since 1.0.0
 */
public final class IridiumColorAPI {

	// ==================== Constants ====================

	/**
	 * The current major version of the Minecraft server.
	 * Returns -1 if running on BungeeCord.
	 *
	 * @since 1.0.0
	 */
	private static final int SERVER_VERSION = detectServerVersion();

	/**
	 * Whether the server supports RGB colors (1.16+).
	 *
	 * @since 1.0.0
	 */
	private static final boolean SUPPORTS_RGB = SERVER_VERSION >= 16 || SERVER_VERSION == -1;

	/**
	 * Special formatting codes that don't affect color but add text styling.
	 *
	 * @since 1.0.0
	 */
	private static final List<String> SPECIAL_FORMATTING_CODES = List.of(
			"&l", "&n", "&o", "&k", "&m",
			"§l", "§n", "§o", "§k", "§m"
	);

	/**
	 * Mapping of legacy colors to their ChatColor equivalents.
	 * Used for fallback when RGB is not supported.
	 *
	 * @since 1.0.0
	 */
	private static final Map<Color, ChatColor> LEGACY_COLORS = ImmutableMap.<Color, ChatColor>builder()
			.put(new Color(0x000000), ChatColor.getByChar('0'))        // Black
			.put(new Color(0x0000AA), ChatColor.getByChar('1'))        // Dark Blue
			.put(new Color(0x00AA00), ChatColor.getByChar('2'))        // Dark Green
			.put(new Color(0x00AAAA), ChatColor.getByChar('3'))        // Dark Aqua
			.put(new Color(0xAA0000), ChatColor.getByChar('4'))        // Dark Red
			.put(new Color(0xAA00AA), ChatColor.getByChar('5'))        // Dark Purple
			.put(new Color(0xFFAA00), ChatColor.getByChar('6'))        // Gold
			.put(new Color(0xAAAAAA), ChatColor.getByChar('7'))        // Gray
			.put(new Color(0x555555), ChatColor.getByChar('8'))        // Dark Gray
			.put(new Color(0x5555FF), ChatColor.getByChar('9'))        // Blue
			.put(new Color(0x55FF55), ChatColor.getByChar('a'))        // Green
			.put(new Color(0x55FFFF), ChatColor.getByChar('b'))        // Aqua
			.put(new Color(0xFF5555), ChatColor.getByChar('c'))        // Red
			.put(new Color(0xFF55FF), ChatColor.getByChar('d'))        // Light Purple
			.put(new Color(0xFFFF55), ChatColor.getByChar('e'))        // Yellow
			.put(new Color(0xFFFFFF), ChatColor.getByChar('f'))        // White
			.build();

	/**
	 * List of color patterns to process in order.
	 *
	 * @since 1.0.2
	 */
	private static final List<Pattern> COLOR_PATTERNS = List.of(
			new GradientPattern(),
			new SolidPattern(),
			new RainbowPattern()
	);

	/**
	 * Regex pattern string for stripping color codes.
	 *
	 * @since 2.0.0
	 */
	private static final String COLOR_STRIP_REGEX = "<#[0-9A-Fa-f]{6}>|[&§][0-9A-Fa-flnokm]|</?[A-Z]{5,8}(:[0-9A-Fa-f]{6})?\\d*>";

	// ==================== Constructor ====================

	/**
	 * Private constructor to prevent instantiation.
	 * This is a utility class with only static methods.
	 */
	private IridiumColorAPI() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	// ==================== Public API Methods ====================

	/**
	 * Processes a string to add color formatting.
	 * Supports gradients, solid colors, rainbow effects, and legacy color codes.
	 *
	 * @param string The string to process
	 * @return The processed string with color codes
	 * @since 1.0.0
	 */
	@Nonnull
	public static String process(@Nonnull String string) {
		if (string.isEmpty()) {
			return string;
		}

		String processed = string;
		for (Pattern pattern : COLOR_PATTERNS) {
			processed = pattern.process(processed);
		}

		return ChatColor.translateAlternateColorCodes('&', processed);
	}

	/**
	 * Processes multiple strings in a collection.
	 *
	 * @param strings The collection of strings to process
	 * @return The list of processed strings with color codes
	 * @since 1.0.3
	 */
	@Nonnull
	public static List<String> process(@Nonnull Collection<String> strings) {
		if (strings == null) {
			return List.of();
		}

		return strings.stream()
				.map(IridiumColorAPI::process)
				.toList();
	}

	/**
	 * Colors a string with a single solid color.
	 *
	 * @param string The string to color
	 * @param color  The color to apply
	 * @return The colored string
	 * @since 1.0.0
	 */
	@Nonnull
	public static String color(@Nonnull String string, @Nonnull Color color) {
		if (string == null || color == null) {
			return string != null ? string : "";
		}

		ChatColor chatColor = SUPPORTS_RGB ? ChatColor.of(color) : getClosestLegacyColor(color);
		return chatColor + string;
	}

	/**
	 * Colors a string with a gradient from start to end color.
	 *
	 * @param string The string to color
	 * @param start  The starting gradient color
	 * @param end    The ending gradient color
	 * @return The colored string with gradient effect
	 * @since 1.0.0
	 */
	@Nonnull
	public static String color(@Nonnull String string, @Nonnull Color start, @Nonnull Color end) {
		if (string == null || start == null || end == null) {
			return string != null ? string : "";
		}

		String cleanString = stripFormatting(string);
		if (cleanString.isEmpty()) {
			return string;
		}

		ChatColor[] colors = createGradient(start, end, cleanString.length());
		return applyColors(string, colors);
	}

	/**
	 * Colors a string with rainbow colors.
	 *
	 * @param string     The string to color
	 * @param saturation The saturation of the rainbow (0.0 to 1.0)
	 * @return The colored string with rainbow effect
	 * @since 1.0.3
	 */
	@Nonnull
	public static String rainbow(@Nonnull String string, float saturation) {
		if (string == null) {
			return "";
		}

		String cleanString = stripFormatting(string);
		if (cleanString.isEmpty()) {
			return string;
		}

		ChatColor[] colors = createRainbow(cleanString.length(), saturation);
		return applyColors(string, colors);
	}

	/**
	 * Parses a hex color code and returns the corresponding ChatColor.
	 *
	 * @param hexCode The hex code (without # prefix)
	 * @return The ChatColor representation
	 * @since 1.0.0
	 */
	@Nonnull
	public static ChatColor getColor(@Nonnull String hexCode) {
		try {
			Color color = new Color(Integer.parseInt(hexCode, 16));
			return SUPPORTS_RGB ? ChatColor.of(color) : getClosestLegacyColor(color);
		} catch (NumberFormatException e) {
			return ChatColor.WHITE;
		}
	}

	/**
	 * Removes all color and formatting codes from the string.
	 *
	 * @param string The string to strip
	 * @return The stripped string without any color codes
	 * @since 1.0.5
	 */
	@Nonnull
	public static String stripColorFormatting(@Nonnull String string) {
		if (string == null || string.isEmpty()) {
			return string != null ? string : "";
		}
		return string.replaceAll(COLOR_STRIP_REGEX, "");
	}

	/**
	 * Checks if the server supports RGB colors (1.16+).
	 *
	 * @return true if RGB is supported, false otherwise
	 * @since 2.0.0
	 */
	public static boolean supportsRGB() {
		return SUPPORTS_RGB;
	}

	/**
	 * Gets the detected server version.
	 *
	 * @return The major version number, or -1 for BungeeCord
	 * @since 2.0.0
	 */
	public static int getServerVersion() {
		return SERVER_VERSION;
	}

	// ==================== Private Helper Methods ====================

	/**
	 * Applies an array of colors to a string, preserving special formatting codes.
	 *
	 * @param source The source string
	 * @param colors The array of colors to apply
	 * @return The colored string
	 */
	@Nonnull
	private static String applyColors(@Nonnull String source, @Nonnull ChatColor[] colors) {
		StringBuilder formatting = new StringBuilder();
		StringBuilder result = new StringBuilder();
		int colorIndex = 0;

		for (int i = 0; i < source.length(); i++) {
			char current = source.charAt(i);

			// Check if this is a color/formatting code
			if ((current == '&' || current == '§') && i + 1 < source.length()) {
				char next = source.charAt(i + 1);

				// Reset formatting on &r or §r
				if (next == 'r' || next == 'R') {
					formatting.setLength(0);
				} else {
					formatting.append(current).append(next);
				}
				i++; // Skip the next character
				continue;
			}

			// Apply color and formatting to the character
			if (colorIndex < colors.length) {
				result.append(colors[colorIndex++]).append(formatting).append(current);
			} else {
				result.append(current);
			}
		}

		return result.toString();
	}

	/**
	 * Strips special formatting codes from a string.
	 *
	 * @param source The source string
	 * @return The string without special formatting codes
	 */
	@Nonnull
	private static String stripFormatting(@Nonnull String source) {
		String result = source;
		for (String code : SPECIAL_FORMATTING_CODES) {
			result = result.replace(code, "");
		}
		return result;
	}

	/**
	 * Creates a rainbow color array.
	 *
	 * @param length     The number of colors to generate
	 * @param saturation The saturation value (0.0 to 1.0)
	 * @return Array of rainbow colors
	 */
	@Nonnull
	private static ChatColor[] createRainbow(int length, float saturation) {
		ChatColor[] colors = new ChatColor[length];
		double hueStep = 1.0 / length;

		for (int i = 0; i < length; i++) {
			Color color = Color.getHSBColor((float) (hueStep * i), saturation, saturation);
			colors[i] = SUPPORTS_RGB ? ChatColor.of(color) : getClosestLegacyColor(color);
		}

		return colors;
	}

	/**
	 * Creates a gradient color array.
	 *
	 * @param start  The starting color
	 * @param end    The ending color
	 * @param length The number of colors to generate
	 * @return Array of gradient colors
	 */
	@Nonnull
	private static ChatColor[] createGradient(@Nonnull Color start, @Nonnull Color end, int length) {
		length = Math.max(length, 2);
		ChatColor[] colors = new ChatColor[length];

		// Calculate step sizes for each RGB component
		int redStep = Math.abs(start.getRed() - end.getRed()) / (length - 1);
		int greenStep = Math.abs(start.getGreen() - end.getGreen()) / (length - 1);
		int blueStep = Math.abs(start.getBlue() - end.getBlue()) / (length - 1);

		// Determine direction for each component
		int redDirection = Integer.compare(end.getRed(), start.getRed());
		int greenDirection = Integer.compare(end.getGreen(), start.getGreen());
		int blueDirection = Integer.compare(end.getBlue(), start.getBlue());

		for (int i = 0; i < length; i++) {
			int red = start.getRed() + (redStep * i * redDirection);
			int green = start.getGreen() + (greenStep * i * greenDirection);
			int blue = start.getBlue() + (blueStep * i * blueDirection);

			Color color = new Color(
					Math.clamp(red, 0, 255),
					Math.clamp(green, 0, 255),
					Math.clamp(blue, 0, 255)
			);

			colors[i] = SUPPORTS_RGB ? ChatColor.of(color) : getClosestLegacyColor(color);
		}

		return colors;
	}

	/**
	 * Finds the closest legacy color for a given RGB color.
	 * Uses Euclidean distance in RGB color space.
	 *
	 * @param color The target color
	 * @return The closest legacy ChatColor
	 */
	@Nonnull
	private static ChatColor getClosestLegacyColor(@Nonnull Color color) {
		Color closest = null;
		double minDistance = Double.MAX_VALUE;

		for (Map.Entry<Color, ChatColor> entry : LEGACY_COLORS.entrySet()) {
			Color legacy = entry.getKey();
			double distance = calculateColorDistance(color, legacy);

			if (distance < minDistance) {
				closest = legacy;
				minDistance = distance;
			}
		}

		return LEGACY_COLORS.get(closest);
	}

	/**
	 * Calculates the Euclidean distance between two colors in RGB space.
	 *
	 * @param c1 First color
	 * @param c2 Second color
	 * @return The distance between the colors
	 */
	private static double calculateColorDistance(@Nonnull Color c1, @Nonnull Color c2) {
		int redDiff = c1.getRed() - c2.getRed();
		int greenDiff = c1.getGreen() - c2.getGreen();
		int blueDiff = c1.getBlue() - c2.getBlue();

		return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
	}

	/**
	 * Detects the major version of the Minecraft server.
	 *
	 * @return The major version number, or -1 for BungeeCord
	 */
	private static int detectServerVersion() {
		// Check if running on BungeeCord
		if (!classExists("org.bukkit.Bukkit") && classExists("net.md_5.bungee.api.ChatColor")) {
			return -1;
		}

		try {
			String version = Bukkit.getVersion();
			Validate.notEmpty(version, "Cannot get major Minecraft version from null or empty string");

			// Parse version from getVersion() format: "git-Paper-123 (MC: 1.20.1)"
			int mcIndex = version.lastIndexOf("MC:");
			if (mcIndex != -1) {
				version = version.substring(mcIndex + 4, version.length() - 1).trim();
			} else if (version.endsWith("SNAPSHOT")) {
				// Parse from getBukkitVersion() format: "1.20.1-SNAPSHOT"
				int dashIndex = version.indexOf('-');
				if (dashIndex != -1) {
					version = version.substring(0, dashIndex);
				}
			}

			// Extract major version from "1.X.Y" format
			int lastDot = version.lastIndexOf('.');
			int firstDot = version.indexOf('.');
			if (firstDot != lastDot) {
				version = version.substring(0, lastDot);
			}

			// Extract the major version number after "1."
			return Integer.parseInt(version.substring(version.indexOf('.') + 1));
		} catch (Exception e) {
			Bukkit.getLogger().warning("[IridiumColorAPI] Failed to detect server version: " + e.getMessage());
			return 16; // Default to 16 (assume RGB support)
		}
	}

	/**
	 * Checks if a class exists in the classpath.
	 *
	 * @param className The fully qualified class name
	 * @return true if the class exists, false otherwise
	 */
	private static boolean classExists(@Nonnull String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}