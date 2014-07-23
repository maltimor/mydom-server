package es.carm.mydom.servlet;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ContextProvider implements ApplicationContextAware {
	final Logger log = LoggerFactory.getLogger(ContextProvider.class);
	private static ApplicationContext ctx;

	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		log.debug("############## ContextProvider:"+appContext);
		ctx = appContext;

	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}