package com.eryingzhang.tomcat.util;

import java.io.File;

import cn.hutool.system.SystemUtil;

public class Constant {

	public static final int CODE_200 = 200;
	public static final int CODE_302 = 302;
	public static final int CODE_404 = 404;
	public static final int CODE_500 = 500;

	public static final String ENCODE_UTF_8 = "UTF-8";
	public static final String RESPONSE_HEAD_200 = "HTTP/1.1 200 OK\r\n" + "Content-Type: {}{}\r\n\r\n";
	public static final String RESPONSE_HEAD_200_GZIP = "HTTP/1.1 200 OK\r\n" + "Content-Type: {}{}"
			+ "Content-Encoding:gzip" + "\r\n\r\n";
	public static final String RESPONSE_HEAD_302 = "HTTP/1.1 302 Found\r\nLocation: {}\r\n\r\n";
	public static final String RESPONSE_HEAD_404 = "HTTP/1.1 404 Not Found\r\n" + "Content-Type:text/html\r\n\r\n";
	public static final String TEXT_FORMAT_404 = "<html><head><title>DIY Tomcat/0.0.1 - Error report</title><style>"
			+ "<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} "
			+ "H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} "
			+ "H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} "
			+ "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} "
			+ "B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} "
			+ "P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}"
			+ "A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> "
			+ "</head><body><h1>HTTP Status 404 - {}</h1>"
			+ "<HR size='1' noshade='noshade'><p><b>type</b> Status report</p><p><b>message</b> <u>{}</u></p><p><b>description</b> "
			+ "<u>The requested resource is not available.</u></p><HR size='1' noshade='noshade'><h3>ErYingZhang DiyTocmat 0.0.1</h3>"
			+ "</body></html>";

	public static final String RESPONSE_HEAD_500 = "HTTP/1.1 500 Internal Server Error\r\n"
			+ "Content-Type: text/html\r\n\r\n";

	public static final String TEXT_FORMAT_500 = "<html><head><title>DIY Tomcat/0.0.1 - Error report</title><style>"
			+ "<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} "
			+ "H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} "
			+ "H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} "
			+ "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} "
			+ "B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} "
			+ "P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}"
			+ "A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> "
			+ "</head><body><h1>HTTP Status 500 - An exception occurred processing {}</h1>"
			+ "<HR size='1' noshade='noshade'><p><b>type</b> Exception report</p><p><b>message</b> <u>An exception occurred processing {}</u></p><p><b>description</b> "
			+ "<u>The server encountered an internal error that prevented it from fulfilling this request.</u></p>"
			+ "<p>Stacktrace:</p>" + "<pre>{}</pre>"
			+ "<HR size='1' noshade='noshade'><h3>ErYingZhang DiyTocmat 0.0.1</h3>" + "</body></html>";
	public static final File RESOURCE_DIR = new File(SystemUtil.get("user.dir"), "src/main/resources");

	public static final File FOLDER_WORK = new File(RESOURCE_DIR, "work");
	public static final File FOLDER_WEBAPPS = new File(RESOURCE_DIR, "webapps");
	public static final File FOLDER_WEBAPPS_ROOT = new File(FOLDER_WEBAPPS, "ROOT");

	public static final File FOLDER_CONF = new File(RESOURCE_DIR, "conf");
	public static final File FILE_CONF_SERVER_XML = new File(FOLDER_CONF, "server.xml");

	public static final File FILE_CONF_WEB_XML = new File(FOLDER_CONF, "web.xml");

	public static final File FILE_CONTEXT_XML = new File(FOLDER_CONF, "context.xml");
}
