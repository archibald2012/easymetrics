/**
 * 
 */
package org.easymetrics.easymetrics.cglib;

import java.util.HashSet;
import java.util.Set;


import net.sf.cglib.proxy.Enhancer;

/**
 * @author Administrator
 * 
 */
public class MetricsProxyInterceptor implements ProxyInterceptor {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T proxyObject(Class<T> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setInterfaces(getInterfaces(clazz).toArray(new Class<?>[0]));
		enhancer.setCallback(new MetricsMethodInterceptor());
		return (T) enhancer.create();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T proxyObject(T t) {
		Enhancer enhancer = new Enhancer();
		Class<? extends Object> clazz = t.getClass();
		enhancer.setSuperclass(clazz);
		enhancer.setInterfaces(getInterfaces(clazz).toArray(new Class<?>[0]));
		enhancer.setCallback(new MetricsObjectInterceptor(t));
		return (T) enhancer.create();
	}

	private Set<Class<?>> getInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaceList = new HashSet<Class<?>>();

		if (clazz.isInterface()) {
			interfaceList.add(clazz);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			interfaceList.addAll(getInterfaces(superClass));
		}
		Class<?>[] superInterfaces = clazz.getInterfaces();
		for (int i = 0; i < superInterfaces.length; i++) {
			Class<?> superInterface = superInterfaces[i];
			interfaceList.addAll(getInterfaces(superInterface));
		}
		return interfaceList;
	}

}
