package com.eryingzhang.tomcat.catalina;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.NotYetBoundException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;
import com.eryingzhang.tomcat.servlet.DefaultServlet;
import com.eryingzhang.tomcat.servlet.InvokerServlet;
import com.eryingzhang.tomcat.servlet.JspServlet;
import com.eryingzhang.tomcat.util.Constant;
import com.eryingzhang.tomcat.util.SessionManager;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.log.LogFactory;

public class HttpProcessor {

	HttpServlet workingServlet;

	public void execute(Socket s, Request request, Response response) {
		try {

			String uri = request.getUri();
			if (null == uri)
				return;

			Context context = request.getContext();
			parseSession(request, response);
			String servletClassName = context.getServletClassName(uri);
			if (null != servletClassName) {
				workingServlet = InvokerServlet.getInstance();
			} else if (uri.endsWith(".jsp")) {
				workingServlet = JspServlet.getInstance();
			} else {
				workingServlet = DefaultServlet.getInstance();
			}
			List<Filter> filters = request.getContext().getMatchedFilters(uri);
			ApplicationFilterChain chain = new ApplicationFilterChain(filters, workingServlet);
			chain.doFilter(request, response);
			if (request.isForwarded())
				return;

			if (response.getStatus() == Constant.CODE_200) {
				handle200(s, request, response);
			} else if (response.getStatus() == Constant.CODE_302) {
				handle302(s, response);

			} else if (response.getStatus() == Constant.CODE_404) {
				handle404(s, uri);
			}
		} catch (

		Exception e) {
			LogFactory.get().error(e);
			handle500(s, e);
		} finally {
			try {
				if (!s.isClosed())
					s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected static void handle200(Socket s, Request request, Response response) throws IOException {
		String contentType = response.getContentType();
		String cookieHeader = response.getCookieHeader();
		String headText = null;

		byte[] body = response.getBody();

		boolean gzip = isGzip(request, body, contentType);
		if (gzip)
			headText = Constant.RESPONSE_HEAD_200_GZIP;
		else
			headText = Constant.RESPONSE_HEAD_200;
		headText = StrUtil.format(headText, contentType, cookieHeader);

		if (gzip)
			body = ZipUtil.gzip(body);

		byte[] head = headText.getBytes();
		byte[] responseBytes = new byte[head.length + body.length];
		ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
		ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

		OutputStream os = s.getOutputStream();
		os.write(responseBytes);
		os.flush();
		os.close();
	}

	protected void handle404(Socket s, String uri) throws IOException {
		OutputStream os = s.getOutputStream();
		String responseText = StrUtil.format(Constant.TEXT_FORMAT_404, uri, uri);
		responseText = Constant.RESPONSE_HEAD_404 + responseText;
		byte[] responseByte = responseText.getBytes("utf-8");
		os.write(responseByte);
	}

	protected void handle500(Socket s, Exception e) {
		try {
			OutputStream os = s.getOutputStream();
			StackTraceElement stes[] = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append(e.toString());
			sb.append("\r\n");
			for (StackTraceElement ste : stes) {
				sb.append("\t");
				sb.append(ste.toString());
				sb.append("\r\n");
			}

			String msg = e.getMessage();

			if (null != msg && msg.length() > 20)
				msg = msg.substring(0, 19);

			String text = StrUtil.format(Constant.TEXT_FORMAT_500, msg, e.toString(), sb.toString());
			text = Constant.RESPONSE_HEAD_500 + text;
			byte[] responseBytes = text.getBytes("utf-8");
			os.write(responseBytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	protected void handle302(Socket s, Response response) throws IOException {
		OutputStream os = s.getOutputStream();
		String redirectPath = response.getRedirectPath();
		String header = String.format(Constant.RESPONSE_HEAD_302, redirectPath);
		byte[] head = header.getBytes(Constant.ENCODE_UTF_8);
		os.write(head);
	}

	public void parseSession(Request request, Response response) {
		String jsessionId = request.getJSessionIdFromCookie();
		HttpSession session = SessionManager.getSession(jsessionId, request, response);
		request.setSession(session);

	}

	private static boolean isGzip(Request request, byte[] body, String mimeType) {
		String acceptEncoding = request.getHeader("accept-encoding");
		if (!StrUtil.containsAny(acceptEncoding, "gzip"))
			return false;
		Connector connector = request.getConnector();
		if (mimeType.contains(";"))
			mimeType = StrUtil.subBefore(mimeType, ";", false);

		if (!"on".equals(connector.getCompression()))
			return false;

		if (body.length < connector.getCommpressionMinSize())
			return false;

		String userAgents = connector.getNoCompressionUserAgents();
		String[] eachUserAgents = StrUtil.split(userAgents, ",");
		for (String eachUserAgent : eachUserAgents) {
			eachUserAgent = eachUserAgent.trim();
			String userAgent = request.getHeader("User-Agent");
			if (StrUtil.containsAny(userAgent, eachUserAgent))
				return false;
		}

		String mimeTypes = connector.getCompressableMimeType();
		String[] eachMimeTypes = mimeTypes.split(",");
		for (String eachMimeType : eachMimeTypes) {
			if (mimeType.equals(eachMimeType))
				return true;
		}
		return false;
	}
}
