package at.ac.tuwien.infosys.jaxb;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides utility methods to cope with JAXB schemagen, which uses
 * com.sun.sun.tools.javac.** to parse Java source code for XSD generation. We 
 * require a way to extract the information from the com.sun.sun.tools.javac.**
 * model classes, which is provided by this class.
 * 
 * @author Waldemar Hummer
 */
public class SchemagenUtil {

    public static final Logger logger = Logger
            .getLogger(SchemagenUtil.class.getName());

    /**
     * Extract the list of annotations from a given parameterized type.
     * 
     * @param type  The runtime type of this parameter is  
     *              assumed to be either of:
     *              * com.sun.tools.javac.code.Type$ClassType
     *              * com.sun.tools.javac.code.Symbol$TypeSymbol
     * @return
     * @throws Exception
     */
    public static <T> List<? extends Annotation> extractAnnotations(T type) throws Exception {
        List<Annotation> result = new LinkedList<Annotation>();
        List<?> annos = null;
        Object tsym = type;
        if(type.getClass().getName().endsWith("ClassType")) {
            tsym = type.getClass().getField("tsym").get(type);
        }
        annos = (List<?>) tsym.getClass().getMethod("getAnnotationMirrors").invoke(tsym);

        for (Object anno : annos) {
            String annoType = anno.getClass().getField("type").get(anno).toString();
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annoClass = (Class<? extends Annotation>)Class.forName(annoType);
            List<?> values = (List<?>)anno.getClass().getField("values").get(anno);
            Map<String,Object> annoValues = new HashMap<String,Object>();
            for (Object value : values) {
                Object fst = value.getClass().getField("fst").get(value);
                Object snd = value.getClass().getField("snd").get(value);
                String attrName = fst.getClass().getField("name").get(fst).toString();
                Object attrValue = snd.getClass().getMethod("getValue").invoke(snd);
                annoValues.put(attrName, attrValue);
            }
            Annotation annoInst = AnnotationUtils.createAnnotationProxy(annoClass, annoValues);
            result.add(annoInst);
        }
        //System.out.println("Extracted annotations for " + type + ": " + result);
        return result;
    }

    /**
     * 
     * @param type
     * @param annoType
     * @return
     */
    @SuppressWarnings("all")
    public static <T, A extends Annotation> A extractAnnotation(T type, Class<A> annoType) {
        try {
            for(Annotation a : extractAnnotations(type)) {
                if(annoType.isAssignableFrom(a.getClass())) {
                    return (A)a;
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to extract annotation '" + 
                    annoType + "' from type '" + type.getClass() + "'", e);
        }
        return null;
    }
}
