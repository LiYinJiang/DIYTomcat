package com.eryingzhang.tomcat.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

import com.eryingzhang.tomcat.catalina.Context;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.LogFactory;

public class ContextFileChangeWatcher {
	private WatchMonitor monitor;

	private boolean stop = false;

	public ContextFileChangeWatcher(final Context context) {
		this.monitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {

			private void dealWith(WatchEvent<?> event) {
				synchronized (ContextFileChangeWatcher.class) {
					String fileName = event.context().toString();
					if (stop)
						return;
					if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".xml")) {
						stop = true;
						LogFactory.get().info(ContextFileChangeWatcher.this + " 检测到了Web应用下的重要文件变化 {} ", fileName);
						context.reload();
					}
				}
			}

			public void onOverflow(WatchEvent<?> arg0, Path arg1) {
				// TODO Auto-generated method stub
				dealWith(arg0);
			}

			public void onModify(WatchEvent<?> arg0, Path arg1) {
				// TODO Auto-generated method stub
				dealWith(arg0);
			}

			public void onDelete(WatchEvent<?> arg0, Path arg1) {
				// TODO Auto-generated method stub
				dealWith(arg0);
			}

			public void onCreate(WatchEvent<?> arg0, Path arg1) {
				// TODO Auto-generated method stub
				dealWith(arg0);
			}
		});
		this.monitor.setDaemon(true);
	}

	public void start() {
		monitor.start();

	}

	public void stop() {
		monitor.close();
	}
}
