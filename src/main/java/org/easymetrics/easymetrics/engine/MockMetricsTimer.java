package org.easymetrics.easymetrics.engine;

import java.util.List;

import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.NameValue;


/**
 * It is used for default metrics measurement with mock operations.
 * 
 */
public class MockMetricsTimer implements MetricsTimer {
	private String	componentName;
	private String	functionName;

	public MockMetricsTimer(String componentName, String functionName) {
		this.componentName = componentName;
		this.functionName = functionName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop(Throwable t, List<NameValue> metricsList) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop(List<NameValue> metricsList) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop(Throwable t) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CorrelationInfo getCorrelationInfo() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCorrelationInfo(CorrelationInfo correlationInfo) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkUnits(int workUnits) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFailStatus(boolean failStatus) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkUser(String workUser) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMetrics(Object component) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMetrics(String name, String value) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChildTimer(MetricsTimer metricsTimer) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreateOrder(int createOrder) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MetricsTimer> getAllMetricsTimers() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Measurement getMeasurement() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getFailStatus() {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append("[component=").append(componentName);
		builder.append(",function=").append(functionName);
		builder.append(']');

		return builder.toString();
	}
}
