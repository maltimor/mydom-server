package es.carm.mydom.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.carm.mydom.entity.Database;
import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.DominoBean;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.entity.DominoViewBean;
import es.carm.mydom.entity.View;
import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.ViewDef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ViewDaoImpl implements View {
	final Logger log = LoggerFactory.getLogger(ViewDaoImpl.class);
	private LotusMapper lotusMapper;
	private Database database;
	private DominoSession domSession;
	private ViewDef viewDef;
	//private DominoViewBean bean;
	//variable que lleva la cuenta del documento actual
	private int index;
	private int count;

	public ViewDaoImpl(LotusMapper lotusMapper,Database database, DominoSession domSession,ViewDef viewDef) throws ParserException, DatabaseException{
		log.debug("############# ViewImpl.new:");
		this.lotusMapper = lotusMapper;
		this.database = database;
		this.domSession = domSession;
		this.viewDef = viewDef;
		this.index = 0;
		this.count = getCount("");
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		if (index<=0) index = 0;
		if (index>=count) index = count;
		this.index = index;
	}
	public int getCount() {
		return count;
	}
	
	public String getName(){
		return viewDef.getName();
	}
	
	public ViewDef getViewDef(){
		return viewDef;
	}
	
	private String getSql() throws DatabaseException{
		if (viewDef.getSqlProvider()!=null){
			try {
				return domSession.executeGet(viewDef.getSqlProvider());
			} catch (ParserException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		} else return viewDef.getSql();
	}

	public List<Document> getAll() throws DatabaseException {
		log.debug("############# ViewImpl.getAll:");
		List<Document> lst = new ArrayList<Document>();
		List<Map<String,Object>> lobj = lotusMapper.getViewAll(getSql());
		for(Map<String,Object> map:lobj){
			//OJO!!!!! DEBO HACER UNA INDIRECCION A TRAVES DEL UNID Y FORM PARA ACCEDER AL DOCUMENTO COMPLETO
			//de no hacerlo así estoy obteniendo las entries
			//Por lo tanto, para cada elemento de lobj debo otener el documento de verdad
			try {
				Document doc = new Document();
				doc.setItems(map);
				if (viewDef.isEntity()){
					//optimizacion. la vista ataca a una tabla con todos sus campos
					lst.add(doc);
				} else {
					Map<String,Object> map2 = lotusMapper.getDocumentByKey(database.canonice(doc.getForm()), "unid='"+doc.getUnid()+"'");
					Document doc2 = new Document();
					doc2.setItems(map2);
					lst.add(doc2);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		}
		return lst;
	}

	public Document getFirstDocument() throws DatabaseException  {
		this.index = 0;
		return getNthDocument(index);
	}

	public Document getNextDocument() throws DatabaseException {
		if (this.index>=this.count) return null;
		this.index++;
		return getNthDocument(index);
	}

	public Document getNthDocument(int index) throws DatabaseException {
		log.debug("############# ViewImpl.getNth:"+index);
		List<Map<String,Object>> lobj = lotusMapper.getViewPage(getSql(), index, 1, "", "");
		for(Map<String,Object> map:lobj){
			try {
				Document doc = new Document();
				doc.setItems(map);
				if (viewDef.isEntity()){
					//optimizacion. la vista ataca a una tabla con todos sus campos
					return doc;
				} else {
					Map<String,Object> map2 = lotusMapper.getDocumentByKey(database.canonice(doc.getForm()), "unid='"+doc.getUnid()+"'");
					Document doc2 = new Document();
					doc2.setItems(map2);
					return doc2;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		}
		return null;
	}

	public List<Document> getAllDocumentByKey(Object keys) throws DatabaseException {
		List<Document> lst = new ArrayList<Document>();
		List<Map<String,Object>> lobj = lotusMapper.getViewAllDocumentByKey(getSql(), SQLUtils.getKeyClause(keys,viewDef.getKeyColumnsNames()));
		for(Map<String,Object> map:lobj){
			try {
				Document doc = new Document();
				doc.setItems(map);
				if (viewDef.isEntity()){
					//optimizacion. la vista ataca a una tabla con todos sus campos
					lst.add(doc);
				} else {
					Map<String,Object> map2 = lotusMapper.getDocumentByKey(database.canonice(doc.getForm()), "unid='"+doc.getUnid()+"'");
					Document doc2 = new Document();
					doc2.setItems(map2);
					lst.add(doc2);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		}
		return lst;
	}

	public Document getDocumentByKey(Object keys) throws DatabaseException {
		List<Map<String,Object>> lobj = lotusMapper.getViewAllDocumentByKey(getSql(), SQLUtils.getKeyClause(keys,viewDef.getKeyColumnsNames()));
		if (lobj.size()==0) return null;
		try {
			Document doc = new Document();
			doc.setItems(lobj.get(0));
			if (viewDef.isEntity()){
				//optimizacion. la vista ataca a una tabla con todos sus campos
				return doc;
			} else {
				Map<String,Object> map2 = lotusMapper.getDocumentByKey(database.canonice(doc.getForm()), "unid='"+doc.getUnid()+"'");
				Document doc2 = new Document();
				doc2.setItems(map2);
				return doc2;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}

	public List<Document> getPage(int start, int count, String order, String where) throws DatabaseException {
		log.debug("############# ViewImpl.getPage:");
		List<Document> lst = new ArrayList<Document>();
		List<Map<String,Object>> lobj = lotusMapper.getViewPage(getSql(), start, count, order, where);
		for(Map<String,Object> map:lobj){
			try {
				Document doc = new Document();
				doc.setItems(map);
				if (viewDef.isEntity()){
					//optimizacion. la vista ataca a una tabla con todos sus campos
					lst.add(doc);
				} else {
					Map<String,Object> map2 = lotusMapper.getDocumentByKey(database.canonice(doc.getForm()), "unid='"+doc.getUnid()+"'");
					Document doc2 = new Document();
					doc2.setItems(map2);
					lst.add(doc2);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException(e.getMessage());
			}
		}
		return lst;
	}

	public int getCount(String where) throws DatabaseException {
		log.debug("############# ViewImpl.getCount:");
		return lotusMapper.getViewCount(getSql(), where);
	}
	
	public int getRowIndex(String key) throws DatabaseException{
		log.debug("############# ViewImpl.getRowIndex:"+key);
		return lotusMapper.getViewRowIndex(getSql(),key);
	}

}
