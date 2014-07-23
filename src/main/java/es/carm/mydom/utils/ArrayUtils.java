package es.carm.mydom.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ArrayUtils {
	public static String toStr(String[] str){
		String res = "("+str.length+") [";
		for(int i=0;i<str.length;i++) res+=str[i]+"|";
		res+="]";
		return res;
	}
	
	public static String toStr(String[] str, String separador){
		String res = str[0];
		for(int i=1;i<str.length;i++) res+=separador+str[i];
		return res;
	}
}
