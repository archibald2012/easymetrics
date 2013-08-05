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
public class Bucket  implements Serializable{

	private static final long	serialVersionUID	= 1L;
	
	private long	startRange;

	private long	count;

	private long	unitCount;

	public long getStartRange() {
		return startRange;
	}

	public void setStartRange(long startRange) {
		this.startRange = startRange;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(long unitCount) {
		this.unitCount = unitCount;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
