package es.carm.mydom.entity;

import javax.naming.directory.DirContext;



import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.DAO.DocumentTableDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Deprecated

public class DatabaseImpl {
	final Logger log = LoggerFactory.getLogger(DatabaseImpl.class);
	private DirContext dirContext;
	private DocumentTableDAO documentDAO;

	public Document getNewDocument() throws DatabaseException {
		log.debug("Database.newDocument.");
		String unid = documentDAO.obtenUNID(null);
		Document doc = new Document();
		doc.setUnid(unid);
		doc.setModified(false);
		doc.setNewDocument(true);
		return doc;
	}
	
	public boolean isDocumentByUnid(String tableName, String unid) throws DatabaseException {
		return documentDAO.isDocumentByUnid(unid);
	}	
	
	public void alterDocument(String tableName, Document doc, String action) throws DatabaseException {
		log.debug("Database.alterDocument:"+action+" "+doc.getUnid());
		documentDAO.alterDocument(tableName, doc, action);
		doc.setModified(false);
		doc.setNewDocument(false);
	}

	public Document getDocument(String tableName,String unid) throws DatabaseException {
		log.debug("Database.getDocument:"+unid);
		Document doc = documentDAO.getDocument(tableName, unid);
		doc.setModified(false);
		doc.setNewDocument(false);
		return doc;
	}

	public DirContext getDirContext() {
		return dirContext;
	}
	public void setDirContext(DirContext dirContext) {
		this.dirContext = dirContext;
	}
	public DocumentTableDAO getDocumentDAO() {
		return documentDAO;
	}
	public void setDocumentDAO(DocumentTableDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void insertSimpleView(String name, String formula) {
		// TODO Auto-generated method stub
		
	}
}
