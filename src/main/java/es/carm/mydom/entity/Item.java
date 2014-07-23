package es.carm.mydom.entity;

import java.io.Serializable;

@Deprecated

public class Item implements Serializable {
	private static final long serialVersionUID = 8115965130882454885L;
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_ATTACHMENT = 1;
	private String name;
	private String mimeType;
	private String fileName;
	private byte[] bytes;
	private int type;
	
	public Item(String name,byte[] bytes){
		this(name,bytes,Item.TYPE_DEFAULT,"text/plain","");
	}
	
	public Item(String name,byte[] bytes,int type,String mimeType,String fileName){
		this.name = name;
		this.bytes = bytes.clone();
		this.type = type;
		this.mimeType = mimeType;
		this.fileName = fileName;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	public String getFileName() {
		return fileName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public String getName() {
		return name;
	}
	public int getType() {
		return type;
	}
	public String getValue() {
		return new String(bytes);
	}
	public void setValue(String value) {
		if (value==null) value="";
		this.bytes = value.getBytes();
	}

/*	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setValue(String value) {
		this.bytes = value.getBytes();
	}*/
}
