package at.ac.tuwien.infosys.jaxb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.Annotation;
import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.Facets.WhiteSpace;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.AnnotationImpl;
import com.sun.xml.xsom.impl.ParticleImpl;

/**
 * wsimport plugin to generate JAXB-Facets specific annotations in generated Java code.
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets 1.1.0
 */
public class WsImportFacetsPlugin extends Plugin {

    /** types of available facets */
    @SuppressWarnings("all")
    public static final Map<String,Class<?>> FACET_TYPES = new HashMap<String,Class<?>>(){
    {
        put("enumeration", String[].class);
        put("length", long.class);
        put("pattern", String.class);
        put("whiteSpace", WhiteSpace.class);
        put("totalDigits", long.class);
        put("fractionDigits", long.class);
        put("maxExclusive", String.class);
        put("minExclusive", String.class);
        put("maxInclusive", String.class);
        put("minInclusive", String.class);
        put("maxLength", long.class);
        put("minLength", long.class);
    }};

    /** list of available facets */
    public static final Set<String> FACET_NAMES = new HashSet<String>(FACET_TYPES.keySet());

    public String getOptionName() {
        return "jaxb-facets";
    }

    public String getUsage() {
        return "  -jaxb-facets    :  Generate JAXB annotations for XSD <facet>'s and <annotation>'s";
    }

    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler)
            throws SAXException {
        for(ClassOutline c: outline.getClasses()) {
            ClassOutlineImpl ci = (ClassOutlineImpl)c;
            XSComponent schemaEl = ci.target.getSchemaComponent();

            addXsdAnnotation(ci, schemaEl);
            addXsdFacets(ci, schemaEl);
        }
        return true;
    }


    public static void addXsdFacets(ClassOutlineImpl ci, XSComponent schemaEl) {
        JDefinedClass clazz = ci.implClass;
        for(FieldOutline f : ci.getDeclaredFields()) {
            XSComponent schema = f.getPropertyInfo().getSchemaComponent();

            final Map<String,String> prefixToNamespace = new HashMap<String,String>();
            prefixToNamespace.put("xsd", XmlSchemaEnhancer.NS_XSD);

            if(schema instanceof ParticleImpl) {
                ParticleImpl p = (ParticleImpl)schema;
                XSType type = p.getTerm().asElementDecl().getType();
                if(type.isSimpleType()) {
                    XSSimpleType stype = type.asSimpleType();
                    for(String fName : FACET_NAMES) {
                        XSFacet fValue = stype.getFacet(fName);
                        if(fValue != null) {
                            String name = f.getPropertyInfo().getName(false);
                            JFieldVar var = clazz.fields().get(name);
                            JAnnotationUse anno = getAnnotation(var, Facets.class);
                            if(FACET_TYPES.get(fName) == long.class) {
                                anno.param(fValue.getName(), Long.parseLong(fValue.getValue().value));
                            } else if(FACET_TYPES.get(fName) == String.class) {
                                anno.param(fValue.getName(), fValue.getValue().value);
                            } else if(FACET_TYPES.get(fName) == WhiteSpace.class) {
                                anno.param(fValue.getName(), WhiteSpace.valueOf(fValue.getValue().value));
                            } else if(FACET_TYPES.get(fName) == String[].class) {
                                // TODO for "enumeration" facet - is this needed?
                            }
                        }
                    }
                }
            }
        }
    }

    public static void addXsdAnnotation(ClassOutlineImpl ci, XSComponent schemaEl) {
        XSAnnotation anno = schemaEl.getAnnotation();
        if(anno != null) {
            AnnotationImpl annoImpl = (AnnotationImpl)anno;
            BindInfo annoInfo = (BindInfo)annoImpl.getAnnotation();
            final String doc = annoInfo.getDocumentation();
            if(doc != null) {
                JAnnotationUse jAnno = getAnnotation(ci.implClass, Annotation.class);
                JAnnotationUse annoUse = jAnno.annotationParam("documentation", Documentation.class);
                annoUse.param("value", doc);
            }
        }
    }

    private static JAnnotationUse getAnnotation(JDefinedClass clazz, 
            Class<? extends java.lang.annotation.Annotation> annoClass) {
        for(JAnnotationUse a : clazz.annotations()) {
            if(a.getAnnotationClass().fullName().equals(annoClass.getName())) {
                return a;
            }
        }
        JAnnotationUse jAnno = clazz.annotate(annoClass);
        return jAnno;
    }

    private static JAnnotationUse getAnnotation(JFieldVar var, 
            Class<? extends java.lang.annotation.Annotation> annoClass) {
        for(JAnnotationUse a : var.annotations()) {
            if(a.getAnnotationClass().fullName().equals(annoClass.getName())) {
                return a;
            }
        }
        JAnnotationUse jAnno = var.annotate(annoClass);
        return jAnno;
    }

}
