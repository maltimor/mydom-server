package es.carm.mydom.DAO;

import java.util.List;

@Deprecated
public class TableModel {
	// campos para la gestion del modelo tabla
	private String tableName;
	private List<ColumnModel> cols;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<ColumnModel> getCols() {
		return cols;
	}
	public void setCols(List<ColumnModel> cols) {
		this.cols = cols;
	}
}
