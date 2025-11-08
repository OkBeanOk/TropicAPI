package com.okbeanok.tropicapi.api.utils.color;

/**
 * Represents a color pattern that can be applied to strings.
 * Implementations process specific color syntax patterns.
 *
 * @since 1.0.0
 */
public interface Pattern {

	/**
	 * Processes a string to apply this pattern's color formatting.
	 *
	 * @param string The string to process
	 * @return The processed string with color codes applied
	 */
	String process(String string);
}