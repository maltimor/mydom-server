package es.carm.mydom.parser;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.naming.resources.Resource;
import es.carm.mydom.filters.utils.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HTMLCompiler extends BasicCompiler {
	public static final int MAX_LEVEL = 10;
	private DirContext dirContext;
	private int level;
	private boolean raiseOnResourceNotFound;
	
	private HTMLCompiler(int level,boolean raise,String value,String charset,DirContext dirContext){
		super(value,charset);
		this.dirContext = dirContext;
		this.level = level+1;
		this.raiseOnResourceNotFound = raise;
	}
	public HTMLCompiler(String value,String charset,DirContext dirContext){
		this(0,true,value,charset,dirContext);
	}
	public HTMLCompiler(boolean raise,String value,String charset,DirContext dirContext){
		this(0,raise,value,charset,dirContext);
	}
	
	public String getStartTag() {
		return "<$";
	}
	public String getEndTag() {
		return "$>";
	}

	public boolean isInnerXML() {
		return false;
	}
	
	public Program instantiateProgram(){
		return new HTMLProgram();
	}

	public void par_text(Program prg, String txt) throws ParserException {
//		prg.add(new MicroActionText(txt));
		prg.add(MicroAction.instance(MicroActionText.class,txt,this));
	}
	
	public void par_accion(Program program, String accion) throws ParserException {
		//log.debug("### Par_accion:" + accion);
		HTMLProgram prg = (HTMLProgram) program;
		MicroAction res=null;
		String actionName=accion;
		String actionText="";
		int i1=accion.indexOf(" "); //TODO otros separadores
		if (i1>0) {
			actionName=accion.substring(0,i1).toLowerCase();
			actionText=accion.substring(i1).trim();
		} else {
			actionName=accion.toLowerCase();
		}
		//log.debug("### comando:" + comando+" resto:"+resto);
		
		// interpreto acciones del tipo this.xxx ->currentdocument.campo
		if (actionName.equals("puttext")) res = MicroAction.instance(MicroActionText.class,actionText,this);
//		else if (actionName.equals("traer")) res = new MicroAction(MicroAction.TRAER,actionText);
		else if (actionName.equals("view")) res = par_view(prg,actionText);
		else if (actionName.equals("endview")) res = MicroAction.instance(MicroActionEndView.class,actionText,this);
		else if (actionName.equals("column")) res = par_column(prg,actionText);
		else if (actionName.equals("include")) prg.concat(par_include(actionText));
		else if (actionName.equals("field")) res = par_field(prg,actionText);
		else if (actionName.equals("form")) res = MicroAction.instance(MicroActionForm.class,actionText,this);
		else if (actionName.equals("endform")) res = MicroAction.instance(MicroActionEndForm.class,actionText,this);
		else if (actionName.equals("hide")) res = MicroAction.instance(MicroActionHide.class,actionText,this);
		else if (actionName.equals("endhide")) res = MicroAction.instance(MicroActionEndHide.class,actionText,this);
		else if (actionName.equals("doaction")) res = MicroAction.instance(MicroActionDoAction.class,actionText,this);
		else if (actionName.equals("computedtext")) res = MicroAction.instance(MicroActionComputedText.class,actionText,this);
		else if (actionName.equals("queryopen")) prg.setQueryOpen(par_queryMethod(actionText));
		else if (actionName.equals("querysave")) prg.setQuerySave(par_queryMethod(actionText));
		else if (actionName.equals("querydelete")) prg.setQueryDelete(par_queryMethod(actionText));
		else if (actionName.equals("insertpoint")) res = MicroAction.instance(MicroActionInsertPoint.class,actionText,this);
		else if (actionName.equals("begininsert")) res = MicroAction.instance(MicroActionBeginInsert.class,actionText,this);
		else if (actionName.equals("endinsert")) res = MicroAction.instance(MicroActionEndInsert.class,actionText,this);
		else if (actionName.equals("$$return")) prg.set$$return(par_queryMethod(actionText));
		else throw new ParserException("Accion no reconocida:"+accion);

		if (res!=null) prg.add(res);
	}
	
	private BeanMethod par_queryMethod(String actionText) throws ParserException{
		return BeanMethod.getBeanMethod(actionText);
	}
	
	private Program par_include(String actionText) throws ParserException{
		if (level>MAX_LEVEL) throw new ParserException("Maximo numero de includes permitido");
		if (dirContext==null) return instantiateProgram();
		Resource resource=null;
		try {
			resource = (Resource) dirContext.lookup("/"+actionText+".html");
		} catch (NamingException e) {
			try {
				resource = (Resource) dirContext.lookup(actionText);
			} catch (NamingException e2) {
				if (raiseOnResourceNotFound) throw new ParserException("Recurso no encontrado."+actionText);
				else return instantiateProgram();
			}
		}
		String value = Resources.getStringFromStream(resource,this.getCharset());
		HTMLCompiler compiler = new HTMLCompiler(level,this.raiseOnResourceNotFound,value,this.getCharset(),dirContext);
		return compiler.compile();
	}
	
	private MicroAction par_field(HTMLProgram prg, String actionText) throws ParserException{
		MicroActionField res = (MicroActionField) MicroAction.instance(MicroActionField.class,actionText,this);
		prg.addField(res.getField());
		return res;
	}

	private MicroAction par_view(HTMLProgram prg, String actionText) throws ParserException{
		MicroActionView res = (MicroActionView) MicroAction.instance(MicroActionView.class,actionText,this);
		prg.setViewDef(res.getViewDef());
		prg.concat(res.getProgram());
		return res;
	}

	private MicroAction par_column(HTMLProgram prg, String actionText) throws ParserException{
		MicroActionColumn res = (MicroActionColumn) MicroAction.instance(MicroActionColumn.class,actionText,this);
		prg.getViewDef().addColumnDef(res.getColumnDef());
		return res;
	}
}
