package com.eryingzhang.tomcat.catalina;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;
import com.eryingzhang.tomcat.util.ThreadPoolUtil;

import cn.hutool.log.LogFactory;

public class Connector implements Runnable {

	private String compression;
	private int commpressionMinSize;
	private String noCompressionUserAgents;
	private String compressableMimeType;

	int port;
	private Service service;

	public Connector(Service service) {
		this.service = service;
	}

	public Service getService() {
		return service;
	}

	public void init() {
		LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]", port);

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getCompression() {
		return compression;
	}

	public void setCompression(String compression) {
		this.compression = compression;
	}

	public int getCommpressionMinSize() {
		return commpressionMinSize;
	}

	public void setCommpressionMinSize(int commpressionMinSize) {
		this.commpressionMinSize = commpressionMinSize;
	}

	public String getNoCompressionUserAgents() {
		return noCompressionUserAgents;
	}

	public void setNoCompressionUserAgents(String noCompressionUserAgents) {
		this.noCompressionUserAgents = noCompressionUserAgents;
	}

	public String getCompressableMimeType() {
		return compressableMimeType;
	}

	public void setCompressableMimeType(String compressableMimeType) {
		this.compressableMimeType = compressableMimeType;
	}

	public void run() {
		try {
			final ServerSocket ss = new ServerSocket(port);

			while (true) {
				Runnable r = new Runnable() {
					Socket s = ss.accept();

					public void run() {
						try {
							Request request = new Request(s, Connector.this);
							Response response = new Response();
							HttpProcessor processor = new HttpProcessor();
							processor.execute(s, request, response);
						} finally {
							if (!s.isClosed())
								try {
									s.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					}
				};

				ThreadPoolUtil.exec(r);
			}
		} catch (IOException e) {
			LogFactory.get().error(e);
			e.printStackTrace();
		}
	}

	public void start() {
		LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]", port);
		new Thread(this).start();
	}

}
