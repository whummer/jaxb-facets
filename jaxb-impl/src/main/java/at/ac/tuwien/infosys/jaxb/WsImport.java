package at.ac.tuwien.infosys.jaxb;

/**
 * Simple wrapper around com.sun.tools.ws.WsImport which avoids
 * the classloading magic of the wsimport command.
 * @author Waldemar Hummer
 */
public class WsImport {

	public static void main(String[] args) throws Throwable {
		/* disable XSD 1.1 features */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(false);

    	/* run wsimport */
		com.sun.tools.ws.WsImport.doMain(args);
	}
	
}
