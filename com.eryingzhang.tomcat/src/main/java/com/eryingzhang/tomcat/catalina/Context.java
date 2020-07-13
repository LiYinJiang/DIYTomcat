package com.eryingzhang.tomcat.catalina;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.jasper.JspC;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eryingzhang.tomcat.classloader.WebappClassLoader;
import com.eryingzhang.tomcat.exception.WebConfigDuplicatedException;
import com.eryingzhang.tomcat.http.ApplicationContext;
import com.eryingzhang.tomcat.http.StandardServletConfig;
import com.eryingzhang.tomcat.util.ContextXmlUtil;
import com.eryingzhang.tomcat.watcher.ContextFileChangeWatcher;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;

public class Context {

	public enum enumCycle {
		init, destory
	}

	private String docBase;
	private String path;

	private File contextWebXmlFile;
	private Map<String, String> url_servletClassName;
	private Map<String, String> url_servletName;
	private Map<String, String> servletName_className;
	private Map<String, String> className_servletName;

	private Map<String, List<String>> url_filterClassName;
	private Map<String, List<String>> url_filterNames;
	private Map<String, String> filterName_className;
	private Map<String, String> className_filterName;

	private WebappClassLoader webappClassLoader;

	private Host host;
	private boolean reloadable;
	private ContextFileChangeWatcher watcher;

	private ServletContext servletContext;

	private Map<Class<?>, HttpServlet> servletPool;
	private Map<Class<?>, Filter> filterPool;

	private Map<String, Map<String, String>> servletInitParam;
	private Map<String, Map<String, String>> filterInitParam;

	private List<String> loadOnStartupServletClassNames;

	private List<ServletContextListener> listeners;

	public Context(String path, String docBase, Host host, boolean reloadable) {

		this.path = path;
		this.docBase = docBase;
		this.host = host;
		this.reloadable = reloadable;
		this.contextWebXmlFile = new File(docBase, ContextXmlUtil.getWacthedResource());
		this.url_servletClassName = new HashMap<String, String>();
		this.url_servletName = new HashMap<String, String>();
		this.servletName_className = new HashMap<String, String>();
		this.className_servletName = new HashMap<String, String>();
		servletInitParam = new HashMap<String, Map<String, String>>();

		this.url_filterClassName = new HashMap<String, List<String>>();
		this.url_filterNames = new HashMap<String, List<String>>();
		this.filterName_className = new HashMap<String, String>();
		this.className_filterName = new HashMap<String, String>();
		filterInitParam = new HashMap<String, Map<String, String>>();

		webappClassLoader = new WebappClassLoader(docBase, Thread.currentThread().getContextClassLoader());

		servletContext = new ApplicationContext(this);
		servletPool = new HashMap<Class<?>, HttpServlet>();
		filterPool = new HashMap<Class<?>, Filter>();
		loadOnStartupServletClassNames = new ArrayList<String>();
		listeners = new ArrayList<ServletContextListener>();
		deploy();

	}

	private void deploy() {
		TimeInterval interval = DateUtil.timer();
		init();
		if (reloadable) {
			watcher = new ContextFileChangeWatcher(this);
			watcher.start();
		}
		LogFactory.get().info("Deploying web application directory {}", this.docBase);
		LogFactory.get().info("Deplyment of web applicton directory {} has finished in {} ms", this.docBase,
				interval.intervalMs());
		// 在jsp所转换的 java 文件里的 javax.servlet.jsp.JspFactory.getDefaultFactory() 能够有返回值
		JspC c = new JspC();
		new JspRuntimeContext(servletContext, c);

	}

