package com.eryingzhang.tomcat.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CommonClassLoader extends URLClassLoader {

	public CommonClassLoader() {
		super(new URL[] {});
		try {
			File workingFolder = new File(System.getProperty("user.dir"));

			// File LibFolder = new File(workingFolder, "lib");
			File LibFolder = new File(workingFolder, "src/main/resources/lib");
			System.out.println("lib Folder:" + LibFolder.getAbsolutePath());
			for (File file : LibFolder.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					URL url = new URL("file:" + file.getAbsolutePath());
					this.addURL(url);
					System.out.println("addUrl: " + url);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
