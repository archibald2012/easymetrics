package org.easymetrics.easymetrics.runtime;

import java.util.Collection;

import org.easymetrics.easymetrics.model.Publishable;
import org.easymetrics.easymetrics.publish.MetricsPublishWorker;
import org.easymetrics.easymetrics.runtime.DefaultRuntimeWorker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MetricsRuntimeWorkerTestCase {

	private DefaultRuntimeWorker	runtimeWorker;

	@Before
	public void setUp() throws Exception {
		runtimeWorker = new DefaultRuntimeWorker();
	}

	@After
	public void tearDown() throws Exception {
		runtimeWorker.setTerminating();
		runtimeWorker = null;
	}

	@Test
	public void testCheckRuntimeMetrics() throws Exception {

		MetricsPublishWorker metricsPublishWorker = new MetricsPublishWorker() {

			@Override
			public void enqueuePublishable(Publishable publishable) {
				System.out.println(publishable);
			}

			@Override
			public void enqueuePublishable(Collection<? extends Publishable> publishables) {

			}

		};
		runtimeWorker.setMetricsPublishWorker(metricsPublishWorker);
		runtimeWorker.checkRuntimeMetrics();

		runtimeWorker.setDetailHeap(true);
		runtimeWorker.setDetailThread(true);
		runtimeWorker.setDetailGc(true);
		runtimeWorker.checkRuntimeMetrics();

	}

}
