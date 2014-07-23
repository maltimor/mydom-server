package es.carm.mydom.parser;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.carm.mydom.filters.ResourceAction;
import es.carm.mydom.filters.utils.Resources;

public abstract class BasicCompiler implements Languaje{
	final Logger log = LoggerFactory.getLogger(BasicCompiler.class);
	private String value;
	private String charset;
	private int len;
	private int pos;
	private int posFinAction;

	public BasicCompiler(String value,String charset){
		log.debug("BasicCompiler:"+this.getClass().getName());
		this.value = value;
		this.charset = charset;
	}
	
	
	/*
	 * Recorre la cadena de texo en busca de patrones <$ $>
	 */
	public Program compile() throws ParserException {
		Program prg = instantiateProgram();
		long initTime = System.currentTimeMillis();
		len = value.length();
		pos = 0;
		String txt = "";
		String startTag = getStartTag();
		String endTag = getEndTag();
		int startTagLen = startTag.length();
		int endTagLen = endTag.length();
		while (pos < len) {
			int i1 = value.indexOf(startTag, pos);
			//log.debug("Compiler.i1="+i1+" pos="+pos);
			if (i1 >= pos) {
				txt = value.substring(pos, i1);
				//log.debug("Compiler.txt="+txt);
				par_text(prg,txt);
				int i2 = value.indexOf(endTag, i1+startTagLen);
				//log.debug("Compiler.i2="+i2+" i1+sl="+(i1+startTagLen));
				if (i2 >= i1 + startTagLen) {
					// obtengo un comando y lo proceso y avanzo el cursor
					int i3 = value.indexOf(startTag, i1 + startTagLen);
					//log.debug("Compiler.i3="+i3+" i1+sl="+(i1+startTagLen));
					if ((i3 < i2) && (i3 > pos)) {
						// este es otro error
						txt = value.substring(i1, i3);
						//log.debug("Compiler.txt="+txt);
						par_text(prg, txt);
						pos = i3;
					} else {
						// por fin un comando correcto. obtengo el comando entero
						txt = value.substring(i1 + startTagLen, i2);
						//actualizo la posicion del puntero, antes de llamar a la accion, para permitir bucles
						//posIniAction = i1;
						//log.debug("Compiler.action="+txt);
						posFinAction = i2+endTagLen;
						pos = posFinAction;
						par_accion(prg, txt);
						//ahora si que avanzo el comando
						//NOTA dejo en manos de par_accion el mover el puntero
						//pos = i2 + 2;
					}
				} else {
					// esto deberia ser un error, no hago nada y avanzo el
					// cursor
					pos = i1 + startTagLen;
				}
			} else {
				// se termino de buscar
				txt = value.substring(pos);
				//log.debug("Compiler.txt="+txt);
				par_text(prg,txt);
				pos = len;
			}
		}
		long endTime = System.currentTimeMillis();
		log.debug("Compiler.compile:"+((endTime-initTime)/1000.0)+" seg.");
		return prg;
	}


	protected String getValue() {
		return value;
	}


	protected void setValue(String value) {
		this.value = value;
	}


	public String getCharset() {
		return charset;
	}


	public void setCharset(String charset) {
		this.charset = charset;
	}

}
