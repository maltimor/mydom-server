package es.carm.mydom.DAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.carm.mydom.parser.ColumnDef;
import es.carm.mydom.utils.StringDateUtils;
public class SQLUtils {
	final static Logger log = LoggerFactory.getLogger(SQLUtils.class);
	public static Object getKeyClause(Object keys,List<String> keyCols) {
		log.debug("getKeyClause:");
		if (keys==null) return null;
		if (keys instanceof Map){
			//devolver tal cual, ya se encargara el lotusmapperprovider de hacerlo
			//TODO ver si esto no sería una violacion de seguridad
			log.debug("keys instance of map");
			return keys;
		}
		if (keys instanceof List){
			//combinar con los key columns. hasta un maximo de lst.count
			List<String> lkeys = (List<String>) keys;
			int keyColsSize = keyCols.size();
			int lkeysSize = lkeys.size();
			log.debug("keys instance of list: keycol.size="+keyColsSize+" lkeys.size="+lkeysSize);
			//en funcion del numero de elementos en keycol y en lkeys implemento un and o un or segun un algoritmo:
			//mientras poslkeys<lkeys.size y poskeycol<keycols.size -> and, poskeys++, poskeycols++
			//si poskeycols>=keycols.size -> or, poskeycols=0
			int posKeyCols=0;
			int posLkeys=0;
			//caso especial donde uno de los sizes es 0
			//TODO
			String res = " ( ";
			while (posLkeys<lkeysSize){
				if (posKeyCols!=0) res+=" AND ";
				
				String key = keyCols.get(posKeyCols);
				//solo trato los casos especiales de String y de Integer
				//TODO Date, Double, etc...
				Object value = lkeys.get(posLkeys);
				if (value==null) res+=key + " IS NULL";
				else if (value instanceof String) res+=key+" = '"+((String)value).replace("'","''")+"'";
				else res+=key+="="+value.toString();
						
				posLkeys++;
				posKeyCols++;
				if (posKeyCols>=keyColsSize){
					posKeyCols=0;
					if (posLkeys<lkeysSize){
						res+=" ) OR ( ";
					}
				}
			}
			res+=" ) ";
			log.debug("getKeyClause:"+res);
			return res;
			/*Map<String,String> res = new HashMap<String,String>();
			int min = Math.min(.....);
			for(int i=0;i<min;i++){
				log.debug("put "+keyCols.get(i)+" = "+ lkeys.get(i));
				res.put(keyCols.get(i), lkeys.get(i));
			}
			return res;*/
		}
		if (keys instanceof String){
			log.debug("keys instance of string");
			//combinar con los key columns. hasta un maximo de lst.count
			Map<String,String> res = new HashMap<String,String>();
			String key = (String) keys;
			//List<String> lst = getKeyColumns();
			if (keyCols.size()>0) {
				log.debug("put "+keyCols.get(0)+" = "+ key);
				res.put(keyCols.get(0), key);
			}
			return res;
		}
		//TODO incorpoerar el String[]
		log.debug("keys instance invalid");
		return null;
	}

