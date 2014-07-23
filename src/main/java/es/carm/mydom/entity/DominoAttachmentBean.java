package es.carm.mydom.entity;

import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.servlet.DominoBeanAnnotation;
import es.carm.mydom.utils.URLComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@DominoBeanAnnotation(name="domServerAttachmentBean")
public class DominoAttachmentBean extends DominoBean {
	final Logger log = LoggerFactory.getLogger(DominoAttachmentBean.class);
	
	public String getAttachmentBody(DominoSession domSession){
		String res = "";
		Document doc = domSession.getDocumentContext();
		boolean esEdicion = doc.isEditMode();
		URLComponents urlComponents = domSession.getUrlComponents();
		try{
			AttachService attachService = domSession.getDatabase().getAttachService();
			//a piñon insertamos la gestion de anexos (siempre y cuando no exista el campo (TODO)
			String unid = doc.getUnid();
			if (attachService.hasAttachments(unid)){
				res+="<iframe id='_attach' width='0' height='0'></iframe>";
				res+="<hr>";
				if (esEdicion) res+="<b>Selecciona los anexos para borrar</b><br>";
				res+="<table><tr>";
				for(String attName:attachService.getAllAttachmentsNames(unid)){
					Attachment attach = attachService.getAttachment(unid, attName);
					String url = urlComponents.getDbFileName()+"/"+doc.getUnid()+"/$FILE/"+attach.getName()+"?openElement";
					String urlEdit = urlComponents.getDbFileName()+"/"+doc.getUnid()+"/$FILE/"+attach.getName()+"?editElement";
					//String urlDav = "http://"+urlComponents.getServer()+urlComponents.getDbFileName()+"/temp/WEB-INF/attach/"+doc.getUnid()+"/"+scapeUrl(attach.getName());
					res+="<td align='center'><a href='"+url+"'><img src='/icons/fileatt.gif' border='0' alt='File Attachment Icon'><br></a>";
					if (esEdicion) res+="<input type='checkbox' name='deleteAttachment' value='"+attach.getName()+"'>";
					res+="<a href='"+url+"'>"+attach.getName()+"</a>";
					//if (esEdicion) res+="|<a target='_new' href=\""+urlEdit+"\" ondblclick='document.all[\"_attach\"].src=\""+urlEdit+"\"; return false;'>editar</a>";
					if (esEdicion) res+="|<a href=\"\" onclick='return false' ondblclick='document.all[\"_attach\"].src=\""+urlEdit+"\"; return false;'>editar</a>";
					//res+="|<input type='button' onclick='abre(\"swriter.exe "+urlDav+"\",\"\")' value='editar'>";
					res+="</td>";
				}
				res+="</tr></table>";
			}
		} catch (DatabaseException e){
			e.printStackTrace();
		}
		return res;
	}
}
