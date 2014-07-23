package es.carm.mydom.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class DominoAgentBean extends DominoBean {
	public abstract boolean doAgent(DominoSession domSession, HttpServletRequest request, HttpServletResponse response);
}
