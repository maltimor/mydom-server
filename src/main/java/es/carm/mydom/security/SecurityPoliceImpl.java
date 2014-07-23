package es.carm.mydom.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityPoliceImpl implements SecurityPolice {
	private Map<String, List<SecurityEntry>> entries;
	private Map<String, Map<String,List<String>>> map;
//	private String fileEntries;
	private Boolean denyWhenNoMatch;
	private Boolean needAllRoles;
	
	public SecurityPoliceImpl(){
		this.map = new HashMap<String,Map<String,List<String>>>();
	}
/*	public void setFileEntries(String fileEntries) {
		this.fileEntries = fileEntries;
		internalSetEntries();
	}*/
	
	public void setEntries(Map<String, List<SecurityEntry>> entries) {
		this.entries = entries;
		internalSetEntries();
	}
	public void setDenyWhenNoMatch(Boolean denyWhenNoMatch) {
		this.denyWhenNoMatch = denyWhenNoMatch;
	}
	public void setNeedAllRoles(Boolean needAllRoles) {
		this.needAllRoles = needAllRoles;
	}



	private void internalSetEntries(){
		//optimizo en un array especial la consulta, invierto la definicion de las entradas
		System.out.println("### SecurityPolice: internalEntries");
		
		for(String rol:entries.keySet()){
			System.out.println("### Rol: "+rol);
			for(SecurityEntry entry:entries.get(rol)){
				rol = rol.toLowerCase();
				String resource = entry.getResource().toLowerCase();
				String action = entry.getAction().toLowerCase();
				System.out.println("### Entry: "+resource+" "+action);
				//ya tengo una trileta rol,resource,action ahora la inserto inversamente
				Map<String,List<String>> res = map.get(resource);
				if (res==null) {
					//es la primera del resource
					Map<String,List<String>> act = new HashMap<String,List<String>>();
					List<String> rl = new ArrayList<String>();
					rl.add(rol);
					act.put(action, rl);
					map.put(resource,act);
				} else {
					//el resource no es nuevo, inserto/actualizo
					List<String> rl = res.get(action);
					if (rl!=null) rl.add(rol);
					else {
						//es la primera accion
						rl = new ArrayList<String>();
						rl.add(rol);
						res.put(action, rl);
					}
				}
			}
		}
		System.out.println("-------------------------------");
		for(String resource:map.keySet()){
			System.out.println("### Resource: "+resource);
			Map<String,List<String>> res = map.get(resource);
			for(String action:res.keySet()){
				for(String rol:res.get(action)){
					System.out.println("### Action: "+action+" -> rol="+rol);
				}
			}
		}
		System.out.println("-------------------------------");
	}

	/*
	 * Comprueba si tengo el permiso requerido, y si no devuelve una excepcion
	 * Tiene dos parametros: denyWhenNoMatch y needAllRoles
	 * denyWhenNoMatch -> indica qué hace si no hay un match (no existe correspondencia resource+action en la lista)
	 * 		Si es True: da excepción en caso de noMatch
	 * 		Si es False: no da excepción
	 * needAllRoles -> indica el comportamiento en caso de haber varios matchs
	 * 		Si es True: necesito tener todos los roles de cada match (lógica AND)
	 * 		Si es False: sólo necesito tener alguno de dichos roles (lógica OR) 
	 * @see es.carm.mydom.security.SecurityPolice#checkPermission(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void checkPermission(String resource, String action, String roles,String separator) throws SecurityException {
		//veo si existe algun match que coincida con el recurso a comprobar
		boolean huboMatch = false;
		resource = resource.toLowerCase();
		action = action.toLowerCase();
		roles = roles.toLowerCase();
		
		System.out.println("### SecurityPolice: check: ("+resource+","+action+") :"+roles);
		for(String mresource:map.keySet()){
			if (mresource.equals(resource)||mresource.equals("*")){
				Map<String,List<String>> res = map.get(mresource);
				for(String maction:res.keySet()){
					if (maction.equals(action)||maction.equals("*")){
						//un match.
						huboMatch = true;
						System.out.println("Match: ("+mresource+","+maction+")");
						for(String mrol:res.get(maction)){
							//para cada rol y segun los parametros de configuracion aplico una logica u otra
							if (roles.contains(separator+mrol+separator)||mrol.equals("*")){
								//tengo el rol. Si no hace falta todos los roles salgo sin mas
								System.out.println("tengo el rol="+mrol);
								if (!needAllRoles) return;
							} else {
								//no tengo el rol. Y si hace falta el rol doy una excepcion
								System.out.println("NO tengo el rol="+mrol);
								if (needAllRoles) throw new SecurityException();
							}
						}
					}
				}
			}
		}
		//llegados aqui si ha habido match habra que comprobar needAllRoles
		//si no ha habido match hago lo que diga el parametro denyWhenNoMatch
		System.out.println("### SecurityPolice: End");
		
		if (huboMatch) {
			//ha habido match, pero si no necesito todos los roles no debería haber llegado aqui
			//por lo tanto si !needAllRoles es un error
			if (!needAllRoles) throw new SecurityException();
			return;
		} else {
			//no hubo match, hago lo que diga el parametro
			if (denyWhenNoMatch) throw new SecurityException();
		}
	}

	public void checkPermission(String resource, String action, List<String> roles) throws SecurityException {
	}

	public void checkPermission(String resource, String action, UserInfo userInfo) throws SecurityException {
	}

}
