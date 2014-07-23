package es.carm.mydom.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.carm.mydom.entity.DominoSession;

public interface HttpFilter {
	public void doFilter(DominoSession domSession,HttpServletRequest req, HttpServletResponse res) throws IOException,ServletException;
	public void setCfg(ServerConfig cfg);
}
