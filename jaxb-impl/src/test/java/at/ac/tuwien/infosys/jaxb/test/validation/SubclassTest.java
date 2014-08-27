
package at.ac.tuwien.infosys.jaxb.test.validation;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class SubclassTest extends AbstractValidationConstraintTest {

    public SubclassTest() {
        super(SubClass.class);
        registerNamespace("xs", "http://www.w3.org/2001/XMLSchema");
    }
    
    @Test
    public void testNotEmpty() throws Exception {

        boolean expResult = true;

        XPathExpression expr = xpath.compile("count(//xs:restriction) > 0");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
}
