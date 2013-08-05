/**
 * 
 */
package org.easymetrics.easymetrics.publish;

import java.util.Collection;

import org.easymetrics.easymetrics.model.Publishable;


/**
 * @author Administrator
 * 
 */
public interface MetricsPublishWorker {
	void enqueuePublishable(Publishable publishable);

	void enqueuePublishable(Collection<? extends Publishable> publishables);
}
