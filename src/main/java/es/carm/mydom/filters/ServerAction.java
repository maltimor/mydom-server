package es.carm.mydom.filters;

import java.io.IOException;
import java.io.Writer;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.Resource;

import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.HTMLCompiler;
import es.carm.mydom.parser.Field;
import es.carm.mydom.parser.HTMLProgram;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.Program;
import es.carm.mydom.parser.ProgramContext;
import es.carm.mydom.DAO.LotusDaoImpl;
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
public class ServerAction implements HttpFilter {
	final Logger log = LoggerFactory.getLogger(ServerAction.class);
	private ServerConfig cfg;

	public void doFilter(DominoSession domSession, HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		Database database = domSession.getDatabase();
		URLComponents urlComponents = domSession.getUrlComponents();
		String actionName = urlComponents.getActionName();
		String fileName = urlComponents.getElFileName();
		String formName = urlComponents.getElName();
		String path = urlComponents.getPath();
		if (actionName.equals("openaction")){
			try {
				//obtengo un bean method a partir de la url
				String uidAction = formName;
				AgentResponse agentResponse = new AgentResponse(response);
				BeanMethod beanMethod = cfg.getActions().get(uidAction);
				if (beanMethod!=null){
					log.debug("EJECUTANDO ACTION:"+beanMethod.getMethod()+" MODIFIED="+agentResponse.isModified());
					boolean res = domSession.executeAgent(beanMethod,request,agentResponse);
				} else {
					//silencio el error y devuelvo una cadena de error
					log.debug("NO SE ENCUENTRA LA ACCION:"+beanMethod.getMethod()+" MODIFIED="+agentResponse.isModified());
					Dispatcher.sendString("<html><body><h1>ERROR: No existe la accion "+uidAction+"</h1></body></html>",cfg.getResourceCharset(), response);
					return;
				}
				
				//finalizo siempre que no se haya modificado la salida del response previamente
				log.debug("MODIFIED="+agentResponse.isModified());
				if (!agentResponse.isModified()) {
					Dispatcher.sendString("<html><body><h1>Action ok.</h1></body></html>",cfg.getResourceCharset(), response);
				}
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("getcreatetable")){
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

				Writer out = response.getWriter();
				out.append("CREATE TABLE "+fileName.substring(1)+" (\n");
				out.append("\tunid varchar2(32),\n");
				out.append("\tform varchar2(200),\n");
				String listaExclusion = ((LotusDaoImpl) database).getListaExclusion();
				boolean first = true;
				for(Field field:prg.getFields()){
					if ((!listaExclusion.contains(","+field.getItemName().toLowerCase()+","))&&
						(field.getItemKind()!=Field.KIND_COMPUTED_FOR_DISPLAY)){
						if (!first) out.append(",\n");
						first = false;
						String type = field.getItemType();
						if (type.equals("text")) type = "varchar2(4000)";
						else if (type.equals("number")) type = "number";
						else if (type.equals("datetime")) type = "date";
						else if (type.equals("richtext")) type = "clob";
						else if (type.equals("keyword")) type = "varchar2(4000)";
	
						String str = "\t"+field.getItemName()+" "+type;
						out.append(str);
					}
				}
				out.append("\n);\n");
			} catch (ParserException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
				return;
			}
		} else if (actionName.equals("getallcreatetable")){
			//Acceso al elemento
			//dado el nombre completo obtener el elemento.
			Writer out = response.getWriter();
			try {
				NamingEnumeration<NameClassPair> ne = cfg.getDirContext().list("/form");
				while (ne.hasMore()){
					NameClassPair nc=ne.next();
					String form = nc.getName();
					if (form.contains(".html")) form=form.substring(0,form.indexOf(".html"));
					log.debug("----"+form);

					Resource resource = Resources.getResource(cfg,"form",form);
					if (resource!=null){
						//obtengo el programa
						String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
						HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
						HTMLProgram prg = (HTMLProgram) compiler.compile();
						out.append("CREATE TABLE "+form.replace(" ","_")+" (\n");
						out.append("\tunid varchar2(32),\n");
						out.append("\tform varchar2(200),\n");
						String listaExclusion = ((LotusDaoImpl) database).getListaExclusion();
						boolean first = true;
						for(Field field:prg.getFields()){
							if ((!listaExclusion.contains(","+field.getItemName().toLowerCase()+","))&&
								(field.getItemKind()!=Field.KIND_COMPUTED_FOR_DISPLAY)){
								if (!first) out.append(",\n");
								first = false;
								String type = field.getItemType();
								if (type.equals("text")) type = "varchar2(4000)";
								else if (type.equals("number")) type = "number";
								else if (type.equals("datetime")) type = "date";
								else if (type.equals("richtext")) type = "clob";
								else if (type.equals("keyword")) type = "varchar2(4000)";
			
								String str = "\t"+field.getItemName()+" "+type;
								out.append(str);
							}
						}
						out.append("\n);\n");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (actionName.equals("getallesquemas")){
			//Acceso al elemento
			//dado el nombre completo obtener el elemento.
			Writer out = response.getWriter();
			try {
				NamingEnumeration<NameClassPair> ne = cfg.getDirContext().list("/form");
				while (ne.hasMore()){
					NameClassPair nc=ne.next();
					String form = nc.getName();
					if (form.contains(".html")) form=form.substring(0,form.indexOf(".html"));
					log.debug("----"+form);

					Resource resource = Resources.getResource(cfg,"form",form);
					if (resource!=null){
						//obtengo el programa
						String value = Resources.getStringFromStream(resource,cfg.getResourceCharset());
						HTMLCompiler compiler = new HTMLCompiler(value,cfg.getResourceCharset(),cfg.getDirContext());
						HTMLProgram prg = (HTMLProgram) compiler.compile();
						out.append(form);
						out.append("=");
						String listaExclusion = ((LotusDaoImpl) database).getListaExclusion();
						out.append("|UNID#T#4000|FORM#T#4000|");
						for(Field field:prg.getFields()){
							if ((!listaExclusion.contains(","+field.getItemName().toLowerCase()+","))&&
								(field.getItemKind()!=Field.KIND_COMPUTED_FOR_DISPLAY)){
								out.append(field.getItemName());
								out.append("#");
								String type = field.getItemType();
								if (type.equals("text")) type = "T#4000";
								else if (type.equals("number")) type = "N#";
								else if (type.equals("datetime")) type = "F#";
								else if (type.equals("richtext")) type = "R#";
								else if (type.equals("keyword")) type = "T#4000";

								out.append(type);
								out.append("|");
							}
						}
						out.append("\n");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
