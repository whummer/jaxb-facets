package com.pellcorp.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.testutil.common.TestUtil;

import org.junit.Assert;

public abstract class AbstractTestCase extends Assert {
    protected static final String PORT = TestUtil.getPortNumber(AbstractTestCase.class);
    private static List<Server> serverList = new ArrayList<Server>();
    
    protected static String getAddress(Class<?> sei) {
        return "http://localhost:" + PORT + "/" + sei.getSimpleName();
    }
    
    protected static void cleanupServers() {
        for (Server server : serverList) {
            server.stop();
        }
    }
    
    protected static String readWsdl(Class<?> serviceClass) throws IOException {
        return TestUtils.readURL(getAddress(serviceClass) + "?wsdl");
    }
    
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

    protected static Server createServer(Class<?> serviceInterface, Object serviceImpl)
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
