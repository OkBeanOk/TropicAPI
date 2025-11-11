package com.okbeanok.tropicapi.api;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Simple color processing API with support for legacy colors and hex codes.
 *
 * Supports:
 * - Legacy colors: &a, &c, &f, etc.
 * - Hex colors: &#FF5555, &#00FF00, etc.
 * - Gradients: <#FF0000>text</#00FF00>
 * - Rainbow: <RAINBOW100>text</RAINBOW>
 *
 * @version 3.0.0
 */
public final class ColorAPI {

	private static final int SERVER_VERSION = detectServerVersion();
	private static final boolean SUPPORTS_HEX = SERVER_VERSION >= 16 || SERVER_VERSION == -1;

	// Pattern to match &#RRGGBB
	private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

	// Pattern to match gradients: <#FF0000>text</#00FF00>
	private static final Pattern GRADIENT_PATTERN = Pattern.compile(
			"<#([A-Fa-f0-9]{6})>(((?!<#[A-Fa-f0-9]{6}>).)*?)</#([A-Fa-f0-9]{6})>"
	);

	// Pattern to match rainbow: <RAINBOW100>text</RAINBOW>
	private static final Pattern RAINBOW_PATTERN = Pattern.compile("<RAINBOW([0-9]{1,3})>(.*?)</RAINBOW>");

	// Pattern to strip all color codes
	private static final Pattern STRIP_PATTERN = Pattern.compile(
			"(?:§x(?:§[0-9A-Fa-f]){6})|(?:§[0-9A-Fa-fk-orK-OR])|(?:&#[A-Fa-f0-9]{6})|(?:&[0-9A-Fa-fk-orK-OR])|" +
					"(?:<#[A-Fa-f0-9]{6}>)|(?:</#[A-Fa-f0-9]{6}>)|(?:<RAINBOW[0-9]{1,3}>)|(?:</RAINBOW>)"
	);

	// Special formatting codes
	private static final List<String> SPECIAL_FORMATTING_CODES = List.of(
			"&l", "&n", "&o", "&k", "&m",
			"§l", "§n", "§o", "§k", "§m"
	);

	private ColorAPI() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * Processes a string to add color formatting.
	 * Supports legacy colors (&a, &c), hex colors (&#FF5555),
	 * gradients (<#FF0000>text</#00FF00>), and rainbow (<RAINBOW100>text</RAINBOW>).
	 *
	 * @param text The string to process
	 * @return The processed string with color codes
	 */
	@Nonnull
	public static String process(@Nonnull String text) {
		if (text == null || text.isEmpty()) {
			return text != null ? text : "";
		}

		// Process in order: gradients, rainbow, hex, then legacy
		text = processGradients(text);
		text = processRainbow(text);

		if (SUPPORTS_HEX) {
			text = processHexColors(text);
		}

		// Finally process legacy colors
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	/**
	 * Processes multiple strings in a collection.
	 *
	 * @param strings The collection of strings to process
	 * @return The list of processed strings with color codes
	 */
	@Nonnull
	public static List<String> process(@Nonnull Collection<String> strings) {
		if (strings == null) {
			return List.of();
		}

		return strings.stream()
				.map(ColorAPI::process)
				.collect(Collectors.toList());
	}

	/**
	 * Colors a string with a gradient from start to end color.
	 *
	 * @param text  The string to color
	 * @param start The starting gradient color
	 * @param end   The ending gradient color
	 * @return The colored string with gradient effect
	 */
	@Nonnull
	public static String color(@Nonnull String text, @Nonnull Color start, @Nonnull Color end) {
		if (text == null || text.isEmpty() || start == null || end == null) {
			return text != null ? text : "";
		}

		// Strip formatting codes to get clean text
		String cleanText = stripFormatting(text);
		if (cleanText.isEmpty()) {
			return text;
		}

		// Create gradient colors
		ChatColor[] colors = createGradient(start, end, cleanText.length());

		// Apply colors to the text
		return applyColors(text, colors);
	}

	/**
	 * Colors a string with rainbow colors.
	 *
	 * @param text       The string to color
	 * @param saturation The saturation of the rainbow (0.0 to 1.0)
	 * @return The colored string with rainbow effect
	 */
	@Nonnull
	public static String rainbow(@Nonnull String text, float saturation) {
		if (text == null || text.isEmpty()) {
			return text != null ? text : "";
		}

		// Strip formatting codes to get clean text
		String cleanText = stripFormatting(text);
		if (cleanText.isEmpty()) {
			return text;
		}

		// Create rainbow colors
		ChatColor[] colors = createRainbow(cleanText.length(), saturation);

		// Apply colors to the text
		return applyColors(text, colors);
	}

	/**
	 * Removes all color and formatting codes from the string.
	 *
	 * @param text The string to strip
	 * @return The stripped string without any color codes
	 */
	@Nonnull
	public static String strip(@Nonnull String text) {
		if (text == null || text.isEmpty()) {
			return text != null ? text : "";
		}
		return STRIP_PATTERN.matcher(text).replaceAll("");
	}

	/**
	 * Checks if the server supports hex colors (1.16+).
	 *
	 * @return true if hex is supported, false otherwise
	 */
	public static boolean supportsHex() {
		return SUPPORTS_HEX;
	}

	/**
	 * Gets the detected server version.
	 *
	 * @return The major version number, or -1 for BungeeCord
	 */
	public static int getServerVersion() {
		return SERVER_VERSION;
	}

	// ==================== Private Helper Methods ====================

	/**
	 * Processes gradient patterns in the format <#FF0000>text</#00FF00>
	 */
	@Nonnull
	private static String processGradients(@Nonnull String text) {
		Matcher matcher = GRADIENT_PATTERN.matcher(text);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			String startHex = matcher.group(1);
			String content = matcher.group(2);
			String endHex = matcher.group(4);

			try {
				Color start = new Color(Integer.parseInt(startHex, 16));
				Color end = new Color(Integer.parseInt(endHex, 16));
				String gradient = color(content, start, end);
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(gradient));
			} catch (Exception e) {
				// If parsing fails, keep original text
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(matcher.group()));
			}
		}
		matcher.appendTail(buffer);

		return buffer.toString();
	}

