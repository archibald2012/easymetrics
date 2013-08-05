package org.easymetrics.easymetrics.measure;

import java.util.List;

import junit.framework.Assert;

import org.easymetrics.easymetrics.measure.JmxMeasurement;
import org.easymetrics.easymetrics.model.Measurement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JmxMeasurementTestCase {

	private JmxMeasurement	jmxMeasurement;

	@Before
	public void setUp() throws Exception {
		jmxMeasurement = new JmxMeasurement();
	}

	@After
	public void tearDown() throws Exception {
		jmxMeasurement = null;
	}

	@Test
	public void testQueryMeasurementList() {

		Measurement mea = new Measurement();
		mea.setComponentName("test");
		mea.setDuration(10L);
		mea.setFailStatus(true);
		mea.setFunctionName("test");

		jmxMeasurement.addMeasurements(mea);

		Measurement mea2 = new Measurement();
		mea2.setComponentName("test");
		mea2.setDuration(10L);
		mea2.setFailStatus(true);
		mea2.setFunctionName("test2");

		jmxMeasurement.addMeasurements(mea2);

		List<String> measurments = jmxMeasurement.queryMeasurementList();

		Assert.assertEquals(2, measurments.size());
		//Assert.assertEquals("test:test[min=10,avg=10.0,max=10,count=1,fail=1]\n", measurments.get(0));
		//Assert.assertEquals("test:test2[min=10,avg=10.0,max=10,count=1,fail=1]\n", measurments.get(1));
	}

	@Test
	public void testAddMeasurement() {

		Measurement mea = new Measurement();
		mea.setComponentName("test");
		mea.setDuration(10L);
		mea.setFailStatus(true);
		mea.setFunctionName("test");

		jmxMeasurement.addMeasurements(mea);

		Measurement mea2 = new Measurement();
		mea2.setComponentName("test");
		mea2.setDuration(20L);
		mea2.setFailStatus(false);
		mea2.setFunctionName("test");

		jmxMeasurement.addMeasurements(mea2);

		List<String> measurments = jmxMeasurement.queryMeasurementList();

		Assert.assertEquals(1, measurments.size());
		Assert.assertEquals("test:test[min=10,avg=15.0,max=20,count=2,fail=1]\n", measurments.get(0));
	}

	@Test
	public void testResetAll() {

		Measurement mea = new Measurement();
		mea.setComponentName("test");
		mea.setDuration(10L);
		mea.setFailStatus(true);
		mea.setFunctionName("test");

		jmxMeasurement.addMeasurements(mea);

		jmxMeasurement.resetAllCounts();

		List<String> measurments = jmxMeasurement.queryMeasurementList();

		Assert.assertEquals(0, measurments.size());

	}

	@Test
	public void testResetCount() {

		Measurement mea = new Measurement();
		mea.setComponentName("test");
		mea.setDuration(10L);
		mea.setFailStatus(true);
		mea.setFunctionName("test");

		jmxMeasurement.addMeasurements(mea);

		Measurement mea2 = new Measurement();
		mea2.setComponentName("test");
		mea2.setDuration(10L);
		mea2.setFailStatus(true);
		mea2.setFunctionName("test2");

		jmxMeasurement.addMeasurements(mea2);

		jmxMeasurement.resetCount("test", "test");

		List<String> measurments = jmxMeasurement.queryMeasurementList();

		Assert.assertEquals(1, measurments.size());
		Assert.assertEquals("test:test2[min=10,avg=10.0,max=10,count=1,fail=1]\n", measurments.get(0));

	}

}
