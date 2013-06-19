package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.*;

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
