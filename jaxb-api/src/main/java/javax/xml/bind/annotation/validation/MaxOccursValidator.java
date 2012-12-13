package javax.xml.bind.annotation.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.MaxOccurs;

/**
 * This class performs JSR-303 compliant validation 
 * of @MaxOccurs annotated data types.
 *
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 1.0
 */
public class MaxOccursValidator implements ConstraintValidator<MaxOccurs, Object> {

	private MaxOccurs annotation;

	public void initialize(MaxOccurs constraintAnnotation) {
		this.annotation = constraintAnnotation;
	}

	public boolean isValid(Object value, ConstraintValidatorContext context) {
		// TODO: implement
		throw new RuntimeException("MinOccurs validation not yet implemented.");
	}

}
