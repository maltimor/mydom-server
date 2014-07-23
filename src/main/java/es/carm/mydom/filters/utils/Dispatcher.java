package es.carm.mydom.filters.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.apache.naming.resources.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Dispatcher {
	public static void sendResourceContent(Resource resource,HttpServletResponse response){
		try{
			OutputStream o = response.getOutputStream();
			o.write(resource.getContent());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void sendResourceStream(String fileName, Resource resource,HttpServletResponse response){
		try{
			//o.write(resource.getContent());
			
			//detecto la longitud
			InputStream in = resource.streamContent();
			System.out.println("Send resource stream: fname="+fileName+" available="+in.available());
			response.setContentLength(in.available());
			
			OutputStream o = response.getOutputStream();
			int b = 1;
			byte[] buff = new byte[16384];
			while (b>0){
				b = in.read(buff);
				if (b>0) o.write(buff,0,b);
			}
			in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sendString(String text,String charset,HttpServletResponse response){
		sendString(text,"text/html",charset,response);
	}

	public static void sendString(String text,String contentType, String charset,HttpServletResponse response){
		try{
			response.setContentType(contentType);
			response.setCharacterEncoding(charset);
			OutputStream o = response.getOutputStream();
			o.write(text.getBytes(charset));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sendArrayString(List<String> text,String charset,HttpServletResponse response){
		sendArrayString(text,"text/html",charset,response);
	}
	public static void sendArrayString(List<String> text,String contentType, String charset,HttpServletResponse response){
		try{
			response.setContentType(contentType);
			response.setCharacterEncoding(charset);
			OutputStream o = response.getOutputStream();
			for(String item:text) o.write(item.getBytes(charset));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
