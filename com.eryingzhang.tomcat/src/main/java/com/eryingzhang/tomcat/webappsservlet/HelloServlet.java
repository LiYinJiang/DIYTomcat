package com.eryingzhang.tomcat.webappsservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class HelloServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			resp.getWriter().println("Hello DIY Tomcat from erYingZhang");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
