package es.carm.mydom.utils;

public class StringUtils {
	
	public static String right(String cad, String car){
		/**
		 * Función que simula el comportamiento de @Right de Lotus.
		 * @param cad
		 * @param car
		 * @return Devuelve los caracteres más a la derecha de cad. Si cadena = null devuelve ""
		 */
		String res="";
		int aux=cad.indexOf(car);
		if (aux!=-1)
			res=cad.substring(aux+car.length());
		return res;
	}
	public static String left(String cad, String car){
		/**
		 * Función que simula el comportamiento de @Left de Lotus.
		 * @param cad
		 * @param car
		 * @return Devuelve los caracteres más a la izquierda de cad. Si cadena = null devuelve ""
		 */
		String res="";
		int aux=cad.indexOf(car);
		if (aux!=-1)
			res=cad.substring(0,aux);
		return res;
	}
	public static String rightBack(String cad, String car){
		/**
		 * Función que simula el comportamiento de @RightBack de Lotus.
		 * @param cad
		 * @param car
		 * @return Devuelve los caracteres más a la derecha de cad. Si cadena = null devuelve ""
		 */
		String res="";
		int aux=cad.lastIndexOf(car);
		if (aux!=-1)
			res=cad.substring(aux+car.length());
		return res;
	}
	public static String leftBack(String cad, String car){
		/**
		 * Función que simula el comportamiento de @LeftBack de Lotus.
		 * @param cad
		 * @param car
		 * @return Devuelve los caracteres más a la izquierda de cad. Si cadena = null devuelve ""
		 */
		String res="";
		int aux=cad.lastIndexOf(car);
		if (aux!=-1)
			res=cad.substring(0,aux);
		return res;
	}
}
