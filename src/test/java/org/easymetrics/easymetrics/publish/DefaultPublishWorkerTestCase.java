package org.easymetrics.easymetrics.publish;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.easymetrics.easymetrics.model.Aggregation;
import org.easymetrics.easymetrics.model.Bucket;
import org.easymetrics.easymetrics.model.CollectorUsage;
import org.easymetrics.easymetrics.model.HeapUsage;
import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.Record;
import org.easymetrics.easymetrics.model.ResourceUsage;
import org.easymetrics.easymetrics.model.ThreadUsage;
import org.easymetrics.easymetrics.publish.DefaultPublishWorker;
import org.easymetrics.easymetrics.publish.MetricsPublisher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultPublishWorkerTestCase {

	private DefaultPublishWorker publishWorker;

	@Before
	public void setUp() throws Exception {
		publishWorker = new DefaultPublishWorker();
		publishWorker.start();
	}

	@After
	public void tearDown() throws Exception {
		publishWorker.setTerminating();
		publishWorker = null;
	}

	@Test
	public void testEnqueuePublishable() throws Exception {

		publishWorker.setPublishSize(3);

		publishWorker.setServiceGroup("app");
		publishWorker.setDomain("domain");
		publishWorker.setService("framework");
		publishWorker.setHost("host");
		publishWorker.setPid("pid");
		publishWorker.setUser("user");
		publishWorker.setVersion("version");

		final Measurement mea = createMeasurement();
		final Aggregation agg = createAggregation();
		final ResourceUsage usage = createResourceUsage();

		List<MetricsPublisher> metricsPublisherList = new ArrayList<MetricsPublisher>();
		MetricsPublisher metricsPublisher = new MetricsPublisher() {

			@Override
			public boolean publish(Record record) {
				Assert.assertEquals("app", record.getServiceGroup());
				Assert.assertEquals("domain", record.getDomain());
				Assert.assertEquals("framework", record.getService());
				Assert.assertEquals("host", record.getHost());
				Assert.assertEquals("pid", record.getPid());
				Assert.assertEquals("user", record.getUser());
				Assert.assertEquals("version", record.getVersion());

				Assert.assertEquals(mea, record.getMeasurementList().get(0));
				Assert.assertEquals(agg, record.getAggregationList().get(0));
				Assert.assertEquals(usage, record.getUsageList().get(0));
				return true;
			}
		};
		MetricsPublisher metricsPublisher2 = new MetricsPublisher() {

			@Override
			public boolean publish(Record record) {
				Assert.assertEquals("app", record.getServiceGroup());
				Assert.assertEquals("domain", record.getDomain());
				Assert.assertEquals("framework", record.getService());
				Assert.assertEquals("host", record.getHost());
				Assert.assertEquals("pid", record.getPid());
				Assert.assertEquals("user", record.getUser());
				Assert.assertEquals("version", record.getVersion());

				Assert.assertEquals(mea, record.getMeasurementList().get(0));
				Assert.assertEquals(agg, record.getAggregationList().get(0));
				Assert.assertEquals(usage, record.getUsageList().get(0));
				return true;
			}
		};

		metricsPublisherList.add(metricsPublisher);
		metricsPublisherList.add(metricsPublisher2);
		publishWorker.setMetricsPublisherList(metricsPublisherList);

		publishWorker.enqueuePublishable(mea);

		publishWorker.enqueuePublishable(agg);

		Thread.sleep(1000);

		publishWorker.enqueuePublishable(usage);

	}

	private ResourceUsage createResourceUsage() {
		ResourceUsage usage = new ResourceUsage();
		usage.setUsageId(UUID.randomUUID().toString());
		usage.setCpuTime(1000L);
		usage.setUpTime(3600L);
		usage.setProcessorCount(8);
		usage.setThreadCount(8);
		usage.setUserTime(1000L);
		usage.setHeapMax(512);
		usage.setHeapUsed(256);
		usage.setHeapMax(128);
		usage.setHeapUsed(64);
		usage.setCheckTime(new Date());

		List<ThreadUsage> threadUsages = new ArrayList<ThreadUsage>();
		ThreadUsage threadUsage = new ThreadUsage();
		threadUsage.setName("threadName");
		threadUsage.setState("active");
		threadUsage.setCpuTime(1000L);
		threadUsage.setUserTime(1000L);
		threadUsages.add(threadUsage);
		ThreadUsage threadUsage2 = new ThreadUsage();
		threadUsage2.setName("threadName2");
		threadUsage2.setState("active");
		threadUsage2.setCpuTime(1000L);
		threadUsage2.setUserTime(1000L);
		threadUsages.add(threadUsage2);
		usage.addThreadUsage(threadUsage);
		usage.addThreadUsage(threadUsage2);

		List<HeapUsage> heapUsages = new ArrayList<HeapUsage>();
		HeapUsage heapUsage = new HeapUsage();
		heapUsage.setName("heap1");
		heapUsage.setMax(2056000000L);
		heapUsage.setUsed(1480000000L);
		heapUsages.add(heapUsage);
		HeapUsage heapUsage2 = new HeapUsage();
		heapUsage2.setName("heap2");
		heapUsage2.setMax(2056000000L);
		heapUsage2.setUsed(1480000000L);
		heapUsages.add(heapUsage2);
		usage.addHeapUsage(heapUsage);
		usage.addHeapUsage(heapUsage2);

		List<CollectorUsage> gcUsages = new ArrayList<CollectorUsage>();
		CollectorUsage gcUsage = new CollectorUsage();
		gcUsage.setName("GC");
		gcUsage.setTime(360000L);
		gcUsage.setCount(100L);
		gcUsages.add(gcUsage);
		CollectorUsage gcUsage2 = new CollectorUsage();
		gcUsage2.setName("GC2");
		gcUsage2.setTime(360000L);
		gcUsage2.setCount(100L);
		gcUsages.add(gcUsage2);
		usage.addCollectorUsage(gcUsage);
		usage.addCollectorUsage(gcUsage2);

		return usage;
	}

	private Aggregation createAggregation() {
		Aggregation agg = new Aggregation();
		agg.setAverage(20);
		agg.setComponentName("test");
		agg.setCount(20L);
		agg.setDuration(30);
		agg.setFunctionName("test");
		agg.setId("test");
		agg.setMaximum(30);
		agg.setMinimum(30);
		agg.setRanges("test");
		agg.setStartTime(new Date());
		agg.setUnitAverage(20);
		agg.setUnitMaximum(2);
		agg.setUnitMinimum(2);

		Bucket bucket = new Bucket();
		bucket.setStartRange(100);
		bucket.setUnitCount(1);
		bucket.setCount(1);
		agg.addBucket(bucket);

		return agg;
	}

	private Measurement createMeasurement() {
		Measurement mea = new Measurement();
		mea.setComponentName("test");
		mea.setCorrelationRequester("requester");
		mea.setCorrelationId("test");
		mea.setDuration(10L);
		mea.setFailStatus(true);
		mea.setFunctionName("test");
		mea.setId("test");
		mea.setThreadName("test");
		mea.setUser("test");
		mea.setTimestamp(new Date());
		mea.setWorkUnits(3);
		mea.setCreateOrder(2);
		mea.setParentId("test");
		mea.addMetrics("exception", IllegalArgumentException.class.getName());
		return mea;
	}

}
