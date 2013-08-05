/**
 * 
 */
package org.easymetrics.easymetrics.engine;

import java.util.List;

import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.NameValue;


/**
 * @author Administrator
 * 
 */
public interface MetricsTimer {

	/**
	 * Get unique identifier
	 * 
	 * @return ID
	 */
	String getId();

	/**
	 * Stop metrics timer with exception and additional info.
	 * 
	 * @param t
	 * @param metricsList
	 * @return duration
	 */
	long stop(Throwable t, List<NameValue> metricsList);

	/**
	 * Stop metrics timer with additional info.
	 * 
	 * @param metricsList
	 * @return duration
	 */
	long stop(List<NameValue> metricsList);

	/**
	 * Stop metrics timer with exception
	 * 
	 * @param t
	 * @return duration
	 */
	long stop(Throwable t);

	/**
	 * Stop metrics timer.
	 * 
	 * @return duration
	 */
	long stop();

	/**
	 * Add kid metrics timer.
	 * 
	 * @param metricsTimer
	 */
	void addChildTimer(MetricsTimer metricsTimer);

	/**
	 * Set work units.
	 * 
	 * @param workUnits
	 */
	void setWorkUnits(int workUnits);

	/**
	 * Set fail status.
	 * 
	 * @param failStatus
	 */
	void setFailStatus(boolean failStatus);

	/**
	 * Set worker user.
	 * 
	 * @param workUser
	 */
	void setWorkUser(String workUser);

	/**
	 * Set create order
	 * 
	 * @param createOrder
	 */
	void setCreateOrder(int createOrder);

	/**
	 * Add additional info.
	 * 
	 * @param component
	 */
	void addMetrics(Object component);

	/**
	 * Add additional info.
	 * 
	 * @param name
	 * @param value
	 */
	void addMetrics(String name, String value);

	/**
	 * get metrics timers of all generations.
	 * 
	 * @return metricsTimers
	 */
	List<MetricsTimer> getAllMetricsTimers();

	/**
	 * Get correlationInfo.
	 * 
	 * @return CorrelationInfo
	 */
	CorrelationInfo getCorrelationInfo();

	/**
	 * Set correlationInfo.
	 * 
	 * @param correlationInfo
	 */
	void setCorrelationInfo(CorrelationInfo correlationInfo);

	/**
	 * 
	 * @return Measurement
	 */
	Measurement getMeasurement();

	/**
	 * 
	 * @return fail status.
	 */
	Boolean getFailStatus();
}
