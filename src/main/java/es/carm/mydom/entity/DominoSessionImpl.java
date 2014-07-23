package es.carm.mydom.entity;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.ViewDef;
import es.carm.mydom.security.UserInfo;
import es.carm.mydom.utils.URLComponents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DominoSessionImpl implements DominoSession {
//	private String user;
//	private String roles;
	private UserInfo userInfo;
	private Database database;
	private Document documentContext;
	private View currentView;
	private URLComponents urlComponents;
	private Map<String, DominoBean> beans;
	private Map<String, Object> daos;
	private ServerDao serverDao;
	
	/*metodo para dar de alta codigo de servidor, por ejemplo para los clicks de botones */
	public String registerBeanMethod(BeanMethod beanMethod){
		String unid = serverDao.registerAction(beanMethod);
		return unid;
	}
	
	public Writer getLog(String ruta) {
		return serverDao.getLog(ruta);
	}
	
	public ViewDef getViewDef(String name){
		return serverDao.getViewDef(name);
	}
	public Object execute(BeanMethod beanMethod) throws ParserException {
		return DominoBean.execute(beanMethod,this);
	}
	public boolean executeAgent(BeanMethod beanMethod,HttpServletRequest request,HttpServletResponse response) throws ParserException {
		return (Boolean) DominoBean.executeAgent(beanMethod,this,request,response);
	}

	public String executeGetColumn(BeanMethod beanMethod,Document doc) throws ParserException {
		return (String) DominoBean.executeGetColumn(beanMethod,this,doc);
	}
	
	public boolean executeCondition(BeanMethod beanMethod) throws ParserException {
		return (Boolean) execute(beanMethod);
	}
	public void executeAction(BeanMethod beanMethod) throws ParserException {
		execute(beanMethod);
	}
	public String executeGet(BeanMethod beanMethod) throws ParserException {
		return (String) execute(beanMethod);
	}
	public List<String> executeGetList(BeanMethod beanMethod) throws ParserException {
		return (List<String>) execute(beanMethod);
	}
	
	public DominoBean getBean(String bean){
		return beans.get(bean);
	}
	public Object getDao(String dao){
		return daos.get(dao);
	}
	public URLComponents getUrlComponents() {
		return urlComponents;
	}
	public void setUrlComponents(URLComponents urlComponents) {
		this.urlComponents = urlComponents;
	}
	public Map<String, DominoBean> getBeans() {
		return beans;
	}
	public void setBeans(Map<String, DominoBean> beans) {
		this.beans = beans;
	}
	public Document getDocumentContext() {
		return documentContext;
	}
	public void setDocumentContext(Document documentContext) {
		this.documentContext = documentContext;
	}
	public Map<String, Object> getDaos() {
		return daos;
	}
	public void setDaos(Map<String, Object> daos) {
		this.daos = daos;
	}

	public ServerDao getServerDao() {
		return serverDao;
	}

	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public View getCurrentView() {
		return currentView;
	}

	public void setCurrentView(View currentView) {
		this.currentView = currentView;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}