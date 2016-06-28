package at.ac.tuwien.infosys.jaxb.test;

import java.util.List;

import javax.xml.bind.annotation.Annotation;
import javax.xml.bind.annotation.AppInfo;
import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.MaxOccurs;
import javax.xml.bind.annotation.MinOccurs;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Used to test jaxb-facets with schemagen, as suggested in 
 * https://github.com/whummer/jaxb-facets/issues/23
 * @author hummer
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Annotation(id = "anno1", documentation = {
    @Documentation(value = "doc 1", lang = "en", source = "src 1"),
    @Documentation("doc 2")
})
public class XmlTestType {

	@Facets(minInclusive="100")
	@MaxOccurs(10)
    Integer someNumber;

    @XmlAttribute
    @Facets(length = 100, pattern = "[a-z]+")
    @Documentation("<b>string attribute</b>")
    @AppInfo(source = "src 1", value = "<foo xmlns=\"myns123\">appinfo 1</foo>")
    private String foo;

    @XmlElement
    @MinOccurs(2)
    @MaxOccurs(10)
    @Facets(pattern = "[0-9]+")
    @Documentation("<b>list of strings</b>")
    private List<String> bar;

}