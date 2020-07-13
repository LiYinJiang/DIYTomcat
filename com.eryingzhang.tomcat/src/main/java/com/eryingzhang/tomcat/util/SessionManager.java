package com.eryingzhang.tomcat.util;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;
import com.eryingzhang.tomcat.http.StandardSession;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;

public class SessionManager {
	private static Map<String, StandardSession> sessionMap = new HashMap<String, StandardSession>();
	private static int defaultTimeout = getTimeOut();
	
	static {
		startSessionOutdateCheckThread();
	}
	
	public static HttpSession getSession(String jSessionId, Request request, Response response) {
		if (null == jSessionId || !sessionMap.containsKey(jSessionId))
			return newSession(request, response);
		else {
			StandardSession currentSession = sessionMap.get(jSessionId);
			currentSession.setLastAccessedTime(System.currentTimeMillis());
			createCookieSession(currentSession, request, response);
			return currentSession;
		}
	}

	private static void createCookieSession(HttpSession session, Request request, Response response) {
		String name;
		Cookie cookie = new Cookie("JSESSIONID", session.getId());
		cookie.setMaxAge(session.getMaxInactiveInterval());
		cookie.setPath(request.getContext().getPath());
		response.addCookie(cookie);
	}

	private static HttpSession newSession(Request request, Response response) {
		ServletContext servletContext = request.getServletContext();
		String sid = generateSessionId();
		StandardSession session = new StandardSession(sid, servletContext);
		session.setMaxInactiveInterval(defaultTimeout);
		sessionMap.put(session.getId(), session);
		createCookieSession(session, request, response);
		return session;
	}

	private static int getTimeOut() {
		int defaultResult = 30;
		try {
			String html;
			Document d = Jsoup.parse(Constant.FILE_CONF_WEB_XML, "UTF-8");
			Element element = d.select("session-config session-timeout").first();
			if (element != null)
				return Convert.toInt(element.text());
			return defaultResult;
		} catch (IOException e) {
			return defaultResult;
		}

	}

	private static void checkOutDateSession() {
		Set<String> jsessionids = sessionMap.keySet();
		List<String> outdateSessionIds = new ArrayList<String>();
		for (String string : jsessionids) {
			StandardSession session = sessionMap.get(string);
			long interval = System.currentTimeMillis();
			session.getLastAccessedTime();
			if (interval > session.getMaxInactiveInterval() * 1000)
				outdateSessionIds.add(string);
		}
		for (String sessionId : outdateSessionIds) {
			sessionMap.remove(sessionId);
		}
	}

	private static void startSessionOutdateCheckThread() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					checkOutDateSession();
					ThreadUtil.sleep(1000 * 30);
				}
			}
		}.start();
	}

	public static synchronized String generateSessionId() {
		String result = null;
		byte[] bytes = RandomUtil.randomBytes(16);
		result = new String(bytes);
		result = SecureUtil.md5(result);
		result = result.toUpperCase();
		return result;
	}
}
