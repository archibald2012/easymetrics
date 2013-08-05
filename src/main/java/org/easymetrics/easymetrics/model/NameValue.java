package org.easymetrics.easymetrics.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public final class NameValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String value;

	public NameValue() {
	}

	public NameValue(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String toString() {
		return getValue();
	}

	public static void addToList(List<NameValue> list, String name, String value) {
		list.add(new NameValue(name, value));
	}

	public static NameValue putToMap(Map<String, NameValue> map, String name,
			String value) {
		return map.put(name, new NameValue(name, value));
	}
}
