
package at.ac.tuwien.infosys.jaxb;

import com.sun.xml.bind.v2.model.annotation.AnnotationSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.MinOccurs;

/**
 * Processes JAXB-Facets annotation objects and provides default values from
 * J2EE validation constraints. Values provided in the Facets annotations take
 * precedence.
 * 
 * <p>
 * No validation is done between constraints and explicitly specified facets.
 * Inconsistent validation constraints and Facets annotations will result in an
 * inconsistent schema.
 * 
 * <p>
 * Supported facets: <br>
 * <b>enumeration</b> by {@link javax.validation.constraints.AssertTrue},
 *   {@link javax.validation.constraints.AssertFalse} <br>
 * <b>fractionDigits</b> by {@link javax.validation.constraints.Digits#fraction() } <br>
 * <b>length</b> by {@link javax.validation.constraints.Size#min() },
 *   {@link javax.validation.constraints.Size#max() } (if identical)<br>
 * <b>fractionDigits</b> by {@link javax.validation.constraints.Digits#fraction() } <br>
 * <b>maxLength</b> by {@link javax.validation.constraints.Size#max() } <br>
 * <b>minLength</b> by {@link javax.validation.constraints.Size#min() } <br>
 * <b>maxInclusive</b> by {@link javax.validation.constraints.DecimalMax },
 *   {@link javax.validation.constraints.Max } (precedence order) <br>
 * <b>minInclusive</b> by {@link javax.validation.constraints.DecimalMin },
 *   {@link javax.validation.constraints.Min } (precedence order) <br>
 * <b>pattern</b> by {@link javax.validation.constraints.Pattern } <br>
 * <b>totalDigits</b> by {@link javax.validation.constraints.Digits#fraction() } +
 *   {@link javax.validation.constraints.Digits#integer() } <br>
 * 
 * <p>
 * Other properties: <br>
 * <b>minOccurs</b> property will be set to 1 if
 *   {@link javax.validation.constraints.NotNull } is present.<br>
 * 
 * @author Varga Bence (vbence@czentral.org)
 */
public class ValidationFacetsFilter {
    
    /**
     * Amends an annotation with defaults taken from the AnnotatedElement given.
     * given.
     * @param cls Expected Annotation class.
     * @param original Original annotation to amend (null is a valid value).
     * @param elem Element to check for validation constraints to be used as
     * defaults.
     * @return Processed annotation object or null.
     */
    public Annotation filterAnnotation(Class<? extends Annotation> cls, Annotation original, AnnotatedElement elem) {
        return filterAnnotation(cls, original, new AnnotatedElementWrapper(elem));
    }

    /**
     * Amends an annotation with defaults taken from the AnnotationSource given.
     * @param cls Expected Annotation class.
     * @param original Original annotation to amend (null is a valid value).
     * @param info Info object to check for validation constraints to be used
     * as defaults.
     * @return Processed Annotation or null if no information available.
     */
    public Annotation filterAnnotation(Class<? extends Annotation> cls, Annotation original, AnnotationSource info) {
        
        if (cls == Facets.class) {
            return filterFacets((Facets)original, info);
        } else if (cls == MinOccurs.class) {
            return filterMinOccurs((MinOccurs)original, info);
        } else {
            return original;
        }
    }
    
