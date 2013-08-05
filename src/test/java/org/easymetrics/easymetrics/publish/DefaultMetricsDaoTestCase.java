//package org.easymetrics.easymetrics.publish;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import junit.framework.Assert;
//
//import org.easymetrics.easymetrics.model.Aggregation;
//import org.easymetrics.easymetrics.model.Bucket;
//import org.easymetrics.easymetrics.model.CollectorUsage;
//import org.easymetrics.easymetrics.model.HeapUsage;
//import org.easymetrics.easymetrics.model.Measurement;
//import org.easymetrics.easymetrics.model.Record;
//import org.easymetrics.easymetrics.model.ResourceUsage;
//import org.easymetrics.easymetrics.model.ThreadUsage;
//import org.easymetrics.easymetrics.publish.dao.DefaultMetricsDao;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.mchange.v2.c3p0.ComboPooledDataSource;
//
//public class DefaultMetricsDaoTestCase {
//
//	private DefaultMetricsDao	metricsDao	= new DefaultMetricsDao();
//
//	@Before
//	public void setUp() throws Exception {
//		ComboPooledDataSource dataSource = new ComboPooledDataSource();
//		dataSource.setDriverClass("com.mysql.jdbc.Driver");
//		dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/metrics?characterEncoding=utf-8");
//		dataSource.setUser("metrics");
//		dataSource.setPassword("metrics");
//		metricsDao.setDataSource(dataSource);
//	}
//
//	@After
//	public void tearDown() throws Exception {
//
//	}
//
//	@Test
//	public void testSaveRecord() {
//		Record record = createRecord();
//		Assert.assertEquals(1, metricsDao.saveRecord(record));
//	}
//
//	@Test
//	public void testSaveMeasurements() {
//		Record record = createRecord();
//		metricsDao.saveRecord(record);
//		Measurement measurement = createMeasurement(record.getId());
//		Assert.assertEquals(1, metricsDao.saveMeasurements(record.getId(), Arrays.asList(new Measurement[] { measurement })));
//	}
//
//	@Test
//	public void testSaveAggregations() {
//		Record record = createRecord();
//		metricsDao.saveRecord(record);
//		Aggregation aggregation = createAggregation(record.getId());
//		Assert.assertEquals(1, metricsDao.saveAggregations(record.getId(), Arrays.asList(new Aggregation[] { aggregation })));
//	}
//
//	@Test
//	public void testSaveRuntimeUsages() {
//		Record record = createRecord();
//		metricsDao.saveRecord(record);
//		ResourceUsage usage = createRuntimeUsage(record.getId());
//		Assert.assertEquals(1, metricsDao.saveRuntimeUsages(record.getId(), Arrays.asList(new ResourceUsage[] { usage })));
//	}
//
//	private ResourceUsage createRuntimeUsage(String recordId) {
//		ResourceUsage usage = new ResourceUsage();
//		usage.setUsageId(UUID.randomUUID().toString());
//		usage.setCpuTime(1000L);
//		usage.setUpTime(3600L);
//		usage.setProcessorCount(8);
//		usage.setThreadCount(8);
//		usage.setUserTime(1000L);
//		usage.setHeapMax(512);
//		usage.setHeapUsed(256);
//		usage.setHeapMax(128);
//		usage.setHeapUsed(64);
//		usage.setCheckTime(new Date());
//
//		List<ThreadUsage> threadUsages = new ArrayList<ThreadUsage>();
//		ThreadUsage threadUsage = new ThreadUsage();
//		threadUsage.setName("threadName");
//		threadUsage.setState("active");
//		threadUsage.setCpuTime(1000L);
//		threadUsage.setUserTime(1000L);
//		threadUsages.add(threadUsage);
//		ThreadUsage threadUsage2 = new ThreadUsage();
//		threadUsage2.setName("threadName2");
//		threadUsage2.setState("active");
//		threadUsage2.setCpuTime(1000L);
//		threadUsage2.setUserTime(1000L);
//		threadUsages.add(threadUsage2);
//		usage.addThreadUsage(threadUsage);
//		usage.addThreadUsage(threadUsage2);
//
//		List<HeapUsage> heapUsages = new ArrayList<HeapUsage>();
//		HeapUsage heapUsage = new HeapUsage();
//		heapUsage.setName("heap1");
//		heapUsage.setMax(2056000000L);
//		heapUsage.setUsed(1480000000L);
//		heapUsages.add(heapUsage);
//		HeapUsage heapUsage2 = new HeapUsage();
//		heapUsage2.setName("heap2");
//		heapUsage2.setMax(2056000000L);
//		heapUsage2.setUsed(1480000000L);
//		heapUsages.add(heapUsage2);
//		usage.addHeapUsage(heapUsage);
//		usage.addHeapUsage(heapUsage2);
//
//		List<CollectorUsage> gcUsages = new ArrayList<CollectorUsage>();
//		CollectorUsage gcUsage = new CollectorUsage();
//		gcUsage.setName("GC");
//		gcUsage.setTime(360000L);
//		gcUsage.setCount(100L);
//		gcUsages.add(gcUsage);
//		CollectorUsage gcUsage2 = new CollectorUsage();
//		gcUsage2.setName("GC2");
//		gcUsage2.setTime(360000L);
//		gcUsage2.setCount(100L);
//		gcUsages.add(gcUsage2);
//		usage.addCollectorUsage(gcUsage);
//		usage.addCollectorUsage(gcUsage2);
//		return usage;
//	}
//
//	private Aggregation createAggregation(String recordId) {
//		Aggregation aggregation = new Aggregation();
//		aggregation.setAverage(100);
//		aggregation.setComponentName("compName");
//		aggregation.setCount(1000L);
//		aggregation.setDuration(1000L);
//		aggregation.setFunctionName("function");
//		aggregation.setId(UUID.randomUUID().toString());
//		aggregation.setMaximum(10000);
//		aggregation.setMinimum(100);
//		aggregation.setRanges("1,100,200,1000,5000,10000");
//		aggregation.setStartTime(new Date());
//		aggregation.setUnitAverage(1000L);
//		aggregation.setUnitMaximum(10000);
//		aggregation.setUnitMinimum(100);
//
//		Bucket bucket = new Bucket();
//		bucket.setStartRange(100);
//		bucket.setUnitCount(1);
//		bucket.setCount(1);
//		aggregation.addBucket(bucket);
//		return aggregation;
//	}
//
//	private Measurement createMeasurement(String recordId) {
//		Measurement measurement = new Measurement();
//		measurement.setComponentName("compName");
//		measurement.setCorrelationRequester("requester");
//		measurement.setCorrelationId("xxxxx");
//		measurement.setDuration(1000L);
//		measurement.setFailStatus(true);
//		measurement.setFunctionName("function");
//		measurement.setId(UUID.randomUUID().toString());
//		measurement.setParentId(recordId);
//		measurement.setThreadName("thread");
//		measurement.setTimestamp(new Date());
//		measurement.setUser("e458432");
//		measurement.setWorkUnits(100);
//		measurement.setCreateOrder(1);
//
//		measurement.addMetrics("exception", IllegalArgumentException.class.getName());
//		return measurement;
//	}
//
//	private Record createRecord() {
//		Record record = new Record();
//		record.setId(UUID.randomUUID().toString());
//		record.setServiceGroup("app");
//		record.setDomain("domain");
//		record.setService("framework");
//		record.setHost("host");
//		record.setPid("pid");
//		record.setUser("user");
//		record.setVersion("version");
//		return record;
//	}
//}
