package com.eryingzhang.tomcat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.eryingzhang.tomcat.classloader.CommonClassLoader;

public class BootStrap {

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		CommonClassLoader loader = new CommonClassLoader();
		Thread.currentThread().setContextClassLoader(loader);

		Class<?> clazz = loader.loadClass("com.eryingzhang.tomcat.catalina.Server");
		Object object = clazz.newInstance();
		Method method = clazz.getMethod("start", null);
		method.invoke(object, null);
		System.out.println(clazz.getClassLoader());

	}

}
