package at.ac.tuwien.infosys.jaxb.test;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.Annotation;
import javax.xml.bind.annotation.AnnotationLocation;
import javax.xml.bind.annotation.AppInfo;
import javax.xml.bind.annotation.Assert;
import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.MaxOccurs;
import javax.xml.bind.annotation.MinOccurs;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.infosys.jaxb.Country;
import at.ac.tuwien.infosys.jaxb.TimeZoneOffset;
import at.ac.tuwien.infosys.jaxb.test.Buddy;
import at.ac.tuwien.infosys.jaxb.test.ChartType;

@XmlRootElement(name = "foo")
@XmlType(name = "TestRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Annotation(id = "id123", documentation = {
    @Documentation(value = "doc 1", lang = "en", source = "src 1"), @Documentation("doc 2")
})
@Documentation("doc 3")
@AppInfo(source = "src 1", value = "appinfo 1")
@SuppressWarnings("all")
@Assert(test = "not(bar1) or not(bar2)")
public class TestRequest {
    @XmlAttribute
    public ChartType type;
    @XmlAttribute
    @Facets(length = 100, pattern = "[a-z]+")
    @Documentation("<b>string foo</b>")
    @AppInfo(source = "src 1", value = "appinfo 1")
    private String foo;
    @XmlAttribute
    @Annotation(documentation = {
        @Documentation(lang = "en", value = "this is the first line"),
        @Documentation(lang = "en", value = "this is the second line")
    })
    private String foo1;
    @XmlElement
    @MinOccurs(2)
    @MaxOccurs(10)
    @Facets(pattern = "[0-9]+")
    @Documentation("<b>string bar</b>")
    private List<String> bar;
    @XmlElement
    private String bar1;
    @XmlElement(name = "bar2")
    @Facets(pattern = "[0-9]+")
    private String barX;

    /* Thanks to Yossi Cohen for the following test cases..! */
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    @Facets(pattern = "https?://.+")
    public java.net.URI value1;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    public java.net.URI value2;
    @XmlElement
    public TimeZoneOffset value3;
    @XmlElement
    public Country country;

    /* Thanks to Jason Pell for pointing out the need for min/max facets of type String..! */
    @XmlElement(required = true)
    @Facets(minInclusive = "2012-12-24T12:00:00Z")
    public Date date1;

    /* Thanks to Uwe Maurer for the following test case..! */
    @XmlElement(required = true)
    public Buddy buddy;

    /* Test <xsd:annotation> on <xsd:choice> INSIDE_ELEMENT */
    @XmlElements({
    	@XmlElement(type=Country.class, name="country1"),
    	@XmlElement(type=Buddy.class, name="buddy1")
    })
    @Annotation(
    	documentation=@Documentation("Country or Buddy INSIDE"), 
    	location=AnnotationLocation.INSIDE_ELEMENT
    )
    public Object countryOrBuddyChoiceAnnoInside;

    /* Test <xsd:annotation> on <xsd:choice> OUTSIDE_ELEMENT */
    @XmlElements({
    	@XmlElement(type=Country.class, name="country2"),
    	@XmlElement(type=Buddy.class, name="buddy2")
    })
    @Annotation(
    	documentation=@Documentation("Country or Buddy OUTSIDE"),
    	location=AnnotationLocation.OUTSIDE_ELEMENT
    )
    public Object countryOrBuddyChoiceAnnoOutside;

    /* test <xsd:annotation> on wrapper element */
    @XmlElement(name = "buddy")
    @XmlElementWrapper(name = "buddies")
    @Annotation(
    	documentation=@Documentation("List of buddies"),
    	location=AnnotationLocation.OUTSIDE_ELEMENT
    )
    public List<Buddy> wrappedBuddies;

    @XmlTransient
    public String getFoo() {
        return foo;
    }

    @XmlTransient
    public List<String> getBar() {
        return bar;
    }

    @XmlTransient
    public String getFoo1() {
        return foo1;
    }

    @XmlTransient
    public String getBar1() {
        return bar1;
    }

    @XmlTransient
    public String getBarX() {
        return barX;
    }
}
