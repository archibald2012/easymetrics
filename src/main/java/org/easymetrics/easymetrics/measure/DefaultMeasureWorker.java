/**
 * 
 */
package org.easymetrics.easymetrics.measure;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.ArrayUtils;
import org.easymetrics.easymetrics.aggregate.MetricsAggregateWorker;
import org.easymetrics.easymetrics.engine.MetricsTimer;
import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.publish.MetricsPublishWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class DefaultMeasureWorker extends Thread {
	private static final Logger					LOGGER				= LoggerFactory.getLogger(DefaultMeasureWorker.class);

	private long								checkInterval		= 2000;
	private LinkedBlockingQueue<MetricsTimer>	metricsTimerQueue	= new LinkedBlockingQueue<MetricsTimer>(1000);
	private String								mbeanObjectName		= "org.easymetrics:type=Metrics,name=Measurement";
	private JmxMeasurement						measurementMBean;
	private MetricsAggregateWorker				metricsAggregateWorker;
	private MetricsPublishWorker				metricsPublishWorker;
	private boolean								publishAll			= false;
	private AtomicBoolean						terminating			= new AtomicBoolean(false);

	@Override
	public void run() {

		registerMBeans();

		while (!getTerminating()) {
			try {

				MetricsTimer metricsTimer = getMetricsTimer();
				if (metricsTimer != null) {
					processMetricsTimer(metricsTimer);
				}
			} catch (Exception e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Failed to process measurement data", e);
				}
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Background measurement worker is terminated");
		}
	}

	public void destroy() {
		setTerminating();
	}

	public void enqueueMetricsTimer(MetricsTimer timer) {
		if (!metricsTimerQueue.offer(timer)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Current queue limit " + metricsTimerQueue.size() + " is reached");
			}
		}
	}

	public int getQueueSize() {
		return metricsTimerQueue.size();
	}

	protected void processMetricsTimer(MetricsTimer metricsTimer) {

		List<MetricsTimer> metricsTimers = metricsTimer.getAllMetricsTimers();
		if (!metricsTimers.isEmpty()) {
			List<Measurement> measurements = toMeasurements(metricsTimers);
			if (measurementMBean != null) {
				measurementMBean.addMeasurements(measurements.toArray(new Measurement[] {}));
			}
			boolean valuable = true;
			if (metricsAggregateWorker != null) {
				valuable = metricsAggregateWorker.addMeasurement(measurements);
			}
			if (publishAll || valuable) {
				// measurements are valuable
				if (metricsPublishWorker != null) {
					// add them to the publish worker at once
					metricsPublishWorker.enqueuePublishable(measurements);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(metricsTimers.size() + " timers dropped from further processing. valuable=[{}], timers=[{}]", valuable,
							ArrayUtils.toString(metricsTimers.toArray()));
				}
			}

		}

	}

	public MetricsTimer getMetricsTimer() {
		for (;;) {
			try {
				// it will wait till an item available
				return metricsTimerQueue.poll(checkInterval, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ie) {
				LOGGER.warn("Poll operation on queue interrupted", ie);
			}
		}
	}

	private void registerMBeans() {
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName measurementName = new ObjectName(mbeanObjectName);
			if (mbeanServer.isRegistered(measurementName)) {
				mbeanServer.unregisterMBean(measurementName);
			}
			measurementMBean = new JmxMeasurement();
			mbeanServer.registerMBean(measurementMBean, measurementName);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Registering with JMX server as MBean [" + measurementName + "]");
			}
		} catch (Exception e) {
			String message = "Unable to register MBeans with error " + e.getMessage();
			LOGGER.error(message, e);
		}
	}

	private List<Measurement> toMeasurements(List<MetricsTimer> metricsTimers) {
		List<Measurement> measurementList = new ArrayList<Measurement>();
		if (metricsTimers != null) {
			for (MetricsTimer metricsTimer : metricsTimers) {
				measurementList.add(metricsTimer.getMeasurement());
			}
		}
		return measurementList;
	}

	public void setTerminating() {
		this.terminating.set(true);
	}

	protected boolean getTerminating() {
		return terminating.get();
	}

	public void setPublishAll(boolean publishAll) {
		this.publishAll = publishAll;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setMetricsPublishWorker(MetricsPublishWorker metricsPublishWorker) {
		this.metricsPublishWorker = metricsPublishWorker;
	}

	public void setMetricsAggregateWorker(MetricsAggregateWorker metricsAggregateWorker) {
		this.metricsAggregateWorker = metricsAggregateWorker;
	}

	public void setMbeanObjectName(String mbeanObjectName) {
		this.mbeanObjectName = mbeanObjectName;
	}

	public void setQueueCapacity(int queueCapacity) {
		metricsTimerQueue = new LinkedBlockingQueue<MetricsTimer>(queueCapacity);
	}

}
