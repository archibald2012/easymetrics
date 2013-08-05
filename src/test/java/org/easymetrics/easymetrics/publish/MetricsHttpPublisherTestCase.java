package org.easymetrics.easymetrics.publish;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.easymetrics.easymetrics.model.Aggregation;
import org.easymetrics.easymetrics.model.Bucket;
import org.easymetrics.easymetrics.model.CollectorUsage;
import org.easymetrics.easymetrics.model.HeapUsage;
import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.Record;
import org.easymetrics.easymetrics.model.ResourceUsage;
import org.easymetrics.easymetrics.model.ThreadUsage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MetricsHttpPublisherTestCase {

	private MetricsHttpPublisher	metricsHttpPublisher	= new MetricsHttpPublisher();

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testPublish() throws Exception {

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

		Record r = new Record();
		r.setServiceGroup("test");
		r.setId("test");
		r.setService("test");
		r.setHost("test");
		r.setPid("test");
		r.setUser("test");
		r.setVersion("test");
		r.setAggregationRanges("test");
		r.setDomain("test");
		r.getAggregationList().add(agg);
		r.getMeasurementList().add(mea);
		r.getUsageList().add(usage);

		metricsHttpPublisher.publish(r);
	}
}
