package es.carm.mydom.servlet;

import java.util.Hashtable;

import javax.naming.directory.DirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MydomProxyDirContext extends org.apache.naming.resources.ProxyDirContext {
	final Logger log = LoggerFactory.getLogger(MydomProxyDirContext.class);

	public MydomProxyDirContext(Hashtable<String, String> env,DirContext dirContext) {
		super(env, dirContext);
		log.debug("##### MydomProxyDirContext Inicializado1!");
	}
	
	public MydomProxyDirContext(DirContext dirContext) {
		super(new Hashtable<String,String>(), dirContext);
		log.debug("##### MydomProxyDirContext Inicializado2!");
	}
	

}
