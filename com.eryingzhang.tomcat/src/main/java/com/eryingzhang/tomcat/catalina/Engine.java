package com.eryingzhang.tomcat.catalina;

import java.util.List;

import com.eryingzhang.tomcat.util.ServerXmlUtil;

public class Engine {

	private String defaultHost;
	private List<Host> hosts;
	private Service service;

	public Engine(Service service) {
		this.defaultHost = ServerXmlUtil.getDefaultHost();
		this.hosts = ServerXmlUtil.getHosts(this);
		this.service = service;
		checkDefault();
	}

	private void checkDefault() {
		if (defaultHost == null)
			throw new RuntimeException("the defaultHost does not exist!");
	}

	public Host getDefaultHost() {
		for (Host host : hosts) {
			if (host.getName().equals(defaultHost))
				return host;
		}
		return null;
	}

	public Service getService() {
		return service;
	}

}
