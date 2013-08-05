package org.easymetrics.easymetrics.cglib;

public interface ProxyInterceptor {

	<T> T proxyObject(Class<T> clazz);

	<T> T proxyObject(T t);
}
