package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 */
/* Thanks to Yossi Cohen for providing this test case. */
@XmlType(name="Country")
@Documentation("The 3-letter ISO 3166-1 codes for countries")
@SuppressWarnings("all")
public enum Country {
    @Documentation("Australia")
       AUS,
    @Documentation("Austria")
       AUT
}