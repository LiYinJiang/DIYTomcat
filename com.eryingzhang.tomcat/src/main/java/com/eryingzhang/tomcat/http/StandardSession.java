package com.eryingzhang.tomcat.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import cn.hutool.core.convert.Convert;
import cn.hutool.log.Log;

public class StandardSession implements HttpSession {
	private Map<String, Object> attributesMap;

	private String id;
	private long creationTime;
	private long lastAccessedTime;
	private ServletContext servletContext;
	private int maxInactiveInterval;

	public StandardSession(String jsessionId, ServletContext servletContext) {
		this.attributesMap = new HashMap<String, Object>();
		this.id = jsessionId;
		this.creationTime = System.currentTimeMillis();
		this.servletContext = servletContext;

	}

	public long getCreationTime() {
		// TODO Auto-generated method stub
		return creationTime;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return lastAccessedTime;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return servletContext;
	}

	public void setMaxInactiveInterval(int interval) {
		// TODO Auto-generated method stub
		maxInactiveInterval = interval;
	}

	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return maxInactiveInterval;
	}

	@SuppressWarnings("deprecation")
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return attributesMap.get(name);
	}

	public Object getValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(attributesMap.keySet());
	}

	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttribute(String name, Object value) {
		attributesMap.put(name, value);

	}

	public void putValue(String name, Object value) {
		// TODO Auto-generated method stub

	}

	public void removeAttribute(String name) {
		attributesMap.remove(name);

	}

	public void removeValue(String name) {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub
		attributesMap.clear();
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return creationTime == lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

}
