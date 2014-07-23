package es.carm.mydom.filters;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.Resource;

import es.carm.mydom.DAO.AttachServiceDAO;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.filters.utils.Dispatcher;
import es.carm.mydom.filters.utils.Resources;
import es.carm.mydom.servlet.HttpFilter;
import es.carm.mydom.servlet.ServerConfig;
import es.carm.mydom.utils.URLComponents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ResourceAction implements HttpFilter {
	final Logger log = LoggerFactory.getLogger(ResourceAction.class);
	private ServerConfig cfg;
	
	public void doFilter(DominoSession domSession, HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		URLComponents urlComponents = domSession.getUrlComponents();
		String fileName = urlComponents.getElFileName();
		Resource resource = Resources.getResourceTypeList(cfg.getDirContext(),"fileresource|imageresource|stylesheetresource|","[|]",fileName);
		if (resource==null){
			log.debug("####### NO ESTA");
			response.sendError(HttpServletResponse.SC_NOT_FOUND,"Elemento no encontrado");
			return;
		}
		//TODO Establecer el content-type y el content-disposition
		String mimeType = cfg.getServlet().getServletContext().getMimeType(fileName);
		response.setContentType(mimeType!=null?mimeType:"text/html");
		Dispatcher.sendResourceStream(fileName,resource, response);
		
		//Redirigir la peticion al dispatcher de tomcat
/*		HttpServlet servlet = cfg.getServlet();
		ServletContext context = servlet.getServletContext();
		RequestDispatcher dispatcher = context.getNamedDispatcher("mydomDispatch");
		if (dispatcher==null) {
			//TODO Establecer el content-type y el content-disposition
			String mimeType = cfg.getServlet().getServletContext().getMimeType(fileName);
			response.setContentType(mimeType!=null?mimeType:"text/html");
			Dispatcher.sendResourceStream(fileName,resource, response);
		} else {
		//TODO cambiar la ruta!!!
			System.out.println("Redirigir a mydomDispatch:"+fileName);
			dispatcher.forward(request,response);
		}*/
		
	}

	public ServerConfig getCfg() {
		return cfg;
	}

	public void setCfg(ServerConfig cfg) {
		this.cfg = cfg;
	}
	
}
