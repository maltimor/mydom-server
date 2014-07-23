package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionHide extends MicroAction {
	final Logger log = LoggerFactory.getLogger(MicroActionHide.class);
	private BeanMethod beanMethod;
	
	public void compile(Languaje lang) throws ParserException {
		beanMethod = BeanMethod.getBeanMethod(text);
	}

	public void execute(ProgramContext pc) throws ParserException {
		log.debug("ComputedHide "+beanMethod.toString());
		if (pc.getDomSession().executeCondition(beanMethod)) pc.setEscribe(false);
	}

}
