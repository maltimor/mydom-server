package es.carm.mydom.filters.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import es.carm.mydom.entity.Attachment;
import es.carm.mydom.entity.Document;
import es.carm.mydom.parser.HTMLProgram;
import es.carm.mydom.parser.ParserException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class RequestReader {
	final static Logger log = LoggerFactory.getLogger(RequestReader.class);
	public static void rellenaCampos(HttpServletRequest request, HTMLProgram prg, Document doc, String actionName, String charset) throws ParserException{
		log.debug("======================== RELLENA CAMPOS1 ==========");
		//primero intento ver si hay parts
		Collection<Part> parts = null;
		boolean hayParts = false;
		try{
			parts = request.getParts();
			hayParts = true;
		} catch (Exception e){ }

		//TODO Ver si coincide los fileds del programa con los parametros del formulario.
		//TODO solo admitir los del programa. Dar error. No hacer nada ya veremos que pasa al grabar
		
		if (!hayParts){
			log.debug("### No hay Parts");
			//TODO Ver si afecta el charset al parameter map
			Map<String,String[]> params = request.getParameterMap();
			for(String key:params.keySet()){
				if (!key.equals(actionName)){
					String[] values = params.get(key);
					String valor = values[0];
					for(int i=1;i<values.length;i++) valor+="\n"+values[i];			//hago como hace lotus para sus multivaluados
					//Item it = new Item(key,valor.getBytes());
					//doc.setItem(key, it);
					log.debug("### key="+key+" valor="+valor);
					doc.setItemValue(key, valor);
				}
			}
		} else {
			//Hay que arreglar el caso en el que los parts vengan duplicados (multivaluados) al igual que en el caso en donde no hay parts, usare un separador
			//en este caso y de momento usare /n TODO ver si eso es buena eleccion
			ArrayList<String> partNames = new ArrayList<String>();		//almaceno el nombre del part para ver si hay duplicados
			for(Part p:parts){
				byte[] buff = new byte[(int) p.getSize()];
				try{
					InputStream in = p.getInputStream();
					int read = in.read(buff, 0, (int) p.getSize());
				} catch (Exception e){
					throw new ParserException("No se puede obtener el part:"+p.getName()+"."+e.getMessage());
				}

				String contentDisposition = p.getHeader("content-disposition");
				String contentType = p.getHeader("content-type");
				log.debug("pname:"+p.getName());
				log.debug("content-type:"+contentType+" : "+p.getContentType());
				String txt="";
				for(byte b:buff) txt+="["+(int)(b+0)+"] "+(char)(b)+" ";
				log.debug("name="+p.getName()+" buff="+txt);
				if ("".equals(contentType)) contentType = "text/plain";
				int i1 = contentDisposition.indexOf("filename=");
				if (i1>=0){
					String fileName = contentDisposition.substring(i1+"filename=".length());
					if (fileName.startsWith("\"")){
						i1 = fileName.indexOf("\"",1);
						fileName = fileName.substring(1,i1);
					}
					if (!fileName.equals("")){
						//caso de un attachement
						//TODO comprobar que no sea un field, si es asi se inserta en la lista de attachments sino como un field normal
						if (prg.getField(p.getName())!=null){
							//Item it = new Item(p.getName(),buff,Item.TYPE_ATTACHMENT,contentType,fileName);
							//doc.setItem(p.getName(), it);
							doc.setItemValue(p.getName(),fileName);
						} else {
							//lo inserto com un attachment
							Attachment attach = new Attachment();
							attach.setName(fileName);
							attach.setBytes(buff);
							doc.addAttachment(attach);
							log.debug("@@@@@@@@@@@@@ doc.addAttachment:"+fileName);
							//Item it = new Item(fileName,buff,Item.TYPE_ATTACHMENT,contentType,fileName);
							//doc.addAttachment(it);
						}
					} else {
						// TODO comprobar si esto es asi
						//no inserto el attachment vacio, es decir, es como si no exisitiera el campo
					}
				} else {
					//caso de un campo de texto
					//Item it = new Item(p.getName(),buff);
					//doc.setItem(p.getName(), it);
					try{
						String fieldName = p.getName();
						if (partNames.contains(fieldName)){
							//concateno el valor usando un separador, esto obliga a usar String
							String val = doc.getItemValue(fieldName);
							if (buff!=null) val += "\n"+(new String(buff,charset));
							doc.setItemValue(fieldName, val);
						} else {
							//reemplazo el valor
							//doc.setItemBytes(p.getName(), buff);
							// Arreglo segun charset
							doc.setItemValue(p.getName(), new String(buff,charset));
							partNames.add(p.getName());
						}
						log.debug("name="+p.getName()+" val="+doc.getItemValue(fieldName));
					} catch (Exception e){
						throw new ParserException(e.getMessage());
					}
				}
			}
		}
		log.debug("==================================1");
	}
}
