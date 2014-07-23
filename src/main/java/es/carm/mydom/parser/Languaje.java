package es.carm.mydom.parser;

public interface Languaje {
	public String getStartTag();
	public String getEndTag();
	public boolean isInnerXML();
	public Program instantiateProgram(); 
	public Program compile() throws ParserException;
	public void par_accion(Program prg, String txt) throws ParserException;
	public void par_text(Program prg,String txt) throws ParserException;
}
