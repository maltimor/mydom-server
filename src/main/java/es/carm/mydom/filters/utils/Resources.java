package es.carm.mydom.filters.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.naming.resources.Resource;

import es.carm.mydom.servlet.ServerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Resources {
	final static Logger log = LoggerFactory.getLogger(Resources.class);
	public static String getStringFromStream(Resource resource,String charset) {
		byte[] buff = Resources.getBytesFromStream(resource);
		try {
			return new String(buff,charset);
		} catch (UnsupportedEncodingException e) {
			return new String(buff);
		}
	}
	
	public static byte[] getBytesFromStream(Resource resource){
		byte[] res = null;
		try{
			InputStream in = resource.streamContent();
			res = new byte[in.available()];
			byte[] buff = new byte[16384];
			log.debug("getBytes.available="+in.available());
			int b = 1;
			int dstPos = 0;
			while (b>0){
				b = in.read(buff);
				if (b>0) {
					System.arraycopy(buff, 0, res, dstPos, b);
					dstPos+=b;
				}
			}
			in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	public static Resource getResource(ServerConfig cfg,String type,String fileName){
		log.debug("#### Resources.get:"+type+":"+fileName);
		Resource resource=null;
		Object obj = null;
		if (fileName==null) return null;
		Map<String,String> alias = cfg.getAlias();
		DirContext dirContext = cfg.getDirContext();
		
		//miro a ver si hay alias y lo transformo
		String elName = fileName;
		if (elName.startsWith("/")) elName=elName.substring(1);
		if (alias.containsKey(elName.toLowerCase())) elName = alias.get(elName.toLowerCase());
		log.debug("#### Resources.getAlias:->"+elName);
		
		try {
			obj = dirContext.lookup("/"+type+"/"+elName+".html");
			if (obj instanceof Resource) resource = (Resource) obj; 
		} catch (NamingException e) {
			try {
				obj = dirContext.lookup("/"+type+"/"+elName);
				if (obj instanceof Resource) resource = (Resource) obj; 
			} catch (NamingException e1) {
				try {
					obj = dirContext.lookup("/"+elName);
					if (obj instanceof Resource) resource = (Resource) obj; 
				} catch (NamingException e2) {
				}
			}
		}
		return resource;
	}
	
	/*
	 * NOTA: La lista de parametros typeList se separa usando sep
	 * Aqui no se contemplan alias
	 */
	public static Resource getResourceTypeList(DirContext dirContext,String typeList,String sep,String fileName){
		Resource resource=null;
		String[] types = typeList.split(sep);
		for(String type:types){
			try {
				Object obj = dirContext.lookup("/"+type+fileName);
				if (obj instanceof Resource) resource = (Resource) obj; 
				if (resource!=null) break;
			} catch (NamingException e) { log.debug("No encuentro: /"+type+fileName); }
		}
		return resource;
	}
}
