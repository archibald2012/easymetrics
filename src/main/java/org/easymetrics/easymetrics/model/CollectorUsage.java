/**
 * 
 */
package org.easymetrics.easymetrics.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Administrator
 * 
 */
public class CollectorUsage implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private transient String	usageId;

	private String				name;

	private long				count;

	private long				time;

	public CollectorUsage() {

	}

	public CollectorUsage(String name, long count, long time) {
		this.name = name;
		this.count = count;
		this.time = time;
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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