    /**
     * Process a Facets annotation.
     * @param original Existing Facets annotation or null.
     * @param info Source for validation constraints.
     * @return Processed Annotation or null if no information available.
     */
    private Facets filterFacets(Facets original, AnnotationSource info) {

        FacetsWrapper wrapper = (original == null)
                ? new FacetsWrapper()
                : new FacetsWrapper(original);
        
        boolean override = false;
        
        if (original == null || original.enumeration().length == 0) {
            
            if (info.readAnnotation(AssertFalse.class) != null) {
                override = true;
                wrapper.enumeration = new String[] { "false", "0" };

            } else if (info.readAnnotation(AssertTrue.class) != null) {
                override = true;
                wrapper.enumeration = new String[] { "true", "1" };
            
            }
        }
        
        if (original == null || original.fractionDigits() == Facets.VOID_LONG) {

            if (info.readAnnotation(Digits.class) != null) {
                override = true;
                wrapper.fractionDigits = info.readAnnotation(Digits.class).fraction();
            }
        }

        if (original == null || original.length() == Facets.VOID_LONG) {
            
            if (info.readAnnotation(Size.class) != null) {
                Size size = info.readAnnotation(Size.class);
                if (size.max() == size.min()) {
                    override = true;
                    wrapper.length = size.max();
                }
            }
        }
    
        if (original == null || original.maxLength() == Facets.VOID_LONG) {
            
            if (info.readAnnotation(Size.class) != null) {
                override = true;
                wrapper.maxLength = info.readAnnotation(Size.class).max();
            }
        }
        
        if (original == null || original.minLength() == Facets.VOID_LONG) {
            
            if (info.readAnnotation(Size.class) != null) {
                override = true;
                wrapper.minLength = info.readAnnotation(Size.class).min();
            }
        }
        
        if (original == null || Facets.VOID_STRING.equals(original.maxInclusive())) {
            
            if (info.readAnnotation(DecimalMax.class) != null) {
                override = true;
                wrapper.maxInclusive = info.readAnnotation(DecimalMax.class).value();
            } else if (info.readAnnotation(Max.class) != null) {
                override = true;
                wrapper.maxInclusive = Long.toString(info.readAnnotation(Max.class).value());
            }
                
        }
        
        if (original == null || Facets.VOID_STRING.equals(original.minInclusive())) {
            
            if (info.readAnnotation(DecimalMin.class) != null) {
                override = true;
                wrapper.minInclusive = info.readAnnotation(DecimalMin.class).value();
            } else if (info.readAnnotation(Min.class) != null) {
                override = true;
                wrapper.minInclusive = Long.toString(info.readAnnotation(Min.class).value());
            }
                
        }
        
        if (original == null || Facets.VOID_STRING.equals(original.pattern())) {

            if (info.readAnnotation(Pattern.class) != null) {
                override = true;
                wrapper.pattern = info.readAnnotation(Pattern.class).regexp();
            }
            
        }
        
        if (original == null || original.totalDigits() == Facets.VOID_LONG) {

            if (info.readAnnotation(Digits.class) != null) {
                override = true;
                wrapper.totalDigits = info.readAnnotation(Digits.class).integer() + 
                        info.readAnnotation(Digits.class).fraction();
            }
        }
        
        return override ? wrapper : original;
    }
    
    /**
     * Process a MinOccurs annotation.
     * @param original Existing MinOccurs annotation or null.
     * @param info Source for validation constraints.
     * @return Processed Annotation or null if no information available.
     */
    private MinOccurs filterMinOccurs(MinOccurs original, AnnotationSource info) {
        
        if (original == null && info.readAnnotation(NotNull.class) != null) {
            MinOccursWrapper mov = new MinOccursWrapper();
            mov.value = 1;
            return mov;
        } else {
            return original;
        }
    }

    /**
     * AnnotationSource implementation based on information from java reflection
     * (AnnotatedElement).
     */
    static class AnnotatedElementWrapper implements AnnotationSource {
        
        private AnnotatedElement element;

        public AnnotatedElementWrapper(AnnotatedElement element) {
            this.element = element;
        }

        @Override
        public <A extends Annotation> A readAnnotation(Class<A> type) {
            return element.getAnnotation(type);
        }
        
        @Override
        public boolean hasAnnotation(Class<? extends Annotation> type) {
            return element.isAnnotationPresent(type);
        }
        
    }
    
    static class FacetsWrapper implements Facets {

        public String[] enumeration = {};

        public long fractionDigits = VOID_LONG;

        public long length = VOID_LONG;

        public String maxExclusive = VOID_STRING;

        public String minExclusive = VOID_STRING;

        public long maxLength = VOID_LONG;

        public long minLength = VOID_LONG;

        public String maxInclusive = VOID_STRING;

        public String minInclusive = VOID_STRING;

        public String pattern = VOID_STRING;

        public long totalDigits = VOID_LONG;

        public WhiteSpace whiteSpace = WhiteSpace.VOID;

        public FacetsWrapper() {
        }

        public FacetsWrapper(Facets original) {
            this.enumeration = original.enumeration();
            this.fractionDigits = original.fractionDigits();
            this.length = original.length();
            this.maxExclusive = original.maxExclusive();
            this.minExclusive = original.minExclusive();
            this.maxLength = original.maxLength();
            this.minLength = original.minLength();
            this.maxInclusive = original.maxInclusive();
            this.minInclusive = original.minInclusive();
            this.pattern = original.pattern();
            this.totalDigits = original.totalDigits();
            this.whiteSpace = original.whiteSpace();
        }


        @Override
        public String[] enumeration() {
            return enumeration;
        }

        @Override
        public long fractionDigits() {
            return fractionDigits;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public String maxExclusive() {
            return maxExclusive;
        }

        @Override
        public String minExclusive() {
            return minExclusive;
        }

        @Override
        public long maxLength() {
            return maxLength;
        }

        @Override
        public long minLength() {
            return minLength;
        }

        @Override
        public String maxInclusive() {
            return maxInclusive;
        }

        @Override
        public String minInclusive() {
            return minInclusive;
        }

        @Override
        public String pattern() {
            return pattern;
        }

        @Override
        public long totalDigits() {
            return totalDigits;
        }

        @Override
        public WhiteSpace whiteSpace() {
            return whiteSpace;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Facets.class;
        }

    }

    static class MinOccursWrapper implements MinOccurs {

        public long value;

        @Override
        public long value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return MinOccurs.class;
        }

    }

}

