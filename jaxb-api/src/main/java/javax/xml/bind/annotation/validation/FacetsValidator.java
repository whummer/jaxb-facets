package javax.xml.bind.annotation.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.Facets;

/**
 * This class performs JSR-303 compliant validation of 
 * Facets-annotated simple data types.
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 1.0
 */
public class FacetsValidator implements ConstraintValidator<Facets, String> {

    private Facets annotation;

    public void initialize(Facets constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            value = "";
        }
        if (annotation.pattern() != null
                && !annotation.pattern().equals(Facets.VOID_STRING)) {
            if (!value.matches(annotation.pattern())) {
                return false;
            }
        }
        // TODO: add more tests for other Facet constraints..
        return true;
    }

}
