/**
 * 
 */
package org.easymetrics.easymetrics.aggregate;

import java.util.Collection;

import org.easymetrics.easymetrics.model.Measurement;


/**
 * @author Administrator
 * 
 */
public interface MetricsAggregateWorker {

	boolean addMeasurement(Collection<Measurement> measurements);
}
