package at.ac.tuwien.infosys.jaxb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the functionality of the JSR-303 based Facets validator.
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 */
@SuppressWarnings("all")
public class FacetsValidatorTest {

    private static Validator validator;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Person")
    @SuppressWarnings("all")
    public static class ConstrainedPerson {
        @XmlElement(required = true, name = "firstName")
        @Facets(pattern = "[A-Z]+")
        private String firstName;
        
    }

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
        ConstrainedPerson p1 = new ConstrainedPerson();
        p1.firstName = "alice";
        ConstrainedPerson p2 = new ConstrainedPerson();
        p2.firstName = "ALICE";

        Set<ConstraintViolation<ConstrainedPerson>> violations = validator.validate(p1);
        assertFalse(violations.isEmpty());

        violations = validator.validate(p2);
        assertTrue(violations.isEmpty());

        // TODO: add more tests
    }

}
