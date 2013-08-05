/**
 * 
 */
package org.easymetrics.easymetrics.cglib;

import org.easymetrics.easymetrics.model.Measurable;

/**
 * @author Administrator
 * 
 */
public class MeasurableHelloWorld implements Measurable {

	public Object testHold(String arg1) {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		return arg1;
	}

	Object testHold1(String arg1) {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		return arg1;
	}

	Object testHold2(String arg1) {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		return arg1;
	}
}
