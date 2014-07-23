package es.carm.mydom.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DatabaseException extends Exception {

	public DatabaseException() {
		super();
	}

	public DatabaseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DatabaseException(String arg0) {
		super(arg0);
	}

	public DatabaseException(Throwable arg0) {
		super(arg0);
	}

}
