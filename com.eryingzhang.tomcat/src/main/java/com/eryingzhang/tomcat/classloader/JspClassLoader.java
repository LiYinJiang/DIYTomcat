package com.eryingzhang.tomcat.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.eryingzhang.tomcat.catalina.Context;
import com.eryingzhang.tomcat.util.Constant;

import cn.hutool.core.util.StrUtil;

public class JspClassLoader extends URLClassLoader {
	private static Map<String, JspClassLoader> map = new HashMap<String, JspClassLoader>();

	public static void invalidJspClassLoader(String uri, Context context) {
		String key = context.getPath() + "/" + uri;
		map.remove(key);
	}

	public static JspClassLoader getJspClassLoader(String uri, Context context) {
		String key = context.getPath() + "/" + uri;
		JspClassLoader classLoader = map.get(key);
		if (null == classLoader) {
			classLoader = new JspClassLoader(context);
			map.put(key, classLoader);
		}
		return classLoader;
	}

	private JspClassLoader(Context context) {
		super(new URL[] {}, context.getWebappClassLoader());
		try {
			String subFolder;
			String path = context.getPath();
			if ("/".equals(path))
				subFolder = "_";
			else
				subFolder = StrUtil.subAfter(path, "/", false);
			File classesFolder = new File(Constant.FOLDER_WORK, subFolder);
			URL url = new URL("file:" + classesFolder + "/");
			this.addURL(url);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
