package es.carm.mydom.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.DominoSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ProgramContext {
	final Logger log = LoggerFactory.getLogger(ProgramContext.class);
	public static final int ACTION_GENERIC = 0;
	public static final int ACTION_NEW = 1;
	public static final int ACTION_READ = 2;
	public static final int ACTION_EDIT = 3;	
	public static final int ACTION_SAVE = 4;

	private Program prg;
	private Map<String,String> prp;
	private DominoSession domSession;
	private int actionType;
	private Document docAct;
	private int posAct;
	private boolean escribe;
	private String insertPoint;
	//canales de salida
	private List<String> body;
	//puntos de insercion
	private Map<String,Integer> insertPoints;//el key es el nombre del punto de insercion, y el int el valor en el array de body
	private Map<String,String> insertPointsDeferred;//para cuando el begin se define antes que el punto
	
	public ProgramContext(Program prg){
		this.prg = prg;
		this.prp = new HashMap<String,String>();
		this.docAct = new Document();
		this.posAct = 0;
		this.body = new ArrayList<String>();
		this.insertPoints = new HashMap<String,Integer>();
		this.insertPointsDeferred = new HashMap<String,String>();
		this.escribe = true;
		this.insertPoint = null;
	}
	
	public void clearBody(){
		if (body == null) log.debug("### BODY NULO");
		body.clear();
	}
	
	private void appendTextToInsertPoint(String name,String txt){
		if (insertPoints.containsKey(name)) {
			Integer pos = insertPoints.get(name);
			String text = body.get(pos);
			body.set(pos, text+txt);
		} else {
			String text = insertPointsDeferred.get(name);
			if (text==null) insertPointsDeferred.put(name, txt);
			else insertPointsDeferred.put(name, text+txt);
		}
	}
	
	public void addInsertPoint(String name){
		this.insertPoints.put(name, body.size()-1);
		//insertar el deferrer insertpoint que hibiere hasta el momento
		if (insertPointsDeferred.containsKey(name)){
			appendTextToInsertPoint(name, insertPointsDeferred.remove(name));
		}
	}
	public void setInsertPoint(String name){
		this.insertPoint = name;
		if (!insertPoints.containsKey(insertPoint)) {
			log.debug("##### BEGIN INSERT NO ENCONTRADO!!!!:"+insertPoint);
		}
	}
	public boolean hasInsertPoint(String name){
		return insertPoints.containsKey(name); 
	}

	public void append(String txt){
		//aqui es donde tengo que tratar el hecho de que este dentro de un hide, o dentro de un beginInsert
		//el algoritmo es asi: si estoy dentro de un hide y este es true, no se anexiona nada
		//pero si dentro de un hide hay un beginInsert, este se tiene que procesar, siempre que el punto de insert exista.
		//si el punto de insert no existe es porque se oculto por un hide(de esta forma no deberia insertar nada), o porque es un error.
		if (insertPoint!=null){
			appendTextToInsertPoint(insertPoint, txt);
		} else if (escribe) body.add(txt);
	}
	
	public List<String> getBody(){
		return body;
/*		if (body.size()==0) return "";
		int size = 0;
		for(String item:body) {
			log.debug("item.length="+item.length());
			size+=item.length();
		}
		log.debug("Body.size="+size);
		StringBuilder res = new StringBuilder(size);
		for(String item:body) res.append(item);
		log.debug("Body.ok");
		return res.toString();*/
	}
	
	public boolean isEscribe() {
		return escribe;
	}

	public void setEscribe(boolean escribe) {
		this.escribe = escribe;
	}

	public String getProperty(String key){
		String res = prp.get(key);
		if (res==null) return "";
		else return res;
	}

	public void setProperty(String key,String value){
		prp.put(key, value);
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public DominoSession getDomSession() {
		return domSession;
	}

	public void setDomSession(DominoSession domSession) {
		this.domSession = domSession;
	}

	public Document getDocAct() {
		return docAct;
	}

	public void setDocAct(Document docAct) {
		this.docAct = docAct;
	}
	public Program getPrg() {
		return prg;
	}

	public void setPrg(Program prg) {
		this.prg = prg;
	}
	public int getPosAct() {
		return posAct;
	}
	public void setPosAct(int posAct) {
		this.posAct = posAct;
	}

	public int incrementAndGetPostAct() {
		int res = posAct;
		posAct++;
		return res;
	}
}