	/**
	 * Processes rainbow patterns in the format <RAINBOW100>text</RAINBOW>
	 */
	@Nonnull
	private static String processRainbow(@Nonnull String text) {
		Matcher matcher = RAINBOW_PATTERN.matcher(text);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			String saturationStr = matcher.group(1);
			String content = matcher.group(2);

			try {
				float saturation = Float.parseFloat(saturationStr) / 100f;
				String rainbowText = rainbow(content, saturation);
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(rainbowText));
			} catch (Exception e) {
				// If parsing fails, keep original text
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(matcher.group()));
			}
		}
		matcher.appendTail(buffer);

		return buffer.toString();
	}

	/**
	 * Processes hex color codes in the format &#RRGGBB
	 */
	@Nonnull
	private static String processHexColors(@Nonnull String text) {
		Matcher matcher = HEX_PATTERN.matcher(text);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			String hexCode = matcher.group(1);
			String replacement = convertHexToMinecraft(hexCode);
			matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(buffer);

		return buffer.toString();
	}

	/**
	 * Converts a hex color code to Minecraft's color format.
	 *
	 * @param hexCode The hex code (6 characters, without #)
	 * @return The Minecraft color code string
	 */
	@Nonnull
	private static String convertHexToMinecraft(@Nonnull String hexCode) {
		try {
			// Parse the hex color
			int rgb = Integer.parseInt(hexCode, 16);
			Color color = new Color(rgb);

			// Use ChatColor.of() for RGB support
			ChatColor chatColor = ChatColor.of(color);
			return chatColor.toString();

		} catch (Exception e) {
			// Return white on error
			return ChatColor.WHITE.toString();
		}
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
		length = Math.max(length, 1);
		ChatColor[] colors = new ChatColor[length];

		if (length == 1) {
			colors[0] = SUPPORTS_HEX ? ChatColor.of(start) : getClosestLegacyColor(start);
			return colors;
		}

		for (int i = 0; i < length; i++) {
			// Calculate interpolation factor (0.0 to 1.0)
			double ratio = (double) i / (length - 1);

			// Interpolate each RGB component
			int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
			int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
			int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

			// Clamp values to valid range
			red = Math.max(0, Math.min(255, red));
			green = Math.max(0, Math.min(255, green));
			blue = Math.max(0, Math.min(255, blue));

			Color color = new Color(red, green, blue);
			colors[i] = SUPPORTS_HEX ? ChatColor.of(color) : getClosestLegacyColor(color);
		}

		return colors;
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
			Color color = Color.getHSBColor((float) (hueStep * i), saturation, 1.0f);

			if (SUPPORTS_HEX) {
				colors[i] = ChatColor.of(color);
			} else {
				// Fallback to closest legacy color
				colors[i] = getClosestLegacyColor(color);
			}
		}

		return colors;
	}

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
	 * Finds the closest legacy color for a given RGB color.
	 * Uses Euclidean distance in RGB color space.
	 *
	 * @param color The target color
	 * @return The closest legacy ChatColor
	 */
	@Nonnull
	private static ChatColor getClosestLegacyColor(@Nonnull Color color) {
		ChatColor closest = ChatColor.WHITE;
		double minDistance = Double.MAX_VALUE;

		// Legacy color mapping
		Color[] legacyColors = {
				new Color(0x000000), // Black - 0
				new Color(0x0000AA), // Dark Blue - 1
				new Color(0x00AA00), // Dark Green - 2
				new Color(0x00AAAA), // Dark Aqua - 3
				new Color(0xAA0000), // Dark Red - 4
				new Color(0xAA00AA), // Dark Purple - 5
				new Color(0xFFAA00), // Gold - 6
				new Color(0xAAAAAA), // Gray - 7
				new Color(0x555555), // Dark Gray - 8
				new Color(0x5555FF), // Blue - 9
				new Color(0x55FF55), // Green - a
				new Color(0x55FFFF), // Aqua - b
				new Color(0xFF5555), // Red - c
				new Color(0xFF55FF), // Light Purple - d
				new Color(0xFFFF55), // Yellow - e
				new Color(0xFFFFFF)  // White - f
		};

		char[] colorCodes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

		for (int i = 0; i < legacyColors.length; i++) {
			double distance = calculateColorDistance(color, legacyColors[i]);
			if (distance < minDistance) {
				closest = ChatColor.getByChar(colorCodes[i]);
				minDistance = distance;
			}
		}

		return closest;
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
			Bukkit.getLogger().info("[ColorAPI] Detected BungeeCord - Enabling hex support");
			return -1;
		}

		try {
			String version = Bukkit.getVersion();
			String originalVersion = version;

			Bukkit.getLogger().info("[ColorAPI] Detecting server version from: " + version);

			// Parse version from getVersion() format: "git-Paper-123 (MC: 1.20.1)"
			int mcIndex = version.lastIndexOf("MC:");
			if (mcIndex != -1) {
				version = version.substring(mcIndex + 4, version.length() - 1).trim();
				Bukkit.getLogger().info("[ColorAPI] Extracted from MC: tag: " + version);
			} else if (version.endsWith("SNAPSHOT")) {
				// Parse from getBukkitVersion() format: "1.20.1-SNAPSHOT"
				int dashIndex = version.indexOf('-');
				if (dashIndex != -1) {
					version = version.substring(0, dashIndex);
					Bukkit.getLogger().info("[ColorAPI] Extracted from SNAPSHOT: " + version);
				}
			}

			// Extract major version from "1.X.Y" format
			int lastDot = version.lastIndexOf('.');
			int firstDot = version.indexOf('.');
			if (firstDot != lastDot) {
				version = version.substring(0, lastDot);
				Bukkit.getLogger().info("[ColorAPI] Trimmed to major.minor: " + version);
			}

			// Extract the major version number after "1."
			int majorVersion = Integer.parseInt(version.substring(version.indexOf('.') + 1));

			Bukkit.getLogger().info("[ColorAPI] ========================================");
			Bukkit.getLogger().info("[ColorAPI] Version Detection Results:");
			Bukkit.getLogger().info("[ColorAPI] Raw version string: " + originalVersion);
			Bukkit.getLogger().info("[ColorAPI] Detected major version: 1." + majorVersion);
			Bukkit.getLogger().info("[ColorAPI] Hex Support: " + (majorVersion >= 16 ? "ENABLED" : "DISABLED"));
			Bukkit.getLogger().info("[ColorAPI] ========================================");

			return majorVersion;
		} catch (Exception e) {
			Bukkit.getLogger().warning("[ColorAPI] Failed to detect server version: " + e.getMessage());
			Bukkit.getLogger().warning("[ColorAPI] Defaulting to version 21 (assuming hex support)");
			return 21;
		}
	}

	/**
	 * Checks if a class exists in the classpath.
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