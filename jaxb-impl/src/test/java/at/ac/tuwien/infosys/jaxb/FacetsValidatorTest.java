package at.ac.tuwien.infosys.jaxb;

import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests the functionality of the JSR-303 based Facets validator.
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 */
public class FacetsValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void startServers() throws Exception {
        // Build the ValidatorFactory
        Configuration<?> configuration = Validation.byDefaultProvider()
                .configure();
        ValidatorFactory factory = configuration.messageInterpolator(
                configuration.getDefaultMessageInterpolator())
                .buildValidatorFactory();
        // Create the Validator
        validator = factory.getValidator();
    }

    @Test
    public void testFacetsValidation() {
        Person p1 = new Person();
        p1.setFirstName("alice");
        Person p2 = new Person();
        p2.setFirstName("ALICE");

        Set<ConstraintViolation<Person>> violations = validator.validate(p1);
        assertFalse(violations.isEmpty());

        violations = validator.validate(p2);
        assertTrue(violations.isEmpty());

        // TODO: add more tests
    }

}
