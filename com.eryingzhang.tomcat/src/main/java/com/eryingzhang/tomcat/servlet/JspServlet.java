package com.eryingzhang.tomcat.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eryingzhang.tomcat.catalina.Context;
import com.eryingzhang.tomcat.classloader.JspClassLoader;
import com.eryingzhang.tomcat.http.Request;
import com.eryingzhang.tomcat.http.Response;
import com.eryingzhang.tomcat.util.Constant;
import com.eryingzhang.tomcat.util.JspUtil;
import com.eryingzhang.tomcat.util.WebXmlUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

public class JspServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static JspServlet g_instance = new JspServlet();

	public static synchronized JspServlet getInstance() {
		return g_instance;
	}

	private JspServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			Request request = (Request) req;
			Response response = (Response) resp;
			String uri = request.getRequestURI();

			if ("/".equals(uri))
				uri = WebXmlUtil.getWelcomeFile(request.getContext());

			String fileName = StrUtil.removePrefix(uri, "/");
			File file = new File(request.getRealPath(fileName));
			File jspFile = file;
			if (jspFile.exists()) {
				Context context = request.getContext();
				String path = context.getPath();
				String subFolder;
				if ("/".equals(path))
					subFolder = "_";
				else
					subFolder = StrUtil.subAfter(path, '/', false);

				String servletClassPath = JspUtil.getServletClassPath(uri, subFolder);
				File jspServletClassFile = new File(servletClassPath);

				if (!jspServletClassFile.exists()) {
					JspUtil.compileJsp(context, jspFile);
				} else if (jspFile.lastModified() > jspServletClassFile.lastModified()) {
					JspUtil.compileJsp(context, jspFile);
					JspClassLoader.invalidJspClassLoader(uri, context);
				}
				String extName = FileUtil.extName(file);
				String mimeType = WebXmlUtil.getMimeType(extName);
				response.setContentType(mimeType);

				JspClassLoader jspClassLoader = JspClassLoader.getJspClassLoader(uri, context);
				String jspServletClassName = JspUtil.getJspServletClassName(uri, subFolder);
				Class jspServletClass = jspClassLoader.loadClass(jspServletClassName);
				HttpServlet servlet = (HttpServlet) context.getServlet(jspServletClass);
				servlet.service(request, response);
				if (null != response.getRedirectPath())
					response.setStatus(Constant.CODE_302);
				else
					response.setStatus(Constant.CODE_200);
			} else {
				response.setStatus(Constant.CODE_404);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
