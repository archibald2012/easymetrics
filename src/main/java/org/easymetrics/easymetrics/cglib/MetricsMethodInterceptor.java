/**
 * 
 */
package org.easymetrics.easymetrics.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.easymetrics.easymetrics.MetricsProxyHandler;
import org.easymetrics.easymetrics.engine.MetricsTimer;
import org.easymetrics.easymetrics.model.annotation.ProxyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class MetricsMethodInterceptor implements MethodInterceptor {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(MetricsMethodInterceptor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[],
	 * net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		ProxyMetrics proxyMetrics = method.getAnnotation(ProxyMetrics.class);
		if (proxyMetrics == null) {
			return proxy.invokeSuper(object, args);
		} else {

			MetricsProxyHandler proxyHandler = MetricsProxyHandler.getInstance();
			Object result = null;
			MetricsTimer metricsTimer = null;
			try {
				Class<?> clazz = object.getClass().getSuperclass();
				String componentName = proxyMetrics.component();
				if (componentName.length() == 0) {
					componentName = clazz.getSimpleName();
				}
				String functionName = proxyMetrics.function();
				if (functionName.length() == 0) {
					functionName = method.getName();
				}
				Object argument = null;
				if (proxyMetrics.inspectable() >= 0 && proxyMetrics.inspectable() < args.length) {
					argument = args[proxyMetrics.inspectable()];
				}
				// start metrics timer.
				metricsTimer = proxyHandler.startMetricsTimer(componentName, functionName, proxyMetrics.initial(), argument);
			} catch (RuntimeException e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Failed to start interceptor with error " + e.getMessage(), e);
				}
			}

			Throwable exception = null;
			try {
				result = proxy.invokeSuper(object, args);
			} catch (Throwable e) {
				exception = e;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Found exception from method " + method + " with error " + e.getMessage(), e);
				}
				throw e;
			} finally {
				try {
					if (metricsTimer != null) {
						// stop metrics timer.
						Object argument = null;
						if (proxyMetrics.inspectable() == -1) {
							argument = result;
						}
						proxyHandler.stopMetricsTimer(metricsTimer, argument, exception);
					}
				} catch (RuntimeException e) {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("Failed to stop interceptor with error " + e.getMessage(), e);
					}
				}
			}
			return result;
		}
	}

}
