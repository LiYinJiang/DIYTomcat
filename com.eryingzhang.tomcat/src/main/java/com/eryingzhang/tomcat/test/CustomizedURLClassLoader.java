package com.eryingzhang.tomcat.test;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CustomizedURLClassLoader extends URLClassLoader {
	public static String jarPath = "H:/Java_source_space/com.eryingzhang.tomcat/testTomcat.jar";// "F:/Java_source_space/how2j/diyTomcat/test.jar";

	public CustomizedURLClassLoader(URL[] url) {
		super(url);
	}

	public static void main(String[] args)
			throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		URL url = new URL("file:" + jarPath);
		
		URL[] urls = new URL[] { url };
		CustomizedURLClassLoader loader1 = new CustomizedURLClassLoader(urls);
		Class<?> how2jClass1 = loader1.loadClass("dao.CategoryDAO");

		CustomizedURLClassLoader loader2 = new CustomizedURLClassLoader(urls);
		Class<?> how2jClass2 = loader2.loadClass("dao.CategoryDAO");

		System.out.println(how2jClass1 == how2jClass2);
		
	}

}
