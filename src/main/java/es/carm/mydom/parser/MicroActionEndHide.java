package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionEndHide extends MicroAction {

	public void compile(Languaje lang) throws ParserException {
	}

	public void execute(ProgramContext pc) throws ParserException {
		pc.setEscribe(true);
	}

}
