/**
 * 
 */
package org.easymetrics.easymetrics.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.NameValue;
import org.easymetrics.easymetrics.util.MetricsUtil;
import org.easymetrics.easymetrics.util.SystemUtil;

/**
 * @author Administrator
 * 
 */
public class DefaultMetricsTimer implements MetricsTimer {

	/**
	 * unique id of metrics timer.
	 */
	private String						uniqueId		= null;
	/**
	 * correlation to identify the group
	 */
	private CorrelationInfo				correlationInfo	= null;

	private final DefaultMetricsEngine	metricsEngine;
	private final String				componentName;
	private final String				functionName;
	private long						startNano;
	private Date						startTime;
	private Integer						workUnits		= 1;
	private Integer						createOrder		= 0;
	private Boolean						failStatus;
	private String						workUser		= SystemUtil.getUserName();
	private List<NameValue>				metricsList		= new ArrayList<NameValue>();

	private MetricsTimer				parentTimer		= null;
	private final List<MetricsTimer>	childTimerList	= new ArrayList<MetricsTimer>();

	private Measurement					measurement		= null;

	/**
	 * Start metrics timer with componentName, functionName and push it to
	 * metrics engine.
	 * 
	 * @param metricsEngine
	 * @param componentName
	 * @param functionName
	 * @param correlationId
	 */
	public DefaultMetricsTimer(final DefaultMetricsEngine metricsEngine, final String componentName, final String functionName, final String correlationId) {
		this.metricsEngine = metricsEngine;
		this.componentName = MetricsUtil.truncate(componentName, 64);
		this.functionName = MetricsUtil.truncate(functionName, 64, "function");

		this.uniqueId = MetricsUtil.createGuid();

		if (metricsEngine.isCollectMetrics()) {
			this.startNano = System.nanoTime();
			this.startTime = new Date();

			// push the timer into the stack
			this.parentTimer = metricsEngine.pushTimer(this);
			if (parentTimer != null) {
				parentTimer.addChildTimer(this);
			} else {
				if (this.correlationInfo == null) {
					this.correlationInfo = new CorrelationInfo(MetricsUtil.createGuid(), uniqueId);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop(Throwable t, List<NameValue> metricsList) {
		long duration = 0;

		try {
			if (startTime != null) {
				duration = MetricsUtil.nanoToMillis(System.nanoTime() - startNano);
				if (metricsList != null) {
					this.metricsList.addAll(metricsList);
				}

				measurement = new Measurement(duration, this.metricsList, t);
				measurement.setId(uniqueId);
				if (parentTimer != null) {
					measurement.setParentId(parentTimer.getId());
				}
				if (correlationInfo != null) {
					measurement.setCorrelationId(correlationInfo.getId());
					measurement.setCorrelationRequester(correlationInfo.getRequester());
				}
				measurement.setComponentName(componentName);
				measurement.setFunctionName(functionName);
				measurement.setUser(MetricsUtil.truncate(workUser, 16));
				measurement.setCreateOrder(createOrder);
				measurement.setThreadName(MetricsUtil.truncate(measurement.getThreadName(), 64));
				for (NameValue nameValue : measurement.getMetricsList()) {
					nameValue.setName(MetricsUtil.truncate(nameValue.getName(), 64, "name"));
					nameValue.setValue(MetricsUtil.truncate(nameValue.getValue(), 1024));
				}
				if (failStatus != null && failStatus) {
					measurement.setFailStatus(failStatus);
				}
				// TODO work units
				measurement.setWorkUnits(workUnits);
				measurement.setTimestamp(startTime);
				measurement.setDuration(duration);

				// remove this timer from stack
				metricsEngine.popTimer(this);

				if (parentTimer == null) {
					// store the root metrics timer to the memory queue
					metricsEngine.enqueueMetricsTimer(this);
				}

			}
		} catch (RuntimeException e) {
			if (metricsEngine.isThrowException()) {
				throw e;
			}
		}
		return duration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop(List<NameValue> metricsList) {
		return stop(null, metricsList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop(Throwable t) {
		return stop(t, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long stop() {
		return stop(null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChildTimer(MetricsTimer metricsTimer) {
		metricsTimer.setCorrelationInfo(correlationInfo);
		metricsTimer.setCreateOrder(childTimerList.size());
		childTimerList.add(metricsTimer);
	}

	@Override
	public CorrelationInfo getCorrelationInfo() {
		return this.correlationInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCorrelationInfo(CorrelationInfo correlationInfo) {
		this.correlationInfo = correlationInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkUnits(int workUnits) {
		this.workUnits = workUnits;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFailStatus(boolean failStatus) {
		this.failStatus = failStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkUser(String workUser) {
		this.workUser = workUser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMetrics(Object requester) {
		if (requester != null) {
			Map<String, NameValue> attributeMap = MetricsUtil.getAttributeMap(requester);
			if (attributeMap != null) {
				Collection<NameValue> metricsList = attributeMap.values();
				this.metricsList.addAll(metricsList);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMetrics(String name, String value) {
		this.metricsList.add(new NameValue(name, value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MetricsTimer> getAllMetricsTimers() {
		List<MetricsTimer> timerList = new ArrayList<MetricsTimer>();
		timerList.add(this);
		for (MetricsTimer childTimer : childTimerList) {
			timerList.addAll(childTimer.getAllMetricsTimers());
		}
		return timerList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Measurement getMeasurement() {
		return measurement;
	}

	@Override
	public String getId() {
		return uniqueId;
	}

	public final Long getDuration() {
		if (measurement != null && measurement.getDuration() != null) {
			return measurement.getDuration();
		} else {
			return 0L;
		}
	}

	@Override
	public Boolean getFailStatus() {
		Boolean failStatus = this.failStatus;
		if (measurement != null) {
			failStatus = measurement.getFailStatus();
		}
		return failStatus == null ? false : failStatus;
	}

	@Override
	public void setCreateOrder(int createOrder) {
		this.createOrder = createOrder;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public Integer getWorkUnits() {
		return workUnits;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		builder.append("[component=").append(componentName);
		builder.append(",function=").append(functionName);
		builder.append("]");

		return builder.toString();
	}

}
