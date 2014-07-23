package es.carm.mydom.entity;

import java.io.Writer;
import java.util.List;

import es.carm.mydom.parser.BeanMethod;
import es.carm.mydom.parser.Field;
import es.carm.mydom.parser.ViewDef;

public interface ServerDao {
	public String registerAction(BeanMethod beanMethod);
	public Writer getLog(String ruta);
	public ViewDef getViewDef(String name);
	public List<Field> getForm(String name);
	//TODO getProgram o el array de fields
	public String getTempPath();
	public String getResourcePath();
	public String getResourceCharset();
	public String getDomainProxy();
	public Object getEnvProperty(String prp);
	public int getDefaultResults();
	public int getMaxResults();
}
