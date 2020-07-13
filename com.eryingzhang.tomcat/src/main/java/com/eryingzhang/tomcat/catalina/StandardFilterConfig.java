package com.eryingzhang.tomcat.catalina;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class StandardFilterConfig implements FilterConfig {
	private String filterName;
	private ServletContext servletContext;
	private Map<String, String> initParameter;

	public StandardFilterConfig(String filterName, ServletContext servletContext, Map<String, String> initParameter) {
		this.filterName = filterName;
		this.initParameter = initParameter;
		this.servletContext = servletContext;
		if (null == initParameter)
			this.initParameter = new HashMap<String, String>();
	}

	public String getFilterName() {
		// TODO Auto-generated method stub
		return filterName;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return servletContext;
	}

	public String getInitParameter(String name) {
		// TODO Auto-generated method stub
		return initParameter.get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		// TODO Auto-generated method stub
		return Collections.enumeration(initParameter.keySet());
	}

}
