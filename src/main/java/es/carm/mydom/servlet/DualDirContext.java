package es.carm.mydom.servlet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.naming.NamingContextEnumeration;
import org.apache.naming.NamingEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DualDirContext implements DirContext {
	final Logger log = LoggerFactory.getLogger(DualDirContext.class);
	private DirContext primary;
	private DirContext secondary;
	
	public DirContext getPrimary() {
		return primary;
	}
	public void setPrimary(DirContext primary) {
		this.primary = primary;
	}
	public DirContext getSecondary() {
		return secondary;
	}
	public void setSecondary(DirContext secondary) {
		this.secondary = secondary;
	}
	
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		//hago un merge
		List<NamingEntry> lst = new ArrayList<NamingEntry>();
		
		try{
			NamingEnumeration<NameClassPair> res2 = primary.list(name);
			if (res2!=null) {
				while (res2.hasMoreElements()) {
					NameClassPair ncp = res2.nextElement();
					NamingEntry ne = new NamingEntry(ncp.getName(),ncp.getClassName(),NamingEntry.ENTRY);
					lst.add(ne);
				}
			}
		} catch (NamingException ne){}
		
		try{
			NamingEnumeration<NameClassPair> res2 = secondary.list(name);
			if (res2!=null) {
				while (res2.hasMoreElements()) {
					NameClassPair ncp = res2.nextElement();
					NamingEntry ne = new NamingEntry(ncp.getName(),ncp.getClassName(),NamingEntry.ENTRY);
					lst.add(ne);
				}
			}
		} catch (NamingException ne){}
		
		return new NamingContextEnumeration(lst.iterator());
	}
	public Object lookup(String name) throws NamingException {
		log.debug("### Lookup primary:"+name);
		try{
			return primary.lookup(name);
		} catch (NamingException ne){
			log.debug("### Lookup secundary:"+name);
			return secondary.lookup(name);
		}
	}

	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void bind(Name name, Object obj) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void bind(String name, Object obj) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void close() throws NamingException {
		throw new NamingException("No implementado");
	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new NamingException("No implementado");
	}

	public String composeName(String name, String prefix) throws NamingException {		
		throw new NamingException("No implementado");
	}

	public Context createSubcontext(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Context createSubcontext(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void destroySubcontext(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void destroySubcontext(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new NamingException("No implementado");
	}

	public String getNameInNamespace() throws NamingException {
		throw new NamingException("No implementado");
	}

	public NameParser getNameParser(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NameParser getNameParser(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Object lookup(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Object lookupLink(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Object lookupLink(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void rebind(Name name, Object obj) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void rebind(String name, Object obj) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void rename(Name oldName, Name newName) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void rename(String oldName, String newName) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void unbind(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void unbind(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void bind(String name, Object obj, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Attributes getAttributes(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Attributes getAttributes(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
		throw new NamingException("No implementado");
	}

	public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
		throw new NamingException("No implementado");
	}

	public DirContext getSchema(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public DirContext getSchema(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public DirContext getSchemaClassDefinition(Name name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public DirContext getSchemaClassDefinition(String name) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
		throw new NamingException("No implementado");
	}

	public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
		throw new NamingException("No implementado");
	}
}

