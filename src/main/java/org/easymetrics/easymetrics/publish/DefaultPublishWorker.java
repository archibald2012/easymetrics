/**
 * 
 */
package org.easymetrics.easymetrics.publish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.ArrayUtils;
import org.easymetrics.easymetrics.model.Aggregation;
import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.Publishable;
import org.easymetrics.easymetrics.model.Record;
import org.easymetrics.easymetrics.model.ResourceUsage;
import org.easymetrics.easymetrics.util.MetricsUtil;
import org.easymetrics.easymetrics.util.StopTimer;
import org.easymetrics.easymetrics.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class DefaultPublishWorker extends Thread implements MetricsPublishWorker {

	private static final Logger					LOGGER					= LoggerFactory.getLogger(DefaultPublishWorker.class);

	private String								domain					= "";
	private String								serviceGroup			= "";
	private String								service					= "";
	private String								version					= "";
	private String								host					= SystemUtil.getHostName();
	private String								user					= SystemUtil.getUserName();
	private String								pid						= SystemUtil.getPid();
	private int									triggerSize				= 100;
	private int									publishSize				= 500;
	private int									timeInterval			= 5000;

	private List<MetricsPublisher>				metricsPublisherList	= new ArrayList<MetricsPublisher>();
	private LinkedBlockingQueue<Publishable>	publishQueue			= new LinkedBlockingQueue<Publishable>(10000);
	private final static Object					publishQueueMonitor		= new Object();

	private AtomicBoolean						terminating				= new AtomicBoolean(false);

	@Override
	public void enqueuePublishable(Publishable publishable) {
		if (!publishQueue.offer(publishable)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Drop 1 item with publish queue at its capacity " + publishQueue.size());
			}
		}

		// check to see should it wake up waiting thread
		if (publishQueue.size() >= publishSize) {
			synchronized (publishQueueMonitor) {
				// notify the waiting threads to publish
				publishQueueMonitor.notify();
			}
		}
	}

	@Override
	public void enqueuePublishable(Collection<? extends Publishable> publishables) {
		boolean isNotify = false;

		int dropCount = 0;
		for (Publishable publishable : publishables) {
			if (!publishQueue.offer(publishable)) {
				dropCount++;
			}

			if (!isNotify) {
				// check to see should it wake up waiting thread
				if (publishQueue.size() >= publishSize) {
					synchronized (publishQueueMonitor) {
						// notify the waiting threads to publish
						publishQueueMonitor.notify();
						isNotify = true;
					}
				}
			}
		}

		if (dropCount > 0) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Drop " + dropCount + " item(s) with publish queue at its capacity " + publishQueue.size());
			}
		}

	}

	@Override
	public final void run() {
		while (!getTerminating()) {
			try {
				publishMetrics();
			} catch (RuntimeException e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Found error during publishing " + e.getMessage(), e);
				}
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Background publish worker is terminated");
		}
	}

	public void destroy() {
		setTerminating();
	}

	protected void publishMetrics() {
		List<Publishable> list = getPublishables();

		if (!list.isEmpty()) {
			StopTimer timer = new StopTimer();

			Record record = new Record(MetricsUtil.createGuid(), domain, host, serviceGroup, service, version, user, pid);

			for (Publishable publishable : list) {
				if (publishable instanceof Measurement) {
					record.getMeasurementList().add((Measurement) publishable);
				} else if (publishable instanceof Aggregation) {
					if (record.getAggregationRanges() == null) {
						record.setAggregationRanges(((Aggregation) publishable).getRanges());
					}
					record.getAggregationList().add((Aggregation) publishable);
				} else if (publishable instanceof ResourceUsage) {
					record.getUsageList().add((ResourceUsage) publishable);
				}
			}

			for (MetricsPublisher publisher : metricsPublisherList) {
				publisher.publish(record);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(metricsPublisherList.size() + " publishers process " + list.size() + " publishables in " + timer.check()
						+ " ms. publishables=[{}]", ArrayUtils.toString(list.toArray()));
			}

		}
	}

	private List<Publishable> getPublishables() {
		List<Publishable> list = new ArrayList<Publishable>();

		if (publishQueue.size() < triggerSize) {
			synchronized (publishQueueMonitor) {
				try {
					publishQueueMonitor.wait(timeInterval);
				} catch (InterruptedException ignore) {
				}
			}
		}

		publishQueue.drainTo(list, publishSize);

		return list;
	}

	public void setTerminating() {
		this.terminating.set(true);
	}

	protected boolean getTerminating() {
		return terminating.get();
	}

	public void setDomain(String domain) {
		this.domain = MetricsUtil.truncate(domain, 32);
	}

	public void setHost(String host) {
		this.host = MetricsUtil.truncate(host, 32);
	}

	public void setServiceGroup(String serviceGroup) {
		this.serviceGroup = MetricsUtil.truncate(serviceGroup, 32);
	}

	public void setService(String service) {
		this.service = MetricsUtil.truncate(service, 32);
	}

	public void setVersion(String version) {
		this.version = MetricsUtil.truncate(version, 16);
	}

	public void setUser(String user) {
		this.user = MetricsUtil.truncate(user, 32);
	}

	public void setPid(String pid) {
		this.pid = MetricsUtil.truncate(pid, 16);
	}

	public void setQueueCapacity(int queueCapacity) {
		publishQueue = new LinkedBlockingQueue<Publishable>(queueCapacity);
	}

	public void setTriggerSize(int triggerSize) {
		this.triggerSize = triggerSize;
	}

	public void setPublishSize(int publishSize) {
		this.publishSize = publishSize;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public void setPublishQueue(LinkedBlockingQueue<Publishable> publishQueue) {
		this.publishQueue = publishQueue;
	}

	public void setMetricsPublisherList(List<MetricsPublisher> metricsPublisherList) {
		this.metricsPublisherList = metricsPublisherList;
	}

}
