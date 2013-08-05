/**
 * 
 */
package org.easymetrics.easymetrics;

import org.easymetrics.easymetrics.engine.MetricsCollector;
import org.easymetrics.easymetrics.engine.MetricsEngine;
import org.easymetrics.easymetrics.engine.MockMetricsEngine;

/**
 * @author Administrator
 * 
 */
public class MetricsCollectorFactory {

	private static MetricsEngine		metricsEngine		= new MockMetricsEngine();

	public static MetricsCollector getMetricsCollector(final Class<? extends Object> clazz) {
		return getMetricsCollector(clazz.getSimpleName());
	}

	public static MetricsCollector getMetricsCollector(final String componentName) {
		return metricsEngine.createMetricsCollector(componentName);
	}

	public void setMetricsEngine(MetricsEngine engine) {
		metricsEngine = engine;
	}

}
