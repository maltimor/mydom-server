package es.carm.mydom.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.Resource;



import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.Field;
import es.carm.mydom.parser.HTMLCompiler;
import es.carm.mydom.parser.HTMLProgram;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.Program;
import es.carm.mydom.parser.ProgramContext;
import es.carm.mydom.entity.AttachService;
import es.carm.mydom.entity.Attachment;
import es.carm.mydom.entity.Database;
import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.entity.DominoViewBean;
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
public class AgentAction implements HttpFilter {
	final Logger log = LoggerFactory.getLogger(AgentAction.class);
	private ServerConfig cfg;

	public void doFilter(DominoSession domSession, HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		Database database = domSession.getDatabase();
		URLComponents urlComponents = domSession.getUrlComponents();
		String actionName = urlComponents.getActionName();
		String fileName = urlComponents.getElFileName();
		String formName = urlComponents.getElName();
		String path = urlComponents.getPath();
		if (actionName.equals("openagent")){
			try {
				//si vengo de un post, actualizo los datos del request
				String form = "";
				HTMLProgram prg = null;
				Document doc = domSession.getDocumentContext();
				if (request.getMethod().equals("POST")){
					form = request.getParameter("form");
					String unid = request.getParameter("unid");
					
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
					
					
					//obtener el programa referido al documento
					Resource resource = Resources.getResource(cfg,"form",form);
					if (resource==null){
						response.sendError(HttpServletResponse.SC_NOT_FOUND,"Elemento no encontrado:"+form);
						return;
					}

					try {
						//obtengo el programa
						String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
						HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
						prg = (HTMLProgram) compiler.compile();
						
						// paso los campos del request parameter map al documento
						log.debug("docAntes="+doc.toString());
						RequestReader.rellenaCampos(request,prg,doc,actionName,cfg.getResourceCharset());
						log.debug("docDespues="+doc.toString());
					
						//Actualizo el documento de contexto
						doc.setEditMode(true);
					} catch (ParserException e){
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
						return;
					}
				}
				
				//obtengo un bean method a partir del nombre del agente
				//BUGFIX: content type
				response.setContentType("text/html");
				response.setCharacterEncoding(cfg.getResourceCharset());
				String agentName = formName;
				AgentResponse agentResponse = new AgentResponse(response);
				BeanMethod agent = new BeanMethod();
				agent.setBean(agentName+"Bean");
				agent.setMethod("doAgent");
				if (domSession.getBeans().containsKey(agent.getBean())){
					log.debug("EJECUTANDO AGENTE:"+agent.getMethod()+" MODIFIED="+agentResponse.isModified());
					boolean res = domSession.executeAgent(agent,request,agentResponse);
				} else {
					//silencio el error y devuelvo una cadena de error
					log.debug("NO SE ENCUENTRA EL AGENTE:"+agent.getMethod()+" MODIFIED="+agentResponse.isModified());
					Dispatcher.sendString("<html><body><h1>ERROR: No existe el agente "+agentName+"</h1></body></html>", cfg.getResourceCharset(), response);
					return;
				}
				
				//finalizo siempre que no se haya modificado la salida del response previamente
				log.debug("MODIFIED="+agentResponse.isModified());
				if (!agentResponse.isModified()) {
					//si vengo de un POST debo devolver la pagina donde estaba
					if (prg!=null){
						//configuro el programa con la accion openform
						ProgramContext pc = new ProgramContext(prg);
						pc.setDomSession(domSession);
						//no tengo otra forma de ver si el documento esta creado o no
						boolean create = !domSession.getDatabase().isDocumentByUnid(form, doc.getUnid());
						log.debug("OOOOOOOOOOOOOOOOOOOOOOOOO Create="+create);
						if (create) {
							pc.setActionType(ProgramContext.ACTION_NEW);
							pc.setProperty("ActionForm",urlComponents.getDbFileName()+"/"+form+"?createdocument");
						} else {
							pc.setActionType(ProgramContext.ACTION_EDIT);
							pc.setProperty("ActionForm",urlComponents.getDbFileName()+"/"+doc.getUnid()+"?savedocument");
						}
						pc.setDocAct(domSession.getDocumentContext());
						log.debug("OOOOOOOOOOOOOOOOOOOOOOOOO ActionForm="+pc.getProperty("ActionForm"));		
						//ejecuto el programa y el resultado lo saco por pantalla
						List<String> res = prg.execute(pc);
							
						log.debug("doc="+doc.toString());
						Dispatcher.sendArrayString(res,cfg.getResourceCharset(),response);	
					} else Dispatcher.sendString("<html><body><h1>Agente ok.</h1></body></html>",cfg.getResourceCharset(),response);
				}
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}			
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No se ha definido correctamente el dispatcher para esta accion:"+actionName);
			return;
		}
	}

	public ServerConfig getCfg() {
		return cfg;
	}

	public void setCfg(ServerConfig cfg) {
		this.cfg = cfg;
	}	
}