	public static String getWhereClause(Object keys,List<ColumnDef> keyCols) {
		String res = "";
		boolean first = true;
		log.debug("getWhereClause");
		if (keys==null) return null;
		
		//tengo en cuenta los multipes valores en la definicion del keycols
		/*SELECT * FROM 
		(SELECT ROWNUM AS fila, t.* FROM 
		(SELECT * FROM ( SELECT * FROM DefDoc WHERE ID IS NOT NULL ORDER BY TipoDoc ) WHERE ( 'Listado' in (select regexp_substr(Categorias,'.+', 1, level) from DEFDOC
		connect by regexp_substr(Categorias, '.+', 1, level) is not null) ) )
		t WHERE ROWNUM <=9999) WHERE fila >= 1;



		select regexp_substr(Categorias,'.+', 1, level) from DEFDOC
		connect by regexp_substr(Categorias, '.+', 1, level) is not null;*/
		
		log.info("getWhereClause. keys="+keys+" keyCols="+keyCols.size());
		
		if (keys instanceof List){
			//combinar con los key columns. hasta un maximo de lst.count
			Map<String,String> map = new HashMap<String,String>();
			List<String> lkeys = (List<String>) keys;
			int min = Math.min(keyCols.size(),lkeys.size());
			for(int i=0;i<min;i++){
				ColumnDef col = keyCols.get(i);
				String colName = col.getName();
				if (col.isSeparatemultiplevalues()) colName+="?"+col.getListseparator();
				map.put(colName, lkeys.get(i));
			}
			keys=map;
			//no hago return, el siguiente trozo de codigo vera que keys es del tipo map y lo hace
		}

		if (keys instanceof String){
			//combinar con los key columns. hasta un maximo de lst.count
			Map<String,String> map = new HashMap<String,String>();
			String key = (String) keys;
			if (keyCols.size()>0) {
				ColumnDef col = keyCols.get(0);
				String colName = col.getName();
				if (col.isSeparatemultiplevalues()) colName+="?"+col.getListseparator();
				map.put(colName, key);
			}
			keys=map;
			//no hago return, el siguiente trozo de codigo vera que keys es del tipo map y lo hace
		}
		
		if (keys instanceof Map){
			//TODO ver si esto no sería una violacion de seguridad
			Map<String,Object> keysMap = (Map<String, Object>) keys;
			//compongo una clausula where mirando el tipo de datos de los valores
			for(String key:keysMap.keySet()){
				if (!first) res+=" AND ";
				first = false;
				
				//solo trato los casos especiales de String y de Integer
				//TODO Date, Double, etc...
				Object value = keysMap.get(key);
				if (value==null) res+=" ( "+key + " IS NULL )";
				else if (value instanceof String) {
					//aqui añado un caso especial para multivaluados de tipo string
					int i1 = key.indexOf("?");
					if (i1>0) {
						String sep = key.substring(i1+1);
						if (sep.equals("\\n")){
							//caso especial de retorno de carro
							sep = "chr(10)";
						} else sep = "'"+sep.replace("'","''")+"'";
						key = key.substring(0,i1);
						//instr('|'||replace(Categorias,chr(10),'|')||'|','|Lis|')>0
						res+=" ( instr('|' || replace( "+key+" ,"+sep+",'|') || '|', '|"+((String)value).replace("'","''")+"|') >0 ) ";
					} else {
						res+=" ( "+key+" = '"+((String)value).replace("'","''")+"' ) ";
					}
				}
				else res+=" ( "+key+"="+value.toString()+" ) ";
			}
			log.info("getWhereClause: "+res);
			return res;
		}

		//TODO incorpoerar el String[]
		return null;
	}
	
