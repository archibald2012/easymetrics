package org.easymetrics.easymetrics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.easymetrics.easymetrics.model.NameValue;


public class MetricsUtil {

	private MetricsUtil() {
	}

	public static String truncate(String value, int size) {
		if (value != null && value.length() > size) {
			return value.substring(0, size);
		}
		return value;
	}

	public static String truncate(String value, int size, String def) {
		if (value != null && value.length() > 0) {
			if (value.length() > size) {
				return value.substring(0, size);
			}
		} else {
			value = def;
		}
		return value;
	}

	private static final long	MILLIS_TO_SECOND	= 1000L;
	private static final long	NANO_TO_MILLIS		= 1000000L;

	public static long millisToSeconds(long milliSecond) {
		return milliSecond / MILLIS_TO_SECOND;
	}

	public static long nanoToMillis(long nanoSecond) {
		return nanoSecond / NANO_TO_MILLIS;
	}

	public static Map<String, NameValue> getAttributeMap(Object request) {
		Map<String, NameValue> attributeMap = null;

		if (request != null) {
			attributeMap = new HashMap<String, NameValue>();
			String name = request.getClass().getSimpleName();
			attributeMap.put(name, new NameValue(name, request.toString()));
		} else {
			attributeMap = new HashMap<String, NameValue>();
		}

		return attributeMap;
	}

	private static final Pattern	DASH_PATTERN	= Pattern.compile("-");

	public static String createGuid() {
		return DASH_PATTERN.matcher(UUID.randomUUID().toString()).replaceAll("");
	}

}
