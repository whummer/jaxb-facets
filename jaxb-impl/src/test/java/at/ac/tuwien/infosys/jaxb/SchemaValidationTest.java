package at.ac.tuwien.infosys.jaxb;

import com.pellcorp.jaxb.test.AbstractTestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class will test if CXF can use the generated schema to perform validation,
 * for now it's just a simple placeholder to make sure stuff is not completely broken.
 * 
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
    
    // this test is currently broken due to introduction of xs:annotation attribute support.
    @Test
    @Ignore
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
