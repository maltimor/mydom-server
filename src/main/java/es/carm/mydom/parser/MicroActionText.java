package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionText extends MicroAction {

//	public MicroActionText(String text) throws ParserException {
//		super(text);
//	}

	public void compile(Languaje lang) throws ParserException {
	}

	public void execute(ProgramContext pc) throws ParserException {
		pc.append(text);
	}
}
