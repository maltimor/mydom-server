package es.carm.mydom.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HexString {
	private static char[]    map1 = new char[256];
	private static char[]    map2 = new char[256];
	static {
		for (int i=0; i<256; i++){
			int a = (i>>4)&0x0f;
			int b = i&0x0f;
			if (a<10) map1[i] = (char)(a+48);
			else map1[i] = (char)(a+55);
			if (b<10) map2[i] = (char)(b+48);
			else map2[i] = (char)(b+55);
		}
	}
	
	public static char[] encode (byte[] in) {
		return encode(in,in.length);
	}

	public static char[] encode (byte[] in, int iLen) {
		int oLen = iLen*2;
//		log.debug("encode:in.len="+iLen+" out.len="+oLen);
		char[] out = new char[oLen];
		for(int j=0,i=0;i<iLen;i++){
//			log.debug("j="+j+" i="+i+" in[i]="+in[i]+" map1[in[i]="+map1[in[i]]+" map2[in[i]]="+map2[in[i]]);
			
			out[j++]=map1[(in[i]&0xff)];
			out[j++]=map2[(in[i]&0xff)];
		}
		return out;
	}
	public static byte[] decode (byte[] in) {
		return decode(in,in.length);
	}
	public static byte[] decode (byte[] in, int iLen) {
		int oLen = (1+iLen)/2;
//		log.debug("encode:in.len="+iLen+" out.len="+oLen);
		byte[] out = new byte[oLen];
		for(int j=0,i=0;i<iLen;i++){
//			log.debug("j="+j+" i="+i+" in[i]="+in[i]+" map1[in[i]="+map1[in[i]]+" map2[in[i]]="+map2[in[i]]);
			int a=in[i++]&0xff;
			int b=in[i]&0xff;
			if ((a>='0') && (a<='9')) a = a-'0';
			else if ((a>='A') && (a<='F')) a = a-'A'+10;
			else if ((a>='a') && (a<='f')) a = a-'a'+10;
			if ((b>='0') && (b<='9')) b = b-'0';
			else if ((b>='A') && (b<='F')) b = b-'A'+10;
			else if ((b>='a') && (b<='f')) b = b-'a'+10;
			out[j++]=(byte)((a*16+b)&0xff);
		}
		return out;
	}
}
