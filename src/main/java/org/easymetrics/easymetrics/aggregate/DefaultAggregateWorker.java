/**
 * 
 */
package org.easymetrics.easymetrics.aggregate;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.easymetrics.easymetrics.model.Bucket;
import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.publish.MetricsPublishWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class DefaultAggregateWorker extends Thread implements MetricsAggregateWorker {

	private static final Logger					LOGGER						= LoggerFactory.getLogger(DefaultAggregateWorker.class);

	static final String							DELIMITER_AGGREGATION_KEY	= ":";

	private long								aggregationInterval			= 1800000;
	private boolean								aggregationFilter			= true;
	private int									keepTopCount				= 2;
	private Date								startTime					= new Date();
	private final Map<String, AggregationEntry>	aggregationMap				= new HashMap<String, AggregationEntry>();
	private final ReentrantLock					aggregationMapLock			= new ReentrantLock();
	private JmxAggregation						aggregationMBean			= null;
	private String								mbeanObjectName				= "org.easymetrics:type=Metrics,name=Aggregation";
	private MetricsPublishWorker				metricsPublishWorker		= null;
	private Long[]								aggregationRanges			= new Long[] { 0L, 2L, 5L, 10L, 20L, 50L, 100L, 200L, 500L, 1000L, 2000L, 5000L,
			10000L, 20000L, 50000L											};
	private String								ranges						= "";
	private Bucket[]							emptyBucket					= new Bucket[0];
	private AggregationEntry[]					emptyEntry					= new AggregationEntry[0];

	private AtomicBoolean						terminating					= new AtomicBoolean(false);

	@Override
	public void run() {
		long prior = -1;
		StringBuilder rangesBuilder = new StringBuilder();
		for (Long value : aggregationRanges) {
			if (value <= prior) {
				String error = "Invalid range value " + value + " <= " + prior;
				LOGGER.error(error);
				throw new RuntimeException(error);
			}
			if (rangesBuilder.length() == 0) {
				rangesBuilder.append(value);
			} else {
				rangesBuilder.append(',').append(value);
			}
		}
		ranges = rangesBuilder.toString();

		registerMBeans();

		while (!getTerminating()) {

			try {
				Thread.sleep(aggregationInterval);
			} catch (InterruptedException e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Aggregation operation thread is interrupted with error " + e.getMessage(), e);
				}
			}

			try {
				flushAggregationMap();
			} catch (Exception e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Failed to flush aggregation data with error " + e.getMessage(), e);
				}
			}
		}

	}

	public void destroy() {
		setTerminating();
	}

	private void registerMBeans() {
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName aggregationName = new ObjectName(mbeanObjectName);
			if (mbeanServer.isRegistered(aggregationName)) {
				mbeanServer.unregisterMBean(aggregationName);
			}
			aggregationMBean = new JmxAggregation();
			mbeanServer.registerMBean(aggregationMBean, aggregationName);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Registering with JMX server as MBean [" + aggregationName + "]");
			}
		} catch (Exception e) {
			String message = "Unable to register MBeans with error " + e.getMessage();
			LOGGER.error(message, e);
		}
	}

	@Override
	public boolean addMeasurement(Collection<Measurement> measurements) {
		boolean valuable = aggregationFilter ? false : true;
		for (Measurement measurement : measurements) {
			if (check(measurement)) {
				valuable = true;
			}
		}
		return valuable;
	}

	/**
	 * Flush cached aggregation.
	 */
	protected void flushAggregationMap() {
		Date currentTime = new Date();
		long duration = currentTime.getTime() - startTime.getTime();

		List<AggregationEntry> aggregationList = new ArrayList<AggregationEntry>();

		aggregationMapLock.lock();
		try {
			Collection<AggregationEntry> aggregations = aggregationMap.values();
			for (AggregationEntry aggregation : aggregations) {
				aggregationList.add(aggregation);
			}
			aggregationMap.clear();

			startTime = currentTime;
		} finally {
			aggregationMapLock.unlock();
		}

		AggregationEntry[] aggregationArray = aggregationList.toArray(emptyEntry);
		for (AggregationEntry aggregation : aggregationArray) {

			// filter empty buckets
			List<Bucket> bucketList = aggregation.getBucketList();
			Bucket[] bucketArray = bucketList.toArray(emptyBucket);
			for (Bucket bucket : bucketArray) {
				if (bucket.getCount() == 0 && bucket.getUnitCount() == 0) {
					bucketList.remove(bucket);
				}
			}
			if (!bucketList.isEmpty()) {
				aggregation.setStartTime(startTime);
				aggregation.setDuration(duration);
				aggregation.setRanges(ranges);
				aggregation.prepare();
			} else {
				aggregationList.remove(aggregation);
			}

			aggregationMBean.addAggregation(aggregation, aggregationRanges);
		}

		// add to publish list
		if (metricsPublishWorker != null) {
			if (!aggregationList.isEmpty()) {
				metricsPublishWorker.enqueuePublishable(aggregationList);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(aggregationList.size() + " aggregations were added to publish queue");
				}
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(aggregationList.size() + " aggregations dropped from publishing");
			}
		}
	}

	private boolean check(final Measurement measurement) {
		boolean valuable = false;

		String key = StringUtils.join(new Object[] { measurement.getComponentName(), measurement.getFunctionName() }, DELIMITER_AGGREGATION_KEY);

		aggregationMapLock.lock();

		try {
			AggregationEntry aggregation = aggregationMap.get(key);
			if (aggregation == null) {
				aggregation = new AggregationEntry(measurement.getComponentName(), measurement.getFunctionName(), aggregationRanges, keepTopCount);
				aggregationMap.put(key, aggregation);
			}
			valuable = aggregation.addMeasurement(measurement.getDuration(), measurement.getWorkUnits());
		} finally {
			aggregationMapLock.unlock();
		}

		return valuable;
	}

	public void setTerminating() {
		this.terminating.set(true);
	}

	protected boolean getTerminating() {
		return terminating.get();
	}

	public void setAggregationRanges(String aggregationRanges) {
		this.ranges = aggregationRanges;
	}

	public void setAggregationInterval(long aggregationInterval) {
		this.aggregationInterval = aggregationInterval;
	}

	public void setAggregationFilter(boolean aggregationFilter) {
		this.aggregationFilter = aggregationFilter;
	}

	public void setMetricsPublishWorker(MetricsPublishWorker metricsPublishWorker) {
		this.metricsPublishWorker = metricsPublishWorker;
	}

	public void setMbeanObjectName(String mbeanObjectName) {
		this.mbeanObjectName = mbeanObjectName;
	}

	public void setKeepTopCount(int keepTopCount) {
		this.keepTopCount = keepTopCount;
	}

	Map<String, AggregationEntry> getAggregationMap() {
		return aggregationMap;
	}

}
