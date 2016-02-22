package at.ac.tuwien.infosys.jaxb;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple DOM manipulation utilities.
 *
 * @author Waldemar Hummer
 */
public class XmlUtil {

	public static String toString(Element element) {
		return toString(element, false);
	}

	@SuppressWarnings("all")
	/** 
	 * This method needs to be synchronized, because of possible 
	 * bug/synchronization issue:
	 * https://issues.apache.org/jira/browse/CXF-1560
	 * */
	public static synchronized String toString(Element element, boolean indent) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			if(indent) {
				tr.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
			} else {
				tr.setOutputProperty(OutputKeys.INDENT, "no");
			}
			tr.transform(new DOMSource(element), new StreamResult(baos));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		String string = null;
		byte[] bytes = baos.toByteArray();
		string = new String(bytes);
		bytes = null;
		string = string
				.replaceAll(
						"<\\?xml version=\"1\\.0\" (encoding=\".*\")?( )*\\?>(\n)*",
						"");
		return string;
	}

	public static String toStringWithStrippedNamespaces(Element e) {
		try {
			RemoveUnusedNamespaces remove = new RemoveUnusedNamespaces();
			Document d = e.getOwnerDocument();
			remove.process(d);
			return toString(d.getDocumentElement());
		} catch (Exception e2) {
			throw new RuntimeException(e2);
		}
	}
	
	/* Based on:
	 * http://stackoverflow.com/a/9087074/2776806
	 */
	public static class RemoveUnusedNamespaces {

	    private interface ElementVisitor {
	        void visit(Element element);
	    }

	    public void process(Document document) {
	        final Set<String> namespaces = new HashSet<String>();

	        Element element = document.getDocumentElement();
	        traverse(element, new ElementVisitor() {

	            public void visit(Element element) {
	                String namespace = element.getNamespaceURI();
	                if (namespace == null)
	                    namespace = "";
	                namespaces.add(namespace);
	                NamedNodeMap attributes = element.getAttributes();
	                for (int i = 0; i < attributes.getLength(); i++) {
	                    Node node = attributes.item(i);
	                    if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI()))
	                        continue;
	                    String prefix;
	                    if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(node.getNamespaceURI())) {
	                        if ("type".equals(node.getLocalName())) {
	                            String value = node.getNodeValue();
	                            if (value.contains(":"))
	                                prefix = value.substring(0, value.indexOf(":"));
	                            else
	                                prefix = null;
	                        } else {
	                            continue;
	                        }
	                    } else {
	                        prefix = node.getPrefix();
	                    }
	                    namespace = element.lookupNamespaceURI(prefix);
	                    if (namespace == null)
	                        namespace = "";
	                    namespaces.add(namespace);
	                }
	            }

	        });
	        traverse(element, new ElementVisitor() {

	            public void visit(Element element) {
	                Set<String> removeLocalNames = new HashSet<String>();
	                NamedNodeMap attributes = element.getAttributes();
	                for (int i = 0; i < attributes.getLength(); i++) {
	                    Node node = attributes.item(i);
	                    if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI()))
	                        continue;
	                    if (namespaces.contains(node.getNodeValue()))
	                        continue;
	                    removeLocalNames.add(node.getLocalName());
	                }
	                for (String localName : removeLocalNames)
	                    element.removeAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName);
	            }

	        });
	    }

	    private final void traverse(Element element, ElementVisitor visitor) {
	        visitor.visit(element);
	        NodeList children = element.getChildNodes();
	        for (int i = 0; i < children.getLength(); i++) {
	            Node node = children.item(i);
	            if (node.getNodeType() != Node.ELEMENT_NODE)
	                continue;
	            traverse((Element) node, visitor);
	        }
	    }

	}

}
