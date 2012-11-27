package at.ac.tuwien.infosys.jaxb;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.Facets;

import com.sun.xml.bind.v2.model.core.EnumConstant;
import com.sun.xml.bind.v2.schemagen.xmlschema.SimpleType;

public final class AnnotationUtils {
    private AnnotationUtils() {
    }
    
    public static Documentation getDocumentation(EnumConstant c) {
        try {
            Class enumClazz = (Class) c.getEnclosingClass().getClazz();
            Field field = enumClazz.getField(c.getName());
            return field.getAnnotation(Documentation.class);
        } catch (Exception fe) {
            return null;
        }
    }
}
