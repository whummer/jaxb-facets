package com.pellcorp.jaxb.test;

import java.io.InputStream;

import org.jdom2.Document;

import org.junit.Assert;
import org.junit.Test;

public class JdomUtilsTest extends Assert {
    @Test
    public void testLoadSchema() throws Exception {
        InputStream xml = ResourceUtils.loadResourceAsInputStream("/test.wsdl");
        Document doc = JdomUtils.getWsdlSchema(xml);
        assertEquals("schema", doc.getRootElement().getName());
    }
}
