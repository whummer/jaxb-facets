# JAXB-Facets

This is a fork of jaxb-facets from http://www.infosys.tuwien.ac.at/staff/hummer/tools/jaxb-facets.html

It aims to automate the creation of facets specific versions of the jaxb-api and jaxb-impl using maven.

A jaxb jira is outstanding to integrate this stuff directly into JAXB RI. 
If and when that happens this project will be obselete (which we are looking forward to!).

http://java.net/jira/browse/JAXB-917

## Quick Summary

Jaxb-facets allows to define specialized annotations on schema Java classes and properties ...

```java
package foo;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlType(name = "TestType")
@Annotation(id = "anno1", documentation = {
    @Documentation(value = "doc 1", lang = "en", source = "src 1"),
    @Documentation("doc 2")
})
@AppInfo(source = "src 2", value = "<custom xmlns=\"myns123\">Custom app info</custom>")
@Assert(id="assert1", test = "not(foo) or not(bar)")
public class TestType {
    @XmlAttribute
    @Facets(length = 100, pattern = "[a-z]+")
    @Documentation("<b>string attribute</b>")
    @AppInfo(source = "src 1", value = "<foo xmlns=\"myns123\">appinfo 1</foo>")
    private String foo;

    @XmlElement
    @MinOccurs(2)
    @MaxOccurs(10)
    @Facets(pattern = "[0-9]+")
    @Documentation("<b>list of strings</b>")
    private List<String> bar;

    //...
}
```

... and automatically generates corresponding XSD documents:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xs:complexType name="testType">
    <xs:annotation id="anno1">
      <xs:appinfo source="src 2">
        <ns1:custom xmlns:ns1="myns123">Custom app info</ns1:custom>
      </xs:appinfo>
      <xs:documentation source="src 1" xml:lang="en">doc 1</xs:documentation>
      <xs:documentation>doc 2</xs:documentation>
    </xs:annotation>
    <xs:sequence>
      <xs:element name="bar" minOccurs="2" maxOccurs="10">
        <xs:annotation>
          <xs:documentation>
            <b>list of strings</b>
          </xs:documentation>
        </xs:annotation>
        <xs:simpleType>
          <xs:restriction base="xs:string">
            <xs:pattern value="[0-9]+"/>
          </xs:restriction>
        </xs:simpleType>
      </xs:element>
    </xs:sequence>
    <xs:attribute name="foo">
      <xs:annotation>
        <xs:appinfo source="src 1">
          <ns2:bar xmlns:ns2="myns123">appinfo 1</ns2:bar>
        </xs:appinfo>
        <xs:documentation>
          <b>string attribute</b>
        </xs:documentation>
      </xs:annotation>
      <xs:simpleType>
        <xs:restriction base="xs:string">
          <xs:length value="100"/>
          <xs:pattern value="[a-z]+"/>
        </xs:restriction>
      </xs:simpleType>
    </xs:attribute>
    <xs:assert id="assert1" test="not(foo) or not(bar)"/>
  </xs:complexType>
</xs:schema>
```

You can generate schema files by running the following commands:

```
mvn install -DskipTests
./bin/schemagen.sh -d /tmp/output_directory/ -cp jaxb-impl/target/test-classes/:jaxb-api/target/classes/ path/to/your/code/TestType.java
```

The procedure also works in the other direction, generating Java source code from existing XSD schemas
with facets and annotations. Currently this only works if the XSD schema is embedded into a WSDL file (see [here](https://github.com/whummer/jaxb-facets/issues/32#issuecomment-228537951) for a minimum working example):

```
./bin/wsimport.sh -keep -B-jaxb-facets -d /tmp/output_directory path/to/your/code/tmpservice.wsdl
```

## Maven Repository

The jaxb-api and jaxb-impl JARs are deployed to a maven repo located here:

https://github.com/whummer/mvn

E.g., see:

https://raw.github.com/whummer/jaxb-facets/master/releases/javax/xml/bind/jaxb-api/2.2.7-facets-1.0.5/jaxb-api-2.2.7-facets-1.0.5.jar
https://raw.github.com/whummer/jaxb-facets/master/releases/com/sun/xml/bind/jaxb-impl/2.2.6-facets-1.3.1/jaxb-impl-2.2.6-facets-1.3.1.jar


## Compile & Build

To compile, test and package the code, run a maven build from the project's root directory:

```
$ mvn clean install
```

## Maven Integration

To integrate JAXB-Facets into your Maven project, simply add the following repository and dependencies. You need to ensure that there are no other versions of jaxb-api and jaxb-impl on the CLASSPATH.

```xml
<project ...>
...
    <dependencies>
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.2.7-facets-1.0.5</version>
        </dependency>
        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-impl</artifactId>
            <version>2.2.6-facets-1.3.1</version>
        </dependency>
        ...
    </dependencies>

    <repositories>
        <repository>
            <id>github-repo-releases</id>
            <url>https://raw.github.com/whummer/jaxb-facets/master/releases</url>
        </repository>
    </repositories>
