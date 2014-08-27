
package at.ac.tuwien.infosys.jaxb.test.validation;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class UnrestrictedTest extends AbstractValidationConstraintTest {

    public UnrestrictedTest() {
        super(UnrestrictedClass.class);
        registerNamespace("xs", "http://www.w3.org/2001/XMLSchema");
    }
    
    @Test
    public void testEmpty() throws Exception {
        
        long expResult = 0;

        XPathExpression expr = xpath.compile("count(//xs:schema/xs:complexType/xs:sequence/xs:element/*)");
        long result = ((Double)expr.evaluate(schemaDocument, XPathConstants.NUMBER)).longValue();

        Assert.assertEquals(expResult, result);
    }
    
    
    
}
