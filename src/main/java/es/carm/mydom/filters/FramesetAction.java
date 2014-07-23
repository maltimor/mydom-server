package es.carm.mydom.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.Resource;

import es.carm.mydom.parser.HTMLCompiler;
import es.carm.mydom.parser.Field;
import es.carm.mydom.parser.HTMLProgram;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.Program;
import es.carm.mydom.parser.ProgramContext;
import es.carm.mydom.DAO.DocumentTableDAO;
import es.carm.mydom.entity.Database;
import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.filters.utils.AgentResponse;
import es.carm.mydom.filters.utils.Dispatcher;
import es.carm.mydom.filters.utils.Resources;
import es.carm.mydom.servlet.HttpFilter;
import es.carm.mydom.servlet.ServerConfig;
import es.carm.mydom.utils.URLComponents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class FramesetAction implements HttpFilter {
	final Logger log = LoggerFactory.getLogger(FramesetAction.class);
	private ServerConfig cfg;

	public void doFilter(DominoSession domSession, HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		Database database = domSession.getDatabase();
		URLComponents urlComponents = domSession.getUrlComponents();
		String actionName = urlComponents.getActionName();
		String fileName = urlComponents.getElFileName();
		String formName = urlComponents.getElName();
		String path = urlComponents.getPath();
		if (actionName.equals("openframeset")){
			//Acceso al elemento
			//dado el nombre completo obtener el elemento.
			Resource resource = Resources.getResource(cfg,"frameset",fileName);
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
				// TODO El documento de contexto en este caso son las variables CGI
				Document doc = domSession.getDocumentContext();
				doc.setEditMode(false);
				doc.setForm(formName);		//TODO limitacion de formularios a un unico directorio
				fillFields(ProgramContext.ACTION_NEW,prg,domSession);	//Obligo a calcular todos los defaults-values
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
				
				//configuro el programa con la accion openview
				ProgramContext pc = new ProgramContext(prg);
				pc.setDomSession(domSession);
				pc.setActionType(ProgramContext.ACTION_READ);		//actuo como si fuera un read
				pc.setDocAct(domSession.getDocumentContext());
					
				//ejecuto el programa y el resultado lo saco por pantalla
				List<String> res = prg.execute(pc);
				
				log.debug("doc="+doc.toString());
				Dispatcher.sendArrayString(res,cfg.getResourceCharset(),response);
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}			
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No se ha definido correctamente el dispatcher para esta accion:"+actionName);
			return;
		}
	}
	
	private void fillFields(int actionType,HTMLProgram prg,DominoSession domSession) throws ParserException {
		Document docAct = domSession.getDocumentContext();
		String value = "";
		for(Field field:prg.getFields()){
			boolean calc=false;
			if (actionType==ProgramContext.ACTION_NEW) calc = true;
			else if (field.getItemKind()==Field.KIND_COMPUTED_FOR_DISPLAY) calc = true;
			else if ((actionType==ProgramContext.ACTION_SAVE)&&(field.getItemKind()==Field.KIND_COMPUTED)) calc = true;
			
			if (calc){
				value="";
				if (field.getDefaultValue()!=null) value = domSession.executeGet(field.getDefaultValue());
				docAct.setItemValue(field.getItemName(), value);
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
