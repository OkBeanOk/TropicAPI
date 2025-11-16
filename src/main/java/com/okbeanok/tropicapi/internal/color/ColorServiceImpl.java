package com.okbeanok.tropicapi.internal.color;

import com.okbeanok.tropicapi.api.color.ColorService;
import com.okbeanok.tropicapi.api.util.NMSUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ColorServiceImpl implements ColorService {

	private static final int CHARS_UNTIL_LOOP = 30;
	private static final Pattern RAINBOW_PATTERN = Pattern.compile(
			"<(?<type>rainbow|r)(#(?<speed>\\d+))?(:(?<saturation>\\d*\\.?\\d+))?(:(?<brightness>\\d*\\.?\\d+))?(:(?<loop>l|L|loop))?>"
	);
	private static final Pattern GRADIENT_PATTERN = Pattern.compile(
			"<(?<type>gradient|g)(#(?<speed>\\d+))?(?<hex>(:#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})){2,})(:(?<loop>l|L|loop))?>"
	);
	private static final List<Pattern> HEX_PATTERNS = Arrays.asList(
			Pattern.compile("<#([A-Fa-f0-9]){6}>"),   // <#FFFFFF>
			Pattern.compile("\\{#([A-Fa-f0-9]){6}}"), // {#FFFFFF}
			Pattern.compile("&#([A-Fa-f0-9]){6}"),    // &#FFFFFF
			Pattern.compile("#([A-Fa-f0-9]){6}")      // #FFFFFF
	);

	private static final Pattern STOP = Pattern.compile(
			"<(rainbow|r)(#(\\d+))?(:(\\d*\\.?\\d+))?(:(\\d*\\.?\\d+))?(:(l|L|loop))?>|" +
					"<(gradient|g)(#(\\d+))?((:#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})){2,})(:(l|L|loop))?>|" +
					"(&[a-f0-9r])|" +
					"<#([A-Fa-f0-9]){6}>|" +
					"\\{#([A-Fa-f0-9]){6}}|" +
					"&#([A-Fa-f0-9]){6}|" +
					"#([A-Fa-f0-9]){6}|" +
					org.bukkit.ChatColor.COLOR_CHAR
	);

	@Override
	public String colorize(String input) {
		if (input == null) {
			return null;
		}
		// Full pipeline: rainbow → gradients → hex → legacy
		String parsed = input;
		parsed = parseRainbow(parsed);
		parsed = parseGradients(parsed);
		parsed = parseHex(parsed);
		parsed = parseLegacy(parsed);
		return parsed;
	}

	@Override
	public String stripColors(String input) {
		if (input == null) return null;
		// Normalize through the pipeline, then strip Bukkit-style § codes
		return org.bukkit.ChatColor.stripColor(colorize(input));
	}

	@Override
	public void sendColored(CommandSender sender, String message) {
		if (sender == null || message == null) return;
		sender.sendMessage(colorize(message));
	}

	@Override
	public void sendColored(Player player, String message) {
		sendColored((CommandSender) player, message);
	}

	// -------------------------------------------------------------------------
	// Internal helpers (merged from util ColorAPI)
	// -------------------------------------------------------------------------

	private static String getCaptureGroup(Matcher matcher, String group) {
		try {
			return matcher.group(group);
		} catch (IllegalStateException | IllegalArgumentException e) {
			return null;
		}
	}

	private static String parseRainbow(String message) {
		String parsed = message;

		Matcher matcher = RAINBOW_PATTERN.matcher(parsed);
		while (matcher.find()) {
			StringBuilder parsedRainbow = new StringBuilder();

			int speed = -1;
			float saturation = 1.0F;
			float brightness = 1.0F;
			boolean looping = getCaptureGroup(matcher, "looping") != null;

			String speedGroup = getCaptureGroup(matcher, "speed");
			if (speedGroup != null) {
				try {
					speed = Integer.parseInt(speedGroup);
				} catch (NumberFormatException ignored) {
				}
			}

			String saturationGroup = getCaptureGroup(matcher, "saturation");
			if (saturationGroup != null) {
				try {
					saturation = Float.parseFloat(saturationGroup);
				} catch (NumberFormatException ignored) {
				}
			}

			String brightnessGroup = getCaptureGroup(matcher, "brightness");
			if (brightnessGroup != null) {
				try {
					brightness = Float.parseFloat(brightnessGroup);
				} catch (NumberFormatException ignored) {
				}
			}

			int stop = findStop(parsed, matcher.end());
			String content = parsed.substring(matcher.end(), stop);
			int contentLength = content.length();
			char[] chars = content.toCharArray();
			for (int i = 0; i < chars.length - 1; i++) {
				if (chars[i] == '&' && "KkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
					contentLength -= 2;
				}
			}

			int length = looping ? Math.min(contentLength, CHARS_UNTIL_LOOP) : contentLength;

			ColorGenerator rainbow;
			if (speed == -1) {
				rainbow = new Rainbow(length, saturation, brightness);
			} else {
				rainbow = new AnimatedRainbow(length, saturation, brightness, speed);
			}

			String compoundedFormat = "";
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == '&' && i + 1 < chars.length) {
					char next = chars[i + 1];
					org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(next);
					if (color != null && color.isFormat()) {
						compoundedFormat += String.valueOf(ChatColor.COLOR_CHAR) + next;
						i++;
						continue;
					}
				}
				parsedRainbow.append(rainbow.nextChatColor()).append(compoundedFormat).append(c);
			}

			String before = parsed.substring(0, matcher.start());
			String after = parsed.substring(stop);
			parsed = before + parsedRainbow + after;
			matcher = RAINBOW_PATTERN.matcher(parsed);
		}

		return parsed;
	}

	private static String parseGradients(String message) {
		String parsed = message;

		Matcher matcher = GRADIENT_PATTERN.matcher(parsed);
		while (matcher.find()) {
			StringBuilder parsedGradient = new StringBuilder();

			int speed = -1;
			boolean looping = getCaptureGroup(matcher, "loop") != null;

			List<Color> hexSteps = Arrays.stream(getCaptureGroup(matcher, "hex").substring(1).split(":"))
					.map(x -> x.length() != 4
							? x
							: String.format("#%s%s%s%s%s%s",
							x.charAt(1), x.charAt(1),
							x.charAt(2), x.charAt(2),
							x.charAt(3), x.charAt(3)))
					.map(Color::decode)
					.collect(Collectors.toList());

			String speedGroup = getCaptureGroup(matcher, "speed");
			if (speedGroup != null) {
				try {
					speed = Integer.parseInt(speedGroup);
				} catch (NumberFormatException ignored) {
				}
			}

			int stop = findStop(parsed, matcher.end());
			String content = parsed.substring(matcher.end(), stop);
			int contentLength = content.length();
			char[] chars = content.toCharArray();
			for (int i = 0; i < chars.length - 1; i++) {
				if (chars[i] == '&' && "KkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
					contentLength -= 2;
				}
			}

			int length = looping ? Math.min(contentLength, CHARS_UNTIL_LOOP) : contentLength;
			ColorGenerator gradient;
			if (speed == -1) {
				gradient = new Gradient(hexSteps, length);
			} else {
				gradient = new AnimatedGradient(hexSteps, length, speed);
			}

			String compoundedFormat = "";
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == '&' && i + 1 < chars.length) {
					char next = chars[i + 1];
					org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(next);
					if (color != null && color.isFormat()) {
						compoundedFormat += String.valueOf(ChatColor.COLOR_CHAR) + next;
						i++;
						continue;
					}
				}
				parsedGradient.append(gradient.nextChatColor()).append(compoundedFormat).append(c);
			}

			String before = parsed.substring(0, matcher.start());
			String after = parsed.substring(stop);
			parsed = before + parsedGradient + after;
			matcher = GRADIENT_PATTERN.matcher(parsed);
		}

		return parsed;
	}

	private static String parseHex(String message) {
		String parsed = message;

		for (Pattern pattern : HEX_PATTERNS) {
			Matcher matcher = pattern.matcher(parsed);
			while (matcher.find()) {
				String color = translateHex(cleanHex(matcher.group())).toString();
				String before = parsed.substring(0, matcher.start());
				String after = parsed.substring(matcher.end());
				parsed = before + color + after;
				matcher = pattern.matcher(parsed);
			}
		}

		return parsed;
	}

	private static String parseLegacy(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	private static int findStop(String content, int searchAfter) {
		Matcher matcher = STOP.matcher(content);
		while (matcher.find()) {
			if (matcher.start() > searchAfter)
				return matcher.start();
		}
		return content.length();
	}

	private static String cleanHex(String hex) {
		if (hex.startsWith("<") || hex.startsWith("{")) {
			return hex.substring(1, hex.length() - 1);
		} else if (hex.startsWith("&")) {
			return hex.substring(1);
		} else {
			return hex;
		}
	}

	private static ChatColor translateHex(String hex) {
		if (NMSUtil.getVersionNumber() >= 16)
			return ChatColor.of(hex);
		return translateHex(Color.decode(hex));
	}

	private static ChatColor translateHex(Color color) {
		if (NMSUtil.getVersionNumber() >= 16)
			return ChatColor.of(color);

		int minDist = Integer.MAX_VALUE;
		ChatColor legacy = ChatColor.WHITE;
		for (ChatColorHexMapping mapping : ChatColorHexMapping.values()) {
			int r = mapping.getRed() - color.getRed();
			int g = mapping.getGreen() - color.getGreen();
			int b = mapping.getBlue() - color.getBlue();
			int dist = r * r + g * g + b * b;
			if (dist < minDist) {
				minDist = dist;
				legacy = mapping.getChatColor();
			}
		}

		return legacy;
	}

	private enum ChatColorHexMapping {

		BLACK(0x000000, ChatColor.BLACK),
		DARK_BLUE(0x0000AA, ChatColor.DARK_BLUE),
		DARK_GREEN(0x00AA00, ChatColor.DARK_GREEN),
		DARK_AQUA(0x00AAAA, ChatColor.DARK_AQUA),
		DARK_RED(0xAA0000, ChatColor.DARK_RED),
		DARK_PURPLE(0xAA00AA, ChatColor.DARK_PURPLE),
		GOLD(0xFFAA00, ChatColor.GOLD),
		GRAY(0xAAAAAA, ChatColor.GRAY),
		DARK_GRAY(0x555555, ChatColor.DARK_GRAY),
		BLUE(0x5555FF, ChatColor.BLUE),
		GREEN(0x55FF55, ChatColor.GREEN),
		AQUA(0x55FFFF, ChatColor.AQUA),
		RED(0xFF5555, ChatColor.RED),
		LIGHT_PURPLE(0xFF55FF, ChatColor.LIGHT_PURPLE),
		YELLOW(0xFFFF55, ChatColor.YELLOW),
		WHITE(0xFFFFFF, ChatColor.WHITE);

		private final int r, g, b;
		private final ChatColor chatColor;

		ChatColorHexMapping(int hex, ChatColor chatColor) {
			this.r = (hex >> 16) & 0xFF;
			this.g = (hex >> 8) & 0xFF;
			this.b = hex & 0xFF;
			this.chatColor = chatColor;
		}

		public int getRed() {
			return this.r;
		}

		public int getGreen() {
			return this.g;
		}

		public int getBlue() {
			return this.b;
		}

		public ChatColor getChatColor() {
			return this.chatColor;
		}
	}

	private interface ColorGenerator {

		ChatColor nextChatColor();

		Color nextColor();
	}

	private static class Gradient implements ColorGenerator {

		private final List<TwoStopGradient> gradients;
		private final int steps;
		protected long step;

		public Gradient(List<Color> colors, int steps) {
			if (colors.size() < 2)
				throw new IllegalArgumentException("Must provide at least 2 colors");

			this.gradients = new ArrayList<>();
			this.steps = steps;
			this.step = 0;

			float increment = (float) (this.steps - 1) / (colors.size() - 1);
			for (int i = 0; i < colors.size() - 1; i++)
				this.gradients.add(new TwoStopGradient(colors.get(i), colors.get(i + 1), increment * i, increment * (i + 1)));
		}

		@Override
		public ChatColor nextChatColor() {
			if (NMSUtil.getVersionNumber() < 16 || this.steps <= 1)
				return translateHex(this.gradients.get(0).colorAt(0));
			return translateHex(this.nextColor());
		}

		@Override
		public Color nextColor() {
			int adjustedStep = this.adjustStep(this.step, this.steps);

			Color color;
			if (this.gradients.size() < 2) {
				color = this.gradients.get(0).colorAt(adjustedStep);
			} else {
				float segment = (float) this.steps / this.gradients.size();
				int index = (int) Math.min(Math.floor(adjustedStep / segment), this.gradients.size() - 1);
				color = this.gradients.get(index).colorAt(adjustedStep);
			}

			this.step++;
			return color;
		}

		private int adjustStep(long t, int steps) {
			int period = steps * 2;
			int modT = (int) (t % period);
			if (modT <= steps) {
				return modT;
			} else {
				return period - modT;
			}
		}

		private static class TwoStopGradient {

			private final Color startColor;
			private final Color endColor;
			private final float lowerRange;
			private final float upperRange;

			private TwoStopGradient(Color startColor, Color endColor, float lowerRange, float upperRange) {
				this.startColor = startColor;
				this.endColor = endColor;
				this.lowerRange = lowerRange;
				this.upperRange = upperRange;
			}

			public Color colorAt(int step) {
				return new Color(
						this.calculateHexPiece(step, this.startColor.getRed(), this.endColor.getRed()),
						this.calculateHexPiece(step, this.startColor.getGreen(), this.endColor.getGreen()),
						this.calculateHexPiece(step, this.startColor.getBlue(), this.endColor.getBlue())
				);
			}

			private int calculateHexPiece(int step, int channelStart, int channelEnd) {
				float range = this.upperRange - this.lowerRange;
				if (range == 0)
					return channelStart;
				float interval = (channelEnd - channelStart) / range;
				return Math.min(Math.max(Math.round(interval * (step - this.lowerRange) + channelStart), 0), 255);
			}
		}
	}

	private static class AnimatedGradient extends Gradient {

		public AnimatedGradient(List<Color> colors, int steps, int speed) {
			super(colors, steps);
			this.step = System.currentTimeMillis() / speed;
		}
	}

	private static class Rainbow implements ColorGenerator {

		protected final float hueStep, saturation, brightness;
		protected float hue;

		public Rainbow(int totalColors, float saturation, float brightness) {
			if (totalColors < 1)
				totalColors = 1;

			this.hueStep = 1.0F / totalColors;
			this.saturation = Math.max(0, Math.min(1, saturation));
			this.brightness = Math.max(0, Math.min(1, brightness));
			this.hue = 0;
		}

		@Override
		public ChatColor nextChatColor() {
			return translateHex(this.nextColor());
		}

		@Override
		public Color nextColor() {
			Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
			this.hue += this.hueStep;
			return color;
		}
	}

	private static class AnimatedRainbow extends Rainbow {

		public AnimatedRainbow(int totalColors, float saturation, float brightness, int speed) {
			super(totalColors, saturation, brightness);
			this.hue = (float) ((((Math.floor(System.currentTimeMillis() / 50.0)) / 360) * speed) % 1);
		}
	}
}