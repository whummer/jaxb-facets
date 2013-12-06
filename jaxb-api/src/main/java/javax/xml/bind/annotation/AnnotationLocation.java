package javax.xml.bind.annotation;

/**
 * This enumeration specifies whether an <annotation> element
 * is placed inside the annotated element, or outside, i.e.,  
 * to the container element containing the annotated element. 
 * Default behavior is INSIDE_ELEMENT (see class
 * javax.xml.bind.annotation.Annotation).
 * 
 * Example for INSIDE_ELEMENT:
 * 
 * <xs:choice>
 * 	 <xs:element name="element1" type="tns:Type1">
 * 	   <xs:annotation>
 * 	     <xs:documentation>element1 or element2 INSIDE</xs:documentation>
 *     </xs:annotation>
 * 	 </xs:element>
 * 	 <xs:element name="element2" type="tns:Type2">
 * 	   <xs:annotation>
 *       <xs:documentation>element1 or element2 INSIDE</xs:documentation>
 * 	   </xs:annotation>
 * 	 </xs:element>
 * </xs:choice>
 * 
 * Example for OUTSIDE_ELEMENT:
 * 
 * <xs:choice>
 * 	   <xs:annotation>
 * 	     <xs:documentation>element1 or element2 OUTSIDE</xs:documentation>
 *     </xs:annotation>
 * 	 <xs:element name="element1" type="tns:Type1"/>
 * 	 <xs:element name="element2" type="tns:Type2"/>
 * </xs:choice>
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 1.0.11
 */
public enum AnnotationLocation {
	INSIDE_ELEMENT, OUTSIDE_ELEMENT
}
