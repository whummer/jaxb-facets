package at.ac.tuwien.infosys.jaxb;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import com.pellcorp.jaxb.test.AbstractTestCase;

/**
 * This class will test if JAXP and CXF can use the generated schema to perform validation
 */
public class SchemaValidationTest extends AbstractTestCase {

    @BeforeClass
    public static void startServers() throws Exception {
    	/* disnable XSD 1.1 features */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(false);

        createServer(PersonService.class, new PersonServiceImpl());
    }

    @AfterClass
    public static void cleanup() throws Exception {
    	/* re-enable XSD 1.1 features */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(true);

        cleanupServers();
    }

    @Test
    public void testValidateGeneratedXsd() throws Exception {
        String xml = getWsdlSchemaAsString(PersonService.class);
        Source schemaSource = new StreamSource(new StringReader(xml));

        try {
        	System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/XML/XMLSchema/v1.1",
                    "org.apache.xerces.jaxp.validation.XMLSchema11Factory");
        	//SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        	SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
            sf.newSchema(schemaSource);
            assertTrue(true); // no errors
        } catch (SAXParseException e) {
        	e.printStackTrace();
        	System.err.println(xml);
            fail("Schema parsing error. See stack trace for details.");
        }
    }

}
