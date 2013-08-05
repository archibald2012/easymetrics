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
public class Record implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private String				id;

	private String				domain;

	private String				host;

	private String				serviceGroup;

	private String				service;

	private String				version;

	private String				user;

	private String				pid;

	private String				aggregationRanges;

	private List<Measurement>	measurementList;

	private List<Aggregation>	aggregationList;

	private List<ResourceUsage>	usageList;

	private Date				createdAt;

	public Record() {

	}

	public Record(String id, String domain, String host, String serviceGroup, String service, String version, String user, String pid) {
		this.id = id;
		this.domain = domain;
		this.host = host;
		this.serviceGroup = serviceGroup;
		this.service = service;
		this.version = version;
		this.user = user;
		this.pid = pid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServiceGroup() {
		return serviceGroup;
	}

	public void setServiceGroup(String serviceGroup) {
		this.serviceGroup = serviceGroup;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAggregationRanges() {
		return aggregationRanges;
	}

	public void setAggregationRanges(String aggregationRanges) {
		this.aggregationRanges = aggregationRanges;
	}

	public List<Measurement> getMeasurementList() {
		if (measurementList == null) {
			measurementList = new ArrayList<Measurement>();
		}
		return measurementList;
	}

	public void setMeasurementList(List<Measurement> measurementList) {
		this.measurementList = measurementList;
	}

	public List<Aggregation> getAggregationList() {
		if (aggregationList == null) {
			aggregationList = new ArrayList<Aggregation>();
		}
		return aggregationList;
	}

	public void setAggregationList(List<Aggregation> aggregationList) {
		this.aggregationList = aggregationList;
	}

	public List<ResourceUsage> getUsageList() {
		if (usageList == null) {
			usageList = new ArrayList<ResourceUsage>();
		}
		return usageList;
	}

	public void setUsageList(List<ResourceUsage> usageList) {
		this.usageList = usageList;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
