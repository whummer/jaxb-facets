package at.ac.tuwien.infosys.jaxb;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.xml.bind.annotation.AnnotationLocation;
import javax.xml.bind.annotation.AppInfo;
import javax.xml.bind.annotation.Assert;
import javax.xml.bind.annotation.Attribute;
import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.Facets.FacetDefinition;
import javax.xml.bind.annotation.Facets.WhiteSpace;
import javax.xml.bind.annotation.MaxOccurs;
import javax.xml.bind.annotation.MinOccurs;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import at.ac.tuwien.infosys.jaxb.AnnotationUtils.AnnotationInvocationHandler;

import com.sun.xml.bind.v2.model.core.ArrayInfo;
import com.sun.xml.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.bind.v2.model.core.EnumConstant;
import com.sun.xml.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.model.core.PropertyInfo;
import com.sun.xml.bind.v2.model.core.TypeRef;
import com.sun.xml.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.bind.v2.schemagen.xmlschema.SimpleRestrictionModel;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.output.ResultFactory;
import com.sun.xml.txw2.output.TXWResult;
import com.sun.xml.txw2.output.TXWSerializer;

/**
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @version 0.2 added support for Facet restrictions for XML attributes
 * @version 0.3 fixed classloading/proxying issue for JBoss, related to:
 *          http://lists.jboss.org/pipermail/forge-issues/2011-October/000351.html
 */
@SuppressWarnings("all")
public class XmlSchemaEnhancer {
    public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    public static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    
    private static ValidationFacetsFilter facetFilter = new ValidationFacetsFilter();

    private static final DocumentBuilderFactory XML_FACTORY = DocumentBuilderFactory.newInstance();

	private static final List<Class<? extends Annotation>> EXT_ANNO_CLASSES_AT_START = 
			new ArrayList<Class<? extends Annotation>>();
	private static final List<Class<? extends Annotation>> EXT_ANNO_CLASSES_AT_END = 
			new ArrayList<Class<? extends Annotation>>();
	
	static {
		EXT_ANNO_CLASSES_AT_START.add(javax.xml.bind.annotation.Annotation.class);
		EXT_ANNO_CLASSES_AT_END.add(Assert.class);
	}

	public static final AtomicBoolean XSD_11_ENABLED = new AtomicBoolean(true);

    public static final Logger logger = Logger
            .getLogger(XmlSchemaEnhancer.class.getName());

    public static <T, C> boolean hasExtendedAnnotations(TypeRef<T, C> t) {
        return hasFacets(t) || hasXsdExtensions(t);
    }

    public static <T, C> boolean hasExtendedAnnotations(
            AttributePropertyInfo<T, C> info) {
        return hasFacets(info) || hasXsdExtensions(info);
    }

    public static <T, C> void addFacets(ValuePropertyInfo<T, C> vp,
            SimpleRestrictionModel restriction) {
        if (!hasFacets(vp))
            return;

        Facets facetsAnno = getFacetsAnnotation(vp);
        addFacets(facetsAnno, restriction, vp.getSource().getSchemaType());
    }

    public static <T, C> void addFacets(EnumLeafInfo<T, C> e,
            SimpleRestrictionModel restriction) {
        T type = e.getType();
        if(type instanceof Class<?>) {
            Facets facets = ((Class<?>)type).getAnnotation(Facets.class);
            addFacets(facets, (TypedXmlWriter)restriction, (Class<?>)type);
        } else if(type.getClass().getName().endsWith("ClassType")) {
            Facets facets = SchemagenUtil.extractAnnotation(type, Facets.class);
            addFacets(facets, (TypedXmlWriter)restriction, (Class<?>)null);
        }
    }

    public static <T, C> void addFacets(TypeRef<T, C> t, LocalElement e) {
        if (!hasFacets(t))
            return;

        Facets facetsAnno = getFacetsAnnotation(t);
        TypedXmlWriter restriction = getRestriction(t, e, null);
        if(t.getTarget().getType() instanceof Class<?>) {
            addFacets(facetsAnno, restriction, (Class<?>)t.getTarget().getType());
        } else if(isClassType(t.getTarget().getType())) {
            addFacets(facetsAnno, restriction, (Class<?>)null);
        }
    }

