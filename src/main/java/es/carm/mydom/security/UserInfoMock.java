package es.carm.mydom.security;

import java.util.ArrayList;
import java.util.List;

public class UserInfoMock implements UserInfo {
	private String defaultUser;
	private String defaultRoles;
	private String defaultRolesLower;
	private String separator;
	
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public void setDefaultUser(String defaultUser) {
		this.defaultUser = defaultUser;
	}
	public void setDefaultRoles(String defaultRoles) {
		this.defaultRoles = defaultRoles;
		this.defaultRolesLower = defaultRoles.toLowerCase();
	}
	
	public UserInfoMock(){
		this.defaultUser = "Anonimo";
		this.defaultRoles = "";
		this.defaultRolesLower = "";
	}

	public String getUser() {
		return defaultUser;
	}

	public String getRoles(String separator) {
		return defaultRoles.replace(this.separator, separator);
	}

	public List<String> getRoleList() {
		List<String> res = new ArrayList<String>();
		String[] aux = defaultRoles.split(this.separator);
		for(String rol:aux) res.add(rol);
		return res;
	}

	public boolean hasRole(String rol) {
		return (this.separator+defaultRolesLower+this.separator).contains(this.separator+rol.toLowerCase()+this.separator);
	}

	public boolean notHasRole(String rol) {
		return !hasRole(rol);
	}

}
