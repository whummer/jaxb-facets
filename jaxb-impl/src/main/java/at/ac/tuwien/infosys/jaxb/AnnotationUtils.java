package at.ac.tuwien.infosys.jaxb;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.Documentation;

import com.sun.xml.bind.v2.model.core.EnumConstant;

@SuppressWarnings("all")
public final class AnnotationUtils {
    private AnnotationUtils() {
    }

    public static <T, C> Documentation getDocumentation(EnumConstant<T, C> c) {
        try {
            Class<?> enumClazz = (Class<?>) c.getEnclosingClass().getClazz();
            Field field = enumClazz.getField(c.getName());
            return field.getAnnotation(Documentation.class);
        } catch (Exception fe) {
            return null;
        }
    }
    // TODO remove
//    public static <T, C> Documentation getDocumentation(
//            com.sun.xml.internal.bind.v2.model.core.EnumConstant<T, C> c) {
//        try {
//            Class<?> enumClazz = (Class<?>) c.getEnclosingClass().getClazz();
//            Field field = enumClazz.getField(c.getName());
//            return field.getAnnotation(Documentation.class);
//        } catch (Exception fe) {
//            return null;
//        }
//    }
}
