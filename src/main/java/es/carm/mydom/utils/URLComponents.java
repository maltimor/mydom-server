package es.carm.mydom.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class URLComponents {
	private String server;
	private String path;
	private String query;
	private String queryLowcase;
	private String dbFileName;
	private String elFileName;
	private String elName;
	private String elPath;
	private String dbName;
	private String dbPath;
	private String actionName;
	private boolean databaseAccess;
	private boolean attachmentAccess;
	private boolean valid;
	
	public URLComponents(String server,String path,String query){
		dbFileName = "";
		elFileName = "";
		elName="";
		elPath="";
		dbName="";
		dbPath="";
		actionName = "";
		this.server = server!=null?server:"";
		this.path = path!=null?path:"";
		this.query = query!=null?query:"";
		try { this.path = URLDecoder.decode(this.path,"UTF8");
		} catch (Exception e) {}

		try { this.query = URLDecoder.decode(this.query,"UTF8");
		} catch (Exception e) {}
		
		this.databaseAccess = false;
		this.attachmentAccess = false;
		this.queryLowcase = "&"+this.query.toLowerCase();
		valid = obtenParametrosURL();
	}
	
	private boolean obtenParametrosURL(){
		if ((path==null) || ("".equals(path))) return false;
		int i1 = path.toLowerCase().indexOf(".nsf");
		if (i1 <= 0) return true;		//caso de una URL webroot, siempre sera valida
		//llegados aqui estamos intentando acceder a una base de datos
		databaseAccess = true;
		dbFileName = path.substring(0, i1 + 4);
		if (!dbFileName.startsWith("/")) dbFileName = "/"+dbFileName;
		if (i1+4<path.length()) elFileName = path.substring(i1 + 4);
		else elFileName = "/";
		if (!elFileName.startsWith("/")) return false;
		//Arreglo de parametros dentro de los nombres (separo las partes)
		int i5 = elFileName.lastIndexOf(";"); //TODO solo ; pero puede haber ',' Y '='
		if (i5>0){
			elFileName = elFileName.substring(0,i5);
			//TODO que hacer con el resto?
		}
		//sustituyo los + por espacios en blanco
		elFileName = elFileName.replace("+", " ");

		//obtengo el name y el path de db y de el
		int i2 = dbFileName.lastIndexOf("/");
		int i3 = elFileName.lastIndexOf("/");
		dbName = dbFileName.substring(i2+1);
		dbPath = dbFileName.substring(0,i2+1);
		elName = elFileName.substring(i3+1);
		elPath = elFileName.substring(0,i3+1);
		
		
		if ((query!=null)&&(!"".equals(query))){ 
			i1 = query.indexOf("&");
			if (i1>=0) actionName = query.substring(0,i1);
			else actionName = query;
			if (!"".equals(actionName)) if (actionName.indexOf("=")>=0) actionName = "";
			actionName = actionName.toLowerCase();
		}
		
		//proceso la existencia del acceso a un attachment
		int i4 = elPath.toLowerCase().indexOf("/$file/"); 
		if (i4>0){
			attachmentAccess = true;
			elPath = elPath.substring(0,i4);
		}
		
		return true;
	}
	
	public String getQueryValue(String str){
		int i1=queryLowcase.indexOf("&"+str.toLowerCase()+"=");
		if (i1>=0){
			int i2 = query.indexOf("&",i1);
			if (i2>=0) return query.substring(i1+str.length()+1,i2);
			else return query.substring(i1+str.length()+1);
		}
		return "";
	}

	public boolean isAttachmentAccess() {
		return attachmentAccess;
	}

	public String getDbFileName() {
		return dbFileName;
	}
	
	public String getElFileName() {
		return elFileName;
	}

	public String getActionName() {
		return actionName;
	}

	public boolean isValid() {
		return valid;
	}
	
	public boolean isDatabaseAccess() {
		return databaseAccess;
	}

	public String getServer() {
		return server;
	}

	public String getPath(){
		return path;
	}

	public String getQuery(){
		return query;
	}
	
	public String getElName() {
		return elName;
	}

	public String getElPath() {
		return elPath;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbPath() {
		return dbPath;
	}

	public String toString(){
		String res="Path="+path+"\n";
		res = res + "Query="+query+" \n";
		res = res + "DbFileName="+dbFileName+"\n";
		res = res + "DbName="+dbName+"\n";
		res = res + "DbPath="+dbPath+"\n";
		res = res + "ElFileName="+elFileName+"\n";
		res = res + "ElName="+elName+"\n";
		res = res + "ElPath="+elPath+"\n";
		res = res + "ActionName="+actionName+"\n";
		res = res + "isDbAccess="+databaseAccess+"\n";
		res = res + "isValid="+valid+"\n";
		return res;
	}
}
