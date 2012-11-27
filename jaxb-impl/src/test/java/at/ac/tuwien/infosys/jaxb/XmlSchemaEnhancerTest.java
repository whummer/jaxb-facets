package at.ac.tuwien.infosys.jaxb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.pellcorp.jaxb.test.AbstractTestCase;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmlSchemaEnhancerTest extends AbstractTestCase {
    @BeforeClass
    public static void startServers() throws Exception {
        createServer(PersonService.class, new PersonServiceImpl());
    }

    @AfterClass
    public static void cleanup() throws Exception {
        cleanupServers();
    }
    
    @Test
    public void testGenderEnumDocumentation() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        String value = engine.evaluate("//xs:simpleType[@name='gender']/xs:annotation/xs:documentation", doc);
        assertEquals("Gender Type", value);
        
        value = engine.evaluate("//xs:simpleType[@name='gender']/xs:restriction/xs:enumeration[@value='F']/xs:annotation/xs:documentation", doc);
        assertEquals("Female", value);
        
        value = engine.evaluate("//xs:simpleType[@name='gender']/xs:restriction/xs:enumeration[@value='M']/xs:annotation/xs:documentation", doc);
        assertEquals("Male", value);
        
        value = engine.evaluate("//xs:simpleType[@name='gender']/xs:restriction/xs:enumeration[@value='O']/xs:annotation/xs:documentation", doc);
        assertEquals("Other", value);
    }
    
    @Test
    public void testAgeDerivedTypeFacets() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        
        String value = engine.evaluate("//xs:simpleType[@name='Age']/xs:restriction/xs:minInclusive/@value", doc);
        assertEquals("5", value);
        
        value = engine.evaluate("//xs:simpleType[@name='Age']/xs:restriction/xs:maxInclusive/@value", doc);
        assertEquals("120", value);
    }
}
