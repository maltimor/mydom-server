package es.carm.mydom.servlet;

import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertyHolder extends PropertyPlaceholderConfigurer {
	private String extLocation;
	private String localhost;
	private String HOSTNAME_PLACEHOLDER = "_HOST_NAME_";
	private String DEFAULT_HOSTNAME = "localhost";
	private Properties extPrp;

	public PropertyHolder() {
		super();
		extLocation = "";
		extPrp = new Properties();
		try {
			localhost = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			localhost = DEFAULT_HOSTNAME;
		}
	}
	
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props) {
		String res = super.resolvePlaceholder(placeholder, props);
		
//		System.out.println("### Property get:"+placeholder+" = "+res);
		//tiene preferencia las propiedades externas que las del propio resolvedor
		if (extPrp.containsKey(placeholder)) res = extPrp.getProperty(placeholder);
		
		//hago los cambios opoertunos para autodescubrimiento de parametros
		if (res!=null) res = res.replaceAll(HOSTNAME_PLACEHOLDER, localhost);
//		System.out.println("### Property res -> "+res);
		
		return res;
	}

	public String get(String key){
		
		return "";
	}
	
	public String getExtLocation() {
		return extLocation;
	}

	public void setExtLocation(String extLocation) {
		// La localizacion del fichero externo será siempre relativo al path del tomcat
		this.extLocation = extLocation;
		System.out.println("########### Property: setExtLocation="+extLocation);
		if (!this.extLocation.startsWith(File.separator)) this.extLocation = File.separator+this.extLocation;
		this.extLocation = System.getProperty("catalina.base")+this.extLocation;
		System.out.println("extLocation -> "+extLocation);

		try{
			File f = new File(this.extLocation);
			System.out.println("CanonicalPath="+f.getCanonicalPath());
			if (f.exists()) extPrp.load(new FileReader(f));
//			for(String key:extPrp.stringPropertyNames()){
//				System.out.println("### "+key +" = "+extPrp.get(key));
//			}
			System.out.println("########### Property: OK");
		} catch (Exception e){
			e.printStackTrace();
			this.extLocation = "";
		}
		
		
	}
}
