package es.carm.mydom.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import es.carm.mydom.DAO.AttachServiceDAO;
import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.entity.AttachService;
import es.carm.mydom.entity.Attachment;
import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.DominoAttachmentBean;
import es.carm.mydom.entity.Item;
import es.carm.mydom.utils.URLComponents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionEndForm extends MicroAction {
	final Logger log = LoggerFactory.getLogger(MicroActionEndForm.class);
	public void compile(Languaje lang) throws ParserException {
	}

	public void execute(ProgramContext pc) throws ParserException {
		String res = "";
		int actionType = pc.getActionType();
		boolean esEdicion = (actionType==ProgramContext.ACTION_NEW)||(actionType==ProgramContext.ACTION_EDIT);
		Document doc = pc.getDomSession().getDocumentContext();
		URLComponents urlComponents = pc.getDomSession().getUrlComponents();
		HTMLProgram prg = (HTMLProgram) pc.getPrg();
		
		//defino el cuerpo del attachment
//		try{
			//defino un punto de insercion si no esta definido previamente
			//implementacion por defecto del cuerpo de anexos
			//me baso en la existencia o no del insertpoint "attachBody"
			if (!pc.hasInsertPoint("attachmentBody")) {
				DominoAttachmentBean dab = new DominoAttachmentBean();
				res+=dab.getAttachmentBody(pc.getDomSession());
//				AttachService attachService = pc.getDomSession().getDatabase().getAttachService();
//				//a piñon insertamos la gestion de anexos (siempre y cuando no exista el campo (TODO)
//				String unid = doc.getUnid();
//				if (attachService.hasAttachments(unid)){
//	//				//TODO A PIÑON METO EL ACTIVEX DE WScript
//	//				res+="\n<script>\n";
//	//				res+="function abre(prog,url){ var shell = new ActiveXObject(\"WScript.shell\"); shell.run(prog+\" \"+url); shell.Quit; shell = null; }\n";
//	//				res+="</script>\n";
//					res+="<iframe id='_attach' width='0' height='0'></iframe>";
//					res+="<hr>";
//					if (esEdicion) res+="<b>Selecciona los anexos para borrar</b><br>";
//					res+="<table><tr>";
//					for(String attName:attachService.getAllAttachmentsNames(unid)){
//						Attachment attach = attachService.getAttachment(unid, attName);
//						String url = urlComponents.getDbFileName()+"/"+doc.getUnid()+"/$FILE/"+attach.getName()+"?openElement";
//						String urlEdit = urlComponents.getDbFileName()+"/"+doc.getUnid()+"/$FILE/"+attach.getName()+"?editElement";
//						//String urlDav = "http://"+urlComponents.getServer()+urlComponents.getDbFileName()+"/temp/WEB-INF/attach/"+doc.getUnid()+"/"+scapeUrl(attach.getName());
//						res+="<td align='center'><a href='"+url+"'><img src='/icons/fileatt.gif' border='0' alt='File Attachment Icon'><br></a>";
//						if (esEdicion) res+="<input type='checkbox' name='deleteAttachment' value='"+attach.getName()+"'>";
//						res+="<a href='"+url+"'>"+attach.getName()+"</a>";
//						//if (esEdicion) res+="|<a target='_new' href=\""+urlEdit+"\" ondblclick='document.all[\"_attach\"].src=\""+urlEdit+"\"; return false;'>editar</a>";
//						if (esEdicion) res+="|<a href=\"\" onclick='return false' ondblclick='document.all[\"_attach\"].src=\""+urlEdit+"\"; return false;'>editar</a>";
//						//res+="|<input type='button' onclick='abre(\"swriter.exe "+urlDav+"\",\"\")' value='editar'>";
//						res+="</td>";
//					}
//					res+="</tr></table>";
//				}
			}
//		} catch (DatabaseException dbe){
//			dbe.printStackTrace();
//			throw new ParserException(dbe.getMessage());
//		}
		
		//a piñon le isertamos el form, y el uid
		res+=obtenHiddenField("form", obtenValor(doc, "form"));
		res+=obtenHiddenField("unid", obtenValor(doc, "unid"));
		// TODO res+=doc.getCreation()+"|"+doc.getItem("creation")+"|"+doc.getItemValue("creation");
		log.debug("OOOOOOOOOOOOOOOOOOOOOOOOOOO PROCESO HIDDEN FIELDS:");
		for(Field field:prg.getFields()){
			//BUG del hide
			if ((field.getItemKind()!=Field.KIND_EDITABLE)||(actionType==ProgramContext.ACTION_READ)||(field.isHide())){
				String item = field.getItemName();
				String valor = obtenValor(doc,item);
				log.debug("item="+item+" Valor="+valor);
				res+=obtenHiddenField(item,valor);
			}
		}
		log.debug("OOOOOOOOOOOOOOOOOOOOOOOOOOO PROCESO HIDDEN FIELDS: FIN");
		res = res + "</form>";
		pc.append(res);
	}
	
	private String obtenValor(Document doc,String item){
		String valor = doc.getItemValue(item);
		if (valor == null) valor = "";
		return valor;
	}
	
	private String obtenHiddenField(String item,String valor){
		valor = Arguments.scapeHTML(valor);
		return "\n<input type=\"hidden\" name=\""+item+"\" value=\""+valor+"\">";
	}
}
