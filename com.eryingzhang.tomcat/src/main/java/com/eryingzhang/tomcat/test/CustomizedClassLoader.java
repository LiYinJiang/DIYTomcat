package com.eryingzhang.tomcat.test;

import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.eryingzhang.tomcat.util.Constant;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

public class CustomizedClassLoader extends ClassLoader	{
	
	private File classLoaderFolder = new File(System.getProperty("user.dir"),"target/classes");
	
	protected Class<?> findClass(String fullQualifiedName) throws ClassNotFoundException{
		byte[] data = loadClassData(fullQualifiedName);
		return defineClass(fullQualifiedName,data, 0, data.length);
	}
	
	private byte[] loadClassData(String fullQualifiedName) throws ClassNotFoundException {
		String fileName = StrUtil.replace(fullQualifiedName, ".", "/") + ".class";
		File classFile = new File(classLoaderFolder, fileName);
		if(!classFile.exists())
			throw new ClassNotFoundException(fullQualifiedName);
		return FileUtil.readBytes(classFile);
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
		CustomizedClassLoader loader = new CustomizedClassLoader();
		try {
			Class<?> instance = loader.findClass("com.eryingzhang.tomcat.test.TestTomcat");
			Object o = instance.newInstance();
			Method method = instance.getMethod("Hello");
			method.invoke(o);
			
			System.out.println(instance.getClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
