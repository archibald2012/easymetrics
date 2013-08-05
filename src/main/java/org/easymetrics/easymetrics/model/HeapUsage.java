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
public class HeapUsage implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private transient String	usageId;

	private String				name;

	private long				max;

	private long				used;

	public HeapUsage() {

	}

	public HeapUsage(String name, long max, long used) {
		this.name = name;
		this.max = max;
		this.used = used;
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

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
