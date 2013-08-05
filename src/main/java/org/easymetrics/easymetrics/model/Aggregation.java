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
public class Aggregation implements Publishable, Serializable {

	private static final long	serialVersionUID	= 1L;

	private String				id;

	private String				componentName;

	private String				functionName;

	private Date				startTime;

	private long				duration;

	private long				maximum;

	private long				minimum;

	private double				average;

	private long				unitMaximum;

	private long				unitMinimum;

	private double				unitAverage;

	private long				count;

	private List<Bucket>		bucketList			= new ArrayList<Bucket>();

	private transient String	recordId;

	private transient String	ranges;

	public Aggregation() {

	}

	public Aggregation(String componentName, String functionName) {
		this.componentName = componentName;
		this.functionName = functionName;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getMaximum() {
		return maximum;
	}

	public void setMaximum(long maximum) {
		this.maximum = maximum;
	}

	public long getMinimum() {
		return minimum;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public long getUnitMaximum() {
		return unitMaximum;
	}

	public void setUnitMaximum(long unitMaximum) {
		this.unitMaximum = unitMaximum;
	}

	public long getUnitMinimum() {
		return unitMinimum;
	}

	public void setUnitMinimum(long unitMinimum) {
		this.unitMinimum = unitMinimum;
	}

	public double getUnitAverage() {
		return unitAverage;
	}

	public void setUnitAverage(double unitAverage) {
		this.unitAverage = unitAverage;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<Bucket> getBucketList() {
		return bucketList;
	}

	public void addBucket(Bucket bucket) {
		getBucketList().add(bucket);
	}

	public String getRanges() {
		return ranges;
	}

	public void setRanges(String ranges) {
		this.ranges = ranges;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
