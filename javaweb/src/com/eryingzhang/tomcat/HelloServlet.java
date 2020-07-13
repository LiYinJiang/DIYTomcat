package com.eryingzhang.tomcat;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

	public HelloServlet() {
		System.out.println("=========construction :" + this);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		String auth = config.getInitParameter("author");
		String site = config.getInitParameter("site");
		System.out.println("=========author:" + auth);
		System.out.println("=========site:" + site);
		System.out.println("=========init :" + this);
	}
 
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
			// System.out.println(clazz);
			// System.out.println(clazz.getClassLoader());
			System.out.println("=========doGet: " + this);
			resp.getWriter().println("Hello DIY Tomcat from eryingzhang HelloServlet@javaweb " + this);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		System.out.println("=========destory: " + this);
	}
	
}
