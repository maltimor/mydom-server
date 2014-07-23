package es.carm.mydom.entity;

import java.util.List;
import java.util.Map;

import es.carm.mydom.DAO.DatabaseException;

public interface Database {
	public String getName();
	public String canonice(String tableName);
	
	public Document getNewDocument() throws DatabaseException;
	
	public boolean isDocumentByUnid(String tableNamae,String unid) throws DatabaseException;
	public Document getDocumentByUnid(String tableName, String unid) throws DatabaseException;
	
	public void insert(String tableNamae,Document data) throws DatabaseException;
	public void update(String tableNamae,Document data) throws DatabaseException;
	public void save(String tableName,Document data) throws DatabaseException;
	public void deleteByUnid(String tableName, String unid) throws DatabaseException;
	public void computeWithForm(DominoSession domSession, String formName, Document data) throws DatabaseException;
	
	public List<Document> getAll(String tableName) throws DatabaseException;
	
	public List<Document> getAllDocumentByKey(String tableName,Object keys) throws DatabaseException;
	public Document getDocumentByKey(String tableName,Object keys) throws DatabaseException;
	
	public List<Document> getPage(String tableName, int page,int pageSize,String sortOrder,String where) throws DatabaseException;
	public int getCount(String tableName, String where) throws DatabaseException;	
	
	public View getView(DominoSession domSession,String name) throws DatabaseException;
	
	public AttachService getAttachService() throws DatabaseException;
}
