package org.easymetrics.easymetrics.cglib;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.easymetrics.easymetrics.MetricsProxyHandler;
import org.easymetrics.easymetrics.engine.MetricsTimer;
import org.easymetrics.easymetrics.model.annotation.ProxyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsObjectInterceptor implements MethodInterceptor {
	private static final Logger	LOGGER		= LoggerFactory.getLogger(MetricsObjectInterceptor.class);

	private Object				target		= null;
	private Set<String>			skipMethods	= new HashSet<String>();
	private boolean				initial		= true;

	public MetricsObjectInterceptor(Object target) {
		this.target = target;
		setSkipMethods("hashCode,toString,equals,wait,notify,notifyAll,getClass,clone,finalize");
	}

	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {

		String functionName = method.getName();
		if (skipMethods.contains(functionName)) {
			return proxy.invokeSuper(object, args);
		} else {

			ProxyMetrics proxyMetrics = method.getAnnotation(ProxyMetrics.class);

			Throwable exception = null;
			MetricsTimer metricsTimer = null;
			MetricsProxyHandler proxyHandler = MetricsProxyHandler.getInstance();

			try {
				Class<?> clazz = target.getClass().getSuperclass();

				String componentName = clazz.getSimpleName();
				Object argument = null;

				if (proxyMetrics != null) {
					if (proxyMetrics.component().length() > 0) {
						componentName = proxyMetrics.component();
					}
					if (proxyMetrics.function().length() > 0) {
						functionName = proxyMetrics.function();
					}
					if (proxyMetrics.inspectable() >= 0 && proxyMetrics.inspectable() < args.length) {
						argument = args[proxyMetrics.inspectable()];
					}
				}
				metricsTimer = proxyHandler.startMetricsTimer(componentName, functionName, initial, argument);
			} catch (RuntimeException e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Failed to start metrics with error " + e.getMessage(), e);
				}
			}

			Object result = null;
			try {
				Method invokeMethod = getInvokeMethod(method);
				result = invokeMethod.invoke(target, args);
			} catch (Throwable e) {
				exception = e.getCause();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Found exception from method " + method + " with error " + e.getMessage(), e);
				}
				throw exception;
			} finally {
				try {
					if (metricsTimer != null) {
						Object argument = null;
						if (proxyMetrics != null && proxyMetrics.inspectable() == -1) {
							argument = result;
						}
						proxyHandler.stopMetricsTimer(metricsTimer, argument, exception);
					}
				} catch (RuntimeException e) {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("Failed to stop metrics with error " + e.getMessage(), e);
					}
				}
			}

			return result;
		}
	}

	private Method getInvokeMethod(Method targetMethod) {

		Class<?> itr = target.getClass();
		Class<?>[] targetTypes = targetMethod.getParameterTypes();
		while (itr != null) {
			for (Method method : itr.getDeclaredMethods()) {
				if (!method.getName().equals(targetMethod.getName())) {
					continue;
				}
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length != targetTypes.length) {
					continue;
				}
				boolean isFound = true;
				for (int i = 0; i < parameterTypes.length; i++) {
					if (!parameterTypes[i].isAssignableFrom(targetTypes[i])) {
						isFound = false;
						break;
					}
				}
				if (isFound) {
					return method;
				}
			}
			itr = itr.getSuperclass();
		}

		throw new UnsupportedOperationException("Failed to find method " + targetMethod.getName() + " from class " + target.getClass());
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	public void setSkipMethods(String skipMethods) {
		this.skipMethods.addAll(Arrays.asList(skipMethods.split(",")));
	}

}
