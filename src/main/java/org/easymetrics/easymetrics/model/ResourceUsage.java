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
public class ResourceUsage implements Publishable, Serializable {

	private static final long		serialVersionUID	= 1L;

	private transient String		recordId;

	private String					usageId;

	private int						processorCount;

	private int						threadCount;

	private Date					checkTime;

	private long					upTime;

	private long					cpuTime;

	private long					userTime;

	private long					heapMax;

	private long					heapUsed;

	private long					nonHeapMax;

	private long					nonHeapUsed;

	private List<ThreadUsage>		threadList			= new ArrayList<ThreadUsage>();

	private List<HeapUsage>			heapList			= new ArrayList<HeapUsage>();

	private List<CollectorUsage>	collectorList		= new ArrayList<CollectorUsage>();

	@Override
	public String getRecordId() {
		return recordId;
	}

	@Override
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getUsageId() {
		return usageId;
	}

	public void setUsageId(String usageId) {
		this.usageId = usageId;
	}

	public int getProcessorCount() {
		return processorCount;
	}

	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
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

	public long getHeapMax() {
		return heapMax;
	}

	public void setHeapMax(long heapMax) {
		this.heapMax = heapMax;
	}

	public long getHeapUsed() {
		return heapUsed;
	}

	public void setHeapUsed(long heapUsed) {
		this.heapUsed = heapUsed;
	}

	public long getNonHeapMax() {
		return nonHeapMax;
	}

	public void setNonHeapMax(long nonHeapMax) {
		this.nonHeapMax = nonHeapMax;
	}

	public long getNonHeapUsed() {
		return nonHeapUsed;
	}

	public void setNonHeapUsed(long nonHeapUsed) {
		this.nonHeapUsed = nonHeapUsed;
	}

	public List<ThreadUsage> getThreadList() {
		return threadList;
	}

	public void addThreadUsage(ThreadUsage threadUsage) {
		threadUsage.setUsageId(this.usageId);
		threadList.add(threadUsage);
	}

	public List<HeapUsage> getHeapList() {
		return heapList;
	}

	public void addHeapUsage(HeapUsage heapUsage) {
		heapUsage.setUsageId(this.usageId);
		heapList.add(heapUsage);
	}

	public List<CollectorUsage> getCollectorList() {
		return collectorList;
	}

	public void addCollectorUsage(CollectorUsage collectorUsage) {
		collectorUsage.setUsageId(this.usageId);
		collectorList.add(collectorUsage);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
