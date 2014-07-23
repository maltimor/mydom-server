package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Token {
	public static final int CON_ERROR=-1;
	public static final int SIN_VALUE=0;
	public static final int SIN_COMILLAS=1;
	public static final int CON_COMILLAS_SIMPLES=2;
	public static final int CON_COMILLAS_DOBLES=3;
	private String token;
	private String key;
	private String value;
	private int valueType;
	
	public Token(String token){
		setToken(token);
		//log.debug("NewToken:"+token+" -> "+this.valueType+" "+this.toString());
	}
	public void setToken(String token) {
		this.token = token;
		calcParams();
	}
	
	private void calcParams(){
		int i1 = token.indexOf("=");
		int i2 = token.indexOf("'");
		int i3 = token.indexOf("\"");
		if (i1>0){
			if (i1+1==i2){
				key = token.substring(0,i1);
				value = token.substring(i1+2,token.length()-1);
				valueType = CON_COMILLAS_SIMPLES;
			} else if (i1+1==i3) {
				key = token.substring(0,i1);
				value = token.substring(i1+2,token.length()-1);
				valueType = CON_COMILLAS_DOBLES;
			} else if ((i2==-1)&&(i3==-1)){
				key = token.substring(0,i1);
				value = token.substring(i1+1,token.length());
				valueType = SIN_COMILLAS;
			} else {
				key = token.substring(0,i1);
				value = token.substring(i1+1,token.length());
				valueType = CON_ERROR;
			}
		} else {
			//no hay key=value
			this.valueType=SIN_VALUE;
			this.key = token;
			this.value = "";
		}
	}
	
	public String toString(){
		String res = "";
		switch(valueType){
		case CON_ERROR:
			res = token;
			break;
		case SIN_VALUE:
			res = key;
			break;
		case SIN_COMILLAS:
			res = key + "=" + value;
			break;
		case CON_COMILLAS_SIMPLES:
			res = key + "='" + value + "'";
			break;
		case CON_COMILLAS_DOBLES:
			res = key + "=\"" + value + "\"";
			break;
		}
		return res;
	}
	
	public String getToken() {
		return token;
	}
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public int getValueType() {
		return valueType;
	}
}
