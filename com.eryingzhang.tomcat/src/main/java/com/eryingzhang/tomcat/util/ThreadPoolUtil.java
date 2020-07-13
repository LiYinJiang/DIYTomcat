package com.eryingzhang.tomcat.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

	TimeUnit unit;

	ThreadFactory threadFactory;

	private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 60, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(10));

	public static void exec(Runnable r) {
		threadPool.execute(r);
	}

}
