package com.eryingzhang.tomcat.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import com.eryingzhang.tomcat.util.Constant;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

public class Response extends BaseResponse {
	private StringWriter stringWriter;
	private PrintWriter writer;
	private String contentType;
	private byte[] body;
	private int status;
	List<Cookie> cookies;
	private String redirectPath;

	public Response() {
		this.stringWriter = new StringWriter();
		this.writer = new PrintWriter(stringWriter);
		this.contentType = "text/html";
		this.cookies = new ArrayList<Cookie>();
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getBody() throws UnsupportedEncodingException {
		if (body == null) {
			String content = stringWriter.toString();
			body = content.getBytes(Constant.ENCODE_UTF_8);
		}
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return writer;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub
		cookies.add(cookie);
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public String getCookieHeader() {
		if (null == cookies)
			return "";
		String pattern = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
		StringBuffer sb = new StringBuffer();
		for (Cookie cookie : getCookies()) {
			sb.append("\r\n");
			sb.append("Set-Cookie: ");
			System.out.println(cookie.getName() + "=" + cookie.getValue() + "; ");
			sb.append(cookie.getName() + "=" + cookie.getValue() + "; ");
			if (-1 != cookie.getMaxAge()) { // -1 mean forever
				sb.append("Expires=");
				Date now = new Date();
				Date expire = DateUtil.offset(now, DateField.MINUTE, cookie.getMaxAge());
				sb.append(sdf.format(expire));
				sb.append("; ");
			}
			if (null != cookie.getPath())
				sb.append("Path=" + cookie.getPath());
		}
		return sb.toString();
	}

	public String getRedirectPath() {
		return redirectPath;
	}

	@Override
	public void sendRedirect(String redirectPath) throws IOException {
		// TODO Auto-generated method stub
		this.redirectPath = redirectPath;
	}
}
