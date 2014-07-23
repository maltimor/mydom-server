package es.carm.mydom.servlet;

import java.util.Hashtable;

import org.apache.naming.resources.FileDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MydomDirContext extends FileDirContext {
	private FileDirContext secondary;
	private String secondaryDocBase;

	public MydomDirContext() {
		super();
		this.secondary = new FileDirContext();
	}
	public MydomDirContext(Hashtable<String, Object> env) {
		super(env);
		this.secondary = new FileDirContext(env);
	}
	
	public String getSecondaryDocBase() {
		return secondaryDocBase;
	}
	public void setSecondaryDocBase(String secondaryDocBase) {
		this.secondaryDocBase = secondaryDocBase;
	}

	
	
	
}
