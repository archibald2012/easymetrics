/**
 * 
 */
package org.easymetrics.easymetrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.easymetrics.easymetrics.engine.MetricsCollector;
import org.easymetrics.easymetrics.engine.MetricsTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class MetricsProxyHandler {
	private static final Logger						LOGGER			= LoggerFactory.getLogger(MetricsProxyHandler.class);

	/**
	 * For holding the metrics collector for the component.
	 */
	private ConcurrentMap<String, MetricsCollector>	collectorMap	= new ConcurrentHashMap<String, MetricsCollector>();

	private MetricsProxyHandler() {
	}

	/**
	 * For returning the internal singleton.
	 * 
	 * @return MetricsProxyHandler instance.
	 */
	public static MetricsProxyHandler getInstance() {
		return MetricsProxyHandlerHolder.instance;
	}

	/**
	 * Start metrics timer with component name and function name.
	 * 
	 * @param component
	 * @param function
	 * @param arguments
	 * @return metricsTimer
	 */
	public MetricsTimer startMetricsTimer(String component, String function, boolean startNew, Object... arguments) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Start metrics on component " + component + " function " + function);
		}

		MetricsCollector metricsCollector = collectorMap.get(component);
		if (metricsCollector == null) {
			MetricsCollector newCollector = MetricsCollectorFactory.getMetricsCollector(component);
			MetricsCollector oldCollector = collectorMap.putIfAbsent(component, newCollector);
			if (oldCollector != null) {
				metricsCollector = oldCollector;
			} else {
				metricsCollector = newCollector;
			}
		}

		MetricsTimer metricsTimer = null;
		if (startNew) {
			metricsTimer = metricsCollector.startInitialTimer(function);
		} else {
			metricsTimer = metricsCollector.startMetricsTimer(function);
		}

		if (arguments != null) {
			for (Object arg : arguments) {
				metricsTimer.addMetrics(arg);
			}
		}

		return metricsTimer;
	}

	/**
	 * 
	 * @param metricsTimer
	 * @param argument
	 * @param exception
	 */
	public void stopMetricsTimer(MetricsTimer metricsTimer, Object argument, Throwable exception) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Stop metrics timer " + metricsTimer + " exception " + exception);
		}

		if (argument != null) {
			metricsTimer.addMetrics(argument);
		}

		metricsTimer.stop(exception);
	}

	private static class MetricsProxyHandlerHolder {
		private static final MetricsProxyHandler	instance	= new MetricsProxyHandler();
	}
}
