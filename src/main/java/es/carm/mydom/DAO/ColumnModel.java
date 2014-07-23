package es.carm.mydom.DAO;
import es.carm.mydom.filters.FramesetAction;
@Deprecated

public class ColumnModel {

	private String name;
	private boolean key;
	private String type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isKey() {
		return key;
	}
	public void setKey(boolean key) {
		this.key = key;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
