package at.ac.tuwien.infosys.jaxb;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import com.pellcorp.jaxb.test.AbstractTestCase;

public class XmlSchemaEnhancerTest extends AbstractTestCase {

    public static final String DOC_SCHEMALEVEL_1 = "schema-level doc 123";
    public static final String DOC_SCHEMALEVEL_2 = "schema-level doc 234";
    public static final String APPINFO_STRING = "schema-level appinfo 1234";
    public static final String APPINFO_ELEMENT = 
    		"<myInfo xmlns=\"ns1\" xmlns:tns=\"ns2\" attr1=\"foo\">" +
    			APPINFO_STRING +
    			"<tns:foo>" +
    			APPINFO_STRING +
    			"</tns:foo>" +
    		"</myInfo>";
    public static final String ATTR_NAME_1 = "attr1";
    public static final String ATTR_NAMESPACE_1 = "http://example.com/attr1";
    public static final String ATTR_NAMESPACE_2 = "http://example.com/attr2";
    public static final String ATTR_VALUE_1 = "value1";
    public static final String ATTR_NAME_2 = "attr2";
    public static final String ATTR_VALUE_2 = "value2";

    @BeforeClass
    public static void startServers() throws Exception {
    	/* enable XSD 1.1 features */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(true);

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
        assertEqualsInDoc("5", value, doc);

        value = engine.evaluate("//xs:simpleType[@name='Age']/xs:restriction/xs:maxInclusive/@value", doc);
        assertEqualsInDoc("120", value, doc);
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
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:sequence/xs:element[@name='bar']/xs:annotation/xs:documentation/b", doc);
        assertEquals("string bar", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:attribute[@name='foo']/xs:annotation/xs:appinfo[@source='src 1']", doc);
        assertEquals("appinfo 1", value);
        
        value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:attribute[@name='foo']/xs:annotation/xs:documentation/b", doc);
        assertEquals("string foo", value);
        
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
    public void testAdditionalFacetsOnEnum() throws Exception {
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:simpleType[@name='gender']/" +
        		"xs:restriction/xs:maxLength/@value", doc);
        assertEquals("20", value);

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
    @Ignore
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
    public void testAppinfoWithXMLContent() throws Exception {
        Document doc = getWsdlSchemaAsDocument(PersonService.class);
        //System.out.println(getWsdlSchemaAsString(PersonServiceNoNS.class));

        addNamespace("ns1", "ns1");
        addNamespace("ns2", "ns2");
        String value = engine.evaluate("//xs:complexType[@name='Person']/xs:annotation/xs:appinfo[1]/ns1:myInfo/text()", doc);
        assertEquals(APPINFO_STRING, value);

        value = engine.evaluate("//xs:complexType[@name='Person']/xs:annotation/xs:appinfo[1]/ns1:myInfo/ns2:foo/text()", doc);
        assertEquals(APPINFO_STRING, value);
    }

    @Test
    public void testAnnotationAttributes() throws Exception {
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        addNamespace("ns1", ATTR_NAMESPACE_1);
        addNamespace("ns2", ATTR_NAMESPACE_2);
        
        String value = engine.evaluate("//xs:complexType[@name='Person']/xs:annotation/@ns1:" + ATTR_NAME_1, doc);
        assertEquals(ATTR_VALUE_1, value);

        value = engine.evaluate("//xs:complexType[@name='Person']/xs:annotation/@ns2:" + ATTR_NAME_2, doc);
        assertEquals(ATTR_VALUE_2, value);
    }

    @Test
    public void testPackageLevelXSDAnnotations() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonServiceNoNS.class));
        Document doc = getWsdlSchemaAsDocument(PersonServiceNoNS.class);

        String value = engine.evaluate("/xs:schema/xs:annotation/xs:appinfo[1]", doc);
        assertEquals(APPINFO_STRING, value);

        value = engine.evaluate("/xs:schema/xs:annotation/xs:documentation[1]", doc);
        assertEquals(DOC_SCHEMALEVEL_1, value);

        value = engine.evaluate("/xs:schema/xs:annotation/xs:documentation[2]", doc);
        assertEquals(DOC_SCHEMALEVEL_2, value);
    }

    @Test
    public void testXSDAnnotationOnChoice() throws Exception {
        //System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:choice/xs:element[@name='country1']//xs:documentation/text()", doc);
        assertEquals("Country or Buddy INSIDE", value);

        value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:choice[xs:element[@name='country1']]/xs:annotation/xs:documentation/text()", doc);
        assertEquals("", value);

        value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:choice/xs:element[@name='buddy2']//xs:documentation/text()", doc);
        assertEquals("", value);

        value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:choice/xs:element[@name='country2']//xs:documentation/text()", doc);
        assertEquals("", value);

        value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:choice[xs:element[@name='country2']]/xs:annotation/xs:documentation/text()", doc);
        assertEquals("Country or Buddy OUTSIDE", value);
    }

    @Test
    public void testXSDAnnotationOnWrapper() throws Exception {
    	//System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:element[@name='buddies']/xs:annotation/xs:documentation/text()", doc);
        assertEquals("List of buddies", value);

        value = engine.evaluate("//xs:complexType[@name='TestRequest']//xs:element[@name='buddies']//xs:element[@name='buddy']//xs:documentation/text()", doc);
        assertEquals("", value);

    }

    @Test
    public void testXSDAssertOnWrapper() throws Exception {
    	//System.out.println(getWsdlSchemaAsString(PersonService.class));
        Document doc = getWsdlSchemaAsDocument(PersonService.class);

        String value = engine.evaluate("//xs:complexType[@name='TestRequest']/xs:assert/@test", doc);
        assertTrue(value != null && !value.trim().equals(""));

    }
}
