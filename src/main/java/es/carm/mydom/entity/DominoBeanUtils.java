package es.carm.mydom.entity;

import java.util.ArrayList;
import java.util.List;

import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.ParserException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DominoBeanUtils {
	public static String getRefresh(DominoSession domSession){
		/* @Command( [RefreshWindow] ) */
		String res = "";
		res+="document.forms[0].action='";
		res+=domSession.getUrlComponents().getDbFileName();
		if (domSession.getDocumentContext().isNewDocument()){
			res+=domSession.getUrlComponents().getElFileName()+"?refreshDocument";
		} else {
			res+="/"+domSession.getDocumentContext().getUnid()+"?refreshDocument";
		}
		res+="&"+domSession.getUrlComponents().getQuery();
		res+="'; document.forms[0].submit();";
		return res;
	}
	
	public static String getFieldFromExport(DominoSession domSession,String idOrigen, String keyExport){
		
		String dato = "";
		try {
				View view = domSession.getDatabase().getView(domSession, "ID");
				Document doc= view.getDocumentByKey(idOrigen);
				if (doc!=null) {
					String Exportacion = "&"+doc.getItemValue("EXPORTACION")+"&";
					String cad = "&"+keyExport+"=";
					int pos1 = Exportacion.indexOf(cad);
					if (pos1!=-1){
						int pos2 = Exportacion.indexOf("&",pos1+1);
						if (pos2!=-1) dato = Exportacion.substring(pos1+cad.length(),pos2);
						else dato = Exportacion.substring(pos1+cad.length());
					}
				}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}
		return dato;
	}
	public static String getFirstDbLookUp(DominoSession domSession, String vista, String clave, String campo){
		List<String> ls = getDbLookUp(domSession, vista, clave, campo);
		if (ls.size()>0) return ls.get(0);
		else return "";
	}

	public static List<String> getDbLookUp(DominoSession domSession, String vista, String clave, String campo){
	//@DbLookup("":"NoCache";"":"";"VISTA";CLAVE;"CAMPO";[FailSilent]);
//			dbLookUp(vista,clave,campo){
			List<String> res=new ArrayList<String>();
			View view;
			try {
				view = domSession.getDatabase().getView(domSession, vista);
				List<Document> ldoc= view.getAllDocumentByKey(clave);
				for(Document doc:ldoc){
					res.add(doc.getItemValue(campo));
				}
//				Document doc= view.getDocumentByKey(clave);
//				if (doc!=null) res=doc.getItemValue(campo);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			return res;
	}

	public static String getEdit(DominoSession domSession){
		/* @Command([EditDocument]) */
		String res = "<a href=\""+domSession.getUrlComponents().getDbFileName()+"/"+domSession.getDocumentContext().getUnid()+"?";
		if (domSession.getDocumentContext().isEditMode()) res+="openDocument";
		else res += "editDocument";
		res+="\">";
		return res;
	}
	
	public static String getSave(DominoSession domSession){
		String res = "<a  onclick=\"if(typeof compruebaCamposDocumento == 'function') \n";
		res+="	if (compruebaCamposDocumento()==true) return false; \n";
		res+="		if (document.all.DLIUploader1) \n";
		res+="			document.all.DLIUploader1.submit(); \n";
		res+="		else \n";
		res+="			document.forms[0].submit(); \n";
		res+="	return false;\" href=\"\">";
		return res;
	}
	
	public static String getDelete(DominoSession domSession){
		String res = "<a  onclick=\"if (confirm('¿Desea eliminar el documento permanentemente?')) \n";
		res+="	abreAgente(&quot;EliminarComponente&quot;,&quot;UNID=&quot;+document.all[&quot;DocumentID&quot;].value) \n";
		res+="	return false;\" href=\"\">";
		return res;
	}
	
	public static String getCompose(DominoSession domSession,String form){
		/* @Command([Compose];"form") */
//		form = form.replace(" ", "+")
		String res = "<a href=\""+domSession.getUrlComponents().getDbFileName()+"/"+form+"?openForm\">";
		return res;
	}
	public static String getPage(DominoSession domSession,String page){
		String res = "<a href=\""+domSession.getUrlComponents().getDbFileName()+"/"+page+"?openPage\">";
		return res;
	}
	public static String getView(DominoSession domSession,String view){
		String res = "<a href=\""+domSession.getUrlComponents().getDbFileName()+"/"+view+"?openView\">";
		return res;
	}
	public static String getUrlOpen(DominoSession domSession,String url){
		/* @URLOpen(url) */
//		
		String res = "<a href=\""+domSession.getUrlComponents().getDbFileName()+"/"+url+"\">";
		return res;
	}
	public static String getToolsRunMacro(DominoSession domSession,String agentName) {
		/* @Command( [ToolsRunMacro]; bean ) */
		String res = "";
		res+="document.forms[0].action='";
		res+=domSession.getUrlComponents().getDbFileName();
		res+="/"+agentName+"?openAgent";
		res+="&"+domSession.getUrlComponents().getQuery();
		res+="'; document.forms[0].submit();";
		return res;
	}
	
	public static String getToolsRunMacroLink(DominoSession domSession,String agentName) {
		/* @Command( [ToolsRunMacro]; bean ) */
		String res = getToolsRunMacro(domSession,agentName);
		res = "<a  onclick=\""+res+"\nreturn false;\" href=\"\">";
		return res;
	}

	public static String getToolsRunMacroWithContext(DominoSession domSession,String beanMethod){
		try{
			BeanMethod bean = BeanMethod.getBeanMethod(beanMethod);
			String unid = domSession.registerBeanMethod(bean);
			String action = domSession.getUrlComponents().getDbFileName()+"/"+ unid+"?openAction";
			return "<a href=\""+action+"\">";
		} catch (Exception e){
			e.printStackTrace();
		}
		return "alert('!Macro no encontrada "+beanMethod+"!')";
	}

	public static boolean isHideWhenEdit(DominoSession domSession){
		return domSession.getDocumentContext().isEditMode();
	}

	public static boolean isHideWhenEditAndRol(DominoSession domSession,String rol){
		return domSession.getDocumentContext().isEditMode() || domSession.getUserInfo().notHasRole(rol);
	}

	public static boolean isHideWhenRead(DominoSession domSession){
		return !domSession.getDocumentContext().isEditMode();
	}

	public static boolean isHideWhenReadAndRol(DominoSession domSession,String rol){
		return !domSession.getDocumentContext().isEditMode() || domSession.getUserInfo().notHasRole(rol);
	}

	public static boolean isHideWhenRol(DominoSession domSession,String rol){
		return domSession.getUserInfo().notHasRole(rol);
	}

}
