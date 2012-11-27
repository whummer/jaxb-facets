package at.ac.tuwien.infosys.jaxb.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.bind.annotation.Annotation;
import javax.xml.bind.annotation.AppInfo;
import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.MaxOccurs;
import javax.xml.bind.annotation.MinOccurs;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.ws.Endpoint;

import org.junit.Test;

/**
 * @author Waldemar Hummer (hummer@infosys.tuwien.ac.at)
 */
@SuppressWarnings("all")
public class FacetsTest {
	
	@XmlRootElement(name="foo")
	@Annotation(id = "id123", documentation={
			@Documentation(value="doc 1", lang="lang 1", source="src 1"),
			@Documentation("doc 2")
	})
	@Documentation("doc 3")
	@AppInfo(source="src 1", value="appinfo 1")
	public static class TestRequest {
		@Documentation(value="Choose Chart Type")
		public static enum ChartType { line, bar, pie }
		@XmlAttribute
		public ChartType type;
		@XmlAttribute
		@Facets(length=100, pattern="[a-z]+")
		@Documentation("<b>string foo</b>")
		@AppInfo(source="src 1", value="appinfo 1")
		private String foo;
		@XmlAttribute
		@Annotation(documentation={
				@Documentation(lang="en", value="this is the first line"),
				@Documentation(lang="en", value="this is the second line")}
		)
		private String foo1;
		@XmlElement
		@MinOccurs(2)
		@MaxOccurs(10)
		@Facets(pattern="[0-9]+")
		@Documentation("<b>string bar</b>")
		private List<String> bar;
		@XmlElement
		private String bar1;
		@XmlElement(name="bar2")
		@Facets(pattern="[0-9]+")
		private String barX;

		/* Thanks to Yossi Cohen for the following two test cases..! */
		@XmlElement(required=true)
		@XmlSchemaType(name="anyURI")
		@Facets(pattern="https?://.+")
		public java.net.URI value1;
		@XmlElement(required=true)
		@XmlSchemaType(name="anyURI")
		public java.net.URI value2;

		/* Thanks to Jason Pell for point out the need for min/max facets of type String..! */
		@XmlElement(required=true)
		@Facets(minInclusive="2012-12-24T12:00:00Z")
		public Date date1;

		/* Thanks to Uwe Maurer for the following test case..! */
		@XmlElement(required=true)
		public Buddy buddy;

		@XmlTransient
		public String getFoo() {
			return foo;
		}
		@XmlTransient
		public List<String> getBar() {
			return bar;
		}
		@XmlTransient
		public String getFoo1() {
			return foo1;
		}
		@XmlTransient
		public String getBar1() {
			return bar1;
		}
		@XmlTransient
		public String getBarX() {
			return barX;
		}
	}

	/* Thanks to Uwe Maurer for the following test class..! */
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings("unused")
	@Documentation("A Buddy.")
	public static class Buddy {
	    @Documentation("Name of buddy.")
	    private String name;
	}
	
	@WebService
	public static interface TestWebService {
		void foo(TestRequest r);
	}
	@WebService
	public static class TestWebServiceImpl implements TestWebService {
		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public void foo(TestRequest r) { }
	}

	@Test
	public void testGenerateFacetsForAttributes() throws Exception {
		String url = "http://localhost:33452/service1";
		String wsdl = url + "?wsdl";
		TestWebServiceImpl s = new TestWebServiceImpl();
		Endpoint.publish(url, s);
		String c = readURL(wsdl);
		if(c.contains("schemaLocation=")) {
			String schema = c.substring(c.indexOf("schemaLocation="));
			schema = schema.substring(schema.indexOf("\"") + 1);
			schema = schema.substring(0, schema.indexOf("\""));
			c = readURL(schema);
		}
		System.out.println(c);
	}
	
	private static String readURL(String url) throws Exception {
		return readStream(new URL(url).openStream());
	}
	private static String readStream(InputStream is) throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String t = null;
		String c = "";
		while((t = r.readLine()) != null) {
			c += t + "\n";
		}
		return c;
	}
	
}
