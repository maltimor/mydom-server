package es.carm.mydom.entity;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.carm.mydom.DAO.ViewDaoImpl;
import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.servlet.DominoBeanAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DominoBeanAnnotation(name="domServerBean")
public class DominoBean {
	final Logger log = LoggerFactory.getLogger(DominoBean.class);
	
	private static Object doExecute(BeanMethod beanMethod,DominoSession domSession) throws ParserException {

		switch (beanMethod.getType()){
		case BeanMethod.STRING:
		case BeanMethod.NUMBER:
			return beanMethod.getValue();
		case BeanMethod.FIELD:
			return domSession.getDocumentContext().getItemValue(beanMethod.getValue());
		case BeanMethod.BEAN:
			//si el beanMethod es de tipo bean, se prosigue con la implementacion original
			DominoBean domBean = domSession.getBean(beanMethod.getBean());
			if (domBean==null) throw new ParserException("No se encuentra el dom bean "+beanMethod.getBean());
			return domBean.aexecute(beanMethod, domSession);
		default:
			throw new ParserException("El tipo de bean no está implementado: "+beanMethod.getTxt());
		}
	}
	
	public static Object execute(BeanMethod beanMethod,DominoSession domSession) throws ParserException {
		//este es el caso generico de ejecucion
		return doExecute(beanMethod,domSession);
	}

	public static Object executeAgent(BeanMethod beanMethod,DominoSession domSession,HttpServletRequest request,HttpServletResponse response) throws ParserException {
		//este es un caso especifico de llamada, donde los parametros estan fijos
		DominoBean domBean = domSession.getBean(beanMethod.getBean());
		if (domBean==null) throw new ParserException("No se encuentra el dom bean "+beanMethod.getBean());
		return (Boolean) domBean.aexecuteAgent(beanMethod, domSession, request, response);
	}
	
	public static Object executeGetColumn(BeanMethod beanMethod,DominoSession domSession,Document doc) throws ParserException {
		//este es un caso especifico de llamada, donde los parametros estan fijos
		DominoBean domBean = domSession.getBean(beanMethod.getBean());
		if (domBean==null) throw new ParserException("No se encuentra el dom bean "+beanMethod.getBean());
		return (String) domBean.aexecuteGetColumn(beanMethod, domSession, doc);
	}

	public Object aexecute(BeanMethod beanMethod,DominoSession domSession) throws ParserException {
		String method = beanMethod.getMethod();
		Method m = getMethod(method);
		if (m==null) throw new ParserException("No se encuentra el metodo "+method+" de la clase "+this.getClass().getName());
		try {
			return m.invoke(this, domSession);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParserException("Error al invocar el metodo "+method+" de la clase "+this.getClass().getName());
		}
	}
	
	public Object aexecuteAgent(BeanMethod beanMethod,DominoSession domSession,HttpServletRequest request,HttpServletResponse response) throws ParserException {
		String method = beanMethod.getMethod();
		Method m = getMethodAgent(method);
		if (m==null) throw new ParserException("No se encuentra el metodo(agente) "+method+" de la clase "+this.getClass().getName());
		try {
			return m.invoke(this, domSession,request,response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParserException("Error al invocar el metodo(agente) "+method+" de la clase "+this.getClass().getName());
		}
	}

	public Object aexecuteGetColumn(BeanMethod beanMethod,DominoSession domSession,Document doc) throws ParserException {
		String method = beanMethod.getMethod();
		Method m = getMethodColumn(method);
		if (m==null) throw new ParserException("No se encuentra el metodo(column) "+method+" de la clase "+this.getClass().getName());
		try {
			return m.invoke(this, domSession,doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParserException("Error al invocar el metodo(column) "+method+" de la clase "+this.getClass().getName());
		}
	}

	private Method getMethod(String name){
		try {
			return this.getClass().getMethod(name, DominoSession.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Method getMethodAgent(String name){
		try {
			return this.getClass().getMethod(name, DominoSession.class, HttpServletRequest.class, HttpServletResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Method getMethodColumn(String name){
		try {
			return this.getClass().getMethod(name, DominoSession.class, Document.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String empty(DominoSession domSession) throws ParserException {
		return "";
	}
}
