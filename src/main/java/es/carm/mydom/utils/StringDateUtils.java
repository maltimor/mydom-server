package es.carm.mydom.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StringDateUtils {
	private int dia;
	private int mes;
	private int anyo;
	private Date fecha;
	private int trimestre;
	private int cuatrimestre;
	private int semestre;
	private String mesTxt;
	public String getMesTxt() {
		return mesTxt;
	}

	private boolean valid;
	
	public StringDateUtils(String fecha){
		
		try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("d/M/yy");
            formatoFecha.setLenient(false);
            this.fecha = formatoFecha.parse(fecha);
        } catch (ParseException e) {
        	valid=false;
        	return;
        }
      	valid = true;
      	GregorianCalendar fechatrabajo= new GregorianCalendar();
      	fechatrabajo.setTime(this.fecha);
		this.dia = fechatrabajo.get(Calendar.DAY_OF_MONTH);
		this.mes =fechatrabajo.get(Calendar.MONTH)+1;
		this.anyo = fechatrabajo.get(Calendar.YEAR);
		// Cálculo de Trimestre
		if (mes<4)
			this.trimestre=1;
		else if (mes<7)
			this.trimestre=2;
		else if (mes<10)
			this.trimestre=3;
		else
			this.trimestre=4;
		// Cálculo de Cuatrimestre
		if (mes<5)
			this.cuatrimestre=1;
		else if (mes<9)
			this.cuatrimestre=2;
		else 
			this.cuatrimestre=3;
		// Cálculo de Semestre
		if (mes<7)
			this.semestre=1;
		else 
			this.semestre=2;
		// Mes en cadena
		String[] mtxt={"enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre"};
		this.mesTxt=mtxt[fechatrabajo.get(Calendar.MONTH)];
	}

	public int getDia() {
		return dia;
	}

	public int getMes() {
		return mes;
	}

	public int getTrimestre() {
		return trimestre;
	}

	public int getCuatrimestre() {
		return cuatrimestre;
	}

	public int getSemestre() {
		return semestre;
	}

	public int getAnyo() {
		return anyo;
	}

	public Date getFecha() {
		return fecha;
	}

	public boolean isValid() {
		return valid;
	}
}


