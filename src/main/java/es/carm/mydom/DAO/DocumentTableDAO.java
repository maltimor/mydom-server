package es.carm.mydom.DAO;

import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import es.carm.mydom.entity.Document;
import es.carm.mydom.entity.Item;
import es.carm.mydom.filters.FormAction;
import es.carm.mydom.utils.HexString;
import es.carm.mydom.utils.UNIDGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Deprecated

public class DocumentTableDAO implements DocumentDAO {
	final Logger log = LoggerFactory.getLogger( DocumentTableDAO.class);
	private String driver;
	private DataSource dataSource;
	private Connection con;
	private Map<String,TableModel> tables;
	
	public Map<String, TableModel> getTables() {
		return tables;
	}

	public void setTables(Map<String, TableModel> tables) {
		this.tables = tables;
	}

	public DocumentTableDAO(){
		this.driver = "oracle";
		this.tables = new HashMap<String,TableModel>();
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	private void prepareConnection() {
		try {
			this.con = dataSource.getConnection();
		} catch (SQLException e) {
			this.con = null;
			e.printStackTrace();
		}
	}
	private void finalizeConnection() {
		try {
			if (con!=null) con.close();
		} catch (SQLException e) {
			this.con = null;
			e.printStackTrace();
		}
	}	
	public String obtenUNID(String tableName) throws DatabaseException {
		log.debug("OOOOOOOOOOOOOOO obtenUNIDDocument:"+tableName+" OOOOOOOOOOOOOOO");
		String sres = UNIDGenerator.getUNID();
		log.debug("********* El unid:"+sres+":");
		
		for(String table:tables.keySet()){
			System.out.print("table="+table+":");
			TableModel tm = tables.get(table);
			System.out.print(" tableName="+tm.getTableName()+":");
			List<ColumnModel> cols = tm.getCols();
			System.out.print(" numCols="+cols.size()+" ");
			for(ColumnModel col:cols){
				System.out.print(col.getName()+":"+col.getType()+":"+col.isKey()+" | ");
			}
		// TODO	log.debug();
		}
		
		
		return sres;
	}
	
	private String toSql(String valor,String tipo) throws NumberFormatException {
		//ojo: el tipo BLOB no se puede contemplar aqui pues recibo un string como parametro
		String res = "";
		if (valor==null) return "null";
		if (tipo.equals("STRING")){
			//asegurarse de que la entrada este bien formada
			//de momento creo que solo es suficiente mirar las '
			// TODO ver caracteres UTF
			res="'"+valor.replace("'", "\"")+"'";
		} else if (tipo.equals("NUMBER")){
			Integer.parseInt(valor);
			res=valor;
		} else if (tipo.equals("DATE")){
			// TODO a lo bestia
			valor = valor.replace("'", "\"");
			//valor = valor.substring(0,valor.length()-2);
			if (driver.equals("mysql")) res="STR_TO_DATE('"+valor+"','%d/%c/%Y %H:%i:%s')";
			else res="TO_DATE('"+valor+"','DD/MM/YYYY HH24:MI:SS')";
		}
		return res;
	}
	
	private String toDocument(String valor,String tipo){
		if (valor==null) return "";
		String res = "";
		if (tipo.equals("STRING")){
			res = valor;
		} else if (tipo.equals("NUMBER")){
			res=valor;
		} else if (tipo.equals("DATE")){
			//la fecha viene en formato estandard, obtengo el date de la fecha
			try{
				SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date f = formatoDelTexto.parse(valor);
				//transformo el date en el formato estandard de fecha dd/mm/yyyy hh2:mi:ss
				SimpleDateFormat formating = new SimpleDateFormat("dd/MM/yyyy");
				valor = formating.format(f);
				res = valor;
			} catch (Exception e){
				res = valor;
			}
		}
		return res;
	}
	
	public void alterDocument(String tableName,Document doc,String action) throws DatabaseException{
		log.debug("OOOOOOOOOOOOOOO saveDocument:"+tableName+":"+action+" OOOOOOOOOOOOOOO");
		long initTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		prepareConnection();
		boolean create = (action.equals("create"));
		boolean save = (action.equals("save"));
		boolean delete = (action.equals("delete"));
		
		//obtengo el modelo a partir del tablename
		TableModel tm = tables.get(tableName.toLowerCase());
		if (tm==null) throw new DatabaseException("No se ha encontrado el modelo de tabla de "+tableName);
		
		try {
			// TODO Actualmente los keys deben darse en el documento
			//preparo las diferentes sentencias sql
			boolean abort = false;
			boolean first = true;
			tableName = tm.getTableName();
			List<ColumnModel> cols = tm.getCols();
			int numFields = cols.size();
			
			if (create){
				//accion de nuevo documento. hago un insert
				String columnas = "(";
				for(int i=0;i<numFields;i++) {
					if (i>0) columnas+=", ";
					columnas+=cols.get(i).getName();
				}
				columnas+=")";
				
				String sqlInsert = "insert into "+tableName+" "+columnas+" values (";
				for(int i=0;i<numFields;i++) {
					if (i!=0) sqlInsert+=", ";
					ColumnModel col = cols.get(i);
					String campo = col.getName();
					String tipo = col.getType();
					try{
						//sqlInsert+=campo+"="+toSql(valor,tipo);
						//trato el campo blob de forma especial
						if (tipo.equals("BLOB")){
							byte[] datos = doc.getItemBytes(campo);
							if ((datos==null)||(datos.length==0)) sqlInsert+="''";
							else sqlInsert+="0x"+new String(HexString.encode(datos))+"";
						} else {
							String valor = doc.getItemValue(campo);
							sqlInsert+=toSql(valor,tipo);
						}
					} catch (NumberFormatException nfe){
						abort=true;
					}
				}
				sqlInsert+=")";
				
				log.debug("SQL(abort="+abort+")="+sqlInsert);
				if (!abort){
					PreparedStatement prstmtInsert = con.prepareStatement(sqlInsert);
					prstmtInsert.execute();
					prstmtInsert.close();
					if (!con.getAutoCommit()) con.commit();
				}
			}
			
			if (delete){
				String sqlDelete = "delete from "+tableName+" where unid='"+doc.getUnid()+"'";
				log.debug("SQL="+sqlDelete);
				if (!abort){
					PreparedStatement prstmtDelete = con.prepareStatement(sqlDelete);
					prstmtDelete.execute();
					prstmtDelete.close();
					if (!con.getAutoCommit()) con.commit();
				}
			}
			
			if (save){
				String sqlUpdate = "update "+tableName+" set ";
				first = true;
				for(int i=0;i<numFields;i++) {
					ColumnModel col = cols.get(i);
					if (!first) sqlUpdate+=", ";
					first = false;
					// TODO evitar inyeccion de sql
					String campo = col.getName();
					String tipo = col.getType(); 
					try{
						//sqlUpdate+=campo+"="+toSql(valor,tipo);
						//trato el campo blob de forma especial
						if (tipo.equals("BLOB")){
							byte[] datos = doc.getItemBytes(campo);
							if ((datos==null)||(datos.length==0)) sqlUpdate+=campo+"=''";
							else sqlUpdate+=campo+"=0x"+new String(HexString.encode(datos))+"";
						} else {
							String valor = doc.getItemValue(campo);
							sqlUpdate+=campo+"="+toSql(valor,tipo);
						}
					} catch (NumberFormatException nfe){
						abort=true;
					}
				}
				sqlUpdate+=" where unid='"+doc.getUnid()+"'";
				log.debug("SQL="+sqlUpdate);
				if (!abort){
					PreparedStatement prstmtUpdate = con.prepareStatement(sqlUpdate);
					prstmtUpdate.execute();
					prstmtUpdate.close();
					if (!con.getAutoCommit()) con.commit();
				}
			}
			
			// ahora toca el turno a los attachments
			/*Map<String, Item> attachments = doc.getAttachments();
			if (delete||save){
				String sqlDelete = "delete from attachments where unid='"+doc.getUnid()+"'";
				log.debug("SQ_AnexosL="+sqlDelete);
				if (!abort){
					PreparedStatement prstmtDelete = con.prepareStatement(sqlDelete);
					prstmtDelete.execute();
					prstmtDelete.close();
					if (!con.getAutoCommit()) con.commit();
				}				
			}
				
			if ((attachments.size()>0)&&(create||save)){
				String sqlInsert = "insert into attachments (unid,name,value,mimetype) values (";
				first = true;
				for(String name:attachments.keySet()){
					if (!first) sqlInsert+="),(";
					first=false;
					Item it = attachments.get(name);
					sqlInsert+="'"+doc.getUnid()+"','"+it.getName()+"',";
					byte[] datos = it.getBytes();
					if ((datos==null)||(datos.length==0)) sqlInsert+="''";
					else sqlInsert+="0x"+new String(HexString.encode(datos))+"";
					sqlInsert+=",'"+it.getMimeType()+"'";
				}
				sqlInsert+=")";
				log.debug("SQL_Anexos="+sqlInsert.substring(0,250));
				if (!abort){
					PreparedStatement prstmtInsert = con.prepareStatement(sqlInsert);
					prstmtInsert.execute();
					prstmtInsert.close();
					if (!con.getAutoCommit()) con.commit();
				}
			}*/
			
			log.debug("OOOOOOOOOOOOOOO saveDocument FIN. OOOOOOOOOOOOOOO");
			endTime = System.currentTimeMillis();
			log.debug("Tiempo total_exec:"+((endTime-initTime)/1000.0)+" seg.");
		}catch( Exception e ) {
			throw new DatabaseException("Error en alterDocument:"+e.getMessage());
		} finally {
			finalizeConnection();
		}
	}
	
	//este metodo sol puede ser usado por el modelo documental
	public Document getDocument(String tableName,String unid) throws DatabaseException{
		//abre una conexion con mysql y consulta la tabla de usuario/passwords
		//log.debug("OOOOOOOOOOOOOOO getDocument:"+databaseName+":"+tableName+" OOOOOOOOOOOOOOO");
		prepareConnection();
		Statement stmt=null;
		ResultSet rs=null;
		Document doc=null;
		
		//caso especial donde no se conoce el tableName, pruebo a buscarlo en la vista global uid
		if (tableName==null) {
			try{
				stmt = con.createStatement();
				rs = stmt.executeQuery("select * from unid where unid='"+unid+"'");
				if (!rs.next()) throw new DatabaseException("No se puede localizar el tableName a partir del uid:"+unid);
				tableName = rs.getString("form");
			} catch (Exception e){
				finalizeConnection();
				throw new DatabaseException("Error en getDocument("+tableName+","+unid+"):"+e.getMessage());
			}
		}
		
		//obtengo el modelo a partir del tablename
		TableModel tm = tables.get(tableName.toLowerCase());
		if (tm==null) throw new DatabaseException("No se ha encontrado el modelo de tabla de "+tableName);
				
		try {
			//primero veo si tiene uid.Si no lo tiene se sale sin mas
			if (unid==null) {
				//log.debug("********* No tiene uid");
				return null;
			}
			
			tableName = tm.getTableName();
			List<ColumnModel> cols = tm.getCols();
			int numFields = cols.size();
			
			//ejecuto el select para obtener todos los registros asociados a ese uid
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from "+tableName+" where unid='"+unid+"'");
			if (!rs.next()) {
				//mal, no hay registros, salgo sin mas
				//log.debug("***** No hay documento ="+uid);
				return null;
			} else {
				//para cada fila del resulset la inserto en el map
				doc = new Document();
				doc.setUnid(unid);
				for(int i=0;i<numFields;i++) {
					ColumnModel col = cols.get(i);
					// TODO evitar inyeccion de sql
					String campo = col.getName();
					String tipo = col.getType(); 
					//trato el campo blob de forma especial
					if (tipo.equals("BLOB")){
						Blob blob = rs.getBlob(i);
						byte[] datos = blob.getBytes(1,(int) blob.length());
						String valor = "";
						if ((datos==null)||(datos.length==0)) valor="";
						else valor = new String(HexString.encode(datos));
						Item it = new Item(campo,valor!=null?valor.getBytes():"".getBytes(),Item.TYPE_DEFAULT,"","");
						doc.setItem(campo,it);
					} else {
						String valor = rs.getString(campo);
						//transformar el valor en fecha o en numero
						valor = toDocument(valor,tipo);
						Item it = new Item(campo,valor!=null?valor.getBytes():"".getBytes(),Item.TYPE_DEFAULT,"","");
						doc.setItem(campo,it);
					}
				}
				// ahora toca el turno a los attachments
/*				stmt = con.createStatement();
				rs = stmt.executeQuery("select name,value,mimetype from attachments where unid='"+unid+"'");
				while (rs.next()) {
					String itemName = rs.getString(1);
					String mimeType = rs.getString(3);
					
					Blob blob = rs.getBlob(2);
					byte[] datos = blob.getBytes(1,(int) blob.length());
					String valor = "";
					if ((datos==null)||(datos.length==0)) valor=null;
					//else valor = new String(HexString.encode(datos));
					//else valor = new String(datos);
					
					Item it = new Item(itemName,valor!=null?datos:"".getBytes(),Item.TYPE_ATTACHMENT,mimeType,itemName);
					doc.addAttachment(it);
				}*/
			}
		}catch( Exception e ) {
			throw new DatabaseException("Error en getDocument("+tableName+","+unid+"):"+e.getMessage());
		} finally {
			finalizeConnection();
		}
		return doc;
	}

	//este metodo se encarga de decir si existe o no un documento a partir de su unid
	//se apoya en la vista unid
	public boolean isDocumentByUnid(String unid) throws DatabaseException {
		prepareConnection();
		Statement stmt=null;
		ResultSet rs=null;
		boolean res = false;
		//caso especial donde no se conoce el tableName, pruebo a buscarlo en la vista global uid
		try{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from unid where unid='"+unid+"'");
			if (rs.next()) res = true;
		} catch (Exception e){
			finalizeConnection();
			throw new DatabaseException("Error en isDocument("+unid+"):"+e.getMessage());
		} finally {
			finalizeConnection();
		}
		return res;
	}
}