...
</project>
```

## JAXB Schemagen Maven Integration

To integrate JAXB-Facets with the schemagen facility of jaxb2-maven-plugin, use the following configuration:

```xml
<project ...>
...
	<build>
		<plugins>
			<plugin>
	    		<groupId>org.codehaus.mojo</groupId>
	    		<artifactId>jaxb2-maven-plugin</artifactId>
	    		<version>1.5</version>
	    		<dependencies>
	    			<dependency>
	    				<groupId>com.sun.xml.bind</groupId>
	    				<artifactId>jaxb-impl</artifactId>
	    				<version>2.2.6-facets-1.3.1</version>
					</dependency>
					<dependency>
	    				<groupId>javax.xml.bind</groupId>
	        			<artifactId>jaxb-api</artifactId>
	        			<version>2.2.7-facets-1.0.5</version>
	    			</dependency> 
                    <dependency>
                        <groupId>com.sun.xml.bind</groupId>
                        <artifactId>jaxb-xjc</artifactId>
                        <version>2.2.6</version>
                        <exclusions>
                        	<exclusion>
			    				<groupId>javax.xml.bind</groupId>
			        			<artifactId>jaxb-api</artifactId>
                        	</exclusion>
                        	<exclusion>
			    				<groupId>com.sun.xml.bind</groupId>
			    				<artifactId>jaxb-impl</artifactId>
                        	</exclusion>
                        </exclusions>
                	</dependency>
				</dependencies>
				<executions>
	        		<execution>
	            		<goals>
	                		<goal>schemagen</goal>
	            		</goals>
	            		<phase>generate-resources</phase>
	            		<configuration>
	            			...
	            		</configuration>
	            	</execution>
	            </executions>
			</plugin>
		</plugins>
	</build>
...
</project>
```

## Wsimport Integration (WSDL-First Schema Generation)

Starting with version 2.2.6-facets-1.1.0, JAXB-facets supports also WSDL-first generation 
using 'wsimport'. JAXB-Facets can easily be hooked as a plugin into wsimport in order to
include the specific annotations (@Facets, @Documentation, @Annotation, ...) in the 
generated Java code.

To activate the plugin, use the wsimport wrapper script with the "-jaxb-facets" switch as follows:
```
./bin/wsimport.sh -keep -B-jaxb-facets -d <target_dir> <source_wsdl>
```
... and for Windows:
```
./bin/wsimport.bat -keep -B-jaxb-facets -Xendorsed -d <target_dir> <source_wsdl>
```

## Maven Endorsed Integration

In some situations in order to make use of the new jaxb facet in your maven project, you may need to make use of the endorsed 
strategy with the maven compiler and surefire plugins.

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-dependency-plugin</artifactId>
	<configuration>
		<outputDirectory>${project.build.directory}/endorsed</outputDirectory>
	</configuration>
	<executions>
		<execution>
			<id>copy-endorsed</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>copy</goal>
			</goals>
			<configuration>
				<artifactItems>
					<artifactItem>
						<groupId>javax.xml.bind</groupId>
						<artifactId>jaxb-api</artifactId>
						<version>2.2.6-facets-1.0.5</version>
						<overWrite>true</overWrite>
						<destFileName>jaxb-api.jar</destFileName>
					</artifactItem>
				</artifactItems>
			</configuration>
		</execution>
	</executions>
</plugin>
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<configuration>
		<compilerArguments>
			<endorseddirs>${project.build.directory}/endorsed</endorseddirs>
		</compilerArguments>
	</configuration>
</plugin>
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<systemPropertyVariables>
			<java.endorsed.dirs>${project.build.directory}/endorsed</java.endorsed.dirs>
		</systemPropertyVariables>
	</configuration>
</plugin>
```


