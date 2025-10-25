package com.okbeanok.tropicapi.api.utils.color;

import java.util.Collection;
import java.util.regex.Pattern;

public class PatternUtil {
	public static Pattern compile(String[] patterns){
		StringBuilder patternBuilder = new StringBuilder();
		for (String entry : patterns) {
			if (patternBuilder.length() <= 0){
				patternBuilder.append("("+ entry);
			} else {
				patternBuilder.append(")|(" + entry);
			}
		}
		patternBuilder.append(")");
		return Pattern.compile(patternBuilder.toString());
	}

	public static Pattern compile(Collection<String> patterns){
		StringBuilder patternBuilder = new StringBuilder();
		for (String entry : patterns) {
			if (patternBuilder.length() <= 0){
				patternBuilder.append("("+ entry);
			} else {
				patternBuilder.append(")|(" + entry);
			}
		}
		patternBuilder.append(")");
		return Pattern.compile("(?i)" + patternBuilder.toString());
	}
}
