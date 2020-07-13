package com.eryingzhang.tomcat.catalina;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;

public class Server {
	Service service;

	public Server() {
		this.service = new Service(this);
	}

	public void start() {
		TimeInterval timeInterval = DateUtil.timer();
		logJVM();
		init();
		LogFactory.get().info("Server startup in {} ms", timeInterval.intervalMs());
	}

	public void init() {
		service.start();
	}

	private static void logJVM() {
		Map<String, String> infos = new LinkedHashMap<String, String>();
		infos.put("Server version", "erYingZhang DiyTomcat/0.0.1");
		infos.put("Server built", DateUtil.now());
		infos.put("Server number", "0.0.1");
		infos.put("OS Name\t", SystemUtil.get("os.name"));
		infos.put("OS Version", SystemUtil.get("os.version"));
		infos.put("Architecture", SystemUtil.get("os.arch"));
		infos.put("Java Home", SystemUtil.get("java.home"));
		infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
		infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

		Set<String> keys = infos.keySet();
		for (String key : keys) {
			LogFactory.get().info(key + ":\t\t" + infos.get(key));
		}
	}
}
