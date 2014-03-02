package at.ac.tuwien.infosys.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.bind.v2.model.core.EnumConstant;

/**
 * Utility methods for handling annotation types.
 * @author Waldemar Hummer
 */
@SuppressWarnings("all")
public final class AnnotationUtils {

    public static final Logger logger = Logger
            .getLogger(SchemagenUtil.class.getName());

	public static final Map<Object,AnnotationInvocationHandler> PROXY_HANDLERS = 
			new IdentityHashMap<Object, AnnotationUtils.AnnotationInvocationHandler>();

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
     * Generic annotation handler to instantiate annotation objects
     * via java.lang.reflect.Proxy mechanism.
     */
    public static class AnnotationInvocationHandler implements InvocationHandler {
    	public final Class<?> annoClass;
    	public final Map<String, Object> annoValues = new HashMap<String,Object>();

    	public AnnotationInvocationHandler(Class<?> annoClass,
				Map<String, Object> annoValues) {
    		this.annoClass = annoClass;
    		this.annoValues.putAll(annoValues);
		}

		public Object invoke(Object o, Method m, Object[] args)
                throws Throwable {
            if(m.getName().equals("toString") && !annoValues.containsKey(m.getName())) {
                return "annotation @" + annoClass.getName() + "(...)";
            } else if(m.getName().equals("hashCode")) {
                return annoClass.hashCode() + annoValues.hashCode();
            } else if(!annoValues.containsKey(m.getName())) {
             	throw new IllegalAccessException("Annotation proxy for '" + this + 
             			"' does not have method '" + m.getName() + "'");
            }
            return annoValues.get(m.getName());
        }
		@Override
		public String toString() {
			return "[AnnotationInvocationHandler @" +
					annoClass.getName().replace("interface ", "") +
					", " + annoValues + "]";
		}
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
    	AnnotationInvocationHandler h = new AnnotationInvocationHandler(annoClass, annoValues);
        T proxy = (T) Proxy.newProxyInstance(
                cl, new Class<?>[] { annoClass }, h);
        PROXY_HANDLERS.put(proxy, h);
        return proxy;
    }

    /**
     * Get @Documentation annotation instance from an enum constant.
     * @param c
     * @return
     */
    public static <T, C, AnnoT extends Annotation> AnnoT getAnnoFromEnum(EnumConstant<T, C> c, 
    		Class<AnnoT> annoClass) {
        try {
            Object enumClazz = c.getEnclosingClass().getClazz();
            if(enumClazz instanceof Class<?>) {
                Class<?> enumClass = (Class<?>)enumClazz;
                Field field = enumClass.getField(c.getName());
                return field.getAnnotation(annoClass);
            } else {
                /* looks like we are running in a schemagen context.. */
                return SchemagenUtil.extractAnnotation(enumClazz, annoClass);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to get annotation '@" + annoClass + "' for enum constant " + c, e);
        }
        return null;
    }
}
