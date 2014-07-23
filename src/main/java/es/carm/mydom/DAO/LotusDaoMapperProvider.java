package es.carm.mydom.DAO;

import java.util.Map;

import es.carm.mydom.entity.DominoBean;
import es.carm.mydom.entity.DominoSession;
import es.carm.mydom.filters.FramesetAction;
import es.carm.mydom.parser.ParserException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class LotusDaoMapperProvider {
	final Logger log = LoggerFactory.getLogger(LotusDaoMapperProvider.class);
	//**********************************************
	//IMPLMENETACION DE LOS PROVIDERS para Database*
	//**********************************************
	public String selectAllProvider(Map params){
		return "SELECT * FROM "+(String) params.get("tableName");
	}
	public String isByUnidProvider(Map params){
		return "SELECT * FROM "+(String) params.get("tableName")+" WHERE unid=#{unid}";
//		return "SELECT * FROM UNID WHERE unid=#{unid}";
	}
	public String selectByUnidProvider(Map params){
		return "SELECT * FROM "+(String) params.get("tableName")+" WHERE unid=#{unid}";
	}
	public String insertProvider(Map params){
		log.debug("##########################:INSERT PROVIDER:");
		for(Object key:params.keySet()){
			log.debug(key+"="+params.get(key));
		}
		log.debug("##########################END INSERT PROVIDER:");
		//se supone que tengo 2 parametros : tableName y el map
		String colNames = "";
		String colValues = "";
		boolean first = true;
		Map<String, Object> map = (Map<String, Object>) params.get("map");
		String listaExclusion = (String) params.get("listaExclusion");
		if (listaExclusion==null) listaExclusion="";
		
		for(String key:map.keySet()){
			if (!listaExclusion.contains(","+key.toLowerCase()+",")){
				if (!first) {
					colNames+=",";
					colValues+=",";
				}
				colNames+=key;
				colValues+="#{map."+key+"}";
				first=false;
			}
		}
		
		String res = "INSERT INTO "+(String) params.get("tableName")+"("+colNames+") VALUES ("+colValues+")";
		log.debug("insertProvider:"+res);
		return res;
	}
	public String updateProvider(Map params){
		log.debug("##########################:UPDATE PROVIDER:");
		for(Object key:params.keySet()){
			log.debug(key+"="+params.get(key));
		}
		log.debug("##########################END UPDATE PROVIDER:");
		//se supone que tengo 2 parametros : tableName y el map
		String colSets = "";
		boolean first = true;
		Map<String, Object> map = (Map<String, Object>) params.get("map");

		String listaExclusion = (String) params.get("listaExclusion");
		if (listaExclusion==null) listaExclusion="";

		for(String key:map.keySet()){
			if (!listaExclusion.contains(","+key.toLowerCase()+",")){
				if (!first) {
					colSets+=",";
				}
				colSets+=key+"=#{map."+key+"}";
				first=false;
			}
		}
		
		String key = "unid";
		if (params.containsKey("key")) key = (String) params.get("key");
		
		String res = "UPDATE "+(String) params.get("tableName")+" SET "+colSets+" WHERE "+key+"=#{map."+key+"}";
		log.debug("updateProvider:"+res);
		return res;
	}
	
	public String deleteProvider(Map params) {
		String res = "DELETE FROM "+(String) params.get("tableName")+" WHERE unid=#{unid}";
		log.debug("deleteProvider:"+res);
		return res;
	}
	
	public String deleteByKeyProvider(Map params) {
		String res = "DELETE FROM "+(String) params.get("tableName")+" WHERE "+(String) params.get("key")+"=#{id}";
		log.debug("deleteByKeyProvider:"+res);
		return res;
	}

	private String getWhereFromKeys(Object keysPrp){
		//permito diferentes tratamientos para los keys:
		//Map<String,Object>
		//String:clausula where arbitraria
		String res = "";
		boolean first = true;
		if (keysPrp!=null){
			if (keysPrp instanceof Map){
				Map<String,Object> keys = (Map<String, Object>) keysPrp;
				if (keys.size()>0) res += " WHERE ";
				//compongo una clausula where mirando el tipo de datos de los valores
				for(String key:keys.keySet()){
					if (!first) res+=" AND ";
					first = false;
					
					//solo trato los casos especiales de String y de Integer
					//TODO Date, Double, etc...
					Object value = keys.get(key);
					if (value==null) res+=key + " IS NULL";
					else if (value instanceof String) res+=key+" = '"+((String)value).replace("'","''")+"'";
					else res+=key+="="+value.toString();
				}
			} else if (keysPrp instanceof String){
				//caso del where hasta aqui el valor no es null por eso a piñon inserto la clausula where
				if (!keysPrp.equals("")) res += " WHERE ( "+keysPrp+ " ) ";
			}
		}
		log.debug("getWhereFromKeys:"+res);
		return res;
	}
	
	public String selectAllDocumentByKeyProvider(Map params){
		String res = "SELECT * FROM "+(String) params.get("tableName");
		res += getWhereFromKeys(params.get("keys"));
		log.debug("selectAllDocumentByKeyProvider:"+res);
		return res;
	}
	
	public String selectDocumentByKeyProvider(Map params){
		String res = "SELECT * FROM "+(String) params.get("tableName");
		//TODO CREO que aqui esto esta mal. deberia estar en el otro metodo
		String where = getWhereFromKeys(params.get("keys"));
		if (where.startsWith(" WHERE")) where += " AND ROWNUM<=1 ";
		else where += " WHERE ROWNUM<=1 ";
		res+=where;
		log.debug("selectDocumentByKeyProvider:"+res);
		return res;
	}

	public String getCountProvider(Map params){
		String tableName = (String) params.get("tableName");
		String where = (String) params.get("where");

		String sql = "SELECT * FROM "+tableName;
		String res = sql;
		if ((where!=null)&&(!where.equals(""))) res=res+" AND ("+where+")";
		res="SELECT count(*) FROM ("+res+")";
		log.debug("getCountProvider:"+res);
		return res;
	}
	
	public String getPageProvider(Map params){
		String tableName = (String) params.get("tableName");
		String where = (String) params.get("where");
		String order = (String) params.get("order");
		int start = (Integer) params.get("start");
		int count = (Integer) params.get("count");
		
		String sql = "SELECT * FROM "+tableName;
		String res = sql;
		if ((where!=null)&&(!where.equals(""))) res += " AND ("+where+")";
		if ((order!=null)&&(!order.equals(""))) res += " ORDER BY "+order;
		res = "SELECT * FROM (SELECT ROWNUM AS fila, t.* FROM ("+res;

		int ini = start<=1?1:start;
		int fin=start+count;
		
		res+=") t WHERE ROWNUM <="+fin+") WHERE fila >= "+ini;
		log.debug("getPageProvider:"+res);
		return res;
	}

	//**********************************************
	//IMPLEMENTACION DE LOS PROVIDERS para View*
	//**********************************************
	public String viewSelectAllProvider(Map params){
		log.debug("viewSelectAllProvider:"+params.get("sql"));
		return (String) params.get("sql");
	}

	public String viewSelectAllDocumentByKeyProvider(Map params){
		String sql = (String) params.get("sql");
		String res = "SELECT * FROM ( "+sql+" ) ";
		try{
			//permito diferentes tratamientos para los keys:
			//Map<String,Object>
			//String:clausula where arbitraria
			boolean first = true;
			Object keysPrp = params.get("keys");
			if (keysPrp!=null){
				if (keysPrp instanceof Map){
					Map<String,Object> keys = (Map<String, Object>) keysPrp;
					if (keys.size()>0) res += " WHERE ";
					//compongo una clausula where mirando el tipo de datos de los valores
					for(String key:keys.keySet()){
						if (!first) res+=" AND ";
						first = false;
						
						//solo trato los casos especiales de String y de Integer
						//TODO Date, Double, etc...
						Object value = keys.get(key);
						if (value==null) res+=key + " IS NULL";
						else if (value instanceof String) res+=key+" = '"+((String)value).replace("'","''")+"'";
						else res+=key+="="+value.toString();
					}
				} else if (keysPrp instanceof String){
					//caso del where hasta aqui el valor no es null por eso a piñon inserto la clausula where
					if (!keysPrp.equals("")) res += " WHERE "+keysPrp;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		log.debug("viewSelectAllDocumentByKeyProvider:"+res);
		return res;
	}

	public String viewCountProvider(Map params){
		String where = (String) params.get("where");

		String sql = (String) params.get("sql");
		String res = sql;
		if ((where!=null)&&(!where.equals(""))) res = "SELECT * FROM ( "+res+" ) WHERE ( "+where+" ) ";
		res="SELECT count(*) FROM ("+res+")";
		log.debug("viewCountProvider:"+res);
		return res;
	}
	
	public String viewPageProvider(Map params){
		String where = (String) params.get("where");
		String order = (String) params.get("order");
		int start = (Integer) params.get("start");
		int count = (Integer) params.get("count");
		
		String sql = (String) params.get("sql");
		String res = sql;
		if ((where!=null)&&(!where.equals(""))) res = "SELECT * FROM ( "+res+" ) WHERE ( "+where+" ) ";
		if ((order!=null)&&(!order.equals(""))) res += " ORDER BY "+order;
		res = "SELECT * FROM (SELECT ROWNUM AS fila, t.* FROM ("+res;

		int ini = start<=1?1:start;
		int fin=start+count;
		
		res+=") t WHERE ROWNUM <="+fin+") WHERE fila >= "+ini;
		log.debug("viewPageProvider:"+res);
		return res;
	}
	
	public String viewRowIndexProvider(Map params){
		String where = (String) params.get("where");
		String sql = (String) params.get("sql");
		String res = sql;
		if ((where!=null)&&(!where.equals("")))res = "SELECT zx_fila FROM (SELECT ROWNUM AS zx_fila, t.* FROM ("+res+") t) WHERE ( "+where+" ) ";
		else res = "SELECT 0 FROM DUAL";
		//select fila from (select rownum as fila,unid.* from unid) where id='C-00108'
		log.debug("viewRowIndexProvider:"+res);
		return res;
	}
	

}
