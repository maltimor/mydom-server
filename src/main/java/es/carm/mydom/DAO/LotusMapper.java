/* |UNID#T#32|FECHAINICIO#F#|FECHAFIN#F#|TITULO#T#80|PERSONACONTACTO#T#30|TELEFONO#T#20|OBSERVACIONES#T#3250|URLIMAGEN#T#20|FORM#T#10|ID#T#10|EXPORTACION#T#10|ESPECIFICACIONES#T#50| */ 
package es.carm.mydom.DAO;

import java.util.List;
import java.util.Map;import org.apache.ibatis.annotations.DeleteProvider;import org.apache.ibatis.annotations.InsertProvider;import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;import org.apache.ibatis.annotations.UpdateProvider;import es.carm.mydom.entity.DominoBean;import es.carm.mydom.entity.DominoSession;
public interface LotusMapper {	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="isByUnidProvider")	public Map<String,Object> isByUnid(@Param("tableName") String tableName, @Param("unid") String unid);
	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="selectByUnidProvider")	public Map<String,Object> get(@Param("tableName") String tableName, @Param("unid") String unid);

	@InsertProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="insertProvider")
	public void insert(@Param("tableName") String tableName, @Param("map") Map<String,Object> data, @Param("listaExclusion") String listaExclusion);

	@UpdateProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="updateProvider")
	public void update(@Param("tableName") String tableName, @Param("map") Map<String,Object> data, @Param("listaExclusion") String listaExclusion);

	@UpdateProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="updateProvider")	public void updateByKey(@Param("tableName") String tableName, @Param("key") String key, @Param("map") Map<String,Object> data, @Param("listaExclusion") String listaExclusion);	@DeleteProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="deleteProvider")	public void delete(@Param("tableName") String tableName, @Param("unid") String unid);
	@DeleteProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="deleteByKeyProvider")	public void deleteByKey(@Param("tableName") String tableName, @Param("key") String key, @Param("id") String id);	
	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="selectAllProvider")
	public List<Map<String,Object>> getAll(@Param("tableName") String tableName);

	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="getPageProvider")
	public List<Map<String,Object>> getPage(@Param("tableName") String tableName, @Param("start") int start, @Param("count") int count, @Param("order") String sortOrder, @Param("where") String where);

	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="getCountProvider")
	public int getCount(@Param("tableName") String tableName, @Param("where") String where);	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="selectAllDocumentByKeyProvider")	public List<Map<String, Object>> getAllDocumentByKey(@Param("tableName") String tableName,@Param("keys") Object keys);
	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="selectDocumentByKeyProvider")	public Map<String, Object> getDocumentByKey(@Param("tableName") String tableName,@Param("keys") Object keys);	//*********************	//* METODOS DE VISTAS *	//*********************	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="viewSelectAllProvider")	public List<Map<String,Object>> getViewAll(@Param("sql") String sql);	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="viewSelectAllDocumentByKeyProvider")	public List<Map<String,Object>> getViewAllDocumentByKey(@Param("sql") String sql, @Param("keys") Object keys);	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="viewCountProvider")	public int getViewCount(@Param("sql") String sql, @Param("where") String where);	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="viewPageProvider")	public List<Map<String,Object>> getViewPage(@Param("sql") String sql, @Param("start") int start, @Param("count") int count, @Param("order") String sortOrder, @Param("where") String where);	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="viewRowIndexProvider")	public int getViewRowIndex(@Param("sql") String sql, @Param("where") String where);}

