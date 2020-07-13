package com.eryingzhang.tomcat.catalina;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eryingzhang.tomcat.util.Constant;
import com.eryingzhang.tomcat.util.ServerXmlUtil;
import com.eryingzhang.tomcat.watcher.WarFileChangeWatcher;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;

public class Host {
	private String name;
	private Engine engine;

	private Map<String, Context> contextMap = new HashMap<String, Context>();

	public Host(String name, Engine engine) {
		this.name = name;
		this.engine = engine;
		init();
		new WarFileChangeWatcher(this).start();
	}

	private void init() {
		scanContextOnWebAppsFolder();
		scanWarOnWebAppsFolder();
		scanContextOnServerXml();
	}

	private void scanContextOnServerXml() {
		List<Context> list = ServerXmlUtil.getContext(this);
		for (Context context : list) {
			contextMap.put(context.getPath(), context);
		}
	}

	private void scanContextOnWebAppsFolder() {
		File[] folders = Constant.FOLDER_WEBAPPS.listFiles();
		for (File file : folders) {
			if (!file.isDirectory())
				continue;
			loadContext(file);
		}
	}

	private void scanWarOnWebAppsFolder() {
		File[] folders = Constant.FOLDER_WEBAPPS.listFiles();
		for (File file : folders) {
			if (file.getName().toLowerCase().endsWith(".war"))
				loadWar(file);

		}

	}

	private void loadContext(File f) {
		String path = f.getName();
		if ("ROOT".equals(path))
			path = "/";
		else
			path = "/" + path;
		String docBase = f.getAbsolutePath();
		Context context = new Context(path, docBase, this, false);
		contextMap.put(context.getPath(), context);
	}

	public void reload(Context context) {
		LogFactory.get().info("Reloading Context with name [{}] has started", context.getPath());
		String path = context.getPath();
		String docBase = context.getDocBase();
		boolean reloadable = context.isReloadable();
		// stop
		context.stop();
		// remove
		contextMap.remove(path);
		// allocate new context
		Context newContext = new Context(path, docBase, this, reloadable);
		// assign it to map
		contextMap.put(newContext.getPath(), newContext);
		LogFactory.get().info("Reloading Context with name [{}] has completed", context.getPath());
	}

	public void load(File folder) {
		String path = folder.getName();
		if ("ROOT".equals(path))
			path = "/";
		else
			path = "/" + path;
		String docBase = folder.getAbsolutePath();
		Context context = new Context(path, docBase, this, false);
		contextMap.put(context.getPath(), context);
	}

	public void loadWar(File warFile) {
		String fileName = warFile.getName();
		String folderName = StrUtil.subBefore(fileName, ".", true);
		Context context = contextMap.get("/" + folderName);
		if (null != context)
			return;

		File folder = new File(Constant.FOLDER_WEBAPPS, folderName);
		if (folder.exists())
			return;

		File tempFile = FileUtil.file(Constant.FOLDER_WEBAPPS, folderName, fileName);
		File contextFolder = tempFile.getParentFile();
		contextFolder.mkdirs();

		FileUtil.copyFile(warFile, tempFile);

		String command = "jar xvf " + fileName;
		Process process = RuntimeUtil.exec(null, contextFolder, command);
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		tempFile.delete();

		load(contextFolder);
	}

	public String getName() {
		return name;
	}

	public Context getContext(String path) {
		return contextMap.get(path);
	}

}
