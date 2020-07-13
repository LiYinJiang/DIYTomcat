package com.eryingzhang.tomcat.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eryingzhang.tomcat.util.Constant;
import com.eryingzhang.tomcat.util.MiniBrowser;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;

public class TestTomcat {

	private static int port = 8053;
	private static String ip = "127.0.0.1";

	@BeforeClass
	public static void beforeClass() {
		// 所有测试开始前看diy tomcat 是否已经启动了
		if (NetUtil.isUsableLocalPort(port)) {
			System.err.println("请先启动 位于端口: " + port + " 的diy tomcat，否则无法进行单元测试");
			System.exit(1);
		} else {
			System.out.println("检测到 diy tomcat已经启动，开始进行单元测试");
		}
	}

	@Test
	public void testHelloTomcat() {
		String html = getContent("/");
		System.out.println("html: " + html);

		Assert.assertEquals(html, "Hello DIY Tomcat from erYingZhang");
	}

	@Test
	public void testIndexTxt() {
		String content = getContent("/a/index.txt");
		System.out.println("html: " + content);
		Assert.assertEquals(content, "Hello DIY Tomcat from index.txt");
	}

	@Test
	public void testTimeConsumeHtml() throws InterruptedException {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
				new LinkedBlockingDeque<Runnable>());
		TimeInterval timeInterval = DateUtil.timer();
		for (int i = 0; i < 3; i++) {
			threadPoolExecutor.execute(new Runnable() {

				public void run() {
					getContent("/timeConsume.html");

				}
			});
		}
		threadPoolExecutor.shutdown();
		threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
		long duration = timeInterval.intervalMs();
		Assert.assertTrue(duration < 3000);
	}

	@Test
	public void testIndexB() {
		String html = getContent("/b/index.html");
		Assert.assertEquals(html, "Hello DIY Tomcat from index.html@b");
	}

	@Test
	public void test404() {

		String httpString = getHttpString("/not_exist.html");
		assertContain(httpString, Constant.RESPONSE_HEAD_404);
	}

	@Test
	public void test500() {

		String httpString = getHttpString("/500.html");
		assertContain(httpString, Constant.RESPONSE_HEAD_500);
	}

	@Test
	public void testPNG() {
		byte[] bytes = getContentBytes("/logo.png");
		int pngFileLength = 1672;
		System.out.println("PNG length:" + bytes.length);
		Assert.assertEquals(pngFileLength, bytes.length);
	}

	@Test
	public void testPDF() {
		String uri = "/etf.pdf";
		byte[] bytes = getContentBytes(uri);
		int pdfFileLength = 3590775;
		System.out.println("PDF length:" + bytes.length);
		Assert.assertEquals(pdfFileLength, bytes.length);
	}

	@Test
	public void testhello() {
		String html = getContent("/j2ee/hello");
		Assert.assertEquals(html, "Hello DIY Tomcat from erYingZhang");
	}

	@Test
	public void testJavaWeb() {
		String html1 = getContent("/javaweb/hello");
		String html2 = getContent("/javaweb/hello");

		System.out.println("javaWeb: " + html1);
		Assert.assertEquals(html1, html2);
	}

	@Test
	public void testPostParam() {
		String uri = "/javaweb/param";
		String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Gaki");
		String content = MiniBrowser.getContentString(url, params, false);
		Assert.assertEquals(content, "doPost value: Gaki");
	}

	@Test
	public void testGetParam() {
		String uri = "/javaweb/param";
		String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Gaki");
		String content = MiniBrowser.getContentString(url, params, true);

		Assert.assertEquals(content, "doGet value: Gaki");
	}

	@Test
	public void testHeader() {

		String content = getContent("/javaweb/header");

		System.out.println("\r\nheader: " + content);
		Assert.assertEquals(content, "eryingzhang mini brower / java1.8");
	}

	@Test
	public void testCookie() {

		String content = getHttpString("/javaweb/setCookie");

		System.out.println("\r\ncookie: " + content);
		assertContain(content, "Set-Cookie: name=Gaki(cookie); Expires=");
	}

	@Test
	public void testGetCookie() throws IOException {

		String url = StrUtil.format("http://{}:{}{}", ip, port, "/javaweb/getCookie");
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestProperty("Cookie", "name=Gaki(cookie)");
		conn.connect();
		InputStream is = conn.getInputStream();
		String html = IoUtil.read(is, "utf-8");
		assertContain(html, "name:Gaki(cookie)");
	}

	@Test
	public void testSession() throws IOException {
		String jsessionid = getContent("/javaweb/setSession");
		if (null != jsessionid)
			jsessionid = jsessionid.trim();
		String url = StrUtil.format("http://{}:{}{}", ip, port, "/javaweb/getSession");
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestProperty("Cookie", "JSESSIONID=" + jsessionid);
		conn.connect();
		InputStream is = conn.getInputStream();
		String html = IoUtil.read(is, "utf-8");
		assertContain(html, "Gaki(session)");
	}

	@Test
	public void testGzip() {
		byte[] gzipContent = getContentBytes("/", true);
		byte[] unGzipContent = ZipUtil.unGzip(gzipContent);
		String html = new String(unGzipContent);
		Assert.assertEquals(html, "Hello DIY Tomcat from erYingZhang");

	}

	@Test
	public void testJSP() {
		String html = getContent("/javaweb/");

		Assert.assertEquals(html, "eryingzhang hello jsp@javaweb");

	}

	@Test
	public void testClientJump() {
		String html = getHttpString("/javaweb/jump");
		assertContain(html, "HTTP/1.1 302 Found");
		String html1 = getHttpString("/javaweb/jump.jsp");
		assertContain(html1, "HTTP/1.1 302 Found");
	}
	
	
	@Test
	public void testServerJumpWithAttribute() {
		String html = getContent("/javaweb/serverJump");
		Assert.assertEquals(html, "Hello DIY Tomcat from HelloServlet@javaweb, the name is Gaki");
	}
	
	@Test
	public void testWar() {
		String html = getContent("/javaweb0/serverJump");
		Assert.assertEquals(html, "Hello DIY Tomcat from HelloServlet@javaweb, the name is Gaki");
	}
	
	@Test
	public void testWarWatcher() {
		String html = getContent("/javaweb5/serverJump");
		Assert.assertEquals(html, "Hello DIY Tomcat from HelloServlet@javaweb, the name is Gaki");
	}
	
	
	public String getContent(String uri) {
		String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
		String content = MiniBrowser.getContentString(url);
		return content;
	}

	public String getContent(String uri, boolean gZip) {
		String url = // ku"http://static.how2j.cn/diytomcat.html";
				StrUtil.format("http://{}:{}{}", ip, port, uri);
		String content = MiniBrowser.getContentString(url, gZip);
		return content;
	}

	public byte[] getContentBytes(String uri) {
		return getContentBytes(uri, false);
	}

	public byte[] getContentBytes(String uri, boolean gzip) {
		String url = // ku"http://static.how2j.cn/diytomcat.html";
				StrUtil.format("http://{}:{}{}", ip, port, uri);
		return MiniBrowser.getContentBytes(url, gzip);
	}

	private String getHttpString(String uri) {
		String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
		String http = MiniBrowser.getHttpString(url);
		return http;
	}

	private void assertContain(String httpString, String string) {
		boolean match = StrUtil.containsAny(httpString, string);
		Assert.assertTrue(match);

	}

	public String Hello() {
		return "hello static method";
	}
}
