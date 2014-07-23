package es.carm.mydom.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DocumentCollection implements Serializable {
	private static final long serialVersionUID = 1565189054635187160L;
	private ArrayList<Document> col;
	private HashMap<String, Integer> map;

	public DocumentCollection() {
		col = new ArrayList<Document>();
		map = new HashMap<String, Integer>();
	}

	public ArrayList<Document> getCol() {
		return col;
	}

	public void addDocument(Document doc) {
		// optimizacion para no calcular
		// size-1
		map.put(doc.getUnid(), col.size());
		col.add(doc);
	}

	public Document getDocument(int i) {
		return col.get(i);
	}

	public int getCount() {
		return col.size();
	}

	public Document getDocumentByUNID(int unid) {
		Integer i = map.get(unid);
		if (i != null) return col.get(i);
		else return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setWrapping(List lst, Class clazz) throws Exception {
		for (int i = 0; i < col.size(); i++) {
			Document doc = col.get(i);
			Object obj = clazz.newInstance();
			try {
				doc.setWrapping(obj, clazz);
				lst.add(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
