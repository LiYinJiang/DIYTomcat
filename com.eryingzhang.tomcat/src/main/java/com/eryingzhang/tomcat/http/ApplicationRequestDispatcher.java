package com.eryingzhang.tomcat.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.eryingzhang.tomcat.catalina.HttpProcessor;

public class ApplicationRequestDispatcher implements RequestDispatcher{
	
	private String uri;
	public ApplicationRequestDispatcher(String uri) {
		if(!uri.startsWith("/"))
			uri = "/" + uri;
		this.uri = uri;
	}
	
	public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		Request req = (Request) request;
		Response resp = (Response) response;
		req.setUri(uri);
		HttpProcessor processor = new HttpProcessor();
		processor.execute(req.getSocket(), req, resp);
		req.setForwarded(true);
		
	}

	public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
