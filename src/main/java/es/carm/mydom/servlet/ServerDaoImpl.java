package es.carm.mydom.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.naming.resources.Resource;

import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.entity.ServerDao;
import es.carm.mydom.filters.utils.Resources;
import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.HTMLCompiler;
import es.carm.mydom.parser.Field;
import es.carm.mydom.parser.HTMLProgram;
import es.carm.mydom.parser.Program;
import es.carm.mydom.parser.ViewDef;
import es.carm.mydom.utils.UNIDGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ServerDaoImpl implements ServerDao {
	final Logger log = LoggerFactory.getLogger(ServerDaoImpl.class);
	private ServerConfig cfg;
	private Writer strDB;
	private Writer strErr;
	
	public String registerAction(BeanMethod beanMethod) {
		//generamos un uid
		String unid = UNIDGenerator.getUNID();
		log.debug("######################3 REGISTRANDO:"+beanMethod.toString()+" "+unid);
		cfg.getActions().put(unid, beanMethod);
		return unid;
	}

	public synchronized Writer getLog(String ruta) {
		log.debug("######################Get Log:"+ruta+"<-");
		try {
			if (strDB==null) {
				log.debug("######################Get Log: procedo a crear strDB");
				File f1 = new File(cfg.getLogPath()+cfg.getServerName()+".log");
				if (!f1.exists()) {
					f1.getParentFile().mkdirs();
					f1.createNewFile();
				}
				strDB = new BufferedWriter(new FileWriter(f1,true));
				log.debug("######################Get Log: strDB="+strDB);
			}
			if (strErr==null){
				log.debug("######################Get Log: procedo a crear strErr");
				File f2 = new File(cfg.getLogPath()+"Errores.log");
				if (!f2.exists()) {
					f2.getParentFile().mkdirs();
					f2.createNewFile();
				}
				strErr = new BufferedWriter(new FileWriter(f2,true));
				log.debug("######################Get Log: strDB="+strErr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (ruta==null) return null;
		if (ruta.equals("")) return strDB;
		if (ruta.equals("err")) return strErr;

		try {
			//procedo a crear un log con una ruta normal. ver si la ruta existe y si no la crea
			File f = new File(ruta);
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			return new BufferedWriter(new FileWriter(ruta,true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ViewDef getViewDef(String name) {
		Resource resource = Resources.getResource(cfg,"view",name);
		if (resource==null) return null;
		
		try {
			//obtengo el programa
			String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
			HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
			HTMLProgram prg = (HTMLProgram) compiler.compile();
			log.debug("#################### VIEWDEF:"+prg.getViewDef());
			return prg.getViewDef();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Field> getForm(String name){
		Resource resource = Resources.getResource(cfg,"form",name);
		if (resource==null) return null;
		
		try {
			//obtengo el programa
			String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
			HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
			HTMLProgram prg = (HTMLProgram) compiler.compile();
			return prg.getFields();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTempPath() {
		return cfg.getTempPath();
	}

	public String getResourcePath() {
		return cfg.getResourcePath();
	}

	public String getResourceCharset() {
		return cfg.getResourceCharset();
	}
	public String getDomainProxy() {
		//compruebo si se ha asignado y si no lo calculo en base al nombre de la base de datos y espero que sea http 80
		String res = cfg.getDomainProxy();
		if (res==null||res.equals("")) res = "http://"+cfg.getServerName();
		return res;
	}
	public Object getEnvProperty(String prp) {
		return cfg.getEnv().get(prp);
	}
	
	public ServerConfig getCfg() {
		return cfg;
	}

	public void setCfg(ServerConfig cfg) {
		this.cfg = cfg;
	}

	public int getDefaultResults() {
		//compruebo si se ha asignado y si no devuelvo una constante
		Integer res = cfg.getDefaultResults();
		if (res==null) res = 30;		//por defecto 30 si no se especifica nada;
		return res;
	}

	public int getMaxResults() {
		//compruebo si se ha asignado y si no devuelvo una constante
		Integer res = cfg.getMaxResults();
		if (res==null) res = 9999;		//por defecto 9999 si no se especifica nada;
		return res;
	}
	
}
