package es.carm.mydom.security;

import java.util.List;

public interface UserInfo {
	public String getUser();
	public String getRoles(String separator);
	public List<String> getRoleList();
	public boolean hasRole(String rol);
	public boolean notHasRole(String rol);
}
