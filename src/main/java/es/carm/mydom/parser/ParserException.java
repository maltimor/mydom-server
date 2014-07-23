package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ParserException extends Exception {
	private static final long serialVersionUID = -2162438715354471617L;

	public ParserException() {
		super();
	}

	public ParserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ParserException(String arg0) {
		super(arg0);
	}

	public ParserException(Throwable arg0) {
		super(arg0);
	}
	
}