## Change Log

- jaxb-impl:2.2.11-facets-1.4.0
    * upgrade to JAXB version 2.2.11. Should fix issues with spring boot 1.3.1 (see issue #29)
- jaxb-impl:2.2.6-facets-1.3.1
    * fix support for command-line "schemagen" schema generation.
      Fix issue github.com/whummer/jaxb-facets/issues/23
- jaxb-impl:2.2.6-facets-1.3.0
    * support for javax bean validation (JSR 303) constraints, which are 
      now included in generated schema (integrated pull request #20).
- jaxb-impl:2.2.6-facets-1.2.0
    * Initial support for xs:assert elements (defined in XML Schema 1.1).
    * Refactorings due to newly introduced XML Schema 1.1 features.
- jaxb-api:2.2.7-facets-1.0.5
	* Added @Assert annotation to define xs:assert elements.
- jaxb-impl:2.2.6-facets-1.1.0
    * Added support for "wsimport" WSDL-first JAXB generation.
- jaxb-impl:2.2.6-facets-1.0.11
	* add location() parameter to @Annotation (INSIDE_ELEMENT, 
	  OUTSIDE_ELEMENT): For XSD groups (in particular <choice>), 
	  this allows to place <annotation> either inside the children 
	  of the group, or into the group itself (as sibling of 
	  the children of the group). This can also be used to place
	  an annotation inside an @XmlElementWrapper, instead of placing
	  it inside the element which is wrapped.
- jaxb-api:2.2.7-facets-1.0.4
	* Added AnnotationLocation class to specify the location of 
	  generated XSD annotations.
- jaxb-impl:2.2.6-facets-1.0.10
	* support jaxb-facets for source code based schemagen
	  (e.g., used for "mvn generate-resources)
	* remaining limitation: package-level annotations do not (yet)
	  work with the schemagen based generation approach. This is
	  planned for a future release.
- jaxb-impl:2.2.6-facets-1.0.9
	* support additional facets (e.g., maxLength) on enumeration types
- jaxb-impl:2.2.6-facets-1.0.8
	* compulsory namespace for xsd:annotation attributes
	* improved schema validation in unit tests
- jaxb-impl:2.2.6-facets-1.0.7
	* support for custom attributes in xsd:annotation
	* support for XML content in xsd:annotation
	* test classes moved back to jaxb-impl project
- jaxb-api:2.2.7-facets-1.0.3
	* minor (changed versions, updated pom.xml file metadata)
- jaxb-impl:2.2.7-facets-1.0.6
	* removed com.sun.xml.internal.* references
	* moved jaxb-impl test classes into separate maven project
	* code refactoring; removed code duplication in XmlSchemaGenerator
- jaxb-impl:2.2.7-facets-1.0.5
	* fixed pom.xml metadata and JAR manifest info
	* Handling of com.sun.xml.internal.* classes in separate XmlSchemaGenerator
- jaxb-impl:2.2.7-facets-1.0.4
	* test utility classes
	* updated CXF dependency version
- jaxb-impl:2.2.7-facets-1.0.3
	* test cases for enum literals support
- jaxb-api:2.2.7-facets-1.0.2
	* initial github version

Older versions (legacy from http://dsg.tuwien.ac.at/staff/hummer/tools/jaxb-facets.html):

- 2012-11-09: version 1.0 
	Minor bug fixes and API changes in @Facets. The type of minInclusive/maxInclusive/minExclusive/maxExclusive has 
	been changed from long to String, which is needed, e.g., to represent min/max for dates. This version is not API
	compatible with 0.x versions!
- 2012-09-05: version 0.4 
	Support for xsd:annotation, xsd:appinfo, and xsd:documentation
- 2012-05-27: version 0.3 
	fixed classloading-related issues; ready for deployment in JBoss
- 2012-04-15: version 0.2 
	supports Facets for XML attributes; support for Java 1.7
- 2011-12-04: version 0.1
	initial release

