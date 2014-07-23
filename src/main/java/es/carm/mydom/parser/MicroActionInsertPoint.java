package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionInsertPoint extends MicroAction {
	private String name;

	@Override
	public void compile(Languaje lang) throws ParserException {
		this.name = text;
	}

	@Override
	public void execute(ProgramContext pc) throws ParserException {
		pc.addInsertPoint(name);
	}

}
