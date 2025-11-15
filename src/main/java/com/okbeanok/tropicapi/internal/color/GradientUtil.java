package com.okbeanok.tropicapi.internal.color;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Applies gradient tags of the form:
 * <gradient:#ff0000:#00ff00:#0000ff>Your text</gradient>
 *
 * Output is expressed as &#RRGGBB before each character,
 * which should then be converted by your existing hex color handling.
 */
public final class GradientUtil {

	// <gradient:#ff0000:#00ff00>Text</gradient>
	private static final Pattern GRADIENT_PATTERN = Pattern.compile(
			"<gradient:([^>]+)>(.*?)</gradient>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL
	);

	private GradientUtil() {
	}

	public static String applyGradients(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		Matcher matcher = GRADIENT_PATTERN.matcher(input);
		StringBuffer out = new StringBuffer();

		while (matcher.find()) {
			String colorsPart = matcher.group(1);
			String text = matcher.group(2);

			String[] colorTokens = colorsPart.split(":");
			List<int[]> colors = parseColors(colorTokens);

			if (colors.size() < 2 || text.isEmpty()) {
				// If parsing fails or there's not enough colors, leave original segment unchanged
				matcher.appendReplacement(out, Matcher.quoteReplacement(matcher.group(0)));
				continue;
			}

			String replaced = applyGradientToText(colors, text);
			matcher.appendReplacement(out, Matcher.quoteReplacement(replaced));
		}

		matcher.appendTail(out);
		return out.toString();
	}

	private static List<int[]> parseColors(String[] tokens) {
		List<int[]> colors = new ArrayList<>();
		for (String token : tokens) {
			token = token.trim();
			if (token.isEmpty()) continue;
			// Expect #RRGGBB
			if (token.charAt(0) == '#') {
				token = token.substring(1);
			}
			if (token.length() != 6) continue;

			try {
				int r = Integer.parseInt(token.substring(0, 2), 16);
				int g = Integer.parseInt(token.substring(2, 4), 16);
				int b = Integer.parseInt(token.substring(4, 6), 16);
				colors.add(new int[]{r, g, b});
			} catch (NumberFormatException ignored) {
				// skip invalid colors
			}
		}
		return colors;
	}

	private static String applyGradientToText(List<int[]> colors, String text) {
		char[] chars = text.toCharArray();
		int length = chars.length;
		if (length == 0) return "";

		int segments = colors.size() - 1;
		StringBuilder sb = new StringBuilder(text.length() * 14); // rough guess

		for (int i = 0; i < length; i++) {
			double position = length == 1 ? 0.0 : (double) i / (double) (length - 1);
			int segmentIndex = (int) Math.floor(position * segments);
			if (segmentIndex < 0) segmentIndex = 0;
			if (segmentIndex >= segments) segmentIndex = segments - 1;

			double segmentStart = (double) segmentIndex / (double) segments;
			double segmentEnd = (double) (segmentIndex + 1) / (double) segments;
			double localT = (position - segmentStart) / (segmentEnd - segmentStart);

			int[] c1 = colors.get(segmentIndex);
			int[] c2 = colors.get(segmentIndex + 1);

			int r = lerp(c1[0], c2[0], localT);
			int g = lerp(c1[1], c2[1], localT);
			int b = lerp(c1[2], c2[2], localT);

			String hex = String.format(Locale.ROOT, "%02X%02X%02X", r, g, b);

			// Emit as &#RRGGBB so your existing colorize logic can handle hex
			sb.append("&#").append(hex).append(chars[i]);
		}
		return sb.toString();
	}

	private static int lerp(int a, int b, double t) {
		return (int) Math.round(a + (b - a) * t);
	}
}