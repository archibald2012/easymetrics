/**
 * 
 */
package org.easymetrics.easymetrics.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Administrator
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ProxyMetrics {

	/**
	 * The component name used in metrics measurement.
	 */
	String component() default "";

	/**
	 * The function name used in metrics measurement.
	 */
	String function() default "";

	/**
	 * Index to the inspectable object for getting additional metrics.
	 */
	int inspectable() default -1;

	/**
	 * Whether it is a initial metrics timer
	 */
	boolean initial() default false;
}
