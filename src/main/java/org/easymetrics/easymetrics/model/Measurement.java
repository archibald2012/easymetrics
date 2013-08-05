/**
 * 
 */
package org.easymetrics.easymetrics.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Administrator
 * 
 */
public class Measurement implements Publishable, Serializable {

	private static final long	serialVersionUID			= 1L;

	private static final String	METRICS_EXCEPTION			= "exception";

	private static final String	METRICS_EXCEPTION_MESSAGE	= "exceptionMessage";

	private transient String	recordId;

	private String				id;

	private String				parentId;

	private String				correlationId;

	private String				correlationRequester;

	private String				componentName;

	private String				functionName;

	private String				threadName;

	private String				user;

	private Date				timestamp;

	private Long				duration;

	private Integer				workUnits;

	private Integer				createOrder;

	private Boolean				failStatus					= Boolean.valueOf(false);

	private List<NameValue>		metricsList					= new ArrayList<NameValue>();

	public Measurement() {

	}

	public Measurement(long duration, List<NameValue> metricsList, Throwable t) {
		this.duration = duration;
		this.metricsList = metricsList;
		this.threadName = Thread.currentThread().getName();

		if (t != null) {
			this.failStatus = true;
			addMetrics(METRICS_EXCEPTION, t.getClass().getName());
			addMetrics(METRICS_EXCEPTION_MESSAGE, t.getMessage());
		}
	}

	@Override
	public String getRecordId() {
		return recordId;
	}

	@Override
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getCorrelationRequester() {
		return correlationRequester;
	}

	public void setCorrelationRequester(String correlationRequester) {
		this.correlationRequester = correlationRequester;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Integer getWorkUnits() {
		return workUnits;
	}

	public void setWorkUnits(Integer workUnits) {
		this.workUnits = workUnits;
	}

	public Integer getCreateOrder() {
		return createOrder;
	}

	public void setCreateOrder(Integer createOrder) {
		this.createOrder = createOrder;
	}

	public Boolean getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(Boolean failStatus) {
		this.failStatus = failStatus;
	}

	public List<NameValue> getMetricsList() {
		return metricsList;
	}

	public void setMetricsList(List<NameValue> metricsList) {
		if (metricsList == null) {
			metricsList = new ArrayList<NameValue>();
		}
		this.metricsList = metricsList;
	}

	public void addMetrics(String name, String value) {
		getMetricsList().add(new NameValue(name, value));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
