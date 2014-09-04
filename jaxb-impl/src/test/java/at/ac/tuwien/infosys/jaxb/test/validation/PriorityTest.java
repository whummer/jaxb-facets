
package at.ac.tuwien.infosys.jaxb.test.validation;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class PriorityTest extends AbstractValidationConstraintTest {
    
    public PriorityTest() {
        super(PriorityClass.class);
        registerNamespace("xs", "http://www.w3.org/2001/XMLSchema");
    }
    
    @Test
    public void testAssertTrue() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("count(//xs:element[@name='assertTrueField']//xs:enumeration[@value='nontrue']) = 1 and count(//xs:element[@name='assertTrueField']//xs:enumeration) = 1");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testAssertFalse() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("count(//xs:element[@name='assertFalseField']//xs:enumeration[@value='nonfalse']) = 1 and count(//xs:element[@name='assertFalseField']//xs:enumeration) = 1");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testLength() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='lengthField']//xs:length/@value = 102");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testMax() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='maxField']//xs:maxInclusive/@value = 103");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testDecimalMax() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='decimalMaxField']//xs:maxInclusive/@value = 104");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testMin() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='minField']//xs:minInclusive/@value = 105");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testDecimalMin() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='decimalMinField']//xs:minInclusive/@value = 106");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testSize() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='sizeField']//xs:minLength/@value = 107 and //xs:element[@name='sizeField']//xs:maxLength/@value = 108");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testDigits() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='digitsField']//xs:fractionDigits/@value = 110 and //xs:element[@name='digitsField']//xs:totalDigits/@value = 119");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testPattern() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("string-length(//xs:element[@name='patternField']//xs:pattern/@value) = 5");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
    @Test
    public void testNotNull() throws Exception {
        
        boolean expResult = true;

        XPathExpression expr = xpath.compile("//xs:element[@name='notNullField']/@minOccurs = 0");
        boolean result = (Boolean)expr.evaluate(schemaDocument, XPathConstants.BOOLEAN);

        Assert.assertEquals(expResult, result);
    }
    
}
