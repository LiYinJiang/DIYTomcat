package com.eryingzhang.tomcat.test;

public class TestClassLoader {
	public static void main(String[] args) {
		System.out.println(String.class.getClassLoader());
		System.out.println(Object.class.getClassLoader());
		System.out.println(TestTomcat.class.getClassLoader());
	}
}
