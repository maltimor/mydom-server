package es.carm.mydom.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionField extends MicroAction {
	final Logger log = LoggerFactory.getLogger(MicroActionField.class);
	private Field field;
	
	/*
	 * La estructura es <$field nombre$> Si se esta en modo lectura obtiene el
	 * valor de un campo del documento Si se esta en modo escritura pone un
	 * <input ...
	 */
	public void compile(Languaje lang) throws ParserException {
		//log.debug("### FIELD:Text="+text);
		this.field = new Field(text);
		//log.debug("### FIELD:FIELD="+field.toString());
		if (field.getItemName()==null) throw new ParserException("Es obligatorio el atributo itemName."+text);
		if ((field.getItemKind()!=Field.KIND_EDITABLE)&&(field.getDefaultValue()==null)) throw new ParserException("Es obligatorio el atributo defaultValue para los campos calculados."+text);
	}

	public void execute(ProgramContext pc) throws ParserException {
		//BUG: cuando un editable esta dentro de unhide, se pierde
		if (!pc.isEscribe()){
			//marco el field como hide, para que endform sepa que hacer con el
			field.setHide(true);
			return;
		}
		String res = "";
		String itemName = field.getItemName();
		String itemType = field.getItemType();
		int itemUi = field.getItemUi();
		String resto = field.getResto();
		String valor = pc.getDocAct().getItemValue(itemName);
		if (valor == null) valor = "";
		
		if (itemName.toLowerCase().startsWith("fecha")){
			log.debug("###########################################################################");
			log.debug("# itemName="+itemName+" valor="+valor+" class:"+pc.getDocAct().getItem(itemName));
			log.debug("###########################################################################");
		}
		
		valor = Arguments.scapeHTML(valor);
		
		if (((pc.getActionType()==ProgramContext.ACTION_NEW)||(pc.getActionType()==ProgramContext.ACTION_EDIT))&&(field.getItemKind()==Field.KIND_EDITABLE)){
			//veo si hay lista dinamica y statica y las proceso
			List<String> options = new ArrayList<String>();
			if (field.getDinamicList()!=null) options.addAll(pc.getDomSession().executeGetList(field.getDinamicList()));
			if (field.getTextList()!=null) {
				String[] opts = field.getTextList().split(field.getSeparator());
				for(String opt:opts) options.add(opt);
			}
			//arreglo para poner columnas en checks y radios
			int col=0;
			int numCols = field.getNumCols();
			if (numCols==0) numCols=options.size();

			//veo si el valor es multivaluado y lo proceso
			String isep = field.getInternalSeparator();
			System.out.println("#### isep="+isep+" valor="+valor);
			List<String> valores = new ArrayList<String>();
			if (isep!=null){
				String[] vals = valor.split(isep);
				for(String val:vals) valores.add(val.trim());
			} else valores.add(valor);
			
			switch(itemUi){
			case Field.UI_TEXT:
				res = "<input type=\"text\" id=\""+itemName+"\" name=\"" + itemName + "\" "+resto+" value=\"" + valor + "\">";
				break;
			case Field.UI_TEXTAREA:
				res = "<textarea id=\""+itemName+"\" name=\"" + itemName + "\" "+resto+">" + valor + "</textarea>";
				break;
			case Field.UI_CHECKBOX:
				for(String option:options){
					//ver caso especial donde | separa texto de valor
					int i1=option.indexOf("|");
					String val = option.trim();
					String txt = option.trim();
					if (i1>0){
						txt = option.substring(0,i1).trim();
						val = option.substring(i1+1).trim();
					}
					res += "<input type=\"checkbox\" name=\"" + itemName + "\" "+resto+" value=\"" + val + "\"";
					//comprobar debo marcar la opcion teniendo en cuenta los multivaluados (internalSeparator)
					if (valores.contains(val)) res+=" checked=\"true\"";
					res += ">"+txt;
					col++;
					if (col>=numCols) {
						res += "<br>";
						col = 0;
					}
				}
				break;
			case Field.UI_RADIOBUTTON:
				for(String option:options){
					//ver caso especial donde | separa texto de valor
					int i1=option.indexOf("|");
					String val = option.trim();
					String txt = option.trim();
					if (i1>0){
						txt = option.substring(0,i1).trim();
						val = option.substring(i1+1).trim();
					}
					res += "<input type=\"radio\" name=\"" + itemName + "\" "+resto+" value=\"" + val + "\"";
					//comprobar debo marcar la opcion teniendo en cuenta los multivaluados (internalSeparator)
					if (valores.contains(val)) res+=" checked=\"true\"";
					res += ">"+txt;
					//ajuste de columna segun el atributo numCols de field
					col++;
					if (col>=numCols) {
						res += "<br>";
						col = 0;
					}
				}
				break;
			case Field.UI_COMBOBOX:
			case Field.UI_DIALOGLIST:
			case Field.UI_LISTBOX:
				res = "<select id=\""+itemName+"\" name=\"" + itemName + "\" "+resto+">\n";
				for(String option:options) {
					//ver caso especial donde | separa texto de valor
					int i1=option.indexOf("|");
					String val = option.trim();
					String txt = option.trim();
					if (i1>0){
						txt = option.substring(0,i1).trim();
						val = option.substring(i1+1).trim();
					}
					res += "<option value=\""+val+"\"";
					//comprobar debo marcar la opcion teniendo en cuenta los multivaluados (internalSeparator)
					if (valores.contains(val)) res+=" selected=\"true\"";
					res+=">"+txt+"</option>";
				}
				res += "</select>";
				break;
			}
		} else res+=valor;
		pc.append(res);
	}
	
	public Field getField() {
		return field;
	}


	public void setField(Field field) {
		this.field = field;
	}
	
}
