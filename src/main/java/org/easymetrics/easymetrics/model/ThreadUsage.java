package org.easymetrics.easymetrics.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ThreadUsage implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private transient String	usageId;

	private String				name;

	private String				state;

	private long				cpuTime;

	private long				userTime;

	public ThreadUsage() {

	}

	public ThreadUsage(String name, String state, long cpuTime, long userTime) {
		this.name = name;
		this.state = state;
		this.cpuTime = cpuTime;
		this.userTime = userTime;
	}

	public String getUsageId() {
		return usageId;
	}

	public void setUsageId(String usageId) {
		this.usageId = usageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(long cpuTime) {
		this.cpuTime = cpuTime;
	}

	public long getUserTime() {
		return userTime;
	}

	public void setUserTime(long userTime) {
		this.userTime = userTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
