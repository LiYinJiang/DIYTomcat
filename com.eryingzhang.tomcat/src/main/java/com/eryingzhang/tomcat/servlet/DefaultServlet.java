package com.eryingzhang.tomcat.servlet;

import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.bcel.Const;

import com.eryingzhang.tomcat.catalina.Context;
import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;
import com.eryingzhang.tomcat.util.Constant;
import com.eryingzhang.tomcat.util.WebXmlUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

public class DefaultServlet extends HttpServlet {

	private static DefaultServlet instance = new DefaultServlet();

	public static synchronized DefaultServlet getInstance() {
		return instance;
	}

	private DefaultServlet() {

	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Request req = (Request) request;
		Response resp = (Response) response;

		String uri = req.getUri();

		if ("/500.html".equals(uri))
			throw new RuntimeException("this is a deliberately created exception");

		if ("/".equals(uri))
			uri = WebXmlUtil.getWelcomeFile(req.getContext());
		else if (uri.endsWith(".jsp")) {
			JspServlet.getInstance().service(req, resp);
			return;
		}

		String fileName = StrUtil.removePrefix(uri, "/");
		File file = FileUtil.file(req.getRealPath(fileName));

		if (file.exists()) {
			String extName = file.getName();
			String mineType = WebXmlUtil.getMimeType(extName);

			byte[] body = FileUtil.readBytes(file);
			resp.setBody(body);
			resp.setContentType(mineType);
			try {
				if (file.getName().equals("timeConsume.html"))
					Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			resp.setStatus(Constant.CODE_200);

		} else {

			resp.setStatus(Constant.CODE_404);
		}

	}
}
