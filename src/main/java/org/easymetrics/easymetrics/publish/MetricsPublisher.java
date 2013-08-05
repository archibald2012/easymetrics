/**
 * 
 */
package org.easymetrics.easymetrics.publish;

import org.easymetrics.easymetrics.model.Record;

/**
 * @author Administrator
 * 
 */
public interface MetricsPublisher {

	boolean publish(Record record);
}