	/*
	 * cols se utiliza para obtener informacion de la columnas, sobre todo el tipo de dato, si es key y si actua en fulltext
	 * Dado una query por url siguiendo la sintaxis:
	 * en el query estring se definen estos parametros:
	 * query=[sintaxis_query]
	 * start=[offset a partir del cual se empieza a mostrar datos, por defecto es 0]
	 * count=[numero de registros que se devuevle, por defecto es 30]
	 * order=[sintaxis_order]
	 * Sintaxis query:
	 * Q -> valor
	 * Q -> '[' key ']' op valor
	 * Q -> Q AND Q  -- AND
	 * Q -> Q OR Q  -- OR
	 * valor -> cadena de texto
	 * key -> nombre del campo
	 * op -> =, >, <, >=, <=, ==
	 * Sintaxis order:
	 * O -> Campo [ASC|DESC] [, O]
	 * Ejemplos de la sintaxis:
	 * ....?start=100&count=5&order=Campo1 ASC, Campo2 DESC&query=[Campo1]=Andres AND [Campo2]>5
	 */
	public static String getQueryWhere(String text,List<ColumnDef> cols){
		if (text.equals("")) return "";
		//protccion
		if (cols==null) cols = new ArrayList<ColumnDef>();
		//optimizacion
		Map<String,ColumnDef> map = new HashMap<String,ColumnDef>();
		for(ColumnDef col:cols) map.put(col.getName().toLowerCase(), col);

		//reemplazo AND Y OR por & y |
		text = text.replace(" AND ", "&").replace(" OR ", "|");
		System.out.println("SEARCH="+text);
		
		//analizo sintacticamente la cadena
		String res = "";
		char[] buff = text.toCharArray();
		List<String> lst = new ArrayList<String>();
		int pos = 0;
		String token="";
		int estado = 0;
		String key = "";
		String op = "";
		String valor = "";
		while(pos<buff.length){
			char act=buff[pos];
			System.out.println("act="+act+"    estado="+estado);
			System.out.println(estado+"|"+key+"|"+op+"|"+valor);
			switch (estado){
			case 0:
				//psao inicial
				if (act=='['){
					estado = 1;
					key = "";
					valor = "";
					op = "";
				} else if ("]<>=&|".indexOf(act)>=0){
					estado = -1;
				} else {
					key="NULL";
					op="NULL";
					valor+=act;
					estado=3;
				}
				break;
			case 1:
				//evaluando [ ->k ]=valor
				if (act==']'){
					estado = 2;
				} else {
					key+=act;
				}
				break;
			case 2:
				//evaluando [k] -> op valor
				if ("<>=".indexOf(act)>=0){
					op += act;
					//veo el siguiente char
					if (pos+1<buff.length){
						if ("<>=".indexOf(buff[pos+1])>=0){
							pos++;
							op += buff[pos];
						}
					}
					estado = 3;
				} else estado = -1;
				break;
			case 3:
				//evluanbdo [k] op -> valor
				if ("&|".indexOf(act)==-1){
					valor += act;
					if (pos+1==buff.length){
						System.out.println("PUSH= key="+key+" valor="+valor+" op="+op);
						lst.add("["+key+"|"+op+"|"+valor);
					}
				} else {
					System.out.println("PUSH= key="+key+" valor="+valor+" op="+op);
					lst.add("["+key+"|"+op+"|"+valor);
					lst.add(""+act);
					estado = 0;
				}
				
				break;
				
				
			}
			pos++;
		}
		if (estado==-1) res+="***ERROR***";
		
		//analizo semanticamente la cadena
		res = "";
		int TYPE_TEXT = 1;
		int TYPE_NUMBER=2;
		int TYPE_DATE = 3;

		for(String cad:lst){
			//por defecto el tipo de datos es texto
			int type=TYPE_TEXT;
			if (cad.startsWith("[")){
				String[] aux = cad.substring(1).split("\\|");
				key = aux[0];
				op = aux[1];
				valor = aux[2];
				//aqui deberia recuperar el tipo de dato segun la columna
				ColumnDef col = null;
				if (!key.equals("NULL")) col = map.get(key.toLowerCase());
				if (col!=null) {
					String strType = col.getItemType().toLowerCase();
					if (strType.equals("text")) type = TYPE_TEXT;
					else if (strType.equals("number")) type = TYPE_NUMBER;
					else if (strType.equals("date")) type = TYPE_DATE;
					else type = TYPE_TEXT;
					System.out.println("COL:"+col);
				}

				//tengo en cuenta que por defecto todos los operadores añaden % al principio y al final
				if (op.equals("=")) {
					//si el tipo de datos es TEXT la semantica es del tipo LIKE
					//en otro caso el = el lo mismo que ==
					if (type==TYPE_TEXT){
						valor = "%"+valor+"%";
						op = " LIKE ";
					} else if (type==TYPE_DATE){
						//comprobar que sea una fecha
						StringDateUtils sdu = new StringDateUtils(valor);
						System.out.println("Es valida:"+sdu.isValid());
						System.out.println("dia:"+sdu.getDia());
						System.out.println("mes:"+sdu.getMes());
						System.out.println("año:"+sdu.getAnyo());
						if (!sdu.isValid()) valor = "";
					}
				} else if (op.equals("==")){
					//este operador es identico a = excepto para TYPE_TEXT que permite comodines
					if (type==TYPE_TEXT){
						//si el valor contiene comodin, en vez de = pongo like
						if (valor.contains("%")) op = " LIKE ";
						else if (valor.contains("*")){
							valor = valor.replace("*", "%");
							op = " LIKE ";
						} else op = "=";
					} else {
						//en cualquier otro caso, el == es lo mismo que =
						op = "=";
					}
				}
				
				//aplico la transformacion a valor en funcion de su tipo de datos
				if ((type==TYPE_TEXT)||(type==TYPE_DATE)) valor = "'"+valor.replace("'","''")+"'";
				
				if (key.equals("NULL")){
					//iterar por todas las columnas de la vista que esten marcadas para la busqyeda fulltext
					//aqui se permite que col==null
					res+="FULLTEXT("+valor+")";
				} else {
					//en funcion del tipo de dato se ponen ' o no
					//hay que escapar datos
					//TODO si col==null lo añado aqui?
					res+= key+op+valor;
				}
			} else {
				if (cad.equals("&")) res+= " AND ";
				else if (cad.equals("|")) res += " OR ";
			}
			System.out.println(cad);
		}
		System.out.println(res);
		return res;
	}

}
