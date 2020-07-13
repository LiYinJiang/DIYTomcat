package com.eryingzhang.tomcat.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.hutool.core.io.FileUtil;

public class ContextXmlUtil {
	public static final Document DOC = Jsoup.parse(FileUtil.readUtf8String(Constant.FILE_CONTEXT_XML));

	public static String getWacthedResource() {
		try {

			return DOC.select("WatchedResource").first().text();
		} catch (Exception e) {
			e.printStackTrace();
			return "WEB-INF/web.xml";
		}

	}
}
