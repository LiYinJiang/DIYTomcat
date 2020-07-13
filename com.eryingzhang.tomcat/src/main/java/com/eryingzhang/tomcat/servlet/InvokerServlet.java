package com.eryingzhang.tomcat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eryingzhang.tomcat.catalina.Context;
import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;
import com.eryingzhang.tomcat.util.Constant;

import cn.hutool.core.util.ReflectUtil;

public class InvokerServlet extends HttpServlet {

	private static InvokerServlet g_instance = new InvokerServlet();

	public static synchronized InvokerServlet getInstance() {
		return g_instance;
	}

	private InvokerServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Request req = (Request) request;
		Response resp = (Response) response;
		String uri = req.getUri();
		Context context = req.getContext();
		String servletName = context.getServletClassName(uri);

		try {
			Class<?> servletClass = context.getWebappClassLoader().loadClass(servletName);
			System.out.println("servletClass:" + servletClass);
			System.out.println("servletClass'classLoader:" + servletClass.getClassLoader());
			Object servlet = context.getServlet(servletClass);
			ReflectUtil.invoke(servlet, "service", req, resp);
			if (null != resp.getRedirectPath())
				response.setStatus(Constant.CODE_302);
			else
				resp.setStatus(Constant.CODE_200);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
