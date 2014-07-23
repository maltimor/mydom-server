/* |UNID#T#32|FECHAINICIO#F#|FECHAFIN#F#|TITULO#T#80|PERSONACONTACTO#T#30|TELEFONO#T#20|OBSERVACIONES#T#3250|URLIMAGEN#T#20|FORM#T#10|ID#T#10|EXPORTACION#T#10|ESPECIFICACIONES#T#50|
package es.carm.mydom.DAO;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
public interface LotusMapper {


	@InsertProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="insertProvider")
	public void insert(@Param("tableName") String tableName, @Param("map") Map<String,Object> data, @Param("listaExclusion") String listaExclusion);

	@UpdateProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="updateProvider")
	public void update(@Param("tableName") String tableName, @Param("map") Map<String,Object> data, @Param("listaExclusion") String listaExclusion);

	@UpdateProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class,method="updateProvider")

	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="selectAllProvider")
	public List<Map<String,Object>> getAll(@Param("tableName") String tableName);

	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="getPageProvider")
	public List<Map<String,Object>> getPage(@Param("tableName") String tableName, @Param("start") int start, @Param("count") int count, @Param("order") String sortOrder, @Param("where") String where);

	@SelectProvider(type=es.carm.mydom.DAO.LotusDaoMapperProvider.class, method="getCountProvider")
	public int getCount(@Param("tableName") String tableName, @Param("where") String where);

