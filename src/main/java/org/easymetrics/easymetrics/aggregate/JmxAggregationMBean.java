package org.easymetrics.easymetrics.aggregate;

import java.util.List;

public interface JmxAggregationMBean {

	List<String> queryDistributionList();

	String queryDistributeDetail(String componentName, String functionName);

	void resetAllCounts();

	boolean resetCount(String componentName, String functionName);
}
