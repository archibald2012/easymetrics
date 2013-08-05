/**
 * 
 */
package org.easymetrics.easymetrics.engine;

/**
 * @author Administrator
 * 
 */
public interface MetricsCollector {

	MetricsTimer startMetricsTimer(String functionName, String correlationId, boolean startNew);

	MetricsTimer startMetricsTimer(String functionName, String correlationId);

	MetricsTimer startMetricsTimer(String functionName);

	MetricsTimer startInitialTimer(String functionName, String correlationId);

	MetricsTimer startInitialTimer(String functionName);
}
