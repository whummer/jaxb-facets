
package at.ac.tuwien.infosys.jaxb.test.validation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
@XmlType(name = "UnrestrictedClass")
public class UnrestrictedClass {
    
    @XmlElement
    boolean assertTrueField;

    @XmlElement
    boolean assertFalseField;

    @XmlElement
    String lenthField;
    
    @XmlElement
    int maxField;

    @XmlElement
    int minField;

    @XmlElement
    String sizeField;
    
    @XmlElement
    double digitsField;

    @XmlElement
    String patternField;

    @XmlElement
    UnrestrictedClass notNullField;

}
