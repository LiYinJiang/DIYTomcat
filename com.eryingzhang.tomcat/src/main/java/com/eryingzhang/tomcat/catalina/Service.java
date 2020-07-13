package com.eryingzhang.tomcat.catalina;

import java.util.List;

import com.eryingzhang.tomcat.util.ServerXmlUtil;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

public class Service {
	private String name;
	private Engine engine;
	private Server server;
	private List<Connector> connectors;

	public Service(Server server) {
		this.name = ServerXmlUtil.getServiceName();
		this.engine = new Engine(this);
		this.server = server;
		this.connectors = ServerXmlUtil.getConnectors(this);
	}

	public Engine getEngine() {
		return engine;
	}

	public Server getServer() {
		return server;
	}

	public void start() {
		init();
	}

	private void init() {
		TimeInterval timeInterval = DateUtil.timer();
		for (Connector connector : connectors) {
			connector.init();
		}
		LogFactory.get().info("initialization processed in {} ms", timeInterval.intervalMs());
		for (Connector connector : connectors) {
			connector.start();
		}
	}

}
