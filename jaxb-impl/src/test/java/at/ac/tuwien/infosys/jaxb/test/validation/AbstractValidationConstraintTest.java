
package at.ac.tuwien.infosys.jaxb.test.validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.w3c.dom.Document;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public abstract class AbstractValidationConstraintTest {
    
    protected Document schemaDocument;
    
    protected XPath xpath;
    
    private NSContext context;
    
    public AbstractValidationConstraintTest(Class<?> cls) {
        this.schemaDocument = createSchemaDocument(cls);
        context = new NSContext();

        XPathFactory xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(context);
    }
    
    protected final Document createSchemaDocument(Class<?> cls) {
        
        Document doc;
        
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(cls);
            
            jaxbContext.generateSchema(new SchemaOutputResolver() {
                @Override
                public Result createOutput(String string, String string1) throws IOException {
                    StreamResult result = new StreamResult(os);
                    result.setSystemId("someID");
                    return result;
                }
            });
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(os.toByteArray()));
        
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return doc;
    }
    
    protected void registerNamespace(String prefix, String namespaceURI) {
        context.registerNamespace(prefix, namespaceURI);
    }

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    static class NSContext implements NamespaceContext {
        
        private Map<String, String> registry = new HashMap<String, String>();
        
        private Map<String, String> reverseRegistry = new HashMap<String, String>();

        @Override
        public String getNamespaceURI(String prefix) {
            return registry.get(prefix);
        }

        @Override
        public String getPrefix(String string) {
            return reverseRegistry.get(string);
        }

        @Override
        public Iterator<?> getPrefixes(String string) {
            return registry.keySet().iterator();
        }
        
        public void registerNamespace(String prefix, String namespaceURI) {
            registry.put(prefix, namespaceURI);
            reverseRegistry.put(namespaceURI, prefix);
        }
        
    }

}
