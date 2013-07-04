package javax.xml.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation class is used to support XSD facets in XML Schema files
 * generated by JAXB RI.
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 0.1
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Facets {

    /* empty marker @interface to annotate the facet methods of @Facets. */
    @Target(value = { ElementType.METHOD })
    @Retention(value = RetentionPolicy.RUNTIME)
    public static @interface FacetDefinition {
        String xsdAttributeName() default VOID_STRING;
    }

    public static final long VOID_LONG = -1;
    public static final String VOID_STRING = "";
    public static final String VALIDATION_MESSAGE_KEY = "javax.xml.bind.annotation.Facets.message";

    public static enum WhiteSpace {
        VOID, preserve, replace, collapse
    }

    @FacetDefinition
    String[] enumeration() default {};

    @FacetDefinition
    long fractionDigits() default VOID_LONG;

    @FacetDefinition
    long length() default VOID_LONG;

    @FacetDefinition
    String maxExclusive() default VOID_STRING;

    @FacetDefinition
    String minExclusive() default VOID_STRING;

    @FacetDefinition
    long maxLength() default VOID_LONG;

    @FacetDefinition
    long minLength() default VOID_LONG;

    @FacetDefinition
    String maxInclusive() default VOID_STRING;

    @FacetDefinition
    String minInclusive() default VOID_STRING;

    @FacetDefinition
    String pattern() default VOID_STRING;

    @FacetDefinition
    long totalDigits() default VOID_LONG;

    @FacetDefinition
    WhiteSpace whiteSpace() default WhiteSpace.VOID;

    public static final long VOID_LONG = -1;
    public static final String VOID_STRING = "";

    public static enum WhiteSpace {
        VOID, preserve, replace, collapse
    }

    @FacetDefinition
    String[] enumeration() default {};

    @FacetDefinition
    long fractionDigits() default VOID_LONG;

    @FacetDefinition
    long length() default VOID_LONG;

    @FacetDefinition
    String maxExclusive() default VOID_STRING;

    @FacetDefinition
    String minExclusive() default VOID_STRING;

    @FacetDefinition
    long maxLength() default VOID_LONG;

    @FacetDefinition
    long minLength() default VOID_LONG;

    @FacetDefinition
    String maxInclusive() default VOID_STRING;

    @FacetDefinition
    String minInclusive() default VOID_STRING;

    @FacetDefinition
    String pattern() default VOID_STRING;

    @FacetDefinition
    long totalDigits() default VOID_LONG;

    @FacetDefinition
    WhiteSpace whiteSpace() default WhiteSpace.VOID;
}
