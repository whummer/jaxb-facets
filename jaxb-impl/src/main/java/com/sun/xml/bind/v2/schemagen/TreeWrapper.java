package com.sun.xml.bind.v2.schemagen;

import java.lang.reflect.Field;

import at.ac.tuwien.infosys.jaxb.XmlSchemaEnhancer;

import com.sun.xml.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.bind.v2.schemagen.xmlschema.TypeDefParticle;

/**
 * This class is used to intercept the schema generation for group 
 * types (<sequence>, <choice>) in order to add <annotation> elements
 * directly into the groups -- instead of putting the <annotation>
 * into the children of the group which is the default case. See
 * class javax.xml.bind.annotation.AnnotationLocation for more info.
 * 
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 * @since JAXB-Facets version 1.0.11
 */
public class TreeWrapper<T,C> extends Tree {

    private Tree wrapped;
    private ElementPropertyInfo<T,C> elementInfo;

    public TreeWrapper(Tree t, ElementPropertyInfo<T,C> elementInfo) {
        this.wrapped = t;
        this.elementInfo = elementInfo;
    }

    public static <T,C> Tree wrap(Tree t, ElementPropertyInfo<T,C> elementInfo) {
        return new TreeWrapper<T,C>(t, elementInfo);
    }

    @Override
    protected void write(ContentModelContainer parent, boolean isOptional,
            boolean repeated) {
        Class<?> clazz = wrapped.getClass();

        /* special treatment for Groups, in particular CHOICE groups.
         * If @Annotation.location() == AnnotationLocation.OUTSIDE_ELEMENT, 
         * we want to be able to write <xsd:annotation> elements
         * into the <xsd:choice> element directly, instead of writing
         * <xsd:annotation> into the child elements of the <xsd:choice> */

        if(wrapped.getClass().getName().endsWith("Group")) {

            try {
                Field fieldKind = clazz.getDeclaredField("kind");
                fieldKind.setAccessible(true);
                Object kind = fieldKind.get(wrapped);

                /*
                 * Note: We only consider CHOICE groups, because for
                 * SEQUENCE groups we might run into the situation that
                 * multiple <annotation> elements are generated (for multiple 
                 * child elements in the <sequence>), which is invalid.
                 */
                if(kind == GroupKind.CHOICE) {

                    /* code below is taken from Tree$Group class! */
                    Particle c = (Particle)kind.getClass().getDeclaredMethod(
                            "write", ContentModelContainer.class).invoke(kind, parent);
                    wrapped.writeOccurs(c,isOptional,repeated);

                    Field fieldChildren = clazz.getDeclaredField("children");
                    fieldChildren.setAccessible(true);
                    Tree[] children = (Tree[])fieldChildren.get(wrapped);

                    XmlSchemaEnhancer.addXsdAnnotationsOutsideElement(elementInfo, c);
                    

                    for (Tree child : children) {
                        child.write(c,false,false);
                    }
                    
                } else {
                    wrapped.write(parent, isOptional, repeated);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
        } else {
            wrapped.write(parent, isOptional, repeated);
        }
    }

    @Override
    protected void write(TypeDefParticle ct) {
        wrapped.write(ct);
    }
    @Override
    Tree makeOptional(boolean really) {
        return really?new Optional(this) :this;
    }
    @Override
    Tree makeRepeated(boolean really) {
        return really?new Repeated(this) :this;
    }
    @Override
    boolean canBeTopLevel() {
        return wrapped.canBeTopLevel();
    }
    @Override
    boolean isNullable() {
        return wrapped.isNullable();
    }

    /**
     * "T?"
     */
    private static final class Optional extends Tree {
        private final Tree body;

        private Optional(Tree body) {
            this.body = body;
        }

        @Override
        boolean isNullable() {
            return true;
        }

        @Override
        Tree makeOptional(boolean really) {
            return this;
        }

        @Override
        protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
            body.write(parent,true,repeated);
        }
    }

    /**
     * "T+"
     */
    private static final class Repeated extends Tree {
        private final Tree body;

        private Repeated(Tree body) {
            this.body = body;
        }

        @Override
        boolean isNullable() {
            return body.isNullable();
        }

        @Override
        Tree makeRepeated(boolean really) {
            return this;
        }

        @Override
        protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
            body.write(parent,isOptional,true);
        }
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return wrapped.equals(obj);
    }
}
