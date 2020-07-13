package com.eryingzhang.tomcat.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eryingzhang.tomcat.catalina.Context;

import cn.hutool.core.io.FileUtil;

public class WebXmlUtil {
	private static final Document DOC = Jsoup.parse(FileUtil.readUtf8String(Constant.FILE_CONF_WEB_XML));
	private static Map<String, String> mimeTypeMap = new HashMap<String, String>();

	public static String getWelcomeFile(Context context) {
		Elements e = DOC.select("welcome-file");

		for (Element element : e) {
			String welcomeFileName = element.text();

			File file = new File(context.getDocBase(), welcomeFileName);
			if (file.exists())
				return file.getName();
		}
		return "index.html";
	}

	public synchronized static void initMimeType() {
		Elements e = DOC.select("mime-mapping");
		for (Element element : e) {
			String extension = element.select("extension").first().text();
			String mimeType = element.select("mime-type").first().text();
			mimeTypeMap.put(extension, mimeType);
		}
	}

	public static String getMimeType(String extension) {

		if (mimeTypeMap.isEmpty())
			initMimeType();
		if (mimeTypeMap.containsKey(extension))
			return mimeTypeMap.get(extension);
		else
			return "text/html";
	}


}
