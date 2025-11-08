package com.okbeanok.tropicapi.api.utils.color;

import com.okbeanok.tropicapi.api.ColorAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a solid color pattern which can be applied to a String.
 * Supports patterns like <#FF0000>text or #FF0000 for solid coloring.
 *
 * @since 1.0.0
 */
public class SolidPattern implements com.okbeanok.tropicapi.api.utils.color.Pattern {

	private static final Pattern PATTERN = Pattern.compile("[<{]#([A-Fa-f0-9]{6})[}>]|[&]?#([A-Fa-f0-9]{6})");

	/**
	 * Applies a solid RGB color to the provided String.
	 * Output might be the same as the input if this pattern is not present.
	 *
	 * @param string The String to which this pattern should be applied to
	 * @return The new String with an applied pattern
	 */
	@Override
	public String process(String string) {
		if (string == null || string.isEmpty()) {
			return string != null ? string : "";
		}

		Matcher matcher = PATTERN.matcher(string);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String color = matcher.group(1);
			if (color == null) {
				color = matcher.group(2);
			}

			String replacement = ColorAPI.getColor(color).toString();
			matcher.appendReplacement(result, java.util.regex.Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(result);

		return result.toString();
	}
}