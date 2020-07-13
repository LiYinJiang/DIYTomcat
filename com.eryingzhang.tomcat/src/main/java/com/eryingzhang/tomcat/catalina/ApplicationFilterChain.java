package com.eryingzhang.tomcat.catalina;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import cn.hutool.core.util.ArrayUtil;

public class ApplicationFilterChain implements FilterChain {
	Filter[] filters;
	Servlet servlet;
	int pos;

	public ApplicationFilterChain(List<Filter> filters, Servlet servlet) {
		this.filters = ArrayUtil.toArray(filters, Filter.class);
		this.servlet = servlet;
	}

	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

		if (pos < filters.length) {
			filters[pos++].doFilter(request, response, this);
		} else {
			servlet.service(request, response);
		}
	}

}
