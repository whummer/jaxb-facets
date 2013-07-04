package com.pellcorp.jaxb.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;

import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.testutil.common.TestUtil;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Assert;

public abstract class AbstractTestCase extends Assert {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestCase.class);
    
    private static Map<String, String> m = new HashMap<String, String>();
    protected static NamespaceContext ctx;
    protected static XpathEngine engine = XMLUnit.newXpathEngine();
    
    protected static final String PORT = TestUtil.getPortNumber(AbstractTestCase.class);
    private static List<Server> serverList = new ArrayList<Server>();
    
    static {
        m.put("xs", XMLConstants.W3C_XML_SCHEMA_NS_URI);
        m.put("wsdl", JdomUtils.NS_WSDL);
        ctx = new SimpleNamespaceContext(m);
        
        engine.setNamespaceContext(ctx);
    }
    
    protected static String getAddress(Class<?> sei) {
        return "http://localhost:" + PORT + "/" + sei.getSimpleName();
    }
    
    protected static void cleanupServers() {
        for (Server server : serverList) {
            server.stop();
        }
    }

    protected static void addNamespace(String prefix, String uri) {
        m.put(prefix, uri);
        ctx = new SimpleNamespaceContext(m);
        engine.setNamespaceContext(ctx);
    }

    protected static String getWsdlSchemaAsString(Class<?> serviceClass) throws IOException {
        String wsdlContent = readWsdl(serviceClass);
        return JdomUtils.toString(JdomUtils.getWsdlSchema(wsdlContent));
    }
    
    protected static Document getWsdlSchemaAsDocument(Class<?> serviceClass) throws IOException {
        String wsdlContent = readWsdl(serviceClass);
        //System.out.println(wsdlContent);
        return JdomUtils.getWsdlSchemaAsW3CDocument(wsdlContent);
    }
    
    protected static String readWsdl(Class<?> serviceClass) throws IOException {
        return TestUtils.readURL(getAddress(serviceClass) + "?wsdl");
    }
    
    @SuppressWarnings("unchecked")
    protected static <T> T createClient(Class<T> serviceClass) {
        JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
        clientFactory.setServiceClass(serviceClass);

        // ensure all client schema validation is disabled
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.NONE);
        clientFactory.setProperties(properties);

        clientFactory.setAddress(getAddress(serviceClass));

        return (T) clientFactory.create();
    }
    
    protected static void assertEqualsInDoc(Object o1, Object o2, Document doc) {
        try {
            assertEquals(o1, o2);
        } catch (AssertionError e) {
            String d = JdomUtils.toString(doc.getDocumentElement());
            System.out.println(d);
            throw e;
        }
    }

    protected static Server createServer(Class<?> serviceInterface, Object serviceImpl)
        throws IOException {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceClass(serviceImpl.getClass());
        String address = getAddress(serviceInterface);
        LOGGER.info("Starting server: " + address);
        svrFactory.setAddress(getAddress(serviceInterface));
        svrFactory.setServiceBean(serviceImpl);
        Server server = svrFactory.create();
        serverList.add(server);
        return server;
    }
}
