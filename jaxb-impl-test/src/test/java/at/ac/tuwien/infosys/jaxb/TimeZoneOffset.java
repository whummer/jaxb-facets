package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.*;

@XmlType(name = "TimeZoneOffset")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("all")
final public class TimeZoneOffset {

    /**
     * 60 (minutes) * 14 (hours) = 840
     */
    @Facets(maxExclusive = "840", minInclusive = "-840")
    @XmlValue
    public java.math.BigInteger OffsetInMinutes;

}
