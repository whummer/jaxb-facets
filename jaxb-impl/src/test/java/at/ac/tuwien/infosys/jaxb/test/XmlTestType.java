package at.ac.tuwien.infosys.jaxb.test;

import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.MaxOccurs;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Used to test jaxb-facets with schemagen, as suggested in 
 * https://github.com/whummer/jaxb-facets/issues/23
 * @author hummer
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlTestType {

	@Facets(minInclusive="100")
	@MaxOccurs(10)
    Integer someNumber;

}