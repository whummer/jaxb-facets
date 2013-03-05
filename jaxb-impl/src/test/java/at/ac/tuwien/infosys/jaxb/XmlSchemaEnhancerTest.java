package at.ac.tuwien.infosys.jaxb;

import org.w3c.dom.Document;

import com.pellcorp.jaxb.test.AbstractTestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class XmlSchemaEnhancerTest extends AbstractTestCase {

    public static final String DOC_SCHEMALEVEL_1 = "schema-level doc 123";
    public static final String DOC_SCHEMALEVEL_2 = "schema-level doc 234";
    public static final String APPINFO_SCHEMALEVEL = "schema-level appinfo 1234";

    @BeforeClass
    public static void startServers() throws Exception {
        createServer(PersonService.class, new PersonServiceImpl());
        createServer(PersonServiceNoNS.class, new PersonServiceNoNSImpl());
    }

    @AfterClass
    public static void cleanup() throws Exception {
        cleanupServers();
    }
    
    @Test
    public void testGenderEnumDocs() throws Exception {
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
    
    @Test
    public void testComplexTypeBuddyDocs() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        
        String value = engine.evaluate("//xs:complexType[@name='Buddy']/xs:annotation/xs:documentation", doc);
        assertEquals("A Buddy.", value);
        
        value = engine.evaluate("//xs:complexType[@name='Buddy']/xs:sequence/xs:element/xs:annotation/xs:documentation", doc);
        assertEquals("Name of buddy.", value);
    }

    @Test
    public void testComplexTypeTestRequest() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        
        String value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:annotation[@id='id123']/xs:appinfo[@source='src 1']", doc);
        assertEquals("appinfo 1", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:annotation[@id='id123']/xs:documentation[@source='src 1']", doc);
        assertEquals("doc 1", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:annotation[@id='id123']/xs:documentation[2]", doc);
        assertEquals("doc 2", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:annotation[@id='id123']/xs:documentation[3]", doc);
        assertEquals("doc 3", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:sequence/xs:element[@name='bar']/xs:annotation/xs:documentation", doc);
        assertEquals("<b>string bar</b>", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:attribute[@name='foo']/xs:annotation/xs:appinfo[@source='src 1']", doc);
        assertEquals("appinfo 1", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:attribute[@name='foo']/xs:annotation/xs:documentation", doc);
        assertEquals("<b>string foo</b>", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:attribute[@name='foo1']/xs:annotation/xs:documentation[1]", doc);
        assertEquals("this is the first line", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:attribute[@name='foo1']/xs:annotation/xs:documentation[2]", doc);
        assertEquals("this is the second line", value);
    }

    @Test
    public void testFacetOnXmlValue() throws Exception {
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:simpleType[@name='TimeZoneOffset']/xs:restriction/@base", doc);
        assertTrue(value != null && value.endsWith("integer"));
    }

    @Test
    public void testPersonNameFacets() throws Exception {
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:element[@name='firstName']//xs:restriction/xs:pattern/@value", doc);
        assertEquals("[A-Z]+", value);

    }

    @Test
    public void testDocumentationOnEnum() throws Exception {
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:simpleType[@name='Country']//" +
        		"xs:enumeration[@value='AUS']/xs:annotation/xs:documentation", doc);
        assertEquals("Australia", value);

        value = engine.evaluate("//xs:simpleType[@name='Country']//" +
                "xs:enumeration[@value='AUT']/xs:annotation/xs:documentation", doc);
        assertEquals("Austria", value);

        value = engine.evaluate("//xs:simpleType[@name='Country']/" +
                "xs:annotation/xs:documentation", doc);
        assertEquals("The 3-letter ISO 3166-1 codes for countries", value);
    }

    @Test
    public void testFieldAnnotationMinMaxOccurs() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        
        String minOccurs = engine.evaluate("//xs:complexType[@name='Person']/xs:sequence/xs:element[@name='lastName']/@minOccurs", doc);
        String maxOccurs = engine.evaluate("//xs:complexType[@name='Person']/xs:sequence/xs:element[@name='lastName']/@maxOccurs", doc);
        assertEquals("1", minOccurs);
        assertEquals("3", maxOccurs);
        
        minOccurs = engine.evaluate("//xs:complexType[@name='Person']/xs:sequence/xs:element[@name='firstName']/@minOccurs", doc);
        maxOccurs = engine.evaluate("//xs:complexType[@name='Person']/xs:sequence/xs:element[@name='firstName']/@maxOccurs", doc);
        assertEquals("1", minOccurs);
        assertEquals("2", maxOccurs);
    }
    
    @Test
    public void testMemberAnnotationMinMaxOccurs() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        
        String minOccurs = engine.evaluate("//xs:complexType[@name='Applicant']/xs:sequence/xs:element[@name='lastName']/@minOccurs", doc);
        String maxOccurs = engine.evaluate("//xs:complexType[@name='Applicant']/xs:sequence/xs:element[@name='lastName']/@maxOccurs", doc);
        assertEquals("1", minOccurs);
        assertEquals("3", maxOccurs);
        
        minOccurs = engine.evaluate("//xs:complexType[@name='Applicant']/xs:sequence/xs:element[@name='firstName']/@minOccurs", doc);
        maxOccurs = engine.evaluate("//xs:complexType[@name='Applicant']/xs:sequence/xs:element[@name='firstName']/@maxOccurs", doc);
        assertEquals("1", minOccurs);
        assertEquals("2", maxOccurs);
    }
    
    @Test
    public void testPackageLevelXSDAnnotations() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonServiceNoNS.class));
        Document doc = getWsdlSchemaAsDocument(PersonServiceNoNS.class);

        String value = engine.evaluate("/xs:schema/xs:annotation/xs:appinfo[1]", doc);
        assertEquals(APPINFO_SCHEMALEVEL, value);

        value = engine.evaluate("/xs:schema/xs:annotation/xs:documentation[1]", doc);
        assertEquals(DOC_SCHEMALEVEL_1, value);

        value = engine.evaluate("/xs:schema/xs:annotation/xs:documentation[2]", doc);
        assertEquals(DOC_SCHEMALEVEL_2, value);
    }
}
