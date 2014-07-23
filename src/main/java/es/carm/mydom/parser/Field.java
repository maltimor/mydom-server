package es.carm.mydom.parser;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Field {
	public static final int KIND_EDITABLE=0;
	public static final int KIND_COMPUTED=1;
	public static final int KIND_COMPUTED_FOR_DISPLAY=2;
	public static final int KIND_COMPUTED_WHEN_COMPOSED=3;
	
	public static final int UI_TEXT=0;
	public static final int UI_TEXTAREA=1;
	public static final int UI_CHECKBOX=2;
	public static final int UI_RADIOBUTTON=3;
	public static final int UI_COMBOBOX=4;
	public static final int UI_LISTBOX=5;
	public static final int UI_DIALOGLIST=6;
	
	private Map<String,Token> prp;
	private BeanMethod defaultValue;
	private String textList;
	private String separator;
	private String internalSeparator;
	private BeanMethod dinamicList;
	private String itemName;
	private int itemKind;
	private String itemType;
	private int itemUi;
	private int numCols;
	private String resto;
	private boolean hide;
	
	public Field(String text) throws ParserException{
		prp = Arguments.getTokenMap(text);
		parseField();
	}

	private String getValueAndRemoveKey(String key,String defaultValue){
		Token tok = prp.remove(key);
		return tok==null?defaultValue:tok.getValue();
	}
	
	private int getKind(String value) throws ParserException{
		value = value.toLowerCase();
		if (value.equals("editable")) return KIND_EDITABLE;
		else if (value.equals("computed")) return KIND_COMPUTED;
		else if (value.equals("computedfordisplay")) return KIND_COMPUTED_FOR_DISPLAY;
		else if (value.equals("computedwhencomposed")) return KIND_COMPUTED_WHEN_COMPOSED;
		throw new ParserException("No se entiende el itemkind:"+value);
	}
	
	private int getUi(String value) throws ParserException{
		value = value.toLowerCase();
		if (value.equals("text")) return UI_TEXT;
		else if (value.equals("textarea")) return UI_TEXTAREA;
		else if (value.equals("combobox")) return UI_COMBOBOX;
		else if (value.equals("radiobutton")) return UI_RADIOBUTTON;
		else if (value.equals("listbox")) return UI_LISTBOX;
		else if (value.equals("checkbox")) return UI_CHECKBOX;
		else if (value.equals("dialoglist")) return UI_DIALOGLIST;
		else if (value.equals("authors")) return UI_TEXT;
		throw new ParserException("No se entiende el itemui:"+value);
	}
	
	private int getInt(String value) throws ParserException{
		try {
			return Integer.parseInt(value);
		} catch (Exception e){
			throw new ParserException("Numero mal formado: "+value);
		}
	}

	private void parseField() throws ParserException{
		hide = false;			//por defecto supongo que es visible, que no esta afectado por un hide
		itemType = getValueAndRemoveKey("itemtype","text");
		itemName = getValueAndRemoveKey("itemname", null);
		itemKind = getKind(getValueAndRemoveKey("itemkind", "editable"));
		itemUi = getUi(getValueAndRemoveKey("itemui", "text"));
		String dv = getValueAndRemoveKey("defaultvalue", null);
		textList = getValueAndRemoveKey("textlist", null);
		separator = getValueAndRemoveKey("separator", "#");
		internalSeparator = getValueAndRemoveKey("internalseparator", null);
		String dl = getValueAndRemoveKey("dinamiclist", null);
		if (dv!=null) defaultValue = BeanMethod.getBeanMethod(dv);
		if (dl!=null) dinamicList = BeanMethod.getBeanMethod(dl);
		numCols = getInt(getValueAndRemoveKey("numcols", "0"));
		resto = Arguments.getParameterString(prp);
	}
	
	public String toString(){
		String res = "ItemName="+itemName+" ItemType="+itemType+" ItemKind="+itemKind;
		res+=" defaultValue="+defaultValue+" textList="+textList;
		res+=" resto="+resto;
		return res;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getResto() {
		return resto;
	}

	public void setResto(String resto) {
		this.resto = resto;
	}
	
	public BeanMethod getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(BeanMethod defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getItemKind() {
		return itemKind;
	}

	public void setItemKind(int itemKind) {
		this.itemKind = itemKind;
	}

	public String getTextList() {
		return textList;
	}

	public void setTextList(String textList) {
		this.textList = textList;
	}

	public BeanMethod getDinamicList() {
		return dinamicList;
	}

	public void setDinamicList(BeanMethod dinamicList) {
		this.dinamicList = dinamicList;
	}

	public int getItemUi() {
		return itemUi;
	}

	public void setItemUi(int itemUi) {
		this.itemUi = itemUi;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public String getInternalSeparator() {
		return internalSeparator;
	}

	public void setInternalSeparator(String internalSeparator) {
		this.internalSeparator = internalSeparator;
	}

	public int getNumCols() {
		return numCols;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}


}
