package com.eryingzhang.tomcat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetCookieServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			Cookie cookie = new Cookie("name", "Gaki(cookie)");
			cookie.setMaxAge(60 * 24 * 60);
			cookie.setPath("/");
			resp.addCookie(cookie);
			resp.getWriter().println("set cookie seccessfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
 