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
    private static PersonService client;
    
    @BeforeClass
    public static void startServers() throws Exception {
        createServer(PersonService.class, new PersonServiceImpl());
        client = createClient(PersonService.class);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        cleanupServers();
    }
    
    @Test
    public void testValidateGeneratedXsd() throws Exception {
        String xml = getWsdlSchemaAsString(PersonService.class);
        Source schemaSource = new StreamSource(new StringReader(xml));
        
        try {
            SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            sf.newSchema(schemaSource);
            assertTrue(true); // no errors
        } catch (SAXParseException e) {
            fail("Expected failure because attribute is on xs:annotation");
        }
    }
    
    @Test
    public void testInvalidFirstName() {
        Person person = new Person();
        person.setFirstName("jason"); // must have first uppercase character
        person.setLastName("Pell"); // this is valid
        
        try {
            client.save(person);
            fail("Expected exception");
        } catch (Exception sfe) {
            assertTrue(sfe.getMessage().contains("Unmarshalling Error: cvc-pattern-valid"));
        }
    }
}
