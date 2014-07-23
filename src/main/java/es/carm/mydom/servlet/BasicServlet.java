package es.carm.mydom.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.entity.DominoSessionImpl;
import es.carm.mydom.entity.ServerDao;
import es.carm.mydom.security.SecurityException;
import es.carm.mydom.utils.HttpServletUtils;
import es.carm.mydom.utils.URLComponents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//public class BasicServlet implements org.springframework.web.HttpRequestHandler, ServerDao {
public class BasicServlet extends HttpServlet {
	final Logger log = LoggerFactory.getLogger(BasicServlet.class);
	private static final long serialVersionUID = -4491812904732286128L;
	private ServerConfig cfg;
	private ServerDao serverDao;
	
	@Override
	public void init() throws ServletException {
		super.init();
		//inicializo el servletConfig
		setCfg( (ServerConfig) getServletContext().getAttribute("serverConfig") );
		setServerDao( (ServerDao) getServletContext().getAttribute("serverDao") );
		cfg.setServlet(this);
	}


	//*******************************************************


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req,resp);
	}


	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long initTime = System.currentTimeMillis();
		log.debug("================ DATOS DEL REQUEST =============="+initTime);
		HttpServletUtils.pintaRequest(request);
		log.debug("=================================================");
//		for(String action:cfg.getFilters().keySet()){
//			log.debug(action+"="+cfg.getFilters().get(action).toString());
//		}
//		log.debug("=================================================");
//		for(String action:cfg.getBeans().keySet()){
//			log.debug(action+"="+cfg.getBeans().get(action).toString());
//		}
//		log.debug("=================================================");

		//pasos:
		//1 crear la session
		//2 identificar la url y el recurso
		//3 despachar
		
		//1 crear la session
		DominoSessionImpl domSession = new DominoSessionImpl();
		try {
			domSession.setUserInfo(cfg.getUserInfo());
			domSession.setDatabase(cfg.getDatabase());
			domSession.setDocumentContext(cfg.getDatabase().getNewDocument());
			domSession.setBeans(cfg.getBeans());
			domSession.setDaos(cfg.getDaos());
			domSession.setServerDao(serverDao);
		} catch (DatabaseException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
			return;
		}
		
		//2 identificar la url y el recurso
		URLComponents urlComponents = new URLComponents(cfg.getServerName(),request.getRequestURI(), request.getQueryString());
		log.debug("URLComponents:"+urlComponents.toString());
		if (!urlComponents.isValid()){
			response.sendError(400,"Url mal escrita");
			return;
		}
		domSession.setUrlComponents(urlComponents);

		//en funcion del action busco un manejador y lo ejecuto
		//CASO ESPECIAL DEL ELEMENTO INICIAL "/" -> redirigir al elemento correspondiente
		if (urlComponents.getElFileName().equals("/")){
			// TODO decicir si hacer un redirect o modificar el urlcomponents y seguir ejecutando codigo
			// de momento un redirect
			if (cfg.getDefaultElement()!=null) response.sendRedirect(cfg.getDefaultElement());
			return;
		}
		String filter = urlComponents.getActionName();
		HttpFilter uAction = cfg.getFilters().get(filter);
		if (uAction==null){
			response.sendError(400,"No encontrado dispatcher para "+filter);
			return;
		}
		
		//2.5 comprobar la seguridad
		try {
			cfg.getSecurityPolice().checkPermission(urlComponents.getElName(), filter, cfg.getUserInfo().getRoles("|"), "|");
		} catch (SecurityException e) {
			response.sendError(400,"No tiene permisos para acceder "+filter);
			return;
		}
		
		//3 despachar
		//configurar el filtro pasandole la configuracion del servidor
		// BUG content-type
		//response.setContentType("text/html");
		uAction.setCfg(cfg);
		uAction.doFilter(domSession,request,response);
		long endTime = System.currentTimeMillis();
		log.debug("BasicServlet:"+((endTime-initTime)/1000.0)+" seg.");
	}

	public ServerConfig getCfg() {
		return cfg;
	}

	public void setCfg(ServerConfig cfg) {
		this.cfg = cfg;
	}
	public ServerDao getServerDao() {
		return serverDao;
	}
	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}
}
