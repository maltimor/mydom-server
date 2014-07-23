package es.carm.mydom.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.DirContext;
import javax.servlet.http.HttpServlet;

import org.reflections.Reflections;

import es.carm.mydom.entity.Database;
import es.carm.mydom.entity.DominoBean;
import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.MicroActionHide;
import es.carm.mydom.security.SecurityPolice;
import es.carm.mydom.security.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ServerConfig {
	final Logger log = LoggerFactory.getLogger(ServerConfig.class);
	private HttpServlet servlet;
	private String serverName;
	private Database database;
	private String domainProxy;
	private String basePath;
	private String defaultElement;
	private Integer maxResults;
	private Integer defaultResults;
	private UserInfo userInfo;
	private SecurityPolice securityPolice;
	private DirContext dirContext;
	private String logPath;
	private String resourcePath;
	private String tempPath;
	private String packagePath;
	private boolean haltOnError;
	private String resourceCharset;
	private Map<String, HttpFilter> filters;
	private Map<String, DominoBean> beans;
	private Map<String, Object> daos;
	private Map<String, BeanMethod> actions;
	private Map<String, String> alias;
	private Map<String, AttachInfo> attachsInfo;
	private Map<String, Object> env;
	
	public ServerConfig(){
		this.filters = new HashMap<String,HttpFilter>();
		this.beans = new HashMap<String,DominoBean>();
		this.daos = new HashMap<String,Object>();
		this.actions = new HashMap<String,BeanMethod>();
		this.alias = new HashMap<String,String>();
		this.attachsInfo = new HashMap<String, AttachInfo>();
		this.setEnv(new HashMap<String, Object>());
	}
	
	private void loadBeans() {
		Set<Class<?>> singletons;
		Reflections reflections = new Reflections(packagePath);
		boolean hayErrores = false;
		try {
			singletons = reflections.getTypesAnnotatedWith(DominoBeanAnnotation.class);
			Iterator<Class<?>> it = singletons.iterator();
			while(it.hasNext()) {
				Class aux = it.next();
				if (aux.isAnnotationPresent(DominoBeanAnnotation.class)) {
					DominoBeanAnnotation cargAux = (DominoBeanAnnotation) aux.getAnnotations()[0];
					if (beans.containsKey(cargAux.name())) {
						log.debug("WARNING: loadBeans-Clase repetida: " + aux.getSimpleName() + "-" + cargAux.name());
						if (haltOnError) {
							hayErrores = true;
						}
					} else {
						Object objeto = aux.newInstance();
						if (objeto instanceof DominoBean) {
							beans.put(cargAux.name(), (DominoBean) objeto);
							log.debug("loadBeans-Clase añadida a beans: " + aux.getSimpleName() + "-" + cargAux.name());
						} else {
							log.debug("WARNING: loadBeans-La clase no es instancia de DominoBean: " + aux.getSimpleName() + "-" + cargAux.name());
							throw new IllegalStateException("La clase no es instancia de DominoBean");
						}
					}
				}
			}
			if (hayErrores) throw new IllegalStateException("Hay clases repetidas en bean de ServerConfig");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public boolean isHaltOnError() {
		return haltOnError;
	}

	public void setHaltOnError(boolean haltOnError) {
		this.haltOnError = haltOnError;
	}

	private void pintaAttachsInfo(){
		log.debug("####ATTACHS INFO ####");
		for(String key:attachsInfo.keySet()){
			AttachInfo attachInfo = attachsInfo.get(key);
			System.out.print(key+"=");
			for(String f:attachInfo.getEditedFiles()){
				System.out.print(f+"|");
			}
			// TODO log.debug();
		}
	}

	public String getResourceCharset() {
		return resourceCharset;
	}

	public void setResourceCharset(String resourceCharset) {
		this.resourceCharset = resourceCharset;
	}
	public AttachInfo getAttachInfo(String unid){
		return attachsInfo.get(unid);
	}
	public void clearAllEditedFiles(){
		attachsInfo.clear();
		pintaAttachsInfo();
	}
	public void clearEditedFiles(String unid){
		attachsInfo.remove(unid);
		pintaAttachsInfo();
	}
	
	public void addEditedFile(String unid,String unidAlt,String file){
		if (attachsInfo.containsKey(unid)){
			attachsInfo.get(unid).getEditedFiles().add(file);
		} else {
			attachsInfo.put(unid, new AttachInfo(unid,unidAlt,file));
		}
		//pinto para depurar
		pintaAttachsInfo();
	}
	
	public Database getDatabase() {
		return database;
	}
	public void setDatabase(Database database) {
		this.database = database;
	}
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public Map<String, HttpFilter> getFilters() {
		return filters;
	}
	public void setFilters(Map<String, HttpFilter> filters) {
		this.filters = filters;
	}
	public Map<String, DominoBean> getBeans() {
		return beans;
	}
	public void setBeans(Map<String, DominoBean> beans) {
		this.beans = beans;
	}
	public Map<String, Object> getDaos() {
		return daos;
	}
	public void setDaos(Map<String, Object> daos) {
		this.daos = daos;
	}
	public String getDefaultElement() {
		return defaultElement;
	}
	public void setDefaultElement(String defaultElement) {
		this.defaultElement = defaultElement;
	}
	public Map<String, BeanMethod> getActions() {
		return actions;
	}
	public void setActions(Map<String, BeanMethod> actions) {
		this.actions = actions;
	}
	public DirContext getDirContext() {
		return dirContext;
	}
	public void setDirContext(DirContext dirContext) {
		this.dirContext = dirContext;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	public String getTempPath() {
		return tempPath;
	}
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
	public Map<String, String> getAlias() {
		return alias;
	}
	public void setAlias(Map<String, String> alias) {
		this.alias = alias;
	}
	public Map<String, AttachInfo> getAttachsInfo() {
		return attachsInfo;
	}
	public void setAttachsInfo(Map<String, AttachInfo> attachsInfo) {
		this.attachsInfo = attachsInfo;
	}
	public HttpServlet getServlet() {
		return servlet;
	}
	public void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public SecurityPolice getSecurityPolice() {
		return securityPolice;
	}
	public void setSecurityPolice(SecurityPolice securityPolice) {
		this.securityPolice = securityPolice;
	}
	public Map<String, Object> getEnv() {
		return env;
	}
	public void setEnv(Map<String, Object> env) {
		this.env = env;
	}

	public String getDomainProxy() {
		return domainProxy;
	}

	public void setDomainProxy(String domainProxy) {
		this.domainProxy = domainProxy;
	}

	public Integer getDefaultResults() {
		return defaultResults;
	}

	public void setDefaultResults(Integer defaultResults) {
		this.defaultResults = defaultResults;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}
}
