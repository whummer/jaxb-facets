package at.ac.tuwien.infosys.jaxb.test;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleDuration {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "time")
    @Documentation("the opening time")
    private String from;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "time")
    private String to;

    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
}
