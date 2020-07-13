package com.eryingzhang.tomcat.http;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import com.eryingzhang.tomcat.catalina.Connector;
import com.eryingzhang.tomcat.catalina.Context;
import com.eryingzhang.tomcat.catalina.Service;
import com.eryingzhang.tomcat.util.Constant;
import com.eryingzhang.tomcat.util.MiniBrowser;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public class Request extends BaseRequest {

	private boolean forwarded;
	private String requestString;
	private String uri;
	private Socket socket;
	private Context context;
	private Connector connector;
	private String method;

	private Map<String, String[]> parameterMap;
	private Map<String, Object> attributeMap;
	private String queryString;

	private Map<String, String> headerMap;

	private Cookie[] cookies;
	private HttpSession session;

	public Request(Socket socket, Connector connector) {

		this.socket = socket;
		this.connector = connector;
		this.parameterMap = new HashMap<String, String[]>();
		attributeMap = new HashMap<String, Object>();
		headerMap = new HashMap<String, String>();

		parseHttpRequest();
		if (StrUtil.isEmpty(requestString))
			return;
		parseUri();
		parseMethod();
		parseContext();
		parseParameters();
		parseHeaders();
		parseCookies();
		if (!"/".equals(context.getPath())) {
			uri = StrUtil.removePrefix(uri, context.getPath());
			if (StrUtil.isEmpty(uri))
				uri = "/";
		}
	}

	private void parseHttpRequest() {
		try {
			byte[] results = MiniBrowser.readBytes(socket.getInputStream(), false);
			requestString = new String(results, Constant.ENCODE_UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void parseUri() {
		String temp;
		temp = StrUtil.subBetween(requestString, " ", " ");
		if (!StrUtil.containsAny(temp, "?")) {
			uri = temp;
			return;
		}
		temp = StrUtil.subBefore(temp, "?", false);
		uri = temp;
	}

	private void parseMethod() {
		method = StrUtil.subBefore(requestString, " ", false);
	}

	private void parseContext() {
		String path = StrUtil.subBetween(uri, "/", "/");
		if (null == path)
			path = "/";
		else {
			path = "/" + path;
		}
		context = connector.getService().getEngine().getDefaultHost().getContext(path);
		if (null == context)
			context = connector.getService().getEngine().getDefaultHost().getContext("/");

	}

	private void parseParameters() {

		if ("GET".equals(method)) {
			String url = StrUtil.subBetween(requestString, " ", " ");
			if (StrUtil.contains(url, '?')) {
				queryString = StrUtil.subAfter(url, "?", false);
			}
		}
		if ("POST".equals(method)) {
			queryString = StrUtil.subAfter(requestString, "\r\n\r\n", false);
		}

		if (null == queryString)
			return;
		String[] parameterValues = queryString.split("&");
		for (String parameterValue : parameterValues) {
			String[] nameValues = parameterValue.split("=");
			String name = nameValues[0];
			String value = nameValues[1];
			String[] values = parameterMap.get(name);
			if (null == values) {
				values = new String[] { value };
			} else {
				values = ArrayUtil.append(values, value);
			}
			parameterMap.put(name, values);
		}
	}

	private void parseHeaders() {
		StringReader stringReader = new StringReader(requestString);
		List<String> lines = new ArrayList<String>();
		IoUtil.readLines(stringReader, lines);
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			if (0 == line.length())
				break;
			String[] segs = line.split(":");
			String headerName = segs[0].toLowerCase();
			String headerValue = segs[1];
			headerMap.put(headerName, headerValue);
		}
	}

	private void parseCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		String cookieStr = headerMap.get("cookie");
		if (cookieStr != null) {
			String[] pairs = StrUtil.split(cookieStr, ";");
			for (String pair : pairs) {
				if (!StrUtil.isBlank(pair)) {
					String[] segs = StrUtil.split(pair, "=");
					String name = segs[0].trim();
					String value = segs[1].trim();
					Cookie cookie = new Cookie(name, value);
					cookies.add(cookie);
				}
			}

		}

		this.cookies = ArrayUtil.toArray(cookies, Cookie.class);
	}

	public boolean isForwarded() {
		return forwarded;
	}

	public void setForwarded(boolean forwarded) {
		this.forwarded = forwarded;
	}

	public String getUri() {
		return uri;

	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getRequestString() {
		return requestString;
	}

	public Context getContext() {
		return context;
	}

	public Service getService() {
		return connector.getService();
	}

	public Connector getConnector() {
		return connector;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return method;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return context.getServletContext();
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return context.getServletContext().getRealPath(path);
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameterMap.keySet());
	}

	@Override
	public String getParameter(String name) {
		if (parameterMap.containsKey(name)) {
			String[] values = parameterMap.get(name);
			if (null != values && 0 != values.length)
				return values[0];
		}

		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		// TODO Auto-generated method stub
		return parameterMap;
	}

	@Override
	public String[] getParameterValues(String name) {
		// TODO Auto-generated method stub
		return parameterMap.get(name);
	}

	@Override
	public String getHeader(String key) {
		// TODO Auto-generated method stub
		return headerMap.get(key);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headerMap.keySet());
	}

	@Override
	public int getIntHeader(String key) {
		return Convert.toInt(headerMap.get(key), 0);
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return socket.getLocalAddress().getHostAddress();
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return socket.getLocalAddress().getHostName();
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return socket.getLocalPort();
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return "HTTP:/1.1";
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
		String temp = isa.getAddress().toString();
		return StrUtil.subAfter(temp, "/", false);
	}

	public String getRemoteHost() {
		InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
		return isa.getHostName();
	};

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return socket.getPort();
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return "http";
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return getHeader("host").trim();
	}

	@Override
	public String getContextPath() {
		String result = context.getPath();
		if ("/".equals(result))
			return "";
		return result;
	}

	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return uri;
	}

	@Override
	public StringBuffer getRequestURL() {
		StringBuffer url = new StringBuffer();
		String schme = getScheme();
		int port = getServerPort();
		if (port < 0)
			port = 80;
		url.append(schme);
		url.append("://");
		url.append(getServerName());
		if ((schme.equals("http") && (port != 80)) || (schme.equals("https") && (port != 443))) {
			url.append(":");
			url.append(port);
		}
		url.append(getRequestURI());
		return url;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return uri;
	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return cookies;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getJSessionIdFromCookie() {
		if (null == cookies)
			return null;
		for (Cookie cookie : cookies) {
			if ("JSESSIONID".equals(cookie.getName()))
				return cookie.getValue();
		}
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String uri) {
		return new ApplicationRequestDispatcher(uri);

	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return attributeMap.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return Collections.enumeration(attributeMap.keySet());
	}

	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		attributeMap.put(arg0, arg1);
	}
}
