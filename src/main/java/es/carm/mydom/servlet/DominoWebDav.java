package es.carm.mydom.servlet;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.servlets.WebdavServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.carm.mydom.entity.ServerDao;

public class DominoWebDav extends WebdavServlet {
	final Logger log = LoggerFactory.getLogger(DominoWebDav.class);
	private static final long serialVersionUID = 4024930099035228479L;
	
	private String initialCharset="UTF-8";
	private String finalCharset="UTF-8";

	public DominoWebDav() {
		System.out.println("###### WEBDAV:NEW");
	}

	@Override
	public void init() throws ServletException {
		super.init();
		//configurado desde el web.xml en init-param
		if (getServletConfig().getInitParameter("initialCharset") != null)
			setInitialCharset(getServletConfig().getInitParameter("initialCharset"));
		if (getServletConfig().getInitParameter("finalCharset") != null)
			setInitialCharset(getServletConfig().getInitParameter("finalCharset"));
		
		//configurado desde spring usando ServletContextAttributeExporter
		if (getServletContext().getAttribute("webdavInitialCharset") != null)
			setInitialCharset((String) getServletContext().getAttribute("webdavInitialCharset"));
		if (getServletContext().getAttribute("webdavFinalCharset") != null)
			setFinalCharset((String) getServletContext().getAttribute("webdavFinalCharset"));
	}

	@Override
	protected String getRelativePath(HttpServletRequest request) {
		String res =  super.getRelativePath(request);
		log.debug("###### WEBDAV:getRelativePath: res="+res);
		try {
			res = new String(res.getBytes(initialCharset),finalCharset);
			log.debug("###### WEBDAV:getRelativePath: res2="+res);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return res;
	}

	public String getInitialCharset() {
		return initialCharset;
	}

	public void setInitialCharset(String initialCharset) {
		this.initialCharset = initialCharset;
	}

	public String getFinalCharset() {
		return finalCharset;
	}

	public void setFinalCharset(String finalCharset) {
		this.finalCharset = finalCharset;
	}
}
