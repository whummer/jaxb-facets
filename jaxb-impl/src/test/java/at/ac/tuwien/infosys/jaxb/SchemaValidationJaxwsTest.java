package at.ac.tuwien.infosys.jaxb;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pellcorp.jaxb.test.AbstractTestCase;

/**
 * This class will test if JAXP and CXF can use the generated schema to perform validation
 */
public class SchemaValidationJaxwsTest extends AbstractTestCase {
    private static PersonService client;

    @BeforeClass
    public static void startServers() throws Exception {
    	/* disable XSD 1.1 features! */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(false);

        createServer(PersonService.class, new PersonServiceImpl());
        client = createClient(PersonService.class);
    }

    @AfterClass
    public static void cleanup() throws Exception {
    	/* re-sable XSD 1.1 features! */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(true);

        cleanupServers();
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
    
    public static void main(String[] args) throws IOException {
        createServer(PersonService.class, new PersonServiceImpl());
        cleanupServers();
        createServer(PersonService.class, new PersonServiceImpl());
        cleanupServers();
        System.exit(0);
	}
}
