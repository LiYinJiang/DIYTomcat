package com.eryingzhang.tomcat.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import cn.hutool.core.io.FileUtil;

public class WebappClassLoader extends URLClassLoader {

	public WebappClassLoader(String docBase, ClassLoader commonClassLoader) {
		// TODO Auto-generated constructor stub
		super(new URL[] {}, commonClassLoader);
		try {

			File workFolder = new File(docBase, "WEB-INF");
			File classesFolder = new File(workFolder, "classes");
			addURL(new URL("file:" + classesFolder.getAbsolutePath() + "/"));

			File libFolder = new File(workFolder, "lib");

			for (File lib : FileUtil.loopFiles(libFolder)) {
				if (lib.getName().endsWith(".jar")) {
					addURL(new URL("file:" + lib.getAbsolutePath()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
