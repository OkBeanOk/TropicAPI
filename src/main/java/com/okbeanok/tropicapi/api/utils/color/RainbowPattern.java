package com.okbeanok.tropicapi.api.utils.color;

import com.okbeanok.tropicapi.api.ColorAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a rainbow color pattern which can be applied to a String.
 * Supports patterns like <RAINBOW100>text</RAINBOW> for rainbow coloring.
 *
 * @since 1.0.0
 */
public class RainbowPattern implements com.okbeanok.tropicapi.api.utils.color.Pattern {

	private static final Pattern PATTERN = Pattern.compile("<RAINBOW([0-9]{1,3})>(.*?)</RAINBOW>");

	/**
	 * Applies a rainbow pattern to the provided String.
	 * Output might be the same as the input if this pattern is not present.
	 *
	 * @param string The String to which this pattern should be applied to
	 * @return The new String with applied pattern
	 */
	@Override
	public String process(String string) {
		if (string == null || string.isEmpty()) {
			return string != null ? string : "";
		}

		Matcher matcher = PATTERN.matcher(string);
		while (matcher.find()) {
			String saturation = matcher.group(1);
			String content = matcher.group(2);
			float saturationValue = Float.parseFloat(saturation) / 100f; // Convert to 0.0-1.0 range
			string = string.replace(matcher.group(), ColorAPI.rainbow(content, saturationValue));
		}
		return string;
	}
}