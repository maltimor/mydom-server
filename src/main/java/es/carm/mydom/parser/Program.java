package es.carm.mydom.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Program {
	final Logger log = LoggerFactory.getLogger(Program.class);
	protected List<MicroAction> actions;
//	private List<Field> fields;
//	private ViewDef viewDef;
//	private BeanMethod queryOpen;
//	private BeanMethod querySave;
//	private BeanMethod queryDelete;
//	private BeanMethod $$return;
	
	public Program(){
		this.actions = new ArrayList<MicroAction>();
//		this.fields = new ArrayList<Field>();
	}
	
	public void concat(Program prg){
		actions.addAll(prg.actions);
//		fields.addAll(prg.fields);
//		if (prg.getQueryOpen()!=null) queryOpen = prg.getQueryOpen();
//		if (prg.getQuerySave()!=null) querySave = prg.getQuerySave();
//		if (prg.getQueryDelete()!=null) queryDelete = prg.getQueryDelete();
	}
	
//	public void addField(Field field){
//		fields.add(field);
//	}
//	public Field getField(String name){
//		for(Field field:fields){
//			if (field.getItemName().equals(name)) return field;
//		}
//		return null;
//	}

	public void add(MicroAction action){
		actions.add(action);
	}
	
	public List<String> execute(ProgramContext pc) throws ParserException{
		long initTime = System.currentTimeMillis();
		pc.setPosAct(0);
		//TODO añadir Puntos de PC para poder implementar los if, y los whiles, etcc
		while (pc.getPosAct()<actions.size()){
			actions.get(pc.incrementAndGetPostAct()).execute(pc);
		}

		long endTime = System.currentTimeMillis();
		log.debug("Program.Execute:"+((endTime-initTime)/1000.0)+" seg.");	
		return pc.getBody();
	}	
//	public BeanMethod getQueryOpen() {
//		return queryOpen;
//	}
//
//	public void setQueryOpen(BeanMethod queryOpen) {
//		this.queryOpen = queryOpen;
//	}
//
//	public BeanMethod getQuerySave() {
//		return querySave;
//	}
//
//	public void setQuerySave(BeanMethod querySave) {
//		this.querySave = querySave;
//	}
//
//	public BeanMethod getQueryDelete() {
//		return queryDelete;
//	}
//
//	public void setQueryDelete(BeanMethod queryDelete) {
//		this.queryDelete = queryDelete;
//	}
//	public List<Field> getFields() {
//		return fields;
//	}
//
//	public void setFields(List<Field> fields) {
//		this.fields = fields;
//	}
//
//	public BeanMethod get$$return() {
//		return $$return;
//	}
//
//	public void set$$return(BeanMethod $$return) {
//		this.$$return = $$return;
//	}
//
//	public ViewDef getViewDef() {
//		return viewDef;
//	}
//
//	public void setViewDef(ViewDef viewDef) {
//		this.viewDef = viewDef;
//	}
}
