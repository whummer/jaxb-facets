package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "TimeZoneOffset")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("all")
final public class TimeZoneOffset {

	public static final String MAX_EXCLUSIVE = "840";
	public static final String MIN_EXCLUSIVE = "-840";

    /**
     * 60 (minutes) * 14 (hours) = 840
     */
    @Facets(maxExclusive = MAX_EXCLUSIVE, minInclusive = MIN_EXCLUSIVE)
    @XmlValue
    public java.math.BigInteger OffsetInMinutes;

}
