package es.carm.mydom.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class UNIDGenerator {
	public static String getUNID(){
		String input = "-"+System.currentTimeMillis()+"-";
		try {
			byte[] res = MessageDigest.getInstance("MD5").digest(input.getBytes());
			String sres = new String(HexString.encode(res));
			Thread.currentThread().sleep(5);
			return sres;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
