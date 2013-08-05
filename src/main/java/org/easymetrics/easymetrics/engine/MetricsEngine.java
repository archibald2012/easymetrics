/**
 * 
 */
package org.easymetrics.easymetrics.engine;


/**
 * @author Administrator
 * 
 */
public interface MetricsEngine {

	boolean isCollectMetrics();

	boolean isThrowException();

	boolean isFilter(String componentName, String functionName);

	boolean updateFilter(String componentName, String functionName, boolean isFilter);

	MetricsCollector createMetricsCollector(String componentName);

}
