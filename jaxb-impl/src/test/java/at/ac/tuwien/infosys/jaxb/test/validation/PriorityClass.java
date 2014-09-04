
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
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.MinOccurs;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
@XmlType
public class PriorityClass {
    
    @AssertTrue
    @Facets(enumeration = { "nontrue" })
    @XmlElement
    boolean assertTrueField;

    @AssertFalse
    @Facets(enumeration = { "nonfalse" })
    @XmlElement
    boolean assertFalseField;

    @Size(min = 2, max = 2)
    @Facets(length = 102)
    @XmlElement
    String lengthField;
    
    @Max(3)
    @Facets(maxInclusive = "103")
    @XmlElement
    int maxField;

    @DecimalMax("4")
    @Facets(maxInclusive = "104")
    @XmlElement
    int decimalMaxField;

    @Min(5)
    @Facets(minInclusive = "105")
    @XmlElement
    int minField;

    @DecimalMin("6")
    @Facets(minInclusive = "106")
    @XmlElement
    int decimalMinField;
    
    @Size(min = 7, max = 8)
    @Facets(minLength = 107, maxLength = 108)
    @XmlElement
    String sizeField;
    
    @Digits(integer = 9, fraction = 10)
    @Facets(totalDigits = 119, fractionDigits = 110)
    @XmlElement
    double digitsField;

    @Pattern(regexp = "ABCDEFGHIJ")
    @Facets(pattern = "ABCDE")
    @XmlElement
    String patternField;

    @NotNull
    @MinOccurs(0)
    @XmlElement
    PriorityClass notNullField;
    
}
