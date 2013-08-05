/**
 * 
 */
package org.easymetrics.easymetrics.runtime;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easymetrics.easymetrics.model.CollectorUsage;
import org.easymetrics.easymetrics.model.HeapUsage;
import org.easymetrics.easymetrics.model.ResourceUsage;
import org.easymetrics.easymetrics.model.ThreadUsage;
import org.easymetrics.easymetrics.publish.MetricsPublishWorker;
import org.easymetrics.easymetrics.util.MetricsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class DefaultRuntimeWorker extends Thread {
	private static final Logger		LOGGER					= LoggerFactory.getLogger(DefaultRuntimeWorker.class);

	private boolean					detailGc				= false;
	private boolean					detailThread			= false;
	private boolean					detailHeap				= false;
	private long					checkInterval			= 60000;
	private long					startNano				= System.nanoTime();
	private MetricsPublishWorker	metricsPublishWorker	= null;
	private AtomicBoolean			terminating				= new AtomicBoolean(false);

	@Override
	public final void run() {
		while (!getTerminating()) {
			checkRuntimeMetrics();
			try {
				Thread.sleep(checkInterval);
			} catch (InterruptedException e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Failed to check runtime metrics with error " + e.getMessage(), e);
				}
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Background runtime worker is terminated");
		}
	}

	public void destroy() {
		setTerminating();
	}

	void checkRuntimeMetrics() {
		try {
			ResourceUsage usage = new ResourceUsage();
			getHeapUsage(usage);
			getThreadUsage(usage);
			getGcUsage(usage);
			usage.setUsageId(MetricsUtil.createGuid());
			usage.setCheckTime(new Date());

			metricsPublishWorker.enqueuePublishable(usage);
		} catch (Exception e) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Failed to collect runtime ata with error " + e.getMessage(), e);
			}
		}

	}

	private void getGcUsage(ResourceUsage usage) {
		if (detailGc) {
			List<GarbageCollectorMXBean> collectorList = ManagementFactory.getGarbageCollectorMXBeans();
			if (collectorList != null) {
				for (GarbageCollectorMXBean collector : collectorList) {
					String name = MetricsUtil.truncate(collector.getName(), 64);
					usage.getCollectorList().add(new CollectorUsage(name, collector.getCollectionCount(), collector.getCollectionTime()));
				}
			}
		}
	}

	private void getHeapUsage(ResourceUsage usage) {
		MemoryMXBean memoryMBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapUsage = memoryMBean.getHeapMemoryUsage();
		usage.setHeapMax(heapUsage.getMax());
		usage.setHeapUsed(heapUsage.getUsed());

		MemoryUsage nonHeapUsage = memoryMBean.getNonHeapMemoryUsage();
		usage.setNonHeapMax(nonHeapUsage.getMax());
		usage.setNonHeapUsed(nonHeapUsage.getUsed());

		if (detailHeap) {
			List<MemoryPoolMXBean> memoryPoolList = ManagementFactory.getMemoryPoolMXBeans();
			if (memoryPoolList != null) {
				for (MemoryPoolMXBean memoryPool : memoryPoolList) {
					MemoryUsage memoryUsage = memoryPool.getUsage();
					if (memoryUsage != null) {
						String name = MetricsUtil.truncate(memoryPool.getName(), 64);
						usage.getHeapList().add(new HeapUsage(name, memoryUsage.getMax(), memoryUsage.getUsed()));
					}
				}
			}

		}
	}

	private void getThreadUsage(ResourceUsage usage) {

		long cpuTotal = 0;
		long userTotal = 0;
		ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
		long[] threads = threadMBean.getAllThreadIds();
		for (int i = 0; i < threads.length; i++) {
			long time = threadMBean.getThreadCpuTime(threads[i]);
			if (time > 0.0) {
				cpuTotal += time;
			}
			time = threadMBean.getThreadUserTime(threads[i]);
			if (time > 0.0) {
				userTotal += time;
			}
			if (detailThread) {
				ThreadInfo info = threadMBean.getThreadInfo(threads[i]);
				if (info != null) {
					String threadName = MetricsUtil.truncate(info.getThreadName(), 64);
					usage.getThreadList().add(
							new ThreadUsage(threadName, info.getThreadState().toString(), threadMBean.getThreadCpuTime(threads[i]), threadMBean
									.getThreadUserTime(threads[i])));
				}
			}
		}

		usage.setProcessorCount(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
		usage.setThreadCount(threads.length);
		usage.setUpTime(System.nanoTime() - startNano);
		usage.setCpuTime(cpuTotal);
		usage.setUserTime(userTotal);
	}

	public void setTerminating() {
		this.terminating.set(true);
	}

	protected boolean getTerminating() {
		return terminating.get();
	}

	public void setDetailGc(boolean detailGc) {
		this.detailGc = detailGc;
	}

	public void setDetailThread(boolean detailThread) {
		this.detailThread = detailThread;
	}

	public void setDetailHeap(boolean detailHeap) {
		this.detailHeap = detailHeap;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setMetricsPublishWorker(MetricsPublishWorker metricsPublishWorker) {
		this.metricsPublishWorker = metricsPublishWorker;
	}

}
