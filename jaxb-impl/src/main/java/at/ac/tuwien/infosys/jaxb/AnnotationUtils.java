package at.ac.tuwien.infosys.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.Documentation;

import com.sun.xml.bind.v2.model.core.EnumConstant;

/**
 * Utility methods for handling annotation types.
 * @author Waldemar Hummer
 */
@SuppressWarnings("all")
public final class AnnotationUtils {

    public static final Logger logger = Logger
            .getLogger(SchemagenUtil.class.getName());

    /**
     * Private constructor.
     */
    private AnnotationUtils() {
    }

    /**
     * Utility method to instantiate a proxy for a given annotation
     * class, using a map of values used for the new annotation instance.
     * Uses the classloader of annoClass (first parameter).
     * @param annoClass
     * @param annoValues
     * @return
     */
    public static <T extends Annotation> T createAnnotationProxy(
            Class<T> annoClass, Map<String, Object> annoValues) {
        return createAnnotationProxy(annoClass, annoValues, annoClass.getClassLoader());
    }

    /**
     * Utility method to instantiate a proxy for a given annotation
     * class, using a map of values used for the new annotation instance.
     * @param annoClass
     * @param annoValues
     * @param cl
     * @return
     */
    public static <T extends Annotation> T createAnnotationProxy(
            final Class<T> annoClass, final Map<String, Object> annoValues, ClassLoader cl) {
        InvocationHandler h = new InvocationHandler() {
            public Object invoke(Object o, Method m, Object[] args)
                    throws Throwable {
                if(m.getName().equals("toString") && !annoValues.containsKey(m.getName())) {
                    return "annotation @" + annoClass.getName() + "(...)";
                }
                return annoValues.get(m.getName());
            }
        };
        return (T) Proxy.newProxyInstance(
                cl, new Class<?>[] { annoClass }, h);
    }

    /**
     * Get @Documentation annotation instance from an enum constant.
     * @param c
     * @return
     */
    public static <T, C> Documentation getDocumentation(EnumConstant<T, C> c) {
        try {
            Object enumClazz = c.getEnclosingClass().getClazz();
            if(enumClazz instanceof Class<?>) {
                Class<?> enumClass = (Class<?>)enumClazz;
                Field field = enumClass.getField(c.getName());
                return field.getAnnotation(Documentation.class);
            } else {
                /* looks like we are running in a schemagen context.. */
                return SchemagenUtil.extractAnnotation(enumClazz, Documentation.class);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to get @Documentation annotation for enum constant " + c, e);
        }
        return null;
    }
}
