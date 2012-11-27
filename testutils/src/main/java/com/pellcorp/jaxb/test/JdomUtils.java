package com.pellcorp.jaxb.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.DOMOutputter;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

public final class JdomUtils {
    private JdomUtils() {
    }
    
    public static String toString(org.jdom2.Document doc) {
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(doc);
    }
    
    public static org.jdom2.Document getWsdlSchema(InputStream wsdlXml) throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();
            
            org.jdom2.Document doc = builder.build(wsdlXml);
            return getWsdlSchema(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }
    
    public static org.jdom2.Document getWsdlSchema(String wsdlXml) throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();
            org.jdom2.Document doc = builder.build(new StringReader(wsdlXml));
            return getWsdlSchema(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }
    
    public static org.jdom2.Document getWsdlSchema(org.jdom2.Document wsdlDoc) {
        List<Element> schemaList = 
            toList(wsdlDoc.getDescendants(new ElementFilter("schema", Namespace.getNamespace("http://www.w3.org/2001/XMLSchema"))));
        
        org.jdom2.Document schemaDoc = new org.jdom2.Document();
        schemaDoc.setRootElement(schemaList.get(0).detach());
        return schemaDoc;
    }
    
    private static List<Element> toList(IteratorIterable<Element> elements) {
        List<Element> elementList = new ArrayList<Element>();
        for (Element e : elements) {
            elementList.add(e);
        }
        return elementList;
    }
    
    public static org.w3c.dom.Document getWsdlSchemaAsW3CDocument(InputStream xml) throws IOException {
        try {
            org.jdom2.Document doc = getWsdlSchema(xml);
            DOMOutputter domOutputer = new DOMOutputter();
            return domOutputer.output(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }
    
    public static org.w3c.dom.Document getWsdlSchemaAsW3CDocument(String xml) throws IOException {
        try {
            org.jdom2.Document doc = getWsdlSchema(xml);
            DOMOutputter domOutputer = new DOMOutputter();
            return domOutputer.output(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }
}
