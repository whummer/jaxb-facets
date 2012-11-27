package com.pellcorp.jaxb.schemagen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.validation.DefaultSchemaValidationTypeProvider;
import org.apache.cxf.feature.validation.SchemaValidationFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.testutil.common.TestUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

// Copied from cxf systests/jaxws
public class JaxbFacetsSchemaValidationTest extends Assert {
    static final String PORT = TestUtil.getPortNumber(JaxbFacetsSchemaValidationTest.class);

    private static List<Server> serverList = new ArrayList<Server>();
    private static PersonService client;

    @BeforeClass
    public static void startServers() throws Exception {
        createServer(PersonService.class, new PersonServiceImpl());
        client = createClient(PersonService.class);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        for (Server server : serverList) {
            server.stop();
        }
    }

    static String getAddress(Class<?> sei) {
        return "http://localhost:" + PORT + "/" + sei.getSimpleName();
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
    
    private static <T> T createClient(Class<T> serviceClass) {
        JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
        clientFactory.setServiceClass(serviceClass);

        // ensure all client schema validation is disabled
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.NONE);
        clientFactory.setProperties(properties);

        clientFactory.setAddress(getAddress(serviceClass));

        return (T) clientFactory.create();
    }

    public static Server createServer(Class<?> serviceInterface, Object serviceImpl)
        throws IOException {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceClass(serviceImpl.getClass());
        svrFactory.setAddress(getAddress(serviceInterface));
        svrFactory.setServiceBean(serviceImpl);
        Server server = svrFactory.create();
        serverList.add(server);
        return server;
    }
}
