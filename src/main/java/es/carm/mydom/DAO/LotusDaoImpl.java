/* |UNID#T#32|FECHAINICIO#F#|FECHAFIN#F#|TITULO#T#80|PERSONACONTACTO#T#30|TELEFONO#T#20|OBSERVACIONES#T#3250|URLIMAGEN#T#20|FORM#T#10|ID#T#10|EXPORTACION#T#10|ESPECIFICACIONES#T#50|
package es.carm.mydom.DAO;

import java.sql.SQLException;
import java.util.Map;


import org.slf4j.LoggerFactory;
public class LotusDaoImpl implements Database {
	private LotusMapper lotusMapper;
