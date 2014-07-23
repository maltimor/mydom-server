package es.carm.mydom.entity;

import java.util.ArrayList;
import java.util.List;

import es.carm.mydom.DAO.DatabaseException;
import es.carm.mydom.DAO.SQLUtils;
import es.carm.mydom.parser.ColumnDef;
import es.carm.mydom.parser.ParserException;
import es.carm.mydom.parser.ViewDef;
import es.carm.mydom.servlet.DominoBeanAnnotation;
import es.carm.mydom.utils.HttpServletUtils;
import es.carm.mydom.utils.URLComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@DominoBeanAnnotation(name="domServerViewBean")


public class DominoViewBean extends DominoBean {
	final Logger log = LoggerFactory.getLogger(DominoViewBean.class);
//	public abstract List<String> getKeyColumns();
//	public abstract String getViewProvider(DominoSession domSession);
//	public abstract String getViewName();
//	public abstract String getRowData(DominoSession domSession,Document doc);
	
//	public View getView(DominoSession domSession) throws DatabaseException{
//		return domSession.getDatabase().getView(domSession, this.getViewName());
//	}
	
	public String getComputedText_viewBody(DominoSession domSession) {
		String res = "";
		try {
			log.debug("================= VIEW BODY ====================");
			//obtener de la url los parametros para pintarse
			URLComponents urlComponents = domSession.getUrlComponents();
			String strMode = urlComponents.getActionName().toLowerCase();
			String strCount =  urlComponents.getQueryValue("count");
			String strStart =  urlComponents.getQueryValue("start");
			String strStartKey =  urlComponents.getQueryValue("startkey");
			String strRestrictToCategory =  urlComponents.getQueryValue("restricttocategory");
			String strOrder = urlComponents.getQueryValue("order");
			String strQuery = urlComponents.getQueryValue("query");
			//obtener del documento de contexto información adicional
			Document docCtx = domSession.getDocumentContext();
			String exportMode = docCtx.getItemValue("exportMode");
			String keyName = docCtx.getItemValue("keyName");
			String getCount = docCtx.getItemValue("getCount");
			boolean json = exportMode.equals("json");
			boolean hasKey = !keyName.equals("");
			boolean hasArray = !hasKey;
			
			log.debug("mode="+strMode);
			log.debug("start="+strStart);
			log.debug("count="+strCount);
			log.debug("startKey="+strStartKey);
			log.debug("restrictToCategory="+strRestrictToCategory);
			log.debug("order="+strOrder);
			log.debug("query="+strQuery);

			log.debug("exportMode="+exportMode);
			log.debug("keyName="+keyName);
			log.debug("getCount="+getCount);
			log.debug("json="+json);
			log.debug("hasKey="+hasKey);

			View view = domSession.getCurrentView();
			ViewDef viewDef = view.getViewDef();
			List<ColumnDef> keyCols = viewDef.getKeyColumns();
			log.debug("viewDef="+viewDef.toString());

			int start,count;
			//intento convertir a int, si falla doy valor por defecto
			try{ start = Integer.parseInt(strStart);
			} catch (Exception e){ start = 0; }
			try{ count = Integer.parseInt(strCount);
			} catch (Exception e){ count = domSession.getServerDao().getDefaultResults(); }
			//limito los resultados de la pagina
			int max = domSession.getServerDao().getMaxResults();
			if (viewDef.getMaxResults()!=0) max = viewDef.getMaxResults();
			if (count>max) count = max;
			log.debug("count="+count+" max="+max);

			//caso especial de json y getCount
			if (exportMode.equals("json")&&getCount.equals("true")){
				return "{ \"count\": "+view.getCount(SQLUtils.getQueryWhere(strQuery, viewDef.getCols()))+" }";
			}
			
			//pinto el encabezado
			res+=this.getHeadData(domSession);
			
			//ahora en funcion de los parametros obtengo el resultado de la consulta y luego la pagino
			//NOTA: restrictToCategory tiene prevalencia con startKey
			//NOTA: json y keyName tienen prevalencia sobre lo anterior
			List<Document> ldoc = null;
			if (json){
				//estamos dentro de getData
				if (hasKey){
					//hay key y estamos en json, ldoc solo tiene un documento y no es array
					ldoc = new ArrayList<Document>();
					Document doc = view.getDocumentByKey(keyName);
					if (doc!=null) ldoc.add(doc);
				} else {
					//aqui recuperamos los datos haciendo uso del query
					ldoc = view.getPage(start, count, strOrder, SQLUtils.getQueryWhere(strQuery, viewDef.getCols()));
				}
			} else if (strMode.equals("searchview")) {
				//aqui trato el caso de la vista, hare caso a query y order
				ldoc = view.getPage(start, count, strOrder, SQLUtils.getQueryWhere(strQuery, viewDef.getCols()));
			} else if (strStartKey.equals("")&&strRestrictToCategory.equals("")){
				//startKey="" y restrictToCategory=""
				//obtengo una pagina y pinto todos los resultados
				//hago caso al query y al order
				ldoc = view.getPage(start, count, "", "");
			} else if (strRestrictToCategory.equals("")){
				//startKey<>"" y restrictToCategory=""
				//he de calcular el start en base al startkey
				start= start + view.getRowIndex(SQLUtils.getWhereClause(strStartKey, keyCols));
				ldoc = view.getPage(start,count,"","");
			} else {
				//startKey="" y restrictToCategory<>""
				//startKey<>"" y restrictToCategory<>""
				//como restrictToCategory tiene prevalencia....
				ldoc = view.getPage(start,count,"",SQLUtils.getWhereClause(strRestrictToCategory, keyCols));
			}
			
			//si es json y no hay key es un array
			if (json&&hasArray) res+="[ ";

			//pinto los resultados
			boolean first = true;
			for(Document doc:ldoc){
				if (json&&hasArray&&!first) res+=", ";
				res+= this.getRowData(domSession, doc);
				first = false;
			}

			//si es json y no hay key es un array
			if (json&&hasArray) res+=" ]";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public String getHeadData(DominoSession domSession){
		ViewDef viewDef = domSession.getCurrentView().getViewDef();
		boolean plain = viewDef.getMode().equals("plain")||viewDef.getMode().equals("notemplateplain");
		boolean json = domSession.getDocumentContext().getItemValue("exportMode").equals("json");
		String res = "";
		
		if (json) return "";
		
		if (plain) for(ColumnDef col:viewDef.getCols()) res+=col.getTitle();
		else {
			res += "<tr>";
			for(ColumnDef col:viewDef.getCols()){
				res+="<th nowrap align=\"left\"><b><font size=\"2\">";
				res+=col.getTitle();
				res+="</font></b></th>";
			}
			res+="</tr>";
		}
		return res;
	}
	
	public String getRowData(DominoSession domSession,Document doc) throws ParserException{
		ViewDef viewDef = domSession.getCurrentView().getViewDef();
		boolean plain = viewDef.getMode().equals("plain")||viewDef.getMode().equals("notemplateplain");
		boolean json = domSession.getDocumentContext().getItemValue("exportMode").equals("json");
		Document docCtx = domSession.getDocumentContext();
		//determino el valor de index en la vista
		String index = docCtx.getItemValue("index");
		docCtx.setItem("index", index.equals("")?1: ((Integer)docCtx.getItem("index"))+1 );
		//asigno el documento actual en la vista
		String res = "";
		
		if (json)  {
			plain = true;
			res +=" { ";
		}
		
		if (!plain) res+="<tr>";
		boolean first = true;
		for(ColumnDef col:viewDef.getCols()){
			if (!col.isHidden()){
				if (!plain) {
					res+="<td>";
					if (col.isLink()) res+= "<a href='"+doc.getUnid()+"?openDocument'>";
				}
				if (json) {
					if (!first) res+=", ";
					res+= "\""+col.getName()+"\": ";
				}
				
				//obtengo el valor
				String value;
				if (col.getFormula()!=null){
					value=domSession.executeGetColumn(col.getFormula(),doc);
				} else value=doc.getItemValue(col.getItem());
				
				if (!json) res+=value;
				else {
					//escribo el valor segun tipo
					//TODO escapar chars!!!!
					//value = escape(value);
					if (col.getItemType().equals("text")) res+="\""+value.replace("\"", "\\\"")+"\"";
					else res+=value;
				}
				
				if (!plain){
					if (col.isLink()) res+= "</a>";
					res+="</td>";
				}
				first = false;
			} else {
				//por si hay efectos laterales en el calculo de la columna
				//pero no escribo nada
				if (col.getFormula()!=null) domSession.executeGetColumn(col.getFormula(),doc);
			}
				
		}
		if (!plain) res+="</tr>";
		if (json) res+=" } ";
		return res;
	}
	
	public String getComputedText_PrevPage(DominoSession domSession) {
		URLComponents urlComponents = domSession.getUrlComponents();
		String strMode = urlComponents.getActionName();
		String strCount =  urlComponents.getQueryValue("count");
		String strStart =  urlComponents.getQueryValue("start");
		String strStartKey =  urlComponents.getQueryValue("startkey");
		String strRestrictToCategory =  urlComponents.getQueryValue("restricttocategory");
		String strOrder = urlComponents.getQueryValue("order");
		String strQuery = urlComponents.getQueryValue("query");
		int start,count;
		//intento convertir a int, si falla doy valor por defecto
		try{ start = Integer.parseInt(strStart);
		} catch (Exception e){ start = 0; }
		try{ count = Integer.parseInt(strCount);
		} catch (Exception e){ count = 30; }
		
		start = start-count;
		if (start<1) start = 0;
		
		String res = urlComponents.getDbFileName()+"/"+domSession.getCurrentView().getName();
		res+="?"+strMode+"&start="+start+"&count="+count;
		if (!strQuery.equals("")) res+="&query="+strQuery;
		if (!strOrder.equals("")) res+="&order="+strOrder;
		if (!strStartKey.equals("")) res+="&startKey="+strStartKey;
		if (!strRestrictToCategory.equals("")) res+="&restrictToCategory="+strRestrictToCategory;
		return res;
	}

	public String getComputedText_NextPage(DominoSession domSession) {
		URLComponents urlComponents = domSession.getUrlComponents();
		String strMode = urlComponents.getActionName();
		String strCount =  urlComponents.getQueryValue("count");
		String strStart =  urlComponents.getQueryValue("start");
		String strStartKey =  urlComponents.getQueryValue("startkey");
		String strRestrictToCategory =  urlComponents.getQueryValue("restricttocategory");
		String strOrder = urlComponents.getQueryValue("order");
		String strQuery = urlComponents.getQueryValue("query");
		int start,count;
		//intento convertir a int, si falla doy valor por defecto
		try{ start = Integer.parseInt(strStart);
		} catch (Exception e){ start = 0; }
		try{ count = Integer.parseInt(strCount);
		} catch (Exception e){ count = 30; }
		
		int total=999999;
		try {
			//total = domSession.getDatabase().getCount("UNID", "");
			total = domSession.getCurrentView().getCount();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		start = start+count;
		if (start>=total) start = total;
		
		String res = urlComponents.getDbFileName()+"/"+domSession.getCurrentView().getName();
		res+="?"+strMode+"&start="+start+"&count="+count;
		if (!strQuery.equals("")) res+="&query="+strQuery;
		if (!strOrder.equals("")) res+="&order="+strOrder;
		if (!strStartKey.equals("")) res+="&startKey="+strStartKey;
		if (!strRestrictToCategory.equals("")) res+="&restrictToCategory="+strRestrictToCategory;
		return res;
	}
}
