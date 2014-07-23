package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MicroActionDoAction extends MicroAction {
	final Logger log = LoggerFactory.getLogger(MicroActionDoAction.class);
	private BeanMethod beanMethod;

	public void compile(Languaje lang) throws ParserException {
		beanMethod = BeanMethod.getBeanMethod(text);
	}

	public void execute(ProgramContext pc) throws ParserException {
		log.debug("ComputedDoAction "+beanMethod.toString());
		pc.getDomSession().executeAction(beanMethod);
	}

}
