package es.carm.mydom.DAO;

import es.carm.mydom.entity.Document;

@Deprecated
public interface DocumentDAO {
	public void alterDocument(String tableName,Document doc,String action) throws DatabaseException;
	public String obtenUNID(String tableName) throws DatabaseException;
	public Document getDocument(String tableName,String unid) throws DatabaseException;
	public boolean isDocumentByUnid(String unid) throws DatabaseException;
}
