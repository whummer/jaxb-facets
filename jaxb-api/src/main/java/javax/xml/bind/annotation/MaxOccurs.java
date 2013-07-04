package javax.xml.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation class is used to support the XSD 'maxOccurs' attribute in XML
 * Schema files generated by JAXB RI.
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 0.1
 */
@Target(value = { ElementType.FIELD, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface MaxOccurs {

    public static final String VALIDATION_MESSAGE_KEY = "javax.xml.bind.annotation.MaxOccurs.message";

    long value();

}