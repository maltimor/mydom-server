package es.carm.mydom.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Arguments {
	public static List<String> getTokenList(String text){
		ArrayList<String> res = new ArrayList<String>();
		int pos = 0;
		int len = text.length();
		while (pos<len){
			while (text.startsWith(" ", pos)) pos++;
			int i1 = pos;
			int i2 = text.indexOf(" ", pos);
			if (i2>0) pos = i1+1;
			else pos = len;
			res.add(text.substring(i1+1, pos-1));
		}
		return res;
	}
	
	public static String getKey(String text){
		String key = text;
		int i3 = key.indexOf("=");
		int i4 = key.indexOf("'");
		int i5 = key.indexOf("\"");
		if ((i3<i4)&&(i3<i5)) key = key.substring(0,i3-1);
		return key;
	}
	public static String getValue(String text){
		String key = text;
		String value = "";
		int i3 = key.indexOf("=");
		int i4 = key.indexOf("'");
		int i5 = key.indexOf("\"");
		if ((i3<i4)&&(i3<i5)){
			if ((i3+1==i4)||(i3+1==i5)) value = key.substring(i3+2,key.length()-1);
			else value = key.substring(i3+1);
		}
		return value;
	}

	public static Map<String,Token> getTokenMap(String text){
		HashMap<String,Token> res = new HashMap<String,Token>();
		
		int pos = 0;
		int posIni = 0;
		int len = text.length();
		while (pos<len){
			//avanzo quitando espacios en blanco
			while (text.startsWith(" ", pos)) pos++;
			posIni = pos;
			//
			int i1 = text.indexOf(" ", pos);
			int i2 = text.indexOf("'", pos);
			int i3 = text.indexOf("\"",pos);
			boolean hayEspacio = (i1>=0);
			boolean hayComillas = (i2>=0)||(i3>=0);
			
			if (!hayEspacio) {
				Token tok = new Token(text.substring(posIni,len));
				res.put(tok.getKey().toLowerCase(), tok);
				pos = len;
			} else if (!hayComillas || ((i1<i2)&&(i1<i3))) {
				Token tok = new Token(text.substring(posIni,i1));
				res.put(tok.getKey().toLowerCase(), tok);
				pos = i1+1;
			} else {
				//hay comillas, determinar cual es la que se esta usando, es la menor que está entre pos e i1
				//avanzar hasta el siguiente ' o "
				String ch = null;
				int ich = -1;
				if ((i2>=0)&&(i3>=0)){
					ch=(i2<i3)?"'":"\"";
					ich =(i2<i3)?i2:i3;
				} else if (i2>=0){
					ch = "'";
					ich = i2;
				} else {
					ch = "\"";
					ich = i3;
				}
				int i4 = text.indexOf(ch,ich+1);
				if (i4>0) {
					Token tok = new Token(text.substring(posIni,i4+1));
					res.put(tok.getKey().toLowerCase(), tok);
					pos = i4+2;
				} else {
					//error pero dejo el token como esta
					Token tok = new Token(text.substring(posIni,len));
					res.put(tok.getKey().toLowerCase(), tok);
					pos = len;
				}
			}
		}
		return res;
	}
	
	public static String getValueAndRemoveKey(Map<String,Token> prp, String key,String defaultValue){
		Token tok = prp.remove(key);
		return tok==null?defaultValue:tok.getValue();
	}
	
	public static String getParameterString(Map<String,Token> map){
		String res = "";
		for(String key:map.keySet()){
			Token tok = map.get(key);
			res = res + " " + tok.toString();
		}
		return res;
	}
	
	public static String scapeHTML(String cad){
		return cad.replaceAll("&", "&amp;")
		.replaceAll("<", "&lt;")
		.replaceAll(">", "&gt;")
		.replaceAll("\"", "&quot;");
	}
}
