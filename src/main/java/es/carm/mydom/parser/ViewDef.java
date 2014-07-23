package es.carm.mydom.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ViewDef {
	private String name;
	private String sql;
	private BeanMethod sqlProvider;
	private String mode;
	private boolean isEntity;
	private int maxResults;
	private List<ColumnDef> cols;
	
	public ViewDef(){
		this.name = "";
		this.sql = "";
		this.sqlProvider = null;
		this.mode = "basic";
		this.isEntity = false;
		this.maxResults = 0;		//por defecto no limito los resultados
		this.cols = new ArrayList<ColumnDef>();
	}
	public void addColumnDef(ColumnDef col){
		cols.add(col);
	}
	public List<ColumnDef> getKeyColumns() {
		List<ColumnDef> res = new ArrayList<ColumnDef>();
		for(ColumnDef col:cols){
			if (col.isKey()) res.add(col);
		}
		return res;
	}
	public List<String> getKeyColumnsNames() {
		List<String> res = new ArrayList<String>();
		for(ColumnDef col:cols){
			if (col.isKey()) res.add(col.getName());
		}
		return res;
	}
	
	public String toString(){
		String res ="";
		res+="name="+name;
		res+="|mode="+mode;
		for(ColumnDef col:cols){
			res+="["+col.toString()+"]";
		}
		return res;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public BeanMethod getSqlProvider() {
		return sqlProvider;
	}
	public void setSqlProvider(BeanMethod sqlProvider) {
		this.sqlProvider = sqlProvider;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public boolean isEntity() {
		return isEntity;
	}
	public void setEntity(boolean isEntity) {
		this.isEntity = isEntity;
	}
	public List<ColumnDef> getCols() {
		return cols;
	}
	public void setCols(List<ColumnDef> cols) {
		this.cols = cols;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
}
