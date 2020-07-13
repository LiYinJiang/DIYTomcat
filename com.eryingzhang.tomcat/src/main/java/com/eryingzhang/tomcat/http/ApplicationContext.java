package com.eryingzhang.tomcat.http;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.eryingzhang.tomcat.catalina.Context;

public class ApplicationContext extends BaseServletContext {
	private Map<String, Object> attributeMap;
	private Context context;

	public ApplicationContext(Context context) {
		this.attributeMap = new HashMap<String, Object>();
		this.context = context;
	}

	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return attributeMap.get(name);
	}

	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		attributeMap.remove(name);
	}

	public void setAttribute(String name, Object object) {
		attributeMap.put(name, object);
	}


	public Enumeration<String> getAttributeNames() {
		Set<String> names = attributeMap.keySet();
		return Collections.enumeration(names);
	}


	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return new File(context.getDocBase(), path).getAbsolutePath();
	}

}
