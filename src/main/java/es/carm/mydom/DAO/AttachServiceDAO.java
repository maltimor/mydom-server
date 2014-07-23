package es.carm.mydom.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.Resource;

import es.carm.mydom.entity.AttachService;
import es.carm.mydom.entity.Attachment;
import es.carm.mydom.filters.utils.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AttachServiceDAO implements AttachService {
	final Logger log = LoggerFactory.getLogger(AttachServiceDAO.class);
	private DirContext dirContext;
	
	public DirContext getDirContext() {
		return dirContext;
	}
	public void setDirContext(DirContext dirContext) {
		this.dirContext = dirContext;
	}

	public boolean hasAttachments(String unid) throws DatabaseException {
		try {
			log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.hasAttachments:"+unid);
			
			Object obj = dirContext.lookup(unid);
			return true;
		} catch (NamingException e) { }

		return false;
	}

	public List<String> getAllAttachmentsNames(String unid) throws DatabaseException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.getAllAttachmentsNames:"+unid);
		List<String> lres = new ArrayList<String>();
		try {
			NamingEnumeration<NameClassPair> list = dirContext.list(unid);
			while (list.hasMore()) {
				NameClassPair nc = (NameClassPair)list.next();
				log.debug("nc="+nc);

				String fileName = nc.getName();
				try{
					//Cualquier error aqui no insertara el attachment y prosigue con el siguiente
					Object obj = dirContext.lookup(unid+File.separator+fileName);
					log.debug("--->"+obj);
					Resource res= (Resource) obj;
					lres.add(fileName);
					log.debug("--->="+fileName);
				} catch (Exception e){
				}
			}
		} catch (NamingException e) {
		}

		return lres;
	}

	public List<Attachment> getAllAttachments(String unid) throws DatabaseException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.getAllAttachments:"+unid);
		List<Attachment> lres = new ArrayList<Attachment>();
		try {
			NamingEnumeration<NameClassPair> list = dirContext.list(unid);
			while (list.hasMore()) {
				NameClassPair nc = (NameClassPair)list.next();
				log.debug("nc="+nc);

				String fileName = nc.getName();
				try{
					//Cualquier error aqui no insertara el attachment y prosigue con el siguiente
					Object obj = dirContext.lookup(unid+File.separator+fileName);
					log.debug("--->"+obj);
					Resource res= (Resource) obj;
					Attachment attach = new Attachment();
					attach.setName(fileName);
					attach.setBytes(Resources.getBytesFromStream(res));
					lres.add(attach);
					log.debug("--->="+attach.getName());
				} catch (Exception e){
				}
				
			}
		} catch (NamingException e) {
		}

		return lres;
	}

	public void deleteAllAttachments(String unid) throws DatabaseException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.deleteAllAttachments:"+unid);
		try{
			//TODO comprobar primero si existe el directorio, asi me ahorro una excepcion
			NamingEnumeration<NameClassPair> list = dirContext.list(unid);
			while (list.hasMore()) {
				NameClassPair nc = (NameClassPair)list.next();
				try{
					log.debug("Borrando:"+nc.getName());
					dirContext.unbind(unid+File.separator+nc.getName());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			dirContext.destroySubcontext(unid);
			log.debug("OK");
		} catch (NamingException e){
			log.warn("@@@@@@@@@@@@@@@@@@@@@ AttachService.deleteAllAttachments: NO HAY ATTACHMENTS: "+unid);
		}
	}

	public void deleteAttachment(String unid, String fileName) throws DatabaseException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.deleteAttachmentsName:"+unid+" "+fileName);
		try{
			dirContext.unbind(unid+File.separator+fileName);
			log.debug("OK");
			NamingEnumeration<NameClassPair> list = dirContext.list(unid);
			if (!list.hasMore()){
				try{
					log.debug("Borrando dir:"+unid);
					dirContext.destroySubcontext(unid);
					log.debug("OK");
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		} catch (NamingException e){
			e.printStackTrace();
		}
	}

	public void addAttachment(String unid, Attachment attach) throws DatabaseException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.addAttachmentsName:"+unid+" "+attach.getName());
		try {
			Resource resource = new Resource(attach.getBytes());
			log.debug("Antes de create");
			try { dirContext.createSubcontext(unid); } catch (Exception e){}
			String name = attach.getName();
			name = name.replace("\\", "/");
			int i1 = name.lastIndexOf("/");
			if (i1>0) name = name.substring(i1+1);
			log.debug("Antes de rebind:name="+name);
			dirContext.rebind(unid+File.separator+name, resource);
			log.debug("OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Attachment getAttachment(String unid, String fileName) throws DatabaseException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@ AttachService.getAttachment:"+unid+" "+fileName);
		try{
			Object obj = dirContext.lookup(unid+File.separator+fileName);
			log.debug("--->"+obj);
			if (obj==null) return null;
			try{
				Resource res= (Resource) obj;
				Attachment attach = new Attachment();
				attach.setName(fileName);
				attach.setBytes(Resources.getBytesFromStream(res));
				return attach;
			} catch (Exception e){
			}
		} catch (NamingException e) {
		}
		return null;
	}

}
