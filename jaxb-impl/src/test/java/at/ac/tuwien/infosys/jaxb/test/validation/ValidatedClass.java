
package at.ac.tuwien.infosys.jaxb.test.validation;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
@XmlType
public class ValidatedClass {
    
    @AssertTrue
    @XmlElement
    boolean assertTrueField;

    @AssertFalse
    @XmlElement
    boolean assertFalseField;

    @Size(min = 2, max = 2)
    @XmlElement
    String lengthField;
    
    @Max(3)
    @XmlElement
    int maxField;

    @DecimalMax("4")
    @XmlElement
    int decimalMaxField;

    @Min(5)
    @XmlElement
    int minField;

    @DecimalMin("6")
    @XmlElement
    int decimalMinField;
    
    @Size(min = 7, max = 8)
    @XmlElement
    String sizeField;
    
    @Digits(integer = 9, fraction = 10)
    @XmlElement
    double digitsField;

    @Pattern(regexp = "ABCDEFGHIJ")
    @XmlElement
    String patternField;

    @NotNull
    @XmlElement
    ValidatedClass notNullField;
    
}
