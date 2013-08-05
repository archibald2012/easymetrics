package org.easymetrics.easymetrics.measure;

import java.util.List;

public interface JmxMeasurementMBean {

	List<String> queryMeasurementList();

	void resetAllCounts();

	boolean resetCount(String componentName, String functionName);
}
