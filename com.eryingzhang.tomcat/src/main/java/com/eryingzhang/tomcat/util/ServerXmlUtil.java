package com.eryingzhang.tomcat.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eryingzhang.tomcat.catalina.Connector;
import com.eryingzhang.tomcat.catalina.Context;
import com.eryingzhang.tomcat.catalina.Engine;
import com.eryingzhang.tomcat.catalina.Host;
import com.eryingzhang.tomcat.catalina.Service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;

public class ServerXmlUtil {
	private static final Document DOC = Jsoup.parse(FileUtil.readUtf8String(Constant.FILE_CONF_SERVER_XML));

	public static List<Context> getContext(Host host) {
		List<Context> cs = new ArrayList<Context>();

		Elements elements = DOC.getElementsByTag("Context");
		for (Element element : elements) {
			String path = element.attr("path");
			String docBase = element.attr("docBase");
			boolean reloadable = Convert.toBool(element.attr("reloadable"), true);
			Context c = new Context(path, docBase, host, reloadable);
			cs.add(c);
		}

		return cs;
	}

	public static String getServiceName() {

		return DOC.select("Service").first().attr("name");
	}

	public static String getDefaultHost() {
		return DOC.select("Engine").first().attr("defaultHost");
	}

	public static List<Host> getHosts(Engine engine) {
		List<Host> hosts = new ArrayList<Host>();
		Elements es = DOC.getElementsByTag("Host");
		for (Element element : es) {

			Host host = new Host(element.attr("name"), engine);
			hosts.add(host);
		}
		return hosts;
	}

	public static List<Connector> getConnectors(Service service) {
		List<Connector> result = new ArrayList<Connector>();
		Elements cs = DOC.select("Connector");
		for (Element connector : cs) {
			Connector c = new Connector(service);
			c.setPort(Convert.convert(Integer.class, connector.attr("port")));
			c.setCommpressionMinSize(Convert.toInt(connector.attr("compressionMinSize"), 0));
			c.setCompressableMimeType(connector.attr("compressableMimeType"));
			c.setCompression(connector.attr("compression"));
			c.setNoCompressionUserAgents(connector.attr("noCompressionUserAgents"));

			result.add(c);
		}
		return result;
	}

	public static Engine getEngine(String serviceName) {
		return null;
	}

}
