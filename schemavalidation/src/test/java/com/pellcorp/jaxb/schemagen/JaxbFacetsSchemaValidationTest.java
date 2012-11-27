package com.pellcorp.jaxb.schemagen;

import javax.xml.ws.soap.SOAPFaultException;

import com.pellcorp.test.AbstractTestCase;
import com.pellcorp.test.TestUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

// Copied from cxf systests/jaxws
public class JaxbFacetsSchemaValidationTest extends AbstractTestCase {
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
    public void testInvalidFirstName() {
        Person person = new Person();
        person.setFirstName("jason"); // must have first uppercase character
        person.setLastName("Pell"); // this is valid
        
        try {
            client.save(person);
            fail("Expected exception");
        } catch (SOAPFaultException sfe) {
            assertTrue(sfe.getMessage().contains("Unmarshalling Error: cvc-pattern-valid"));
        }
    }
    
    @Test
    public void testDocumentation() throws Exception {
        String schemaDoc = readWsdl(PersonService.class);
        System.out.println(schemaDoc);
    }
}
