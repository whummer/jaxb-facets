package at.ac.tuwien.infosys.jaxb;

import java.io.IOException;

import com.pellcorp.test.AbstractTestCase;

import org.apache.cxf.endpoint.Endpoint;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmlSchemaEnhancerTest extends AbstractTestCase {
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
    public void testEnumLiteralDocumentation() throws IOException {
        String schemaDoc = readWsdl(PersonService.class);
        System.out.println(schemaDoc);
    }
    
    
}
