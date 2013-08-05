/**
 * 
 */
package org.easymetrics.easymetrics.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.easymetrics.easymetrics.model.Aggregation;
import org.easymetrics.easymetrics.model.Bucket;

import com.alibaba.fastjson.JSON;

/**
 * @author Administrator
 * 
 */
public class JmxAggregation implements JmxAggregationMBean {

	private ConcurrentHashMap<String, AggregationSum>	dataMap	= new ConcurrentHashMap<String, AggregationSum>();

	@Override
	public List<String> queryDistributionList() {
		List<String> nameList = new ArrayList<String>();

		List<AggregationSum> dataList = new ArrayList<AggregationSum>();
		dataList.addAll(dataMap.values());
		Collections.sort(dataList);
		for (AggregationSum mbeanData : dataList) {
			Aggregation aggregation = mbeanData.getAggregation();
			StringBuilder builder = new StringBuilder(join(aggregation.getComponentName(), aggregation.getFunctionName()));
			builder.append("[min=").append(aggregation.getMinimum());
			builder.append(",avg=").append(aggregation.getAverage());
			builder.append(",max=").append(aggregation.getMaximum());
			builder.append(",count=").append(aggregation.getCount());
			builder.append(",startTime=").append(aggregation.getStartTime()).append("]\n");
			nameList.add(builder.toString());
		}
		return nameList;
	}

	@Override
	public String queryDistributeDetail(String componentName, String functionName) {
		AggregationSum sumData = dataMap.get(join(componentName, functionName));
		return JSON.toJSONString(sumData);
	}

	@Override
	public void resetAllCounts() {
		dataMap.clear();
	}

	@Override
	public boolean resetCount(String componentName, String functionName) {
		return dataMap.remove(join(componentName, functionName)) == null ? false : true;
	}

	void addAggregation(AggregationEntry aggregation, Long[] ranges) {
		String key = join(aggregation.getComponentName(), aggregation.getFunctionName());
		AggregationSum sumData = dataMap.get(key);

		if (sumData == null) {
			sumData = new AggregationSum(aggregation.getComponentName(), aggregation.getFunctionName(), aggregation.getStartTime());
			List<Bucket> bucketList = sumData.getAggregation().getBucketList();
			for (int index = ranges.length - 1; index >= 0; index--) {
				Bucket bucket = new Bucket();
				bucket.setStartRange(ranges[index]);
				bucketList.add(bucket);
			}

			AggregationSum oldData = dataMap.putIfAbsent(key, sumData);
			if (oldData != null) {
				sumData = oldData;
			}
		}

		sumData.addAggregation(aggregation);
	}

	private String join(String componentName, String functionName) {
		return StringUtils.join(new Object[] { componentName, functionName }, ":");
	}
}

class AggregationSum implements Comparable<AggregationSum> {

	private Aggregation	aggregation;

	public AggregationSum() {

	}

	public AggregationSum(String componentName, String functionName, Date startTime) {
		aggregation = new Aggregation(componentName, functionName);
		aggregation.setStartTime(startTime);
	}

	public Aggregation getAggregation() {
		return aggregation;
	}

	public synchronized void addAggregation(AggregationEntry entry) {
		if (aggregation.getMaximum() < entry.getMaximum()) {
			aggregation.setMaximum(entry.getMaximum());
		}
		if (aggregation.getMinimum() < entry.getMinimum()) {
			aggregation.setMinimum(entry.getMinimum());
		}
		if (aggregation.getUnitMaximum() < entry.getUnitMaximum()) {
			aggregation.setUnitMaximum(entry.getUnitMaximum());
		}
		if (aggregation.getUnitMinimum() < entry.getUnitMinimum()) {
			aggregation.setUnitMinimum(entry.getUnitMinimum());
		}

		double total = (aggregation.getAverage() * aggregation.getCount() + entry.getAverage() * entry.getCount());
		double unitTotal = (aggregation.getUnitAverage() * aggregation.getCount()) + entry.getUnitAverage() * entry.getCount();
		long count = aggregation.getCount() + entry.getCount();
		if (count > 0) {
			aggregation.setCount(count);
			aggregation.setAverage(total / count);
			aggregation.setUnitAverage(unitTotal / count);
		}

		for (Bucket source : entry.getBucketList()) {
			for (Bucket target : aggregation.getBucketList()) {
				if (source.getStartRange() == target.getStartRange()) {
					target.setCount(target.getCount() + source.getCount());
					target.setUnitCount(target.getUnitCount() + source.getUnitCount());
					break;
				}
			}
		}
	}

	@Override
	public int compareTo(AggregationSum other) {
		return other.aggregation.getAverage() > this.aggregation.getAverage() ? 1 : -1;
	}
}
