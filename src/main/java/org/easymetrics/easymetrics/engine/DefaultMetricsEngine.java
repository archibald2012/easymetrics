/**
 * 
 */
package org.easymetrics.easymetrics.engine;

import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.easymetrics.easymetrics.exception.MetricsException;
import org.easymetrics.easymetrics.measure.DefaultMeasureWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 1. Metrics filtering.
 * </p>
 * <p>
 * 2. Metrics timer stack.
 * </p>
 * <p>
 * 3. Metrics measurement.
 * </p>
 * 
 * @author Administrator
 * 
 */
public class DefaultMetricsEngine implements MetricsEngine {
	private static final Logger						LOGGER					= LoggerFactory.getLogger(DefaultMetricsEngine.class);

	private static final String						KEY_JOIN				= ":";

	private AtomicBoolean							collectMetrics			= new AtomicBoolean(false);
	private AtomicBoolean							throwException			= new AtomicBoolean(false);
	private DefaultMeasureWorker					metricsMeasureWorker	= null;
	private final ThreadLocal<Stack<MetricsTimer>>	localTimerStack			= new ThreadLocal<Stack<MetricsTimer>>();
	// key is componentName:functionName
	// key is componentName
	private ConcurrentMap<String, Boolean>			filterMap				= new ConcurrentHashMap<String, Boolean>();
	private boolean									filterAll				= false;
	private ReentrantLock							filterMapLock			= new ReentrantLock();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCollectMetrics() {
		return collectMetrics.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isThrowException() {
		return throwException.get();
	}

	@Override
	public MetricsCollector createMetricsCollector(String componentName) {
		return new DefaultMetricsCollector(this, componentName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFilter(String componentName, String functionName) {
		boolean isFitler = false;

		filterMapLock.lock();
		try {
			if (filterAll) {
				isFitler = true;
			} else {
				String key = StringUtils.join(new Object[] { componentName, functionName }, KEY_JOIN);
				Boolean result = filterMap.get(key);
				if (result != null) {
					isFitler = result;
				} else {
					key = StringUtils.join(new Object[] { componentName, "" }, KEY_JOIN);
					result = filterMap.get(key);
					if (result != null) {
						isFitler = result;
					}
				}
			}
		} finally {
			filterMapLock.unlock();
		}
		return isFitler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateFilter(String componentName, String functionName, boolean isFilter) {
		boolean oldValue = false;

		filterMapLock.lock();
		try {
			if (StringUtils.isBlank(componentName) && StringUtils.isBlank(functionName)) {
				oldValue = filterAll;
				filterAll = isFilter;
			} else if (StringUtils.isNotBlank(componentName) && StringUtils.isNotBlank(functionName)) {
				String key = StringUtils.join(new Object[] { componentName, functionName }, KEY_JOIN);
				Boolean result = filterMap.put(key, Boolean.valueOf(isFilter));
				if (result != null) {
					oldValue = result;
				}
			} else if (StringUtils.isNotBlank(componentName)) {
				String key = StringUtils.join(new Object[] { componentName, "" }, KEY_JOIN);
				Boolean result = filterMap.get(key);
				if (result != null) {
					oldValue = result;
				}
				Set<String> keySet = filterMap.keySet();
				for (String entry : keySet) {
					if (entry.startsWith(key)) {
						filterMap.remove(entry);
					}
				}
				filterMap.put(key, Boolean.valueOf(isFilter));
			} else {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Invalid component " + componentName + " function " + functionName + " isFilter " + isFilter);
				}
			}
		} finally {
			filterMapLock.unlock();
		}
		return oldValue;
	}

	Stack<MetricsTimer> getTimerStack() {
		Stack<MetricsTimer> timerStack = localTimerStack.get();
		if (timerStack == null) {
			timerStack = new Stack<MetricsTimer>();
			localTimerStack.set(timerStack);
		}
		return timerStack;
	}

	/**
	 * Push metrics timer into stack.
	 * 
	 * @param timer
	 * @return parent metrics timer
	 */
	MetricsTimer pushTimer(MetricsTimer timer) {
		MetricsTimer parent = null;

		Stack<MetricsTimer> timerStack = getTimerStack();
		if (!timerStack.isEmpty()) {
			parent = timerStack.peek();
		}
		timerStack.push(timer);

		return parent;
	}

	/**
	 * Pop metrics timer
	 * 
	 * @param timer
	 * @return metrics timer in stack.
	 */
	MetricsTimer popTimer(MetricsTimer timer) {
		Stack<MetricsTimer> timerStack = getTimerStack();

		while (!timerStack.isEmpty()) {
			MetricsTimer temp = timerStack.pop();
			if (temp.equals(timer)) {
				return timer;
			} else {
				if (isThrowException()) {
					throw new MetricsException("Invalid stack timer " + temp);
				} else {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("Invalid stack timer " + temp);
					}
				}
			}
		}

		if (isThrowException()) {
			throw new MetricsException("Unable to find timer " + timer);
		} else {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Unable to find timer " + timer);
			}
		}

		return null;
	}

	/**
	 * enqueue a metrics timer into measure worker.
	 * 
	 * @param metricsTimer
	 */
	void enqueueMetricsTimer(MetricsTimer metricsTimer) {
		if (metricsMeasureWorker != null) {
			metricsMeasureWorker.enqueueMetricsTimer(metricsTimer);
		}
	}

	public void setMetricsMeasureWorker(DefaultMeasureWorker metricsMeasureWorker) {
		this.metricsMeasureWorker = metricsMeasureWorker;
	}

	public void setThrowException(boolean throwException) {
		this.throwException.set(throwException);
	}

	public void setCollectMetrics(boolean collectMetrics) {
		this.collectMetrics.set(collectMetrics);
	}

}
