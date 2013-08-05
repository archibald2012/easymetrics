/**
 * 
 */
package org.easymetrics.easymetrics.measure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.easymetrics.easymetrics.model.Measurement;

/**
 * @author Administrator
 * 
 */
public class JmxMeasurement implements JmxMeasurementMBean {

	// key - componentName:functionName
	private ConcurrentMap<String, MBeanData>	dataMap	= new ConcurrentHashMap<String, MBeanData>();

	@Override
	public List<String> queryMeasurementList() {
		List<String> nameList = new ArrayList<String>();

		List<MBeanData> dataList = new ArrayList<MBeanData>();
		dataList.addAll(dataMap.values());
		Collections.sort(dataList);
		for (MBeanData mbeanData : dataList) {
			StringBuilder builder = new StringBuilder(join(mbeanData.getComponentName(), mbeanData.getFunctionName()));
			builder.append("[min=").append(mbeanData.getMinimum());
			builder.append(",avg=").append(mbeanData.getAverage());
			builder.append(",max=").append(mbeanData.getMaximum());
			builder.append(",count=").append(mbeanData.getCount());
			builder.append(",fail=").append(mbeanData.getFailCount()).append("]\n");
			nameList.add(builder.toString());
		}
		return nameList;
	}

	@Override
	public void resetAllCounts() {
		dataMap.clear();
	}

	@Override
	public boolean resetCount(String componentName, String functionName) {
		String key = join(componentName, functionName);
		return dataMap.remove(key) == null ? false : true;
	}

	void addMeasurements(Measurement... measurements) {
		for (Measurement measurement : measurements) {
			String key = join(measurement.getComponentName(), measurement.getFunctionName());
			MBeanData mbeanData = dataMap.get(key);
			if (mbeanData == null) {
				mbeanData = new MBeanData(measurement);
				MBeanData oldData = dataMap.putIfAbsent(key, mbeanData);
				if (oldData != null) {
					oldData.addMeasurement(measurement);
				}
			} else {
				mbeanData.addMeasurement(measurement);
			}
		}
	}

	private String join(String componentName, String functionName) {
		return StringUtils.join(new Object[] { componentName, functionName }, ":");
	}
}

class MBeanData implements Comparable<MBeanData> {
	private String	componentName;
	private String	functionName;
	private long	minimum;
	private long	maximum;
	private double	average;
	private long	count;
	private long	failCount;

	public MBeanData(Measurement measurement) {
		this.componentName = measurement.getComponentName();
		this.functionName = measurement.getFunctionName();
		if (measurement.getDuration() != null) {
			maximum = measurement.getDuration();
			minimum = measurement.getDuration();
			average = measurement.getDuration();
			count = 1;
			if (measurement.getFailStatus() != null && measurement.getFailStatus()) {
				failCount = 1;
			}
		}
	}

	public synchronized void addMeasurement(Measurement measurement) {
		if (measurement.getDuration() != null) {
			if (maximum < measurement.getDuration()) {
				maximum = measurement.getDuration();
			}
			if (minimum > measurement.getDuration()) {
				minimum = measurement.getDuration();
			}

			average = ((average * count) + measurement.getDuration()) / (count + 1);
			count++;
			if (measurement.getFailStatus() != null && measurement.getFailStatus()) {
				failCount++;
			}
		}
	}

	@Override
	public int compareTo(MBeanData other) {
		return other.average > this.average ? 1 : -1;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public long getMinimum() {
		return minimum;
	}

	public long getMaximum() {
		return maximum;
	}

	public double getAverage() {
		return average;
	}

	public long getCount() {
		return count;
	}

	public long getFailCount() {
		return failCount;
	}

}
