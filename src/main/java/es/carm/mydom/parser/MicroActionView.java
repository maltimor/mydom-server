package es.carm.mydom.parser;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionView extends MicroAction {
	private ViewDef viewDef;
	private Program program;
	private Map<String,Token> prp;
	
	public ViewDef getViewDef() {
		return viewDef;
	}
	public Program getProgram() {
		return program;
	}

	public MicroActionView() throws ParserException {
		super();
		this.viewDef = new ViewDef();
		this.prp = new HashMap<String,Token>();
		this.program = new Program();
	}
	
	private int getInt(String value) throws ParserException{
		try {
			return Integer.parseInt(value);
		} catch (Exception e){
			throw new ParserException("Numero mal formado: "+value);
		}
	}	

	public void compile(Languaje lang) throws ParserException {
		prp = Arguments.getTokenMap(text);
		if (!prp.containsKey("name")) throw new ParserException("La vista debe tener un atributo name");
		if (!prp.containsKey("sql")&&!prp.containsKey("sqlprovider")) throw new ParserException("La vista debe tener un atributo sql o sqlProvider");
		if (prp.containsKey("sql")&&prp.containsKey("sqlprovider")) throw new ParserException("La vista debe tener unicamente atributo sql o sqlProvider");
		viewDef.setName(Arguments.getValueAndRemoveKey(prp,"name",""));
		viewDef.setMode(Arguments.getValueAndRemoveKey(prp,"mode","basic"));
		viewDef.setEntity(Arguments.getValueAndRemoveKey(prp,"isentity","false").equals("true")?true:false);
		viewDef.setMaxResults(getInt(Arguments.getValueAndRemoveKey(prp,"maxresults", "0")));
		if (prp.containsKey("sql")) viewDef.setSql(Arguments.getValueAndRemoveKey(prp,"sql",""));
		if (prp.containsKey("sqlprovider")) viewDef.setSqlProvider(BeanMethod.getBeanMethod(Arguments.getValueAndRemoveKey(prp,"sqlprovider","")));
		if (viewDef.getMode().equals("basic")) basicTemplate(lang);
		else if (viewDef.getMode().equals("notemplate")) noTemplate();
		else if (viewDef.getMode().equals("notemplateplain")) noTemplate();
		else if (viewDef.getMode().equals("plain")) basicTemplate(lang);
	}

	public void execute(ProgramContext pc) throws ParserException {
	}
	
	private void basicTemplate(Languaje lang) throws ParserException{
		String res ="";
		res+="<html>\n";
		res+="<head>\n";
		res+="<META HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=ISO-8859-1'>\n";
		program.add(MicroAction.instance(MicroActionText.class,res,lang));
		program.add(MicroAction.instance(MicroActionInsertPoint.class,"htmlhead",lang));
		res="\n</head>\n";
		res+="<body>\n";
		program.add(MicroAction.instance(MicroActionInsertPoint.class,"actionbar",lang));
		res+="<hr>\n";
		res+="<div align='center'>\n";
		res+="	<table cellspacing='2' cellpadding='2'>\n";
		res+="		<tr valign='top'>\n";
		res+="			<td><a href='";
		program.add(MicroAction.instance(MicroActionText.class,res,lang));
		program.add(MicroAction.instance(MicroActionComputedText.class,"domServerViewBean.getComputedText_PrevPage",lang));
		res="'><img align='top' src='/icons/prevview.gif' border='0' alt='Left Arrow Icon'>Previous</a></td>\n";
		res+="			<td><a href='";
		program.add(MicroAction.instance(MicroActionText.class,res,lang));
		program.add(MicroAction.instance(MicroActionComputedText.class,"domServerViewBean.getComputedText_NextPage",lang));
		res="'><img align='top' src='/icons/nextview.gif' border='0' alt='Right Arrow Icon'>Next</a></td>\n";
		res+="		</tr>\n";
		res+="	</table>\n";
		res+="</div>\n";
		res+="<table border='0' cellpadding='2' cellspacing='0'>\n";
		program.add(MicroAction.instance(MicroActionText.class,res,lang));
		program.add(MicroAction.instance(MicroActionComputedText.class,"domServerViewBean.getComputedText_viewBody",lang));
		res="	</table>\n";
		res+="</body>\n";
		res+="</html>\n";
		program.add(MicroAction.instance(MicroActionText.class,res,lang));
	}

	private void noTemplate() throws ParserException{
		//program.add(new MicroActionComputedText("domServerViewBean.getComputedText_viewBody"));
	}
}
