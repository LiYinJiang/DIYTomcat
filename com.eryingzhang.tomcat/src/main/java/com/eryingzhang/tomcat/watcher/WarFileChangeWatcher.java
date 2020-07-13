package com.eryingzhang.tomcat.watcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import com.eryingzhang.tomcat.catalina.Host;
import com.eryingzhang.tomcat.util.Constant;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;

public class WarFileChangeWatcher {
	private WatchMonitor monitor;

	public WarFileChangeWatcher(final Host host) {
		monitor = WatchUtil.createAll(Constant.FOLDER_WEBAPPS, 0, new Watcher() {
			private void dealWith(WatchEvent<?> event) {
				synchronized (event) {
					String fileName = event.context().toString();
					if (fileName.toLowerCase().endsWith(".war")
							&& event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {

						File warFolder = new File(Constant.FOLDER_WEBAPPS, fileName);
						host.loadWar(warFolder);
					}

				}
			}

			public void onCreate(WatchEvent<?> event, Path currentPath) {
				// TODO Auto-generated method stub
				dealWith(event);
			}

			public void onModify(WatchEvent<?> event, Path currentPath) {
				// TODO Auto-generated method stub
				dealWith(event);
			}

			public void onDelete(WatchEvent<?> event, Path currentPath) {
				// TODO Auto-generated method stub
				dealWith(event);
			}

			public void onOverflow(WatchEvent<?> event, Path currentPath) {
				// TODO Auto-generated method stub
				dealWith(event);
			}

		});
	}

	public void start() {
		monitor.start();
	}

	public void stop() {
		monitor.close();
	}
}
