package es.carm.mydom.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class BeanMethod {
	public static final int BEAN=0;
	public static final int STRING=1;
	public static final int NUMBER=2;
	public static final int FIELD=3;
	public static final int LIST=4;
	public static final int BEANLIST=5;
	
	private int type;
	private String value;
	private String bean;
	private String method;
	private List<BeanMethod> list;
	private String txt;
	
	public BeanMethod(){
		this.type=BEAN;
		this.list = new ArrayList<BeanMethod>();
		this.value="";
		this.bean="";
		this.method="";
		this.txt="";
	}
	
	public static BeanMethod getBeanMethod(String text) throws ParserException{
		//este metodo procesa el texto y lo transforma en campos de la clase
		Env env = new Env(text);
		//System.out.println("PARSE INI:"+env.toString());
		BeanMethod bm = par_beanMethod(env);
		bm.setTxt(text);
		//System.out.println("PARSE RES:"+bm.toString());
		return bm;
/*		BeanMethod res = new BeanMethod();
		//BeanMethod -> valor | array | bean '.' method ( '(' valor? (',' valor)* ')' )?
		//valor -> "literal" | numero | campoDoc
		//array -> '[' valor? (',' valor)* ']'
		
		//si empieza por " asumo que es un literal
		//si empieza por digito es un numero
		//si empieza por [ es un array
		//si es literal debo 
/*
		//literal
		char act = text.charAt(0);
		if (act=='"') {
			res.setType(STRING);
			//avanzo hasta el final y elimino los caracteres "
			if (text.endsWith("\"")) res.setValue(text.substring(1,text.length()-2));
			else res.setValue(text.substring(1));
			return res;
		}

		//array
		if (text.startsWith("[")) {
		}
		
		//ver si es beanMethod
		int i1=text.indexOf(".");
		
			
		}
		
		
*/		
/*		int i1 = text.indexOf(".");
		if (i1<0) throw new ParserException("Esperaba un '.' :"+text);
		res.setBean(text.substring(0,i1));
		res.setMethod(text.substring(i1+1));
		return res;*/
	}
	
	private static BeanMethod par_beanMethod(Env env) throws ParserException{
		BeanMethod res = new BeanMethod();
		char act = env.getAct();
		//caso de literal
		if (act=='"'){
			res.setType(STRING);
			env.incPos(1);		//salto el "
			int apos = env.getPos();
			int i1 = env.getText().indexOf("\"",apos);
			if (i1>apos){
				res.setValue(env.getText().substring(apos,i1));
				env.setPos(i1+1);		//salto el "
			} else {
				//he llegado hasta el final, esto sería un error, pero no es muy grave o si?
				res.setValue(env.getText().substring(apos));
				env.setPos(env.getTam());
			}
			//System.out.println("Literal:"+res.getValue());
			return res;
		}
		
		//caso de numero
		if ((act>='0')&&(act<='9')){
			res.setType(NUMBER);
			String aux = "";
			while ((!env.isEof())&&(act>='0')&&(act<='9')){
				aux+=act;
				env.incPos(1);
				act = env.getAct();
			}
			res.setValue(aux);
			//System.out.println("Numero:"+res.getValue());
			return res;
		}
		
		//caso de array
		if (act=='['){
			res.setType(LIST);
			env.incPos(1);		//salto el [
			res.setList(par_listBeanMethod(env));
			if (env.getAct()==']') env.incPos(1);		//salto el ]
			//System.out.println("Array:"+res.getList().size());
			return res;
		}
		
		//caso de campo o beanMethod
		String aux = "";
		while ((!env.isEof())&&(",()[]".indexOf(act)<0)){
			aux+=act;
			env.incPos(1);
			act = env.getAct();
		}
		//System.out.println("aux:"+aux);
		
		//determino si es bean o campos
		if (aux.contains(".")){
			res.setType(BEAN);
			int i1 = aux.indexOf(".");
			if (i1<0) throw new ParserException("Esperaba un '.' :"+env.getText()+" "+env.getPos()+" "+aux);
			res.setBean(aux.substring(0,i1));
			res.setMethod(aux.substring(i1+1));
			//ver si tiene lista de parametros
			if (act=='('){
				res.setType(BEANLIST);
				env.incPos(1);		//salto el (
				res.setList(par_listBeanMethod(env));
				if (env.getAct()==')') env.incPos(1);		//salto el )
			}
			//System.out.println("Bean:"+res.getBean()+" method:"+res.getMethod());
			return res;
		} else {
			res.setType(FIELD);
			res.setValue(aux);
			//ver si tiene array de parametros
			if (act=='['){
				res.setType(BEANLIST);
				env.incPos(1);		//salto el (
				res.setList(par_listBeanMethod(env));
				if (env.getAct()==']') env.incPos(1);		//salto el ]
			}
			//System.out.println("Field:"+res.getValue());
			return res;
		}
		//no hay que hacer return pues ya se han evaluado todos los casos
	}
	
	private static List<BeanMethod> par_listBeanMethod(Env env) throws ParserException {
		List<BeanMethod> res = new ArrayList<BeanMethod>();
		char act = env.getAct();
		if ((act==')')||(act==']')||(env.isEof())) return res;
		
		do {
			res.add(par_beanMethod(env));
			act = env.getAct();
			if (act==',') env.incPos(1);
		} while ((act==',')&&(!env.isEof()));
		
		return res;
	}
	
	public String toString(){
		String txt = "L["+list.size()+"]=";
		int i=0;
		for(BeanMethod bm:list){
			txt+="("+i+")="+bm.toString();
		}
		
		return "BM="+type+"|"+value+"|"+bean+"."+method+"|"+txt;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type=type;
	}
	
	public String getBean() {
		return bean;
	}
	public void setBean(String bean) {
		this.bean = bean;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<BeanMethod> getList() {
		return list;
	}

	public void setList(List<BeanMethod> list) {
		this.list = list;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}
	
	
}

class Env{
	private String text;
	private String utext;
	private int pos;
	private char act;
	private int tam;
	private boolean eof;
	public Env(String text){
		this.text = text;
		this.utext = text.toUpperCase();
		this.tam = text.length();
		this.pos = 0;
		this.act = text.charAt(pos);
	}
	public void incPos(int delta) {
		this.pos += delta;
		if (pos>=tam) eof=true;
		else act = text.charAt(pos);
	}
	public void setPos(int pos) {
		this.pos = pos;
		if (pos>=tam) eof=true;
		else act = text.charAt(pos);
	}
	public int getPos(){
		return pos;
	}
	public char getAct() {
		return act;
	}
	public int getTam() {
		return tam;
	}
	public boolean isEof() {
		return eof;
	}
	public String getText() {
		return text;
	}
	public String getUtext() {
		return utext;
	}
	
	public String toString(){
		return "ENV="+text+"|"+pos+"|"+act;
	}
	
}
