/**
 * 
 */
package org.easymetrics.easymetrics.cglib;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Administrator
 * 
 */
public class MeasurableHelloWorldTestCase {

	private AbstractApplicationContext	ctx;
	private MeasurableHelloWorld		measurableHelloWorld;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] { "metrics_config.xml" });
		measurableHelloWorld = (MeasurableHelloWorld) ctx.getBean("measurableHelloWorld");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		ctx = null;
	}

	@Test
	public void test() {
		Assert.assertEquals("1", measurableHelloWorld.testHold("1"));
	}

	@Test
	public void testNonPublic() {
		Assert.assertEquals("1", measurableHelloWorld.testHold1("1"));
	}

	@Test
	public void testNonAnnotated() {
		Assert.assertEquals("1", measurableHelloWorld.testHold2("1"));
	}

}
