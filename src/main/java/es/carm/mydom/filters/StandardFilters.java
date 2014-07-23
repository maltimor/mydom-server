package es.carm.mydom.filters;

import java.util.HashMap;

import es.carm.mydom.servlet.HttpFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class StandardFilters extends HashMap<String,HttpFilter> {
	private ResourceAction resourceAction;
	private FormAction formAction;
	private ViewAction viewAction;
	private PageAction pageAction;
	private ServerAction serverAction;
	private FramesetAction framesetAction;
	private AgentAction agentAction;
	
	public StandardFilters(){
		super();
		resourceAction = new ResourceAction();
		formAction = new FormAction();
		viewAction = new ViewAction();
		pageAction = new PageAction();
		serverAction = new ServerAction();
		framesetAction = new FramesetAction();
		agentAction = new AgentAction();
		this.put("",resourceAction);
		this.put("openform",formAction);
		this.put("opendocument",formAction);
		this.put("editdocument",formAction);
		this.put("createdocument",formAction);
		this.put("savedocument",formAction);
		this.put("deletedocument",formAction);
		this.put("refreshdocument",formAction);
		this.put("openelement", formAction);
		this.put("editelement",formAction);
		this.put("openview",viewAction);
		this.put("searchview",viewAction);
		this.put("getdata",viewAction);
		this.put("getcount",viewAction);
		this.put("openagent",agentAction);
		this.put("openpage",pageAction);
		this.put("openframeset",framesetAction);
		this.put("openaction",serverAction);
		this.put("getcreatetable",serverAction);
		this.put("getallcreatetable",serverAction);
		this.put("getallesquemas",serverAction);
	}
}
