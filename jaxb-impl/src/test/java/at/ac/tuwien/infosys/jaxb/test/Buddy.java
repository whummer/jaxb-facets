package at.ac.tuwien.infosys.jaxb.test;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/* Thanks to Uwe Maurer for the following test class..! */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Buddy")
@Documentation("A Buddy.")
public class Buddy {
    @Documentation("Name of buddy.")
    private String name;
}
