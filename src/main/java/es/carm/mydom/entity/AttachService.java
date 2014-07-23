package es.carm.mydom.entity;

import java.util.List;

import es.carm.mydom.DAO.DatabaseException;

public interface AttachService {
	public boolean hasAttachments(String unid) throws DatabaseException;
	public List<String> getAllAttachmentsNames(String unid) throws DatabaseException;
	public List<Attachment> getAllAttachments(String unid) throws DatabaseException;
	public void deleteAllAttachments(String unid) throws DatabaseException;
	public void deleteAttachment(String unid,String fileName) throws DatabaseException;
	public void addAttachment(String unid,Attachment attach) throws DatabaseException;
	public Attachment getAttachment(String unid,String fileName) throws DatabaseException;
}
