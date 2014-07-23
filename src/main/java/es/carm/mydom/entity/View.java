package es.carm.mydom.entity;

import java.util.List;

import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.parser.ViewDef;

public interface View {
	public String getName();
	public ViewDef getViewDef();
	
	public int getCount() throws DatabaseException;
	public int getIndex() throws DatabaseException;
	public void setIndex(int index) throws DatabaseException;
	
	public Document getFirstDocument() throws DatabaseException;
	public Document getNextDocument() throws DatabaseException;
	public Document getNthDocument(int index) throws DatabaseException;
	
	public List<Document> getAll() throws DatabaseException;

	public List<Document> getAllDocumentByKey(Object keys) throws DatabaseException;
	public Document getDocumentByKey(Object keys) throws DatabaseException;

	//TODO getFirstEntry, getNextEntry....getAllEntriesByKey....
	public int getRowIndex(String key) throws DatabaseException;
	
	public List<Document> getPage(int start,int count,String order,String where) throws DatabaseException;
	public int getCount(String where) throws DatabaseException;	
}
