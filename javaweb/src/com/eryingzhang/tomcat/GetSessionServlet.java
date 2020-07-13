package com.eryingzhang.tomcat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetSessionServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType("text/html;charset=UTF-8");
			String name_in_session = (String) req.getSession().getAttribute("name_in_session");
			resp.getWriter().println(name_in_session);
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
}
