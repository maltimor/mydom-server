package es.carm.mydom.utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import es.carm.mydom.entity.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServletUtils {
	final static Logger log = LoggerFactory.getLogger(HttpServletUtils.class);
	/*
	 * devuelve un documento con tantos campos como parametros de la url tiene la cadena.
	 */
	public static Document rellenaQueryString(String query){
		Document doc = new Document();
		String[] params = query.split("&");
		for(String param:params){
			int i1 = param.indexOf("=");
			String key = (i1>=0)?param.substring(0,i1):param;
			String value = (i1>=0)?param.substring(i1+1):"";
			doc.setItemValue(key, value);
		}
		return doc;
	}

	/*
	 * devuelve un documento con tantos campos como parametros de la url tiene el request.
	 * Solo tiene en cuenta el primer valor del parameter map
	 */
	public static Document rellenaParameterMap(HttpServletRequest request){
		Document doc = new Document();
		Map<String,String[]> params = request.getParameterMap();
		for(String key:params.keySet()){
			String[] values = params.get(key);
			if (values.length>0) doc.setItemValue(key, values[0]);
		}
		return doc;
	}
	
	public static void pintaRequest(HttpServletRequest request){
		
		log.debug("request.getAuthType="+request.getAuthType());
		log.debug("request.getContextPath="+request.getContextPath());
		log.debug("request.getContentType="+request.getContentType());
		log.debug("request.getContentLength="+request.getContentLength());
		log.debug("request.getCharacterEncoding="+request.getCharacterEncoding());
		log.debug("request.getLocalAddr="+request.getLocalAddr());
		log.debug("request.getLocalName="+request.getLocalName());
		log.debug("request.getLocalPort="+request.getLocalPort());
		log.debug("request.getMethod="+request.getMethod());
		log.debug("request.getPathInfo="+request.getPathInfo());
		log.debug("request.getPathTranslated="+request.getPathTranslated());
		log.debug("request.getProtocol="+request.getProtocol());
		log.debug("request.getQueryString="+request.getQueryString());
		log.debug("request.getRemoteAddr="+request.getRemoteAddr());
		log.debug("request.getRemoteHost="+request.getRemoteHost());
		log.debug("request.getRemotePort="+request.getRemotePort());
		log.debug("request.getRemoteUser="+request.getRemoteUser());
		log.debug("request.getRequestedSessionId="+request.getRequestedSessionId());
		log.debug("request.getRequestURI="+request.getRequestURI());
		log.debug("request.getRequestURL="+request.getRequestURL());
		log.debug("request.getScheme="+request.getScheme());
		log.debug("request.getServerName="+request.getServerName());
		log.debug("request.getServerPort="+request.getServerPort());
		log.debug("request.getServletPath="+request.getServletPath());
		
		Collection<Part> parts = null;
		boolean hayParts = false;
		try{
			parts = request.getParts();
			hayParts = true;
		} catch (Exception e){ }

		log.debug("request.getAttributes:");
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()){
			String name = names.nextElement();
			log.debug(name+"="+request.getAttribute(name));
		}
		Map<String,String[]> params = request.getParameterMap();
		log.debug("request.getParameterMap:("+params.size()+")");
		for(String key:params.keySet()){
			String[] values = params.get(key);
			System.out.print(key + "=");
			for(int i=0;i<values.length;i++) System.out.print(values[i]+", ");
			System.out.println();
		}

		if (hayParts){
			try{
				log.debug("request.getParts:("+parts.size()+")");
				int j=0;
				for(Part part:parts){
					log.debug("Part("+j+"): name="+part.getName()+" contentType="+part.getContentType()+
							" size="+part.getSize()+" headers.size="+part.getHeaderNames().size());
					j++;
				}
			} catch (Exception e){
				log.error("ERROR al obtener los parts."+e.getMessage());
			}
		}
	}
	
}
