package es.carm.mydom.parser;

public abstract class MicroAction {
	protected String text;
	protected MicroAction(){
	}
//	public MicroAction(String text) throws ParserException{
//		this.text = text;
//		compile();
//	}
	public static MicroAction instance(Class<?> clazz,String text, Languaje lang) throws ParserException{
		try{
			MicroAction res = (MicroAction) clazz.newInstance();
			res.text = text;
			res.compile(lang);
			return res;
		} catch (Exception e){
			e.printStackTrace();
			throw new ParserException(e.getMessage());
		}
	}
	public abstract void compile(Languaje lang) throws ParserException;
	public abstract void execute(ProgramContext pc) throws ParserException;
}