	public void init() {
		if (!contextWebXmlFile.exists())
			return;
		Document doc = Jsoup.parse(FileUtil.readUtf8String(contextWebXmlFile));
		try {
			checkDuplicated(doc);
		} catch (WebConfigDuplicatedException e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
		loadListener(doc);
		fireEvent(enumCycle.init);
		parseServletMapping(doc);
		parseFilterMapping(doc);

		parseServletInitParam(doc);
		parseFilterInitParam(doc);
		initFilter();

		parseServletAutoload(doc);
		handleServletAutoLoad();
	}

	private void loadListener(Document d) {
		Elements mappingUrlElements = d.select("listener listener-class");
		for (Element mappingUrlElement : mappingUrlElements) {
			try {
				String className = mappingUrlElement.text();
				Class<?> clazz = webappClassLoader.loadClass(className);
				ServletContextListener listener = (ServletContextListener) ReflectUtil.newInstance(clazz);
				addListener(listener);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

	}

	private void parseServletMapping(Document d) {
		Elements mappingUrlElements = d.select("servlet-mapping url-pattern");
		for (Element mappingUrlElement : mappingUrlElements) {
			String urlPattern = mappingUrlElement.text();
			String servletName = mappingUrlElement.parent().select("servlet-name").first().text();
			url_servletName.put(urlPattern, servletName);
		}

		// servletName_className / className_servletName
		Elements servletNameElements = d.select("servlet servlet-name");
		for (Element servletNameElement : servletNameElements) {
			String servletName = servletNameElement.text();
			String servletClass = servletNameElement.parent().select("servlet-class").first().text();
			servletName_className.put(servletName, servletClass);
			className_servletName.put(servletClass, servletName);
		}

		Set<String> urls = url_servletName.keySet();
		for (String url : urls) {
			String servletName = url_servletName.get(url);
			String servletClassName = servletName_className.get(servletName);
			url_servletClassName.put(url, servletClassName);
		}
	}

	private void parseFilterMapping(Document d) {
		Elements mappingUrlElements = d.select("filter-mapping url-pattern");
		for (Element mappingUrlElement : mappingUrlElements) {
			String urlPattern = mappingUrlElement.text();
			String filterName = mappingUrlElement.parent().select("filter-name").first().text();
			if (!url_filterNames.containsKey(urlPattern))
				url_filterNames.put(urlPattern, new ArrayList<String>());
			if (!url_filterNames.get(urlPattern).contains(filterName))
				url_filterNames.get(urlPattern).add(filterName);
		}

		// servletName_className / className_servletName
		Elements servletNameElements = d.select("filter filter-name");
		for (Element servletNameElement : servletNameElements) {
			String servletName = servletNameElement.text();
			String servletClass = servletNameElement.parent().select("filter-class").first().text();
			filterName_className.put(servletName, servletClass);
			className_filterName.put(servletClass, servletName);
		}

		Set<String> urls = url_filterNames.keySet();
		for (String url : urls) {
			List<String> filterNames = url_filterNames.get(url);
			// filterName_className
			for (String filterName : filterNames) {

				String filterClassName = filterName_className.get(filterName);
				if (!url_filterClassName.containsKey(url))
					url_filterClassName.put(url, new ArrayList<String>());
				if (!url_filterClassName.get(url).contains(filterClassName))
					url_filterClassName.get(url).add(filterClassName);

			}

		}
	}

	private void parseServletInitParam(Document d) {
		Elements servletNameElements = d.select("servlet servlet-name");
		for (Element servletNameElement : servletNameElements) {
			String servletName = servletNameElement.text();
			if (!servletInitParam.containsKey(servletName))
				servletInitParam.put(servletName, new HashMap<String, String>());
			Elements params = servletNameElement.parent().select("init-param");
			if (params.isEmpty())
				continue;
			for (Element element : params) {
				servletInitParam.get(servletName).put(element.select("param-name").first().text(),
						element.select("param-value").first().text());
			}

		}
	}

	private void parseFilterInitParam(Document d) {
		Elements filterNameElements = d.select("filter filter-name");
		for (Element filterNameElement : filterNameElements) {
			String filterName = filterNameElement.text();
			if (!filterInitParam.containsKey(filterName))
				filterInitParam.put(filterName, new HashMap<String, String>());
			Elements params = filterNameElement.parent().select("init-param");
			if (params.isEmpty())
				continue;
			for (Element element : params) {
				filterInitParam.get(filterName).put(element.select("param-name").first().text(),
						element.select("param-value").first().text());
			}

		}
	}

	private void initFilter() {
		Set<String> classNames = className_filterName.keySet();
		for (String className : classNames) {
			try {
				Class<?> clazz = this.getWebappClassLoader().loadClass(className);
				Map<String, String> initParameters = filterInitParam.get(className);
				String filterName = className_filterName.get(className);
				FilterConfig filterConfig = new StandardFilterConfig(filterName, getServletContext(), initParameters);
				Filter filter = filterPool.get(clazz);
				if (null == filter) {
					filter = (Filter) ReflectUtil.newInstance(clazz);
					filter.init(filterConfig);
					filterPool.put(clazz, filter);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
	}

	private void parseServletAutoload(Document d) {

		Elements loadUps = d.select("load-on-startup");
		for (Element element : loadUps) {
			String servletClass = element.parent().select("servlet-class").first().text();
			loadOnStartupServletClassNames.add(servletClass);
		}
	}

	private void handleServletAutoLoad() {
		for (String servletClassName : loadOnStartupServletClassNames) {
			try {
				Class<?> clazz = webappClassLoader.loadClass(servletClassName);
				getServlet(clazz);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void checkDuplicated(Document d, String mapping, String desc) throws WebConfigDuplicatedException {
		Elements elements = d.select(mapping);

		List<String> contents = new ArrayList<String>();
		for (Element e : elements) {
			contents.add(e.text());
		}
		Collections.sort(contents);
		for (int i = 0; i < contents.size() - 1; i++) {
			String contentPre = contents.get(i);
			String contentNext = contents.get(i + 1);
			if (contentPre.equals(contentNext)) {
				throw new WebConfigDuplicatedException(String.format(desc, contentPre));
			}
		}
	}

	private void checkDuplicated(Document doc) throws WebConfigDuplicatedException {

		checkDuplicated(doc, "servlet-mapping url-pattern", "servlet url重复,请保持唯一性: {}");
		checkDuplicated(doc, "servlet servlet-name", "servlet 名称重复,请保持其唯一性:{} ");
		checkDuplicated(doc, "servlet servlet-class", "servlet 类名重复,请保持其唯一性:{} ");
	}

	private boolean match(String pattern, String uri) {
		if (StrUtil.equals(pattern, uri))
			return true;
		if (StrUtil.equals(pattern, "/*"))
			return true;
		if (StrUtil.startWith(pattern, "/*.")) {
			String patternExtName = StrUtil.subAfter(pattern, ".", false);
			String uriExtName = StrUtil.subAfter(uri, ".", false);
			if (StrUtil.equals(patternExtName, uriExtName))
				return true;
		}
		return false;
	}

	private void fireEvent(enumCycle cycle) {
		ServletContextEvent event = new ServletContextEvent(servletContext);
		for (ServletContextListener listener : listeners) {
			if (cycle.equals(enumCycle.init)) {
				listener.contextInitialized(event);
			} else if (cycle.equals(enumCycle.destory)) {
				listener.contextDestroyed(event);

			}
		}
	}

	public List<Filter> getMatchedFilters(String uri) {
		List<Filter> filters = new ArrayList<Filter>();
		Set<String> patterns = url_filterClassName.keySet();
		Set<String> matchedPatterns = new HashSet<String>();
		for (String pattern : patterns) {
			if (match(pattern, uri))
				matchedPatterns.add(pattern);
		}
		Set<String> matchedFilterClassNames = new HashSet<String>();
		for (String matchedPattern : matchedPatterns) {
			List<String> filterClassName = url_filterClassName.get(matchedPattern);
			matchedFilterClassNames.addAll(filterClassName);
		}
		for (String matchedFilterClassName : matchedFilterClassNames) {

			Filter filter;
			try {
				filter = filterPool.get(webappClassLoader.loadClass(matchedFilterClassName));
				filters.add(filter);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return filters;
	}

	public void stop() {
		fireEvent(enumCycle.destory);
		if (watcher != null)
			watcher.stop();
		webappClassLoader.stop();
		destory();
	}

	public void reload() {
		host.reload(this);
	}

	public void destory() {

		Iterator<Class<?>> itClasses = servletPool.keySet().iterator();
		while (itClasses.hasNext()) {
			servletPool.get(itClasses.next()).destroy();
		}
		servletPool.clear();
		itClasses = filterPool.keySet().iterator();
		while (itClasses.hasNext()) {
			filterPool.get(itClasses.next()).destroy();
		}
		filterPool.clear();
	}

	public Servlet getServlet(Class<?> clazz) throws InstantiationException, IllegalAccessException, ServletException {
		if (!servletPool.containsKey(clazz)) {
			HttpServlet servlet = (HttpServlet) clazz.newInstance();

			String servletName = className_servletName.get(clazz.getName());
			ServletConfig config = new StandardServletConfig(getServletContext(), servletName,
					servletInitParam.get(servletName));
			servlet.init(config);

			servletPool.put(clazz, servlet);
		}

		return servletPool.get(clazz);
	}

	public String getServletClassName(String uri) {
		return url_servletClassName.get(uri);
	}

	public String getDocBase() {
		return docBase;
	}

	public String getPath() {
		return path;
	}

	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public WebappClassLoader getWebappClassLoader() {
		return webappClassLoader;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void addListener(ServletContextListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ServletContextListener listener) {
		listeners.remove(listener);
	}

}
