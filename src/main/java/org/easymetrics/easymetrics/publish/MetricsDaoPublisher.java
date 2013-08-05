/**
 * 
 */
package org.easymetrics.easymetrics.publish;

import org.easymetrics.easymetrics.model.Record;
import org.easymetrics.easymetrics.publish.dao.DefaultMetricsDao;

/**
 * @author Administrator
 * 
 */
public class MetricsDaoPublisher implements MetricsPublisher {

	private DefaultMetricsDao	metricsDao;

	@Override
	public boolean publish(Record record) {
		int count = metricsDao.saveRecord(record);
		return count > 0;
	}

	public void setMetricsDao(DefaultMetricsDao metricsDao) {
		this.metricsDao = metricsDao;
	}

}
