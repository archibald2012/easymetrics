package org.easymetrics.easymetrics;

import org.easymetrics.easymetrics.MetricsProxyHandler;
import org.easymetrics.easymetrics.engine.MetricsTimer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MetricsCollectorFactoryTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMetricsTimer() throws Exception {

		MetricsTimer metricsTimer = MetricsProxyHandler.getInstance().startMetricsTimer("componentName", "function1", true);

		MetricsProxyHandler.getInstance().stopMetricsTimer(metricsTimer, null, null);

		Thread.sleep(1000);
	}

}