    private static boolean isClassType(Object type) {
    	//return type.getClass().getName().equals("com.sun.tools.javac.code.Type$ClassType");
    	if (type instanceof DeclaredType) {
    		DeclaredType dc = (DeclaredType) type;
    		if (dc.getKind() == TypeKind.DECLARED) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static <T, C> void addFacets(AttributePropertyInfo<T, C> info,
            LocalAttribute attr) {
        if (!hasFacets(info))
            return;

        Facets facetsAnno = getFacetsAnnotation(info);
        TypedXmlWriter restriction = getRestriction(info, attr, null);
        addFacets(facetsAnno, restriction, info.getSource().getSchemaType());
    }

    public static <T, C> void addFacets(Facets facetsAnno,
            TypedXmlWriter restriction, QName baseType) {
    	addFacets(facetsAnno, restriction, baseType == null ? null :
    			Constants.FACET_TYPES.get(baseType.getLocalPart()));
    }
    public static <T, C> void addFacets(Facets facetsAnno,
            TypedXmlWriter restriction, Class<?> baseType) {

        Map<String, List<String>> facets = null;
        try {
            facets = getDefinedFacets(facetsAnno);
        } catch (Exception ex) {
            logger.log(Level.WARNING,
                    "Unable to add XSD Facets in Schema generated by JAXB.", ex);
            return;
        }

        /* check if we have invalid facets, e.g., minExclusive on xs:string is not allowed */
        checkFacetsValidity(baseType, facets);

        for (String facetName : facets.keySet()) {
            for (String facetValue : facets.get(facetName)) {
                logger.fine("Adding XSD-Facets schema restriction: "
                        + new QName(NS_XSD, facetName));
                restriction._element(new QName(NS_XSD, facetName),
                        TypedXmlWriter.class)._attribute("value", facetValue);
            }
        }
    }

	private static void checkFacetsValidity(Class<?> baseType, 
			Map<String, List<String>> facets) {
		if(baseType == null)
			return;
		Set<String> allowed = Constants.FACETS_BY_TYPE.get(baseType);
		if(allowed == null && Enum.class.isAssignableFrom(baseType)) {
			allowed = Constants.FACETS_BY_TYPE.get(Enum.class);
		}
		if(allowed == null) {
    		logger.fine("Cannot determine allowed facets for base type " + baseType);
		} else {
	        for (String facetName : facets.keySet()) {
	        	if(!allowed.contains(facetName)) {
	        		logger.info("Facet '" + facetName + "' not in allowed facets " + 
	        				allowed + " for base type " + baseType);
	        	}
	        }
		}
	}

	public static <T, C> boolean hasFacets(ValuePropertyInfo<T, C> vp) {
        Facets facets = getFacetsAnnotation(vp);
        return hasFacets(facets);
    }

    public static <T, C> boolean hasFacets(TypeRef<T, C> t) {
        Facets facets = getFacetsAnnotation(t);
        return hasFacets(facets);
    }

    public static <T, C> boolean hasFacets(AttributePropertyInfo<T, C> ap) {
        Facets facets = getFacetsAnnotation(ap);
        return hasFacets(facets);
    }

    public static <T, C> boolean hasFacets(Facets facets) {
        if (facets == null)
            return false;

        try {
            Map<String, List<String>> definedFacets = getDefinedFacets(facets);
            return definedFacets.size() > 0;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to get defined XSD Facets", e);
            return false;
        }
    }

    public static <T, C> void addXsdExtensions(Set<ClassInfo<T, C>> classes,
            Set<EnumLeafInfo<T, C>> enums, Set<ArrayInfo<T, C>> arrays,
            TypedXmlWriter w) {
        Set<Package> annotatedPackages = new HashSet<Package>();
        for (ClassInfo<T, C> c : classes) {
            annotatedPackages.addAll(extractPackage(c.getType()));
        }
        for (EnumLeafInfo<T, C> c : enums) {
            annotatedPackages.addAll(extractPackage(c.getType()));
        }
        for (ArrayInfo<T, C> c : arrays) {
            annotatedPackages.addAll(extractPackage(c.getType()));
        }
        for (Package p : annotatedPackages) {
            XmlSchemaEnhancer.addXsdExtensionsAtStart(p, w);
        }
    }

    public static <T> void addXsdExtensionsAtStart(T type, TypedXmlWriter w) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(type, c)) {
    	        Annotation anno = getXsdExtensionAnnotation(type, c);
    	        addXsdExtensionInsideElement(anno, w);
    		}
    	}
    }
    public static <T, C> void addXsdExtensionsAtStart(ClassInfo<T, C> ci,
            TypedXmlWriter w) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(ci, c)) {
    	        Annotation anno = getXsdExtensionAnnotation(ci, c);
    	        addXsdExtensionInsideElement(anno, w);
    		}
    	}
    }
    public static <T, C> void addXsdExtensionsAtStart(
            AttributePropertyInfo<T, C> _info, LocalAttribute _attr) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(_info, c)) {
    	        Annotation anno = getXsdExtensionAnnotation(_info, c);
    	        addXsdExtensionInsideElement(anno, _attr);
    		}
    	}
    }
    public static <T, C> void addXsdExtensionsAtStart(TypeRef<T, C> t, LocalElement e) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(t, c)) {
    	        Annotation anno = getXsdExtensionAnnotation(t, c);
    	        addXsdExtensionInsideElement(anno, e);
    		}
    	}
    }

    public static <T,C> void addXsdExtensionsAtEnd(ClassInfo<T,C> type, TypedXmlWriter w) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_END) {
    		if(hasXsdExtension(type, c)) {
    	        Annotation anno = getXsdExtensionAnnotation(type, c);
    	        addXsdExtensionInsideElement(anno, w);
    		}
    	}
    }
    public static <T,C> void addXsdExtensionsAtEnd(PropertyInfo<T, C> elementInfo, TypedXmlWriter w) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_END) {
	    	Annotation anno = elementInfo.readAnnotation(c);
	    	if(anno != null) {
	    		addXsdExtensionInsideElement(anno, w);
	    	}
    	}
    }

    public static <T, C> void addXsdAnnotationsOutsideElement(
    		ElementPropertyInfo<T, C> elementInfo, LocalElement el) {

    	/* INFO: For now, this can only affect <xsd:annotation>, i.e., class
    	 * javax.xml.bind.annotation.Annotation. The other XSD elements are 
    	 * usually INSIDE the element they belong to... */

    	/* only write the annotation if location == OUTSIDE_ELEMENT ! */
    	javax.xml.bind.annotation.Annotation anno = 
    			elementInfo.readAnnotation(javax.xml.bind.annotation.Annotation.class);
        if(anno != null && anno.location() == AnnotationLocation.OUTSIDE_ELEMENT) {
            addXsdExtension(anno, el);
        }
    }

    public static <T, C> void addXsdAnnotationsOutsideElement(
    		PropertyInfo<T, C> elementInfo, Particle c) {

    	/* INFO: For now, this can only affect <xsd:annotation>, i.e., class
    	 * javax.xml.bind.annotation.Annotation. The other XSD elements are 
    	 * usually INSIDE the element they belong to... */

    	/* only write the annotation if location == OUTSIDE_ELEMENT ! */
    	javax.xml.bind.annotation.Annotation anno = 
    			elementInfo.readAnnotation(javax.xml.bind.annotation.Annotation.class);
        if(anno != null && anno.location() == AnnotationLocation.OUTSIDE_ELEMENT) {
            addXsdExtension(anno, c);
        }
    }

    private static <T, C> void addXsdExtensionInsideElement(
            Annotation anno, TypedXmlWriter obj) {

    	if(instanceOf(anno, javax.xml.bind.annotation.Annotation.class)) {
    		javax.xml.bind.annotation.Annotation annoCast = (javax.xml.bind.annotation.Annotation)anno;
			/* only write the annotation if location == INSIDE_ELEMENT ! */
	    	if(annoCast.location() == AnnotationLocation.INSIDE_ELEMENT) {
	    		addXsdExtension(anno, obj);
	    	}
    	} else if(instanceOf(anno, Assert.class)) {
    		addXsdExtension(anno, obj);
    	} else {
    		logger.warning("Unexpected annotation type: " + anno.getClass());
    	}
    }

    public static <T, C> void addXsdExtension(
            Annotation annoInst, TypedXmlWriter obj) {

    	if(instanceOf(annoInst, javax.xml.bind.annotation.Annotation.class)) {

    		javax.xml.bind.annotation.Annotation anno = 
    				(javax.xml.bind.annotation.Annotation)annoInst;
        	TypedXmlWriter annoEl = writeXsdAnnotationElement(obj, 
	                anno.id(), anno.attributes());
	        for (AppInfo info : anno.appinfo()) {
	            TypedXmlWriter w = annoEl._element(new QName(NS_XSD, "appinfo"),
	                    TypedXmlWriter.class);
	            if (info.source() != null && !info.source().equals("")) {
	                w._attribute(new QName("source"), info.source());
	            }
	            /* Use XML parser to allow XML content in appinfo */
	            writeXMLOrPCData(w, info.value());
	        }
	        for (Documentation doc : anno.documentation()) {
	            TypedXmlWriter w = annoEl._element(new QName(NS_XSD,
	                    "documentation"), TypedXmlWriter.class);
	            if (doc.source() != null && !doc.source().equals("")) {
	                w._attribute(new QName("source"), doc.source());
	            }
	            if (doc.lang() != null && !doc.lang().equals("")) {
	                w._attribute(new QName(NS_XML, "lang"), doc.lang());
	            }
	            /* Use XML parser to allow XML content in documentation */
	            writeXMLOrPCData(w, doc.value());
	        }
    	} else if(instanceOf(annoInst, javax.xml.bind.annotation.Assert.class)) {
    		Assert anno = (Assert)annoInst;

        	if(checkXSD11Enabled()) {
	    		writeXsdAssertElement(obj, anno.id(), anno.test(), anno.attributes());
	    		if(anno.annotation() != null && anno.annotation().length > 0) {
	    			/* recurse into this same method to write <xsd:annotation>s 
	    			 * inside this <xsd:assert> */
	    			for(javax.xml.bind.annotation.Annotation xsdAnno : anno.annotation()) {
	    				addXsdExtension(xsdAnno, obj);
	    			}
	    		}
        	}
    	} else {
    		logger.warning("Unexpected annotation instance: " + annoInst);
    	}
    }

	/**
     * Try parsing a value as an XML root element. Returns a corresponding XML Document 
     * if the string value is a valid XML root element, otherwise null.
     * @param value
     * @return
     */
    private static Document parseValueAsXML(String value) {
        try {
            if(value == null || !value.trim().startsWith("<")) {
                return null;
            }
            DocumentBuilder dBuilder = XML_FACTORY.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(value.getBytes()));
            doc.getDocumentElement().normalize();
            logger.fine("Treating string as valid XML: '" + value + "'");
            return doc;
        } catch (Exception e) {
            logger.fine("Cannot parse value as XML, treating as regular string: '" + value + "'");
            return null;
        }
    }

    /**
     * If the passed value is a valid XML root element, then we parse it and
     * write the XML element to the given TypedXmlWriter. Otherwise, the 
     * value will be written to TypedXmlWriter as a PCDATA string (i.e., 
     * characters like '<', '&' etc. will be replaced by '&lt;', '&amp;' etc.).
     * @param w
     * @param value
     */
    private static void writeXMLOrPCData(TypedXmlWriter w, String value) {
        Document doc = parseValueAsXML(value);
        if(doc == null) {
            w._pcdata(value);
            return;
        }
        try {
            writeXML(w, value);
        } catch (Exception e) {
            logger.info("Unable to write XML data to TXW2 serializer: " + e);
            w._pcdata(value);
        }
    }

    private static void writeXML(final TypedXmlWriter w, String value) throws Exception {
        TXWSerializer ser = (TXWSerializer)ResultFactory.createSerializer(new TXWResult(w));
        new DOMtoTXW(w).convert(value);
    }

    public static <T, C> boolean hasXsdExtensions(ClassInfo<T, C> ci) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(ci, c)) {
    			return true;
    		}
    	}
    	return false;
    }
    public static <T, C> boolean hasXsdExtensions(TypeRef<T, C> t) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(t, c)) {
    			return true;
    		}
    	}
    	return false;
    }
    public static <T, C> boolean hasXsdExtensions(AttributePropertyInfo<T, C> info) {
    	for(Class<? extends Annotation> c : EXT_ANNO_CLASSES_AT_START) {
    		if(hasXsdExtension(info, c)) {
    			return true;
    		}
    	}
    	return false;
    }

    public static <T, C> boolean hasXsdExtension(ClassInfo<T, C> ci, Class<? extends Annotation> annoClass) {
        Annotation anno = getXsdExtensionAnnotation(ci, annoClass);
        return anno != null;
    }

    public static <T> boolean hasXsdExtension(T type, Class<? extends Annotation> annoClass) {
        Annotation anno = getXsdExtensionAnnotation(type, annoClass);
        return anno != null;
    }

    public static <T, C> boolean hasXsdExtension(TypeRef<T, C> t, Class<? extends Annotation> annoClass) {
        Annotation anno = getXsdExtensionAnnotation(t, annoClass);
        return anno != null;
    }

    public static <T, C> boolean hasXsdExtension(
            AttributePropertyInfo<T, C> ap, Class<? extends Annotation> annoClass) {
        Annotation anno = getXsdExtensionAnnotation(ap, annoClass);
        return anno != null;
    }

    public static <T, C> boolean writeCustomOccurs(TypeRef<T, C> t,
            LocalElement e, boolean isOptional, boolean repeated) {
        MaxOccurs max = null;
        MinOccurs min = null;
        try {
            max = (MaxOccurs) getAnnotationOfProperty(t.getSource(),
                    MaxOccurs.class);
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get @MaxOccurs annotation from type " + t, e2);
        }
        try {
            min = (MinOccurs) getAnnotationOfProperty(t.getSource(),
                    MinOccurs.class);
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get @MinOccurs annotation from type " + t, e2);
        }

        if (min == null && max == null)
            return false;

        if (min != null) {
            int value = (int) min.value();
            e.minOccurs(value);
        } else if (isOptional) {
            e.minOccurs(0);
        }

        if (max != null) {
            int value = (int) max.value();
            e.maxOccurs(value);
        } else if (repeated) {
            e.maxOccurs("unbounded");
        }

        return true;
    }

    /* PRIVATE HELPER METHODS */

    /**
     * We need to disable XSD 1.1 features (e.g., <assert>)
     * if running in wsimport context, otherwise we get
     * parsing errors later on in the process:
     * com.sun.tools.ws.wscompile.AbortException
        at com.sun.tools.ws.processor.modeler.wsdl.JAXBModelBuilder.bind(JAXBModelBuilder.java:144)
        at com.sun.tools.ws.processor.modeler.wsdl.WSDLModeler.buildJAXBModel(WSDLModeler.java:2244)
        at com.sun.tools.ws.processor.modeler.wsdl.WSDLModeler.internalBuildModel(WSDLModeler.java:191)
        at com.sun.tools.ws.processor.modeler.wsdl.WSDLModeler.buildModel(WSDLModeler.java:137)
        
     *  Disabling has to be done manually by setting XSD_11_ENABLED
     */
	private static boolean checkXSD11Enabled() {
		return XSD_11_ENABLED.get();
	}

    private static <T> Set<Package> extractPackage(T type) {
        Set<Package> packages = new HashSet<Package>();
        if(type instanceof Class<?>) {
            Class<?> cl = (Class<?>) type;
            Package pkg = cl.getPackage();
            packages.add(pkg);
        } else {
            /* If jaxb-facets is used in the context of JAXB schemagen,
             * the incoming parameter 'type' is not Class<?>, but
             * com.sun.tools.javac.code.Type$ClassType. Since
             * we don't want a hard-coded dependency on that class 
             * within jaxb-facets, we use a workaround here. */
            try {
                String className = type.toString();
                String packageName = className.substring(0, className.lastIndexOf("."));
                Package pkg = Package.getPackage(packageName);
                if(pkg != null) {
                    /* TODO: pkg seems to be null here all the time.
                     * This means that package-level annotations are 
                     * currently not supported for schemagen-based JAXB,
                     * because the schemagen mechanism is based on on-the-fly 
                     * compilation and hence reflection (classes/packages) 
                     * is not available at runtime. This shall be fixed
                     * in a future release. */
                    packages.add(pkg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "Unable to derive package name from class type: " + type, e);
            }
        }
        return packages;
    }

    private static boolean instanceOf(Annotation annoInst,
			Class<? extends Annotation> clazz) {
    	if(annoInst == null) {
    		return false;
    	}
		/* if we deal with a java.lang.reflect.Proxy instance,
		 * things get a bit nasty... 
		 * FIXME find better approach, and check if needed at all!! */
		if(Proxy.isProxyClass(annoInst.getClass())) {
			AnnotationInvocationHandler h = (AnnotationInvocationHandler)
					AnnotationUtils.PROXY_HANDLERS.get(annoInst);
			if(h != null) {
				return isAssignableFrom(clazz, h.annoClass);
			}
		}
		return isAssignableFrom(clazz, annoInst.getClass());
	}
    private static boolean isAssignableFrom(Class<?> clazz1, Class<?> clazz2) {
    	/* Due to possible classloading magic taking place, we simply compare 
    	 * canonical classnames here (and do not compare classes directly) */
		if(clazz1.getName().equals(clazz2.getName())) {
			return true;
		}
		/* if we deal with some other class */
		if(clazz1.isAssignableFrom(clazz2)) {
			return true;
		}
		return false;
	}

    private static <T, C, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            ClassInfo<T, C> ci, Class<AnnoT> annoClass) {
        return getXsdExtensionAnnotation(ci.getType(), annoClass);
    }

    private static <T, C, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            EnumConstant c, Class<AnnoT> annoClass) {
    	if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
	        Documentation doc = AnnotationUtils
	                .getAnnoFromEnum((EnumConstant) c, Documentation.class);
	        return (AnnoT)getXsdAnnotationAnnotation(null, doc, null);
    	}  else if(isAssignableFrom(Assert.class, annoClass)) {
	        return (AnnoT)AnnotationUtils.getAnnoFromEnum((EnumConstant) c, annoClass);
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    private static <T, C, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            Class<?> clazz, Class<AnnoT> annoClass) {
    	if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
            javax.xml.bind.annotation.Annotation anno = clazz
                    .getAnnotation(javax.xml.bind.annotation.Annotation.class);
            AppInfo appinfo = clazz.getAnnotation(AppInfo.class);
            Documentation doc = clazz.getAnnotation(Documentation.class);
            return (AnnoT)getXsdAnnotationAnnotation(anno, doc, appinfo);
    	} else {
    		throw new IllegalArgumentException("" + annoClass);
    	}
    }
    private static <T, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            T type, Class<AnnoT> annoClass) {
    	AnnoT anno = null;
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            anno = (AnnoT)clazz.getAnnotation(annoClass);
            if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
	            AppInfo appinfo = clazz.getAnnotation(AppInfo.class);
	            Documentation doc = clazz.getAnnotation(Documentation.class);
	            return (AnnoT)getXsdAnnotationAnnotation(
	            		(javax.xml.bind.annotation.Annotation)anno, doc, appinfo);
            } else if(isAssignableFrom(Assert.class, annoClass)) {
            	return (AnnoT)clazz.getAnnotation(Assert.class);
            } else {
            	throw new IllegalArgumentException("" + annoClass);
            }
        } else if (type instanceof Package) {
            Package pkg = (Package) type;
            anno = pkg.getAnnotation(annoClass);
            if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
                AppInfo appinfo = pkg.getAnnotation(AppInfo.class);
                Documentation doc = pkg.getAnnotation(Documentation.class);
                return (AnnoT)getXsdAnnotationAnnotation(
                		(javax.xml.bind.annotation.Annotation)anno, doc, appinfo);
            } else if(isAssignableFrom(Assert.class, annoClass)) {
            	return (AnnoT)pkg.getAnnotation(Assert.class);
            } else {
            	throw new IllegalArgumentException("" + annoClass);
            }
        } else if (type instanceof EnumConstant) {
            if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
	            Documentation doc = AnnotationUtils
	                    .getAnnoFromEnum((EnumConstant) type, Documentation.class);
	            return (AnnoT)XmlSchemaEnhancer
	                    .getXsdAnnotationAnnotation(null, doc, null);
            } else if(isAssignableFrom(Assert.class, annoClass)) {
            	return (AnnoT)AnnotationUtils
	                    .getAnnoFromEnum((EnumConstant) type, Assert.class);
            } else {
            	throw new IllegalArgumentException("" + annoClass);
            }
        } else if (type.getClass().getName().endsWith("ClassType")) {
            anno = (AnnoT)SchemagenUtil.extractAnnotation(type, annoClass);
            if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
	            AppInfo appinfo = SchemagenUtil.extractAnnotation(type, AppInfo.class);
	            Documentation doc = SchemagenUtil.extractAnnotation(type, Documentation.class);
	            return (AnnoT)getXsdAnnotationAnnotation(
	            		(javax.xml.bind.annotation.Annotation)anno, doc, appinfo);
            } else if(isAssignableFrom(Assert.class, annoClass)) {
            	return (AnnoT)SchemagenUtil.extractAnnotation(type, Assert.class);
            } else {
            	throw new IllegalArgumentException("" + annoClass);
            }
        } else {
            logger.warning("Cannot get annotation '@" + annoClass + "' for unknown type '" + type + "'");
        }
        return null;
    }

    private static <T, C, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            TypeRef<T, C> t, Class<AnnoT> annoClass) {
        return getXsdExtensionAnnotation(t.getSource(), annoClass);
    }

    private static <T, C, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            AttributePropertyInfo<T, C> t, Class<AnnoT> annoClass) {
        return getXsdExtensionAnnotation(t.getSource(), annoClass);
    }

    private static <T, C, AnnoT extends Annotation> AnnoT getXsdExtensionAnnotation(
            PropertyInfo<T, C> propInfo, Class<AnnoT> annoClass) {

        if(isAssignableFrom(javax.xml.bind.annotation.Annotation.class, annoClass)) {
	        javax.xml.bind.annotation.Annotation anno = null;
	        AppInfo appinfo = null;
	        Documentation doc = null;
	
	        try {
	            Object value = getAnnotationOfProperty(propInfo,
	                    javax.xml.bind.annotation.Annotation.class);
	            if (value instanceof javax.xml.bind.annotation.Annotation) {
	                anno = (javax.xml.bind.annotation.Annotation) value;
	            }
	        } catch (Exception e2) {
	            logger.log(Level.WARNING,
	                    "Unable to get XSD Annotation annotation from type " + propInfo,
	                    e2);
	        }
	
	        try {
	            Object value = getAnnotationOfProperty(propInfo, AppInfo.class);
	            if (value instanceof AppInfo) {
	                appinfo = (AppInfo) value;
	            }
	        } catch (Exception e2) {
	            logger.log(Level.WARNING,
	                    "Unable to get XSD AppInfo annotation from type " + propInfo, e2);
	        }
	
	        try {
	            Object value = getAnnotationOfProperty(propInfo, Documentation.class);
	            if (value instanceof Documentation) {
	                doc = (Documentation) value;
	            }
	        } catch (Exception e2) {
	            logger.log(
	                    Level.WARNING,
	                    "Unable to get XSD Documentation annotation from type " + propInfo,
	                    e2);
	        }

	        AnnoT result = (AnnoT)getXsdAnnotationAnnotation(anno, doc, appinfo);
	        return result;

        } else if(isAssignableFrom(Assert.class, annoClass)) {
        	try {
				return (AnnoT)getAnnotationOfProperty(propInfo, Assert.class);
			} catch (Exception e) {
	            logger.log(
	                    Level.WARNING,
	                    "Unable to get @javax.xml.bind.annotation.Assert annotation from type " + propInfo,
	                    e);
			}
        }
        throw new IllegalArgumentException("" + annoClass);

    }

    private static ClassLoader selectClassLoader(Object ... objects) {
    	ClassLoader cl = null;
    	for(Object o : objects) {
    		if(o != null) {
    			cl = o.getClass().getClassLoader();
    			break;
    		}
    	}
        // Java7 users have encountered problems here.
        // Fallback to system classloader is necessary for compatibility
        // with Java7 JAXB bootstrapping/overriding mechanism.
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    
    protected static <T, C> Assert getXsdAssertAnnotation(String id, String test,
    		String xpathDefaultNamespace, Attribute[] attributes, Annotation[] annotation) {

        final Map<String, Object> annoValues = new HashMap<String, Object>();
        annoValues.put("id", id);
        annoValues.put("test", test);
        annoValues.put("xpathDefaultNamespace", xpathDefaultNamespace);
        annoValues.put("attributes", attributes);
        annoValues.put("annotation", annotation);
        annoValues.put("xpathDefaultNamespace", xpathDefaultNamespace);

        ClassLoader cl = selectClassLoader();

        Assert anno = AnnotationUtils.createAnnotationProxy(Assert.class, annoValues, cl);
    	return anno;
    }

    protected static <T, C> javax.xml.bind.annotation.Annotation getXsdAnnotationAnnotation(
            javax.xml.bind.annotation.Annotation _anno, Documentation _doc, AppInfo _appinfo) {
    	
    	// jpell - no point if none of the params is provided.
    	if (_anno == null && _doc == null && _appinfo == null) {
    		return null;
    	}
    	
        ClassLoader cl = selectClassLoader(_anno, _doc,_appinfo);

        final Map<String, Object> annoValues = new HashMap<String, Object>();
        annoValues.put("id", "");
        annoValues.put("appinfo", new AppInfo[] {});
        annoValues.put("attributes", new Attribute[] {});
        annoValues.put("documentation", new Documentation[] {});
        annoValues.put("location", AnnotationLocation.INSIDE_ELEMENT);

        boolean hasAnno = false;

        try {
            if (_anno instanceof javax.xml.bind.annotation.Annotation) {
                annoValues.put("id", _anno.id());
                annoValues.put("appinfo", _anno.appinfo());
                annoValues.put("attributes", _anno.attributes());
                annoValues.put("documentation", _anno.documentation());
                annoValues.put("location", _anno.location());
                hasAnno = true;
            }
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get XSD Annotation annotation from type "
                            + _anno, e2);
        }

        try {
            if (_appinfo instanceof AppInfo) {
                AppInfo[] appinfos = (AppInfo[]) annoValues.get("appinfo");
                annoValues.put("appinfo", concat(appinfos, _appinfo));
                hasAnno = true;
            }
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get XSD AppInfo annotation from type "
                            + _appinfo, e2);
        }

        try {
            if (_doc instanceof Documentation) {
                Documentation[] docs = (Documentation[]) annoValues
                        .get("documentation");
                annoValues.put("documentation", concat(docs, _doc));
                hasAnno = true;
            }
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get XSD Documentation annotation from type "
                            + _doc, e2);
        }

        javax.xml.bind.annotation.Annotation anno = AnnotationUtils.
                createAnnotationProxy(javax.xml.bind.annotation.Annotation.class, annoValues, cl);

        return hasAnno ? anno : null;
    }

    private static <T, C> Facets getFacetsAnnotation(TypeRef<T, C> t) {
        if (!t.getTarget().isSimpleType())
            return null;

        try {
            Object value = getAnnotationOfProperty(t.getSource(), Facets.class);
            if (value instanceof Facets)
                return (Facets) value;
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get Facets annotation from type " + t, e2);
        }
        return null;
    }

    private static <T, C> Facets getFacetsAnnotation(ValuePropertyInfo<T, C> vp) {
        if (!vp.getTarget().isSimpleType())
            return null;

        try {
            Object value = getAnnotationOfProperty(vp.getSource(), Facets.class);
            if (value instanceof Facets)
                return (Facets) value;
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get Facets annotation from type " + vp, e2);
        }
        return null;
    }

    private static <T, C> Facets getFacetsAnnotation(
            AttributePropertyInfo<T, C> t) {
    	/* make sure this is either
    	 *  - a simple type like string, or
    	 *  - an IDREF, which is also considered a simple type
    	 */
        if (!t.getTarget().isSimpleType() && t.getSource().id() != ID.IDREF)
            return null;

        try {
            Object value = getAnnotationOfProperty(t.getSource(), Facets.class);
            if (value instanceof Facets)
                return (Facets) value;
        } catch (Exception e2) {
            logger.log(Level.WARNING,
                    "Unable to get Facets annotation from type " + t, e2);
        }
        return null;
    }

    private static <T, C> Object getAnnotationOfProperty(
            PropertyInfo<T, C> info, Class<? extends Annotation> annoClass)
            throws Exception {
        if (annoClass == Facets.class) {
            Object result = facetFilter.filterAnnotation(annoClass, info.readAnnotation(Facets.class), info);
        	if (result != null) {
                return result;
            }
        } else if (annoClass == MaxOccurs.class && info.hasAnnotation(MaxOccurs.class)) {
            return info.readAnnotation(MaxOccurs.class);
        } else if (annoClass == MinOccurs.class) {
            Object result = facetFilter.filterAnnotation(annoClass, info.readAnnotation(MinOccurs.class), info);
            if (result != null) {
                return result;
            }
        } else if (annoClass == Documentation.class && info.hasAnnotation(Documentation.class)) {
            return info.readAnnotation(Documentation.class);
        } else if (annoClass == AppInfo.class && info.hasAnnotation(AppInfo.class)) {
            return info.readAnnotation(AppInfo.class);
        } else if (info.parent() == null) {
            return null;
        } else if (!(info.parent().getType() instanceof Class<?>)) {
            return null;
        }

        String name = info.getName();
    	Object type = info.parent().getType();
    	if (isClassType(info.parent().getType())) {
    		/* We are (most likely) executing in the scope of a schemagen run. It seems
    		 * that at this point it is sufficient to return null from this method. 
    		 * This is also covered by a test case named "SchemagenTest", and 
    		 * it seems to work (see also github issue #23). */
        	return null;
        }

        if(type instanceof Class<?>) {
	        Class<?> parent = (Class<?>) info.parent().getType();
	        return getAnnotationOfProperty(parent, name, annoClass);
        } else {
        	throw new RuntimeException("Unexpected type of property parent: " + info.parent().getType());
        }
    }

	protected static <T extends Annotation> T getAnnotationOfProperty(
            Class<?> parent, String fieldName, Class<T> annoClass)
            throws Exception {
        try {
            Field field = findAnnotatedField(parent, fieldName);
            if (field == null)
                return null;
            Object a = facetFilter.filterAnnotation(annoClass, getAnnotation(field, annoClass), field);
            return (T) a;
        } catch (Exception e) {
            throw new RuntimeException("Could not get annotation '"
                    + annoClass.getSimpleName() + "' of field " + fieldName
                    + " of class " + parent, e);
        }
    }

    private static Field findAnnotatedField(Class<?> parent, String fieldName) {
        Field field = null;
        for (Field f : parent.getDeclaredFields()) {
            if (f.getName().equals(fieldName)) {
                field = f;
                break;
            } else {
                if (getAnnotation(f, XmlElement.class) != null) {
                    XmlElement e = (XmlElement) getAnnotation(f,
                            XmlElement.class);
                    if (fieldName.equals(e.name())) {
                        field = f;
                        break;
                    }
                }
                if (getAnnotation(f, XmlAttribute.class) != null) {
                    XmlAttribute a = (XmlAttribute) getAnnotation(f,
                            XmlAttribute.class);
                    if (fieldName.equals(a.name())) {
                        field = f;
                        break;
                    }
                }
            }
        }
        return field;
    }

    protected static <T extends Annotation> T getAnnotation(AccessibleObject field,
            Class<T> annoClass) {
        for (Annotation anno : field.getAnnotations()) {
            try {
                return annoClass.cast(anno);
            } catch (Exception e) {
                /* swallow */
            }
            if (anno instanceof Proxy) {
                try {
                    Object handler = Proxy.getInvocationHandler(anno);
                    if (handler instanceof InvocationHandler) {
                        T annoObj = convertToAnnotation(
                                (InvocationHandler) handler, annoClass);
                        return (T) annoObj;
                    }
                } catch (Exception e) {
                    /* swallow */
                }
            }
            if (annoClass.equals(anno.getClass())
                    || annoClass.isAssignableFrom(anno.getClass())) {
                return (T) anno;
            }
        }
        return null;
    }

    private static <T extends Annotation> T convertToAnnotation(
            final InvocationHandler handler, final Class<T> expectedClass) {
        try {
            Field f = InvocationHandler.class.getDeclaredField("memberValues");
            f.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) f
                    .get(handler);
            Field f1 = InvocationHandler.class.getDeclaredField("type");
            f1.setAccessible(true);
            final Class<?> type = (Class<?>) f1.get(handler);
            if (!expectedClass.getName().equals(type.getName())) {
                throw new RuntimeException("Not the expected annotation type: "
                        + type + " != " + expectedClass);
            }
            T anno = (T) Proxy.newProxyInstance(expectedClass.getClassLoader(),
                    new Class[] { expectedClass }, new InvocationHandler() {
                        public Object invoke(Object proxy, Method method,
                                Object[] args) {
                            Object o = null;
                            try {
                                o = handler.invoke(proxy, method, args);
                                if (o != null) {
                                    Class<?> componentClass = o.getClass();
                                    if (componentClass.isArray()) {
                                        componentClass = componentClass
                                                .getComponentType();
                                    }
                                    if (!componentClass.isPrimitive()
                                            && !componentClass.getName()
                                                    .startsWith("java.lang")) {
                                        ClassLoader cl = expectedClass
                                                .getClassLoader() != null ? expectedClass
                                                .getClassLoader() : ClassLoader
                                                .getSystemClassLoader();
                                        String name = componentClass.getName();
                                        if (cl.loadClass(name) != componentClass) {
                                            /*
                                             * we need to import/convert the
                                             * class into the classloader of
                                             * expectedClass
                                             */
                                            if (componentClass.getName()
                                                    .endsWith("WhiteSpace")) {
                                                o = WhiteSpace.valueOf(o
                                                        .toString());
                                            } else {
                                                logger.warning("Unknown/Unexpected class "
                                                        + componentClass);
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable e) {
                                logger.log(Level.WARNING, "", e);
                            }
                            return o;
                        }
                    });
            return anno;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to convert AnnotationInvocationHandler to annotation.",
                    e);
        }
    }

    private static <T, C> TypedXmlWriter getRestriction(
            AttributePropertyInfo<T, C> info, TypedXmlWriter obj) {
        return getRestriction(info, obj, null);
    }
    private static <T, C> TypedXmlWriter getRestriction(
            AttributePropertyInfo<T, C> info, TypedXmlWriter obj,
            TypedXmlWriter w) {
    	QName restrName = info.getTarget().getTypeName();
    	if(info.getSource().id() == ID.ID) {
    		restrName = new QName(NS_XSD, "ID");
    	} else if(info.getSource().id() == ID.IDREF) {
    		restrName = new QName(NS_XSD, "IDREF");
        }
        return getRestriction(restrName, obj, w);
    }
    private static <T, C> TypedXmlWriter getRestriction(
            ValuePropertyInfo<T, C> info, TypedXmlWriter obj, TypedXmlWriter w) {
        return getRestriction(info.getTarget().getTypeName(), obj, w);
    }
    private static <T, C> TypedXmlWriter getRestriction(TypeRef<T, C> t,
            TypedXmlWriter obj, TypedXmlWriter w) {
        if (w != null) {
            return w;
        }
        QName schemaType = t.getSource() == null ? null : t.getSource()
                .getSchemaType();
        if (schemaType == null)
            schemaType = t.getTarget().getTypeName();
        return getRestriction(schemaType, obj, w);
    }

    private static  <T, C> TypedXmlWriter getRestriction(
            QName typeName, TypedXmlWriter obj, TypedXmlWriter w) {
        if (w != null) {
            return w;
        }
        
        TypedXmlWriter st = obj._element(new QName(NS_XSD, "simpleType"),
                TypedXmlWriter.class);
        TypedXmlWriter r = st._element(new QName(NS_XSD, "restriction"),
                TypedXmlWriter.class);
        r._attribute("base", typeName);
        return r;
    }

    private static <T, C> TypedXmlWriter writeXsdAnnotationElement(TypedXmlWriter obj,
            String annoID, Attribute[] otherAttributes) {
        TypedXmlWriter anno = obj._element(new QName(NS_XSD, "annotation"),
                TypedXmlWriter.class);
        if (annoID != null && !annoID.trim().isEmpty()) {
            anno._attribute(new QName("id"), annoID);
        }
        if (otherAttributes != null && otherAttributes.length > 0) {
            for(Attribute attr : otherAttributes) {
                // namespace is compulsory
                QName attrName = new QName(attr.namespace(), attr.name());
                anno._attribute(attrName, attr.value());
            }
        }
        return anno;
    }

    private static <T, C> TypedXmlWriter writeXsdAssertElement(TypedXmlWriter obj,
            String id, String test, Attribute[] otherAttributes) {

        TypedXmlWriter ass = obj._element(new QName(NS_XSD, "assert"),
                TypedXmlWriter.class);
        if (id != null && !id.trim().isEmpty()) {
            ass._attribute(new QName("id"), id);
        }
        if (test != null && !test.trim().isEmpty()) {
            ass._attribute(new QName("test"), test);
        }
        if (otherAttributes != null && otherAttributes.length > 0) {
            for(Attribute attr : otherAttributes) {
                // namespace is compulsory
                QName attrName = new QName(attr.namespace(), attr.name());
                ass._attribute(attrName, attr.value());
            }
        }
        return ass;
    }

    /**
     * Note: we are returning a SortedMap here in order to make the schema 
     * generation process deterministic. Yossi Cohen (YossiCO@Amdocs.com) 
     * reported indeterministic behavior (different order of generated XML 
     * nodes) in the old version where we still used a regular HashMap, 
     * which does not preserve the order of items...
     * 
     * @param facetsAnnotation
     * @return
     * @throws Exception
     */
    protected static SortedMap<String, List<String>> getDefinedFacets(
    		Facets facetsAnnotation) throws Exception {
        List<Method> annoMethods = new LinkedList<Method>();
        SortedMap<String, List<String>> result = new TreeMap<String, List<String>>();

        if (facetsAnnotation == null)
            return result;

        for (Method m : Facets.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(FacetDefinition.class))
                annoMethods.add(m);
        }

        for (Method m : annoMethods) {
            /* additional code suggested by Jason Pell (jason@pellcorp.com) */
            FacetDefinition facetDefinition = m
                    .getAnnotation(FacetDefinition.class);
            String facetName = m.getName();
            if (facetDefinition.xsdAttributeName() != null
                    && facetDefinition.xsdAttributeName().length() > 0) {
                facetName = facetDefinition.xsdAttributeName();
            }
            /* end additional code */

            Object value = null;
            try {
				value = m.invoke(facetsAnnotation);
			} catch (Exception e) {
				// sometimes happens due to our proxying mechanism, especially for javac/schemagen.
			}
            Object defaultValue = m.getDefaultValue();
            if (value != null && !value.equals(defaultValue)) {

                if (!result.containsKey(facetName)) {
                    result.put(facetName, new LinkedList<String>());
                }
                if (value instanceof String[]) {
                    for (String s : (String[]) value)
                        result.get(facetName).add(s);
                } else {
                    result.get(facetName).add("" + value);
                }
            }
        }

        return result;
    }

    /**
     * Helper method to concatenate two arrays.
     * 
     * @param first
     * @param second
     * @return
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Helper method to concatenate an arrays with an additional value.
     * 
     * @param first
     * @param second
     * @return
     */
    public static <T> T[] concat(T[] first, T second) {
        T[] result = Arrays.copyOf(first, first.length + 1);
        System.arraycopy(first, 0, result, 0, first.length);
        result[result.length - 1] = second;
        return result;
    }
        
    
    

    
    /* COMPATIBILITY METHODS TO DEAL WITH com.sun.xml.internal.bind.* */
    

//    /** for compatibility with Java 1.7 */
//    public static <T, C> boolean hasExtendedAnnotations(
//            com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo<T, C> info) {
//        return hasFacets(info) || hasXsdAnnotations(info);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> boolean hasExtendedAnnotations(
//            com.sun.xml.internal.bind.v2.model.core.TypeRef<T, C> t) {
//        return hasFacets(t) || hasXsdAnnotations(t);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addFacets(
//            com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo<T, C> vp,
//            com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleRestriction sr) {
//        XmlSchemaEnhancerJava7.addFacets(vp, sr);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addFacets(
//            com.sun.xml.internal.bind.v2.model.core.TypeRef<T, C> t,
//            com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement e) {
//        XmlSchemaEnhancerJava7.addFacets(t, e);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addFacets(
//            com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo<T, C> info,
//            com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute attr) {
//        XmlSchemaEnhancerJava7.addFacets(info, attr);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> boolean hasFacets(
//            com.sun.xml.internal.bind.v2.model.core.TypeRef<T, C> t) {
//        return XmlSchemaEnhancerJava7.hasFacets(t);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> boolean hasFacets(
//            com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo<T, C> ap) {
//        return XmlSchemaEnhancerJava7.hasFacets(ap);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addXsdAnnotations(T type,
//            com.sun.xml.internal.txw2.TypedXmlWriter w) {
//        XmlSchemaEnhancerJava7.addXsdAnnotations(type, w);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addXsdAnnotations(
//            Set<com.sun.xml.internal.bind.v2.model.core.ClassInfo<T, C>> classes,
//            Set<com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo<T, C>> enums,
//            Set<com.sun.xml.internal.bind.v2.model.core.ArrayInfo<T, C>> arrays,
//            com.sun.xml.internal.txw2.TypedXmlWriter w) {
//        Set<Package> annotatedPackages = new HashSet<Package>();
//        for (com.sun.xml.internal.bind.v2.model.core.ClassInfo<T, C> c : classes) {
//            Class<?> cl = (Class<?>) c.getType();
//            Package pkg = cl.getPackage();
//            annotatedPackages.add(pkg);
//        }
//        for (com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo<T, C> c : enums) {
//            Class<?> cl = (Class<?>) c.getType();
//            Package pkg = cl.getPackage();
//            annotatedPackages.add(pkg);
//        }
//        for (com.sun.xml.internal.bind.v2.model.core.ArrayInfo<T, C> c : arrays) {
//            Class<?> cl = (Class<?>) c.getType();
//            Package pkg = cl.getPackage();
//            annotatedPackages.add(pkg);
//        }
//        for (Package p : annotatedPackages) {
//            XmlSchemaEnhancerJava7.addXsdAnnotations(p, w);
//        }
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addXsdAnnotations(
//            com.sun.xml.internal.bind.v2.model.core.ClassInfo<T, C> ci,
//            com.sun.xml.internal.txw2.TypedXmlWriter w) {
//        XmlSchemaEnhancerJava7.addXsdAnnotations(ci, w);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addXsdAnnotations(
//            com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo<T, C> _info, 
//            com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute _attr) {
//        XmlSchemaEnhancerJava7.addXsdAnnotations(_info, _attr);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addXsdAnnotations(
//            com.sun.xml.internal.bind.v2.model.core.TypeRef<T, C> _info, 
//            com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement _el) {
//        XmlSchemaEnhancerJava7.addXsdAnnotations(_info, _el);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> void addXsdAnnotations(
//            javax.xml.bind.annotation.Annotation anno, 
//            com.sun.xml.internal.txw2.TypedXmlWriter obj) {
//        XmlSchemaEnhancerJava7.addXsdAnnotations(anno, obj);
//    }
//    /** for compatibility with Java 1.7 */
//    public static <T, C> boolean writeCustomOccurs(
//            com.sun.xml.internal.bind.v2.model.core.TypeRef<T, C> t,
//            com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement e,
//            boolean isOptional, boolean repeated) {
//        return XmlSchemaEnhancerJava7.writeCustomOccurs(t, e, isOptional,
//                repeated);
//    }

}
