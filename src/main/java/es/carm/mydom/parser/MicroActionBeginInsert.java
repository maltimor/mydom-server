package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionBeginInsert extends MicroAction {
	final Logger log = LoggerFactory.getLogger(MicroActionBeginInsert.class);
	private String name;

	public void compile(Languaje lang) throws ParserException {
		this.name = text;
		log.debug("###BEGIN INSERT:"+name);
	}

	public void execute(ProgramContext pc) throws ParserException {
		pc.setInsertPoint(name);
	}

}
