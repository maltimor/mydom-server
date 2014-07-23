package es.carm.mydom.servlet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AttachInfo {
	private String unid;
	private String alternateUnid;
	private List<String> editedFiles;
	
	public AttachInfo(){
		this.editedFiles = new ArrayList<String>();
	}
	
	public AttachInfo(String unid,String unidAlt,String file){
		this();
		this.unid = unid;
		this.alternateUnid = unidAlt;
		editedFiles.add(file);
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getAlternateUnid() {
		return alternateUnid;
	}

	public void setAlternateUnid(String alternateUnid) {
		this.alternateUnid = alternateUnid;
	}

	public List<String> getEditedFiles() {
		return editedFiles;
	}

	public void setEditedFiles(List<String> editedFiles) {
		this.editedFiles = editedFiles;
	}
}
