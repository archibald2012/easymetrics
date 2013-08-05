package org.easymetrics.easymetrics.engine;

public class MockMetricsCollector implements MetricsCollector {
	private String	componentName;

	/**
	 * @param componentName
	 */
	public MockMetricsCollector(String componentName) {
		this.componentName = componentName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MetricsTimer startMetricsTimer(String functionName, String correlationId, boolean startNew) {
		return new MockMetricsTimer(componentName, functionName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MetricsTimer startMetricsTimer(String functionName, String correlationId) {
		return new MockMetricsTimer(componentName, functionName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MetricsTimer startMetricsTimer(String functionName) {
		return new MockMetricsTimer(componentName, functionName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MetricsTimer startInitialTimer(String functionName, String correlationId) {
		return new MockMetricsTimer(componentName, functionName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MetricsTimer startInitialTimer(String functionName) {
		return new MockMetricsTimer(componentName, functionName);
	}

}
