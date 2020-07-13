package com.eryingzhang.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PfmFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("======PfmFilter===destroy============");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		long start = System.currentTimeMillis();
		chain.doFilter(request, response);
		long end = System.currentTimeMillis();
		System.out.println((end - start) + "  ms elapsed on url:" + url);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Enumeration<String> enumeration = filterConfig.getInitParameterNames();
		System.out.println("======PfmFilter===init============");
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			String value = filterConfig.getInitParameter(name);
			System.out.println("name:" + name + "   value:" + value);

		}

	}

}
