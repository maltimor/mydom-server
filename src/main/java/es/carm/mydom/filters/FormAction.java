package es.carm.mydom.filters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.catalina.connector.Response;
import org.apache.naming.resources.Resource;

import es.carm.mydom.parser.Field;
import es.carm.mydom.parser.HTMLCompiler;
import es.carm.mydom.parser.HTMLProgram;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.Program;
import es.carm.mydom.parser.ProgramContext;
import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.entity.AttachService;
import es.carm.mydom.entity.Attachment;
import es.carm.mydom.entity.Database;
import es.carm.mydom.entity.DatabaseImpl;
import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.entity.View;
import es.carm.mydom.filters.utils.AgentResponse;
import es.carm.mydom.filters.utils.Dispatcher;
import es.carm.mydom.filters.utils.RequestReader;
import es.carm.mydom.filters.utils.Resources;
import es.carm.mydom.servlet.AttachInfo;
import es.carm.mydom.servlet.HttpFilter;
import es.carm.mydom.servlet.ServerConfig;
import es.carm.mydom.utils.FilesUtils;
import es.carm.mydom.utils.URLComponents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class FormAction implements HttpFilter {
	final Logger log = LoggerFactory.getLogger(FormAction.class);
	private ServerConfig cfg;

	public void doFilter(DominoSession domSession, HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		Database database = domSession.getDatabase();
		URLComponents urlComponents = domSession.getUrlComponents();
		String actionName = urlComponents.getActionName();
		String fileName = urlComponents.getElFileName();
		String formName = urlComponents.getElName();
		String path = urlComponents.getPath();
		if (actionName.equals("openform")){
			//Acceso al elemento
			//dado el nombre completo obtener el elemento.
			Resource resource = Resources.getResource(cfg,"form",fileName);
			if (resource==null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND,"Elemento no encontrado");
				return;
			}

			try {
				//obtengo el programa
				String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
				HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
				HTMLProgram prg = (HTMLProgram) compiler.compile();
				
				//Actualizo el documento de contexto
				Document doc = domSession.getDocumentContext();
				doc.setEditMode(true);
				doc.setForm(formName);		//TODO limitacion de formularios a un unico directorio
				fillFields(ProgramContext.ACTION_NEW,prg,domSession);
				
				//ejecuto el webqueryopen
				//if (prg.getQueryOpen()!=null) domSession.executeAction(prg.getQueryOpen());
				if (prg.getQueryOpen()!=null) {
					AgentResponse agentResponse = new AgentResponse(response);
					log.debug("EJECUTANDO AGENTE:"+prg.getQueryOpen().getMethod()+" MODIFIED="+agentResponse.isModified());
					boolean doOpen=domSession.executeAgent(prg.getQueryOpen(),request,agentResponse);

					//atiendo la peticion siempre que no se haya modificado la salida del response previamente
					//o si cancelo la apertura
					log.debug("MODIFIED="+agentResponse.isModified());
					if (agentResponse.isModified()||(doOpen==false)) return;
				}
				
				//configuro el programa con la accion openform
				ProgramContext pc = new ProgramContext(prg);
				pc.setDomSession(domSession);
				pc.setActionType(ProgramContext.ACTION_NEW);
				pc.setProperty("ActionForm",path+"?createdocument");
				pc.setDocAct(domSession.getDocumentContext());
						
				//ejecuto el programa y el resultado lo saco por pantalla
				List<String> res = prg.execute(pc);
					
				log.debug("doc="+doc.toString());
				Dispatcher.sendArrayString(res,cfg.getResourceCharset(),response);
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("refreshdocument")){
			log.debug("######################################### refresh");

			//solo acepto metodo POST (de momento no los gets)
			if (!request.getMethod().equals("POST")){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Solo se acepta POST en esta accion "+actionName);
				return;
			}

			//TODO entre el form,formName y fileName meundo lio!
			//obtengo el form del request y el programa, debe coincidir con la url
			String form = request.getParameter("form");
			
			//determinar si provengo de un createdocument o de un savedocument
			//en funcion del ElName y del form del request
			boolean create = (formName.toLowerCase().equals(form.toLowerCase()));
			boolean save = !create;
			Document doc = domSession.getDocumentContext();
			
			//copypaste del save, pero actualizado para el refresh
			if(save){
				//recuperar el documento
				String unid = formName;	//aqui deberia ser el uid del documento
				log.debug("docAntes="+doc.toString());
				// obtengo el documento y si no, salgo con error
				try {
					doc = database.getDocumentByUnid(null,unid);
					if (doc == null) {
						response.sendError(Response.SC_NOT_FOUND, "Documento no encontrado");
						return;
					}
				} catch (DatabaseException e) {
					response.sendError(Response.SC_INTERNAL_SERVER_ERROR, e.getMessage());
					return;
				}

				try{
					//borramos los attachments seleccionados
					AttachService attachService = database.getAttachService();
					String[] deleteAttachment = request.getParameterValues("deleteAttachment");
					if (deleteAttachment!=null){
						for(String delAtt:deleteAttachment){
							log.debug("BORRANDO:"+delAtt);
							attachService.deleteAttachment(unid, delAtt);
							//doc.removeAttachment(delAtt);
						}
					}
					//recargamos los anexos editados y los borramos de temp y de cfg
					AttachInfo attachInfo = cfg.getAttachInfo(unid);
					if (attachInfo!=null){
						log.debug("######HAY EDITED FILES!!!!!");
						for(String f:attachInfo.getEditedFiles()){
							log.debug("Add:"+f);
							byte[] buff = FilesUtils.getBytesFromFile(f);
							Attachment attach = new Attachment();
							attach.setName(f);
							attach.setBytes(buff);
							attachService.addAttachment(unid, attach);
							//doc.addAttachment(attach);
						}
						cfg.clearEditedFiles(unid);
					}
				} catch (Exception e){
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
					return;
				}
				
				
				//asignamos a la session el nuevo documento
				domSession.setDocumentContext(doc);
				log.debug("docDespues="+doc.toString());

				//nos preparamos para encontrar el formulario
				// TODO crear un nuevo objeto urlComponents con los nuevos campos
				formName = form;
				fileName = "/"+form;
			}
			
			if (!form.equals(formName)){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No coincide el nombre del formulario.");
				return;
			}
			
			log.debug("form="+form+" forName="+formName+" fileName="+fileName);
			
			//a partir de aqui tengo el doc antiguo, 
			Resource resource = Resources.getResource(cfg,"form",fileName);
			if (resource==null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND,"No se puede modificar un documento sin form. Elemento no encontrado:"+fileName);
				return;
			}			

			try {
				//obtengo el programa
				String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
				HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
				HTMLProgram prg = (HTMLProgram) compiler.compile();

				// paso los campos del request parameter map al documento
				doc = domSession.getDocumentContext();
				log.debug("docAntes="+doc.toString());
				RequestReader.rellenaCampos(request,prg,doc,actionName,cfg.getResourceCharset());
				log.debug("docDespues="+doc.toString());
				// TODO ver si aqui toca fillfields
				fillFields(ProgramContext.ACTION_SAVE, prg, domSession);
				log.debug("docDespues2="+doc.toString());				
				
				//Actualizo el documento de contexto
				doc.setEditMode(true);
				doc.setForm(formName);		//TODO limitacion de formularios a un unico directorio
				fillFields(ProgramContext.ACTION_EDIT,prg,domSession);
				
				//configuro el programa con la accion openform
				ProgramContext pc = new ProgramContext(prg);
				pc.setDomSession(domSession);
				if (create) {
					pc.setActionType(ProgramContext.ACTION_NEW);
					pc.setProperty("ActionForm",path+"?createdocument");
				} else {
					pc.setActionType(ProgramContext.ACTION_EDIT);
					pc.setProperty("ActionForm",path+"?savedocument");
				}
				pc.setDocAct(domSession.getDocumentContext());
						
				//ejecuto el programa y el resultado lo saco por pantalla
				List<String> res = prg.execute(pc);
					
				//antes de terminar vacio la lista de attachments editados por si hubiera
				cfg.clearEditedFiles(doc.getUnid());
				
				log.debug("doc="+doc.toString());
				Dispatcher.sendArrayString(res,cfg.getResourceCharset(),response);				
				
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("createdocument")||actionName.equals("savedocument")){
			boolean create = actionName.equals("createdocument");
			boolean save = actionName.equals("savedocument");
			log.debug("######################################### create:"+create+" save:"+save);

			//solo acepto metodo POST (de momento no los gets)
			if (!request.getMethod().equals("POST")){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Solo se acepta POST en esta accion "+actionName);
				return;
			}
			
			//TODO entre el form,formName y fileName meundo lio!
			//obtengo el form del request y el programa, debe coincidir con la url
			String form = request.getParameter("form");
			Document doc = domSession.getDocumentContext();
			
			if(save){
				//recuperar el documento
				String unid = formName;	//aqui deberia ser el uid del documento
				log.debug("docAntes="+doc.toString());
				// obtengo el documento y si no, salgo con error
				try {
					doc = database.getDocumentByUnid(null,unid);
					if (doc == null) {
						response.sendError(Response.SC_NOT_FOUND, "Documento no encontrado");
						return;
					}
				} catch (DatabaseException e) {
					response.sendError(Response.SC_INTERNAL_SERVER_ERROR, e.getMessage());
					return;
				}

				try{
					//borramos los attachments seleccionados
					AttachService attachService = database.getAttachService();
					String[] deleteAttachment = request.getParameterValues("deleteAttachment");
					if (deleteAttachment!=null){
						for(String delAtt:deleteAttachment){
							log.debug("BORRANDO:"+delAtt);
							attachService.deleteAttachment(unid, delAtt);
							//doc.removeAttachment(delAtt);
						}
					}
					//recargamos los anexos editados y los borramos de temp y de cfg
					AttachInfo attachInfo = cfg.getAttachInfo(unid);
					if (attachInfo!=null){
						log.debug("######HAY EDITED FILES!!!!!");
						for(String f:attachInfo.getEditedFiles()){
							log.debug("Add:"+f);
							byte[] buff = FilesUtils.getBytesFromFile(f);
							Attachment attach = new Attachment();
							attach.setName(f);
							attach.setBytes(buff);
							attachService.addAttachment(unid, attach);
							//doc.addAttachment(attach);
						}
						cfg.clearEditedFiles(unid);
					}
				} catch (Exception e){
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
					return;
				}
				
				
				//asignamos a la session el nuevo documento
				domSession.setDocumentContext(doc);
				log.debug("docDespues="+doc.toString());

				//nos preparamos para encontrar el formulario
				// TODO crear un nuevo objeto urlComponents con los nuevos campos
				formName = form;
				fileName = "/"+form;
			}
	
			if (form==null){
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Form=null,"+urlComponents);
				return;
			}
			if (!form.equals(formName)){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No coincide el nombre del formulario.");
				return;
			}
			
			log.debug("form="+form+" forName="+formName+" fileName="+fileName);
			
			//a partir de aqui tengo el doc antiguo, 
			Resource resource = Resources.getResource(cfg,"form",fileName);
			if (resource==null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND,"No se puede modificar un documento sin form. Elemento no encontrado");
				return;
			}

			try {
				//obtengo el programa
				String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
				HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
				HTMLProgram prg = (HTMLProgram) compiler.compile();
				// paso los campos del request parameter map al documento
				doc = domSession.getDocumentContext();
				log.debug("docAntes="+doc.toString());
				RequestReader.rellenaCampos(request,prg,doc,actionName,cfg.getResourceCharset());
				log.debug("docDespues="+doc.toString());
				// TODO ver si aqui toca fillfields
				fillFields(ProgramContext.ACTION_SAVE, prg, domSession);
				log.debug("docDespues2="+doc.toString());
				
				//atiendo el webquerysave
				AgentResponse agentResponse = new AgentResponse(response);
				boolean doSave=true;
				if (prg.getQuerySave()!=null) {
					log.debug("EJECUTANDO AGENTE:"+prg.getQuerySave().getMethod()+" MODIFIED="+agentResponse.isModified());
					doSave = domSession.executeAgent(prg.getQuerySave(),request,agentResponse);
				}
				//grabo el resultado
				try {
					if (doSave) {
						if (create) database.insert(form,doc);
						else if (save) database.update(form,doc);
					}
				} catch (DatabaseException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
					return;
				}
				
				//atiendo el $$return siempre que no se haya modificado la salida del response previamente
				log.debug("MODIFIED="+agentResponse.isModified());
				if (!agentResponse.isModified()) {
					String url = null;
					if (prg.get$$return()!=null) {
						Dispatcher.sendString("<html><body>"+domSession.executeGet(prg.get$$return())+"</body></html>",cfg.getResourceCharset(),response);
						return;
					}
					else url = response.encodeRedirectURL(urlComponents.getDbFileName()+"/"+ doc.getItemValue("unid")+"?opendocument");
					log.debug("****** Redirect a "+url);
					response.sendRedirect(url);					
				}
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("deletedocument")){
			String unid = formName;	//aqui deberia ser el uid del documento
			Document doc = domSession.getDocumentContext();
			log.debug("docAntes="+doc.toString());
			// obtengo el documento y si no, salgo con error
			try {
				doc = database.getDocumentByUnid(null,unid);
				if (doc == null) {
					response.sendError(Response.SC_NOT_FOUND, "Documento no encontrado");
					return;
				}
			} catch (DatabaseException e) {
				response.sendError(Response.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				return;
			}
			
			//antes de seguir vacio la lista de attachments editados por si hubiera
			cfg.clearEditedFiles(doc.getUnid());
			
			//asignamos a la session el nuevo documento
			domSession.setDocumentContext(doc);
			log.debug("docDespues="+doc.toString());

			//nos preparamos para encontrar el formulario
			// TODO crear un nuevo objeto urlComponents con los nuevos campos
			fileName = "/"+doc.getForm();
			String form = doc.getForm();
			
			//A partir de aqui es muy parecido al create document con algunas modificaciones
			//obtengo el formulario.
			// TODO si no encuentro el formulario no pasa nada? borro el documento sin mas
			Resource resource = Resources.getResource(cfg,"form",fileName);
			if (resource==null){
				//borro el documento
				try {
					database.deleteByUnid(form, doc.getUnid());
				} catch (DatabaseException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
					return;
				}
				Dispatcher.sendString("<html><body><h1>Document deleted</h1></body></html>",cfg.getResourceCharset(), response);
				return;
			}

			try {
				//obtengo el programa
				String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
				HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
				HTMLProgram prg = (HTMLProgram) compiler.compile();
				
				//atiendo el webquerydelete
				AgentResponse agentResponse = new AgentResponse(response);
				boolean doDelete = true;
				if (prg.getQueryDelete()!=null) {
					log.debug("EJECUTANDO AGENTE:"+prg.getQueryDelete().getMethod()+" MODIFIED="+agentResponse.isModified());
					doDelete = domSession.executeAgent(prg.getQueryDelete(),request,agentResponse);
				}
				//borro el documento
				try {
					if (doDelete) database.deleteByUnid(form, doc.getUnid());
				} catch (DatabaseException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
					return;
				}
				
				//atiendo el $$return siempre que no se haya modificado la salida del response previamente
				log.debug("MODIFIED="+agentResponse.isModified());
				if (!agentResponse.isModified()) {
					if (prg.get$$return()!=null) {
						Dispatcher.sendString("<html><body>"+domSession.executeGet(prg.get$$return())+"</body></html>",cfg.getResourceCharset(), response);
						return;
					} else {
						Dispatcher.sendString("<html><body><h1>Document deleted</h1></body></html>",cfg.getResourceCharset(), response);
						return;
					}
				}
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("opendocument")||actionName.equals("editdocument")){
			boolean read = actionName.equals("opendocument");
			int actionType = read?ProgramContext.ACTION_READ:ProgramContext.ACTION_EDIT;
			Document doc = domSession.getDocumentContext();
			log.debug("docAntes="+doc.toString());

			// obtengo el documento y si no, salgo con error
			try {
				// TODO Aun no contemplo los keys de las vistas
				if (urlComponents.getElPath().equals("/")){
					String unid = formName;	//aqui deberia ser el uid del documento
					doc = database.getDocumentByUnid(null,unid);
				} else {
					//se supone que aqui tengo una vista y uno o varios keys
					//ElPath debe acabar con una barra, las quito para obtener el nombre del view bean
					log.debug("open/edit document type VIEW:");
					String viewName = urlComponents.getElPath().replace("/", "");
					log.debug("#### View Name ="+viewName);
					View view = database.getView(domSession, viewName);
					if (view==null){
						response.sendError(Response.SC_NOT_FOUND, "Vista "+viewName+" no encontrada");
						return;
					}
					doc = view.getDocumentByKey(urlComponents.getElName());
				}
				if (doc == null) {
					response.sendError(Response.SC_NOT_FOUND, "Documento no encontrado");
					return;
				}
			} catch (DatabaseException e) {
				response.sendError(Response.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				return;
			}
			
			//asignamos a la session el nuevo documento
			doc.setEditMode(!read);
			domSession.setDocumentContext(doc);
			log.debug("docDespues="+doc.toString());

			//nos preparamos para encontrar el formulario
			// TODO crear un nuevo objeto urlComponents con los nuevos campos
			fileName = "/"+doc.getForm();
			formName = doc.getForm();
			
			// TODO a partir de aqui igual que en open form con alguna modificacion
			//Acceso al elemento
			//dado el nombre completo obtener el elemento.
			Resource resource = Resources.getResource(cfg,"form",fileName);
			if (resource==null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND,"Elemento no encontrado");
				return;
			}

			try {
				//obtengo el programa
				String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
				HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
				HTMLProgram prg = (HTMLProgram) compiler.compile();
				
				//Actualizo el documento de contexto
				doc = domSession.getDocumentContext();
				//doc.setForm(formName);		//TODO limitacion de formularios a un unico directorio
				fillFields(actionType,prg,domSession);
				//ejecuto el webqueryopen
				//if (prg.getQueryOpen()!=null) domSession.executeAction(prg.getQueryOpen());
				if (prg.getQueryOpen()!=null) {
					AgentResponse agentResponse = new AgentResponse(response);
					log.debug("EJECUTANDO AGENTE:"+prg.getQueryOpen().getMethod()+" MODIFIED="+agentResponse.isModified());
					boolean doOpen=domSession.executeAgent(prg.getQueryOpen(),request,agentResponse);

					//atiendo la peticion siempre que no se haya modificado la salida del response previamente
					//o si cancelo la apertura
					log.debug("MODIFIED="+agentResponse.isModified());
					if (agentResponse.isModified()||(doOpen==false)) return;
				}
				
				//configuro el programa con la accion openform
				ProgramContext pc = new ProgramContext(prg);
				pc.setDomSession(domSession);
				pc.setActionType(actionType);
				pc.setProperty("ActionForm",path+"?savedocument");
				pc.setDocAct(domSession.getDocumentContext());
					
				//ejecuto el programa y el resultado lo saco por pantalla
				List<String> res = prg.execute(pc);
				
				//antes de terminar vacio la lista de attachments editados por si hubiera
				cfg.clearEditedFiles(doc.getUnid());
				
				log.debug("doc="+doc.toString());
				Dispatcher.sendArrayString(res,cfg.getResourceCharset(),response);
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("openelement")||actionName.equals("editelement")){
			//elPath debe contener $file e ir precedido del uid
			if (!urlComponents.isAttachmentAccess()){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No se ha especificado el parametros $file:"+urlComponents.getPath());
				return;
			}
			String unid = urlComponents.getElPath().substring(1);	//aqui deberia ser el uid del documento
			Document doc = domSession.getDocumentContext();
			log.debug("docAntes="+doc.toString());

			// obtengo el documento y si no, salgo con error
			try {
				doc = database.getDocumentByUnid(null,unid);
				if (doc == null) {
					response.sendError(Response.SC_NOT_FOUND, "Documento no encontrado");
					return;
				}
			} catch (DatabaseException e) {
				response.sendError(Response.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				return;
			}
			log.debug("docDespues="+doc.toString());
			
			//si tiene el anexo correspondiente lo envia
			try{
				AttachService attachService = database.getAttachService();
				Attachment attach = attachService.getAttachment(unid, unscapeUrl(urlComponents.getElName()));
	//			Item it = doc.getAttachment(unscapeUrl(urlComponents.getElName()));
	//			if (it==null){
				if (attach==null){
					response.sendError(HttpServletResponse.SC_NOT_FOUND,"No existe el anexo "+urlComponents.getElName());
					return;
				}
				if (actionName.equals("openelement")) {
					if (attach.getBytes()!=null) {
						Resource resource = new Resource();
						//TODO determinar el content type en funcion de la extension
						String mimeType = cfg.getServlet().getServletContext().getMimeType(attach.getName());
						response.setContentType(mimeType!=null?mimeType:"text/html");
						response.setHeader("content-disposition","attachment;filename="+scapeUrl(attach.getName()));
						resource.setContent(attach.getBytes());
						Dispatcher.sendResourceContent(resource, response);
					}
				} else {
					//editelement
					
					//response.setContentType("application/launch");
					//response.setHeader("content-disposition", "attachment;filename=launch.launch");
					//String url = "http://"+request.getServerName()+":"+request.getServerPort();
					//url+=urlComponents.getDbFileName()+"/temp/WEB-INF/attach/"+unid+"/"+attach.getName();
					//String res = url;
					
					//TODO A PIÑON METO EL ACTIVEX DE WScript
					//TODO Varias versiones:
					//TODO Desacoplar el tempPath y la ruta del webdav con la hubicacion del war
					//1º descargo el anexo al direcotrio temp y luego ya veo como recuperarlo I:doble guardar
					//1a la ruta comienza por el unid V:es determinista
					//1b la ruta es aleatoria I:no se como recoger el documento editado
					//		creo que se puede hacer asociando el unid a un unid aleatorio y guardando el par en cfg
					//que pasa si hay algun error al guardar? debo siempre extraer el file al temp? o no?
					//2º edito directamente el anexo V:un unico guardar I:seguridad?
					//------------------------------
					//hago la 1a
					try{
						String outputFile = cfg.getTempPath()+File.separator+domSession.getDatabase().getName() +File.separator+ unid+File.separator+attach.getName();
						FilesUtils.preparaPath(outputFile, true);
						attach.extractFile(outputFile);
						//inserto en cfg la relacion teniendo en cuenta el metodo elegido
						cfg.addEditedFile(unid, unid, outputFile);
					} catch (Exception e){
						e.printStackTrace();
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
					}
					String urlDav = "http://"+urlComponents.getServer()+urlComponents.getDbFileName()+"/webdav/temp/"+domSession.getDatabase().getName()+"/"+unid+"/"+scapeUrl(attach.getName());
					String res="";
					res+="<html><head><script>\n";
					res+="function abre(prog,url){ var shell = new ActiveXObject(\"WScript.shell\"); shell.run(prog+\" \"+url); shell.Quit; shell = null; }\n";
					res+="</script>\n";
					res+="</head><body onload='abre(\"swriter.exe "+urlDav+"\",\"\")'>\n";
					res+="</body></html>";
					log.debug("***** Sirviendo:"+res);
					
					Dispatcher.sendString(res, cfg.getResourceCharset(), response);
				}
			} catch (DatabaseException dbe){
				dbe.printStackTrace();
				response.sendError(HttpServletResponse.SC_NOT_FOUND,dbe.getMessage());
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No se ha definido correctamente el dispatcher para esta accion:"+actionName);
			return;
		}
	}
	
	private String unscapeUrl(String url) {
		try {
			return URLDecoder.decode(url,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String scapeUrl(String url) {
		String res = url.replace(" ", "%20");
		//TODO mejorar
		/*try{
			res = URLEncoder.encode(res,"utf-8");
		} catch (Exception e){
		}*/
		return res;
	}	

	private void fillFields(int actionType,HTMLProgram prg,DominoSession domSession) throws ParserException {
		Document docAct = domSession.getDocumentContext();
		String value = "";
		for(Field field:prg.getFields()){
			boolean calc=false;
			if (actionType==ProgramContext.ACTION_NEW) calc = true;
			else if (field.getItemKind()==Field.KIND_COMPUTED_FOR_DISPLAY) calc = true;
			else if ((actionType==ProgramContext.ACTION_SAVE)&&(field.getItemKind()==Field.KIND_COMPUTED)) calc = true;
			else if ((actionType==ProgramContext.ACTION_EDIT)&&(field.getItemKind()==Field.KIND_COMPUTED)) calc = true;
			
			if (calc){
				value="";
				log.debug("ComputeDefaultValue:"+field.getItemName());
				if (field.getDefaultValue()!=null) value = domSession.executeGet(field.getDefaultValue());
				docAct.setItemValue(field.getItemName(), value);
			} else {
				log.debug("ComputeDefaultValue: OMITIR "+field.getItemName());
			}
			
		}
	}

	public ServerConfig getCfg() {
		return cfg;
	}

	public void setCfg(ServerConfig cfg) {
		this.cfg = cfg;
	}
}

