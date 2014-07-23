package es.carm.mydom.servlet;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.ProxyDirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DispatchServlet extends org.apache.catalina.servlets.DefaultServlet {
	private static final long serialVersionUID = -9055985246681913856L;
	final Logger log = LoggerFactory.getLogger(DispatchServlet.class);

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		log.debug("###### INICIALIZANDO DispatchServlet");
		super.init();
		String host = this.getServletContext().getServletContextName();
		String context = this.getServletContext().getContextPath();
		log.debug("Host:"+host);
		log.debug("Context:"+context);
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(ProxyDirContext.HOST, host);
		env.put(ProxyDirContext.CONTEXT, context);
		this.getServletConfig().getServletContext();
		DirContext dirContext = (DirContext) getServletContext().getAttribute("dirContext");
		this.resources = new MydomProxyDirContext(env,dirContext);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		log.debug("###### DOGET DispatchServlet:"+request.getPathInfo());
		super.doGet(request, response);
	}
	
	

}
