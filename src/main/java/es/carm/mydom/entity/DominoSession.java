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

public interface DominoSession {
	public Object execute(BeanMethod beanMethod) throws ParserException;
	public boolean executeCondition(BeanMethod beanMethod) throws ParserException;
	public void executeAction(BeanMethod beanMethod) throws ParserException;
	public String executeGet(BeanMethod beanMethod) throws ParserException;
	public List<String> executeGetList(BeanMethod beanMethod) throws ParserException;
	public boolean executeAgent(BeanMethod beanMethod,HttpServletRequest request,HttpServletResponse response) throws ParserException;
	public String executeGetColumn(BeanMethod beanMethod,Document doc) throws ParserException;
	
	public Database getDatabase();
	public Document getDocumentContext();
	public ViewDef getViewDef(String name);
	public View getCurrentView();

	public Writer getLog(String ruta);
	public Map<String, DominoBean> getBeans();
	public DominoBean getBean(String bean);
	public Object getDao(String dao);
	public Map<String, Object> getDaos();
	public URLComponents getUrlComponents();
	public ServerDao getServerDao();
	public String registerBeanMethod(BeanMethod beanMethod);
	
	public UserInfo getUserInfo();
//	public String getUser();
//	public String getRoles();
//	public boolean hasRole(String rol);
//	public boolean notHasRole(String rol);

	public void setDocumentContext(Document documentContext);
	public void setCurrentView(View currentView);
}