package at.ac.tuwien.infosys.jaxb;

/**
 * Simple wrapper around com.sun.tools.ws.WsImport which avoids
 * the classloading magic of the wsimport command.
 * @author Waldemar Hummer
 */
public class WsImport {

	public static void main(String[] args) throws Throwable {
		com.sun.tools.ws.WsImport.doMain(args);
	}
	
}
