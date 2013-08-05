package org.easymetrics.easymetrics.engine;

public class MockMetricsEngine implements MetricsEngine {

	@Override
	public MetricsCollector createMetricsCollector(String name) {
		return new MockMetricsCollector(name);
	}

	@Override
	public boolean isCollectMetrics() {
		return false;
	}

	@Override
	public boolean isThrowException() {
		return false;
	}

	@Override
	public boolean isFilter(String componentName, String functionName) {
		return false;
	}

	@Override
	public boolean updateFilter(String componentName, String functionName, boolean isFilter) {
		return false;
	}

}
