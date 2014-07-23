package es.carm.mydom.parser;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionColumn extends MicroAction {
	private Map<String,Token> prp;
	private ColumnDef columnDef;

	public MicroActionColumn() throws ParserException {
		super();
		this.columnDef = new ColumnDef();
		this.prp = new HashMap<String,Token>();
	}

	public void compile(Languaje lang) throws ParserException {
		prp = Arguments.getTokenMap(text);
		if (!prp.containsKey("name")) throw new ParserException("La columna debe tener un atributo name");
		if (!prp.containsKey("item")&&!prp.containsKey("formula")) throw new ParserException("La columna debe tener un atributo item o formula");

		columnDef.setName(Arguments.getValueAndRemoveKey(prp,"name",""));
		columnDef.setTitle(Arguments.getValueAndRemoveKey(prp,"title",""));
		columnDef.setKey(Arguments.getValueAndRemoveKey(prp,"key","false").equals("true")?true:false);
		columnDef.setShowasicons(Arguments.getValueAndRemoveKey(prp,"icon","false").equals("true")?true:false);
		columnDef.setHidden(Arguments.getValueAndRemoveKey(prp,"hidden","false").equals("true")?true:false);
		columnDef.setSeparatemultiplevalues(Arguments.getValueAndRemoveKey(prp,"separatemultiplevalues","false").equals("true")?true:false);
		columnDef.setListseparator(Arguments.getValueAndRemoveKey(prp,"listseparator",""));
		columnDef.setItemType(Arguments.getValueAndRemoveKey(prp,"itemtype","text"));
		columnDef.setFullText(Arguments.getValueAndRemoveKey(prp,"fulltext","true").equals("true")?true:false);
		if (prp.containsKey("item"))columnDef.setItem(Arguments.getValueAndRemoveKey(prp,"item",""));
		if (prp.containsKey("formula")) columnDef.setFormula(BeanMethod.getBeanMethod(Arguments.getValueAndRemoveKey(prp,"formula","")));

		columnDef.setLink(Arguments.getValueAndRemoveKey(prp,"link","false").equals("true")?true:false);
	}

	public void execute(ProgramContext pc) throws ParserException {

	}
	
	public String toString(){
		String res = "[Column:";
		res+=columnDef.toString();
		return res;
	}
	public ColumnDef getColumnDef() {
		return columnDef;
	}
	public void setColumnDef(ColumnDef columnDef) {
		this.columnDef = columnDef;
	}
}
