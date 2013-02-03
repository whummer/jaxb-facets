package com.pellcorp.jaxb.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.DOMOutputter;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

public final class JdomUtils {

    public static final String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";

    private JdomUtils() {
    }

    public static String toString(org.jdom2.Document doc) {
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(doc);
    }

    public static String toString(org.w3c.dom.Element element) {
        return toString(element, true);
    }
    public static String toString(org.w3c.dom.Element element, boolean indent) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            if (indent) {
                tr.setOutputProperty(
                        "{http://xml.apache.org/xslt}indent-amount", "2");
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
            } else {
                tr.setOutputProperty(OutputKeys.INDENT, "no");
            }
            tr.transform(new DOMSource(element), new StreamResult(baos));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray());
    }

    public static org.jdom2.Document getWsdlSchema(InputStream wsdlXml)
            throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();

            org.jdom2.Document doc = builder.build(wsdlXml);
            return getWsdlSchema(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }

    public static org.jdom2.Document getWsdlSchema(String wsdlXml)
            throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();
            org.jdom2.Document doc = builder.build(new StringReader(wsdlXml));
            return getWsdlSchema(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }

    public static org.jdom2.Document getWsdlSchema(org.jdom2.Document wsdlDoc) {
        List<Element> schemaList = toList(wsdlDoc
                .getDescendants(new ElementFilter("schema", Namespace
                        .getNamespace(XMLConstants.W3C_XML_SCHEMA_NS_URI))));

        if (schemaList.isEmpty()) {
            List<Element> importList = toList(wsdlDoc
                    .getDescendants(new ElementFilter("import", Namespace
                            .getNamespace(NS_WSDL))));
            for (Element imp : importList) {
                String location = imp.getAttributeValue("location");
                // System.out.println("Loading schema from WSDL location: " +
                // location);
                try {
                    SAXBuilder builder = new SAXBuilder();
                    // String schema = TestUtils.readURL(location);
                    // System.out.println(schema);
                    org.jdom2.Document wsdlDocImported = builder.build(new URL(
                            location));
                    // TODO (whu): circular dependencies not handled
                    // (--> we might end up in an infinite loop;
                    // unlikely, but possible)
                    schemaList.add(getWsdlSchema(wsdlDocImported)
                            .getRootElement());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (schemaList.size() != 1) {
            throw new RuntimeException(
                    "Unexpected number of schema elements in WSDL. Expected 1, got "
                            + schemaList.size());
        }
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

    public static org.w3c.dom.Document getWsdlSchemaAsW3CDocument(
            InputStream xml) throws IOException {
        try {
            org.jdom2.Document doc = getWsdlSchema(xml);
            DOMOutputter domOutputer = new DOMOutputter();
            return domOutputer.output(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }

    public static org.w3c.dom.Document getWsdlSchemaAsW3CDocument(String xml)
            throws IOException {
        try {
            org.jdom2.Document doc = getWsdlSchema(xml);
            DOMOutputter domOutputer = new DOMOutputter();
            return domOutputer.output(doc);
        } catch (JDOMException e) {
            throw new IOException(e);
        }
    }
}
