package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionForm extends MicroAction {
	private String resto;
	private boolean multipart;

	public void compile(Languaje lang) throws ParserException {
		multipart=false;
		resto = text;
		if (text.startsWith("multipart")){
			multipart=true;
			resto=text.substring("multipart".length());
		}
	}

	public void execute(ProgramContext pc) throws ParserException {
		String res = "";
		String enctype = multipart?"enctype=\"multipart/form-data\"":"";
		int actionType = pc.getActionType();
		if ((actionType==ProgramContext.ACTION_NEW)||(actionType==ProgramContext.ACTION_EDIT)){
			res = "<form name='_"+pc.getDomSession().getUrlComponents().getElName()+"' method='POST' action='" + pc.getProperty("ActionForm") + "' "+enctype+" "+resto+">";
		} else {
			//Corregido error, lo unico que no hay en modo lectura es action
			res = "<form name='_"+pc.getDomSession().getUrlComponents().getElName()+"' method='POST' action='' "+enctype+" "+resto+">";
			//res = "<form action=\"\">";
		}
		pc.append(res);
	}
}
