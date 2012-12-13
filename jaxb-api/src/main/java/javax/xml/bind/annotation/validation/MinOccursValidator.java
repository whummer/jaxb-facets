package javax.xml.bind.annotation.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.MinOccurs;

/**
 * This class performs JSR-303 compliant validation 
 * of @MinOccurs annotated data types.
 *
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 1.0
 */
public class MinOccursValidator implements ConstraintValidator<MinOccurs, Object> {

	private MinOccurs annotation;

	public void initialize(MinOccurs constraintAnnotation) {
		this.annotation = constraintAnnotation;
	}

	public boolean isValid(Object value, ConstraintValidatorContext context) {
		// TODO: implement
		throw new RuntimeException("MinOccurs validation not yet implemented.");
	}

}
