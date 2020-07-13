package com.eryingzhang.tomcat.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class StandardServletConfig implements ServletConfig {

	private String servletName;
	private Map<String, String> initParameterMap;
	private ServletContext servletContext;

	public StandardServletConfig(ServletContext servletContext, String servletName,  Map<String, String> initParameterMap) {
		this.servletContext = servletContext;
		this.servletName = servletName;
		this.initParameterMap = initParameterMap;

	}

	public String getServletName() {
		// TODO Auto-generated method stub
		return servletName;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return servletContext;
	}

	public String getInitParameter(String name) {
		// TODO Auto-generated method stub
		return initParameterMap.get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		Set<String> paraNames = initParameterMap.keySet();
		return Collections.enumeration(paraNames);
	}

}
