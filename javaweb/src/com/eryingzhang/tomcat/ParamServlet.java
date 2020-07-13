package com.eryingzhang.tomcat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParamServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {

			String value = req.getParameter("name");
			resp.getWriter().println("doGet value: " + value);
			 
		} catch (Exception e) {
			// TODO: handle exception
		}
 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {

			String value = req.getParameter("name");
			resp.getWriter().println("doPost value: " + value);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
