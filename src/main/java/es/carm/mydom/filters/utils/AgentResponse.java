package es.carm.mydom.filters.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.carm.mydom.filters.ViewAction;
public class AgentResponse extends HttpServletResponseWrapper {
	final Logger log = LoggerFactory.getLogger(AgentResponse.class);
	private boolean modified;

	public AgentResponse(HttpServletResponse response) {
		super(response);
		modified = false;
	}
	
	public boolean isModified(){
		return modified;
	}

	@Override
	public void addCookie(Cookie cookie) {
		modified = true; log.debug("MODIFIED");
		super.addCookie(cookie);
	}

	@Override
	public void addDateHeader(String name, long date) {
		modified = true; log.debug("MODIFIED");
		super.addDateHeader(name, date);
	}

	@Override
	public void addHeader(String name, String value) {
		modified = true; log.debug("MODIFIED");
		super.addHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		modified = true; log.debug("MODIFIED");
		super.addIntHeader(name, value);
	}

	@Override
	public String encodeRedirectURL(String url) {
		modified = true; log.debug("MODIFIED");
		return super.encodeRedirectURL(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		modified = true; log.debug("MODIFIED");
		return super.encodeRedirectUrl(url);
	}

	@Override
	public String encodeURL(String url) {
		modified = true; log.debug("MODIFIED");
		return super.encodeURL(url);
	}

	@Override
	public String encodeUrl(String url) {
		modified = true; log.debug("MODIFIED");
		return super.encodeUrl(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		modified = true; log.debug("MODIFIED");
		super.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws IOException {
		modified = true; log.debug("MODIFIED");
		super.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		modified = true; log.debug("MODIFIED");
		super.sendRedirect(location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		modified = true; log.debug("MODIFIED");
		super.setDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		modified = true; log.debug("MODIFIED");
		super.setHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		modified = true; log.debug("MODIFIED");
		super.setIntHeader(name, value);
	}

	@Override
	public void setStatus(int sc, String sm) {
		modified = true; log.debug("MODIFIED");
		super.setStatus(sc, sm);
	}

	@Override
	public void setStatus(int sc) {
		modified = true; log.debug("MODIFIED");
		super.setStatus(sc);
	}

	@Override
	public void flushBuffer() throws IOException {
		modified = true; log.debug("MODIFIED");
		super.flushBuffer();
	}

	@Override
	public void reset() {
		modified = true; log.debug("MODIFIED");
		super.reset();
	}

	@Override
	public void resetBuffer() {
		modified = true; log.debug("MODIFIED");
		super.resetBuffer();
	}

	@Override
	public void setBufferSize(int size) {
		modified = true; log.debug("MODIFIED");
		super.setBufferSize(size);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		modified = true; log.debug("MODIFIED");
		super.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		modified = true; log.debug("MODIFIED");
		super.setContentLength(len);
	}

	@Override
	public void setContentType(String type) {
		modified = true; log.debug("MODIFIED");
		super.setContentType(type);
	}

	@Override
	public void setLocale(Locale loc) {
		modified = true; log.debug("MODIFIED");
		super.setLocale(loc);
	}

	@Override
	public void setResponse(ServletResponse response) {
		modified = true; log.debug("MODIFIED");
		super.setResponse(response);
	}

	@Override
	public boolean containsHeader(String name) {
		modified = true; log.debug("MODIFIED");
		return super.containsHeader(name);
	}

	@Override
	public String getHeader(String name) {
		modified = true; log.debug("MODIFIED");
		return super.getHeader(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		modified = true; log.debug("MODIFIED");
		return super.getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		modified = true; log.debug("MODIFIED");
		return super.getHeaders(name);
	}

	@Override
	public int getStatus() {
		modified = true; log.debug("MODIFIED");
		return super.getStatus();
	}

	@Override
	public int getBufferSize() {
		modified = true; log.debug("MODIFIED");
		return super.getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		modified = true; log.debug("MODIFIED");
		return super.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		modified = true; log.debug("MODIFIED");
		return super.getContentType();
	}

	@Override
	public Locale getLocale() {
		modified = true; log.debug("MODIFIED");
		return super.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		modified = true; log.debug("MODIFIED");
		return super.getOutputStream();
	}

	@Override
	public ServletResponse getResponse() {
		modified = true; log.debug("MODIFIED");
		return super.getResponse();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		modified = true; log.debug("MODIFIED");
		return super.getWriter();
	}
	
}