package es.carm.mydom.security;

import java.util.List;

public interface SecurityPolice {
	public void checkPermission(String resource,String action,String roles,String separator) throws SecurityException;
	public void checkPermission(String resource,String action,List<String> roles) throws SecurityException;
	public void checkPermission(String resource,String action,UserInfo userInfo) throws SecurityException;
}
