package es.carm.mydom.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import es.carm.mydom.utils.HexString;
import es.carm.mydom.utils.HttpServletUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Document {
	final Logger log = LoggerFactory.getLogger(Document.class);
	private String unid;
	private String form;
	private Date creation;
	private Map<String, Object> items;
	private List<Attachment> attachments;
	//private Map<String,Item> attachments;
	private boolean editMode;
	private boolean modified;
	private boolean newDocument;

	public Document() {
		this.modified = false;
		this.newDocument = true;
		this.editMode = false;
		clear();
	}

	public void clear() {
		//this.attachments = new HashMap<String, Item>();
		this.attachments = new ArrayList<Attachment>();
		this.items = new HashMap<String, Object>();
		this.unid = null;
		this.form = "";
		this.creation = new Date();
		this.items.put("creation", this.creation);
	}

	public Set<String> getAllItems() {
		return items.keySet();
	}

	public Map<String, Object> getItems() {
		return items;
	}

	public Object getItem(String key) {
		return items.get(key.toLowerCase());
	}
	
	private java.util.Date getInternalDate(Object obj){
		java.util.Date res;
		if (obj instanceof java.sql.Date) res = (java.sql.Date) obj;
		else if (obj instanceof java.util.Date) res = (java.util.Date) obj;
		else {
			try {
				res = SimpleDateFormat.getDateTimeInstance().parse(obj.toString());
			} catch (ParseException e) {
				e.printStackTrace();
				res = new Date();
			}
		}
		return res;
	}

	private String getInternalItemValue(String key){
		//supone que key ya viene en lowercase y que existe
		Object value = items.get(key);
		String res = value.toString();
		if (value instanceof java.sql.Date){
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime((java.sql.Date) value);
			res = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
			//val += " "+cal1.get(Calendar.HOUR_OF_DAY)+":"+cal1.get(Calendar.MINUTE);
		} else if (value instanceof java.util.Date){
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime((java.util.Date) value);
			res = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
			//val += " "+cal1.get(Calendar.HOUR_OF_DAY)+":"+cal1.get(Calendar.MINUTE);
		} else if (value instanceof java.lang.Double){
			//Arreglo problema de transformacion de . a , 
			Double val = (Double) value;
			res = val.toString().replace(".", ",");
		} else if (value instanceof java.lang.Float){
			//Arreglo problema de transformacion de . a , 
			Float val = (Float) value;
			res = val.toString().replace(".", ",");
		} else if (value instanceof java.math.BigDecimal){
			BigDecimal val = (BigDecimal) value;
			res = val.toString().replace(".", ",");
		} else if (value instanceof byte[]){
			res = new String((byte[])value);
		}
		return res;
	}

	public String getItemValue(String key) {
		key = key.toLowerCase();
		if (items.containsKey(key))
			return getInternalItemValue(key);
		else
			return "";
	}

	public byte[] getItemBytes(String key) {
		key = key.toLowerCase();
		if (items.containsKey(key)){
			Object value = items.get(key);
			if (value instanceof byte[]) return (byte[]) value;
			else return getInternalItemValue(key).getBytes();
		} else return null;
	}

	public String getItemValue(String key, String def) {
		key = key.toLowerCase();
		return items.containsKey(key) ? getInternalItemValue(key) : def;
	}

	public byte[] getItemBytes(String key, byte[] def) {
		key = key.toLowerCase();
		return items.containsKey(key) ? getInternalItemValue(key).getBytes() : def;
	}

	public String getUnid() {
		return unid;
	}

	public String getForm() {
		return this.form;
	}

	public boolean isItem(String key) {
		return items.containsKey(key.toLowerCase());
	}

	public void removeItem(String key) {
		items.remove(key.toLowerCase());
		modified = true;
	}
	
	private void setItemInternal(String key,Object value) throws SQLException{
		//en funcion del tipo me conviene transformar los datos de entrada o no
		if (value instanceof java.sql.Clob){
			Clob clob = (Clob) value;
			this.items.put(key.toLowerCase(), clob.getSubString(1,(int) clob.length()));
		} else if (value instanceof java.sql.Blob){
			Blob blob = (Blob) value;
			this.items.put(key.toLowerCase(), blob.getBytes(1,(int) blob.length()));
//		} else if (value instanceof java.sql.Date){
//			Calendar cal1 = Calendar.getInstance();
//			cal1.setTime((java.sql.Date) value);
//			String val = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
//			//val += " "+cal1.get(Calendar.HOUR_OF_DAY)+":"+cal1.get(Calendar.MINUTE);
//			this.items.put(key.toLowerCase(), val);
//		} else if (value instanceof java.util.Date){
//			Calendar cal1 = Calendar.getInstance();
//			cal1.setTime((java.util.Date) value);
//			String val = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
//			//val += " "+cal1.get(Calendar.HOUR_OF_DAY)+":"+cal1.get(Calendar.MINUTE);
//			this.items.put(key.toLowerCase(), val);
		} else {
			this.items.put(key.toLowerCase(), value);
		}
	}
	
	public void setItems(Map<String, Object> items) throws SQLException {
		//Este metodo suele ser usado por el modulo jdbc
		this.items = new HashMap<String,Object>();
		if (items==null) return;
		
		for(String key:items.keySet()){
			setItemInternal(key,items.get(key));
		}
		// NOTA no olvidar los campos especiales
		if (this.items.containsKey("unid")) this.unid=this.items.get("unid").toString();
		if (this.items.containsKey("form")) this.form = this.items.get("form").toString();
		if (this.items.containsKey("creation")) this.creation = getInternalDate(this.items.get("creation"));
		modified = true;
	}

	public void setItem(String key, Object it) {
		key = key.toLowerCase();
		if (it==null) it = "";
		try{
			setItemInternal(key,it);
		} catch (Exception e){
			e.printStackTrace();
			items.put(key, it);
		}
		//items.put(key, it);
		// ahora los campos especiales.
		// NOTA para evitar un bucle infinito no llamo a los setXX
		if (key.equals("unid")) this.unid =it.toString();
		if (key.equals("form")) this.form = it.toString();
		if (key.equals("creation")) this.creation = getInternalDate(it);
		modified = true;
	}

	public void setItemValue(String key, String value) {
		key = key.toLowerCase();
		if (value==null) value="";
		items.put(key, value);
		// ahora los campos especiales.
		// NOTA para evitar un bucle infinito no llamo a los setXX
		if (key.equals("unid")) this.unid = value;
		if (key.equals("form")) this.form = value;
		if (key.equals("creation")) this.creation = getInternalDate(value);
		modified = true;
	}

	public void setItemBytes(String key, byte[] value) {
		key = key.toLowerCase();
		String it = "";
		if (value!=null) it = new String(value);
		items.put(key, it);
		// ahora los campos especiales.
		// NOTA para evitar un bucle infinito no llamo a los setXX
		if (key.equals("unid")) this.unid = it;
		if (key.equals("form")) this.form = it;
		if (key.equals("creation")) this.creation = getInternalDate(it);
		modified = true;
	}

	public void setUnid(String unid) {
		setItemValue("unid",""+unid);
		this.unid = unid;
		modified = true;
	}

	public void setForm(String form) {
		setItemValue("form", form);
		this.form = form;
		modified = true;
	}

	@Override
	public String toString() {
		String output = "(new:"+newDocument+" edit:"+editMode+" modified:"+modified+" form:"+form+" unid:"+unid+" Attachments:"+this.attachments.size()+") ";
		for (String paramName : this.getAllItems()) {
			output += paramName + "=";
			String paramValue = this.getItemValue(paramName);
			if (paramValue == null) {
				output += "Sin valor";
			} else {
				output += paramValue;
			}
			output += "|";
		}
//		if (attachments.size()>0) output +="\nATTACHMENTS("+attachments.size()+"):";
//		for (String attachName:attachments.keySet()){
//			Item it = attachments.get(attachName);
//			output += "(attachment: "+it.getFileName()+" "+it.getMimeType()+" "+it.getBytes().length+")";
//			output += "|";
//		}
		return output;
	}

	@SuppressWarnings("rawtypes")
	public void setWrapping(Object obj, Class clazz) throws Exception {
		Field[] fields = clazz.getFields();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			String value = this.getItemValue(fieldName);
			field.set(obj, value);
		}
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if ((methodName.startsWith("set"))
					&& (method.getParameterTypes().length == 1)) {
				String fieldName = methodName.substring(3, 4).toLowerCase()
						+ methodName.substring(4);
				String value = this.getItemValue(fieldName);
				method.invoke(obj, value);
			}
		}
	}
	
	public Object getWrappedObject(Class clazz) throws Exception {
		Object obj = clazz.newInstance();
		setWrapping(obj, clazz);
		return obj;
	}
	
	public void setWrappedObject(Object obj,Class clazz) throws Exception {
		HashMap<String,Field> fieldMap = new HashMap<String,Field>(); 
		Field[] fields = clazz.getDeclaredFields();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			fieldMap.put(field.getName().toLowerCase(), field);
			int modifiers = field.getModifiers();
			log.debug("FIELD:"+field.getName()+" "+modifiers);
			if (Modifier.isPublic(modifiers)){
				String fieldName = field.getName();
				Object value = field.get(obj);
				this.setItemValue(fieldName,value.toString());
			}
		}
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if ((methodName.startsWith("get"))&&(method.getParameterTypes().length == 0)) {
				String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
				if (fieldMap.containsKey(fieldName)){
					Object value = method.invoke(obj);
					this.setItemValue(fieldName,value.toString());
				}
			}
		}
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isNewDocument() {
		return newDocument;
	}
	
	//TODO
	public boolean isValid() {
		return true;
	}

	public void setNewDocument(boolean newDocument) {
		this.newDocument = newDocument;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	public void addAttachment(Attachment attach){
		this.attachments.add(attach);
	}
	public boolean hasAttachments(){
		return attachments.size()>0;
	}

	public Date getCreation() {
		return creation;
	}

//	public Map<String, Item> getAttachments() {
//		return attachments;
//	}
//
//	public void setAttachments(Map<String, Item> attachments) {
//		this.attachments = attachments;
//	}
//	public Item getAttachment(String name){
//		return this.attachments.get(name);
//	}
//	public void addAttachment(Item item){
//		this.attachments.put(item.getName(), item);
//	}
//	public void removeAttachment(String name){
//		this.attachments.remove(name);
//	}
//	public boolean hasAttachments(){
//		return attachments.size()>0;
//	}
}