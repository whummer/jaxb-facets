package at.ac.tuwien.infosys.jaxb;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.Facets.WhiteSpace;

/**
 * Common constants.
 * @author hummer
 */
public class Constants {

	public static String FACET_ENUMERATION = "enumeration";
	public static String FACET_LENGTH = "length";
	public static String FACET_PATTERN = "pattern";
	public static String FACET_WHITESPACE = "whiteSpace";
	public static String FACET_TOTALDIGITS = "totalDigits";
	public static String FACET_FRACTIONDIGITS = "fractionDigits";
	public static String FACET_MAXEXCLUSIVE = "maxExclusive";
	public static String FACET_MINEXCLUSIVE = "minExclusive";
	public static String FACET_MAXINCLUSIVE = "maxInclusive";
	public static String FACET_MININCLUSIVE = "minInclusive";
	public static String FACET_MAXLENGTH = "maxLength";
	public static String FACET_MINLENGTH = "minLength";

    /** types of available facets */
    @SuppressWarnings("all")
    public static final Map<String,Class<?>> FACET_TYPES = new HashMap<String,Class<?>>(){
    {
        put(FACET_ENUMERATION, String[].class);
        put(FACET_LENGTH, long.class);
        put(FACET_PATTERN, String.class);
        put(FACET_WHITESPACE, WhiteSpace.class);
        put(FACET_TOTALDIGITS, long.class);
        put(FACET_FRACTIONDIGITS, long.class);
        put(FACET_MAXEXCLUSIVE, String.class);
        put(FACET_MINEXCLUSIVE, String.class);
        put(FACET_MAXINCLUSIVE, String.class);
        put(FACET_MININCLUSIVE, String.class);
        put(FACET_MAXLENGTH, long.class);
        put(FACET_MINLENGTH, long.class);
    }};

    /** list of available facets */
    public static final Set<String> FACET_NAMES = new HashSet<String>(FACET_TYPES.keySet());

    /** facets allowed for specific base type, based on 
     * [XSD2] http://www.w3.org/TR/xmlschema-2/#defn-coss */
    @SuppressWarnings("all")
    public static final Map<Class<?>,Set<String>> FACETS_BY_TYPE = new HashMap<Class<?>,Set<String>>(){
    {
        put(String.class, new HashSet<String>(Arrays.asList(
        		FACET_LENGTH,
        		FACET_MINLENGTH,
        		FACET_MAXLENGTH,
        		FACET_PATTERN,
        		FACET_ENUMERATION,
        		FACET_WHITESPACE)));
        put(URI.class, get(String.class));
        put(Enum.class, new HashSet<String>(Arrays.asList(
        		FACET_PATTERN,
        		FACET_ENUMERATION)));
        put(Boolean.class, new HashSet<String>(Arrays.asList(
        		FACET_PATTERN,
        		FACET_WHITESPACE,
        		FACET_ENUMERATION)));
        put(BigDecimal.class, new HashSet<String>(Arrays.asList(
        		FACET_TOTALDIGITS,
        		FACET_FRACTIONDIGITS,
        		FACET_PATTERN,
        		FACET_WHITESPACE,
        		FACET_ENUMERATION,
        		FACET_MAXINCLUSIVE,
        		FACET_MAXEXCLUSIVE,
        		FACET_MININCLUSIVE,
        		FACET_MINEXCLUSIVE)));
        put(Integer.class, get(BigDecimal.class));
        put(Double.class, new HashSet<String>(Arrays.asList(
        		FACET_TOTALDIGITS,		/* not strictly [XSD2], but still allowed here */
        		FACET_FRACTIONDIGITS,		/* not strictly [XSD2], but still allowed here */
        		FACET_PATTERN,
        		FACET_WHITESPACE,
        		FACET_ENUMERATION,
        		FACET_MAXINCLUSIVE,
        		FACET_MAXEXCLUSIVE,
        		FACET_MININCLUSIVE,
        		FACET_MINEXCLUSIVE)));
        put(Date.class, get(Double.class));
    }};

}
