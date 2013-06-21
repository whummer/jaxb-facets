JAXB-Facets
-----------

This is a fork of jaxb-facets from http://www.infosys.tuwien.ac.at/staff/hummer/tools/jaxb-facets.html

It aims to automate the creation of facets specific versions of the jaxb-api and jaxb-impl using maven.

A jaxb jira is outstanding to integrate this stuff directly into JAXB RI.  If and when that happens this project
will be obselete (which we are looking forward to!)

http://java.net/jira/browse/JAXB-917

Maven Repository
----------------

The jaxb-api and jaxb-impl JARs are deployed to a maven repo located here:

https://github.com/whummer/mvn

E.g., see:

https://raw.github.com/whummer/mvn/master/releases/javax/xml/bind/jaxb-api/2.2.7-facets-1.0.2/jaxb-api-2.2.7-facets-1.0.2.jar
https://raw.github.com/whummer/mvn/master/releases/com/sun/xml/bind/jaxb-impl/2.2.6-facets-1.0.6/jaxb-impl-2.2.6-facets-1.0.6.jar


Compile & Build
---------------

To compile, test and package the code, run a maven build from the project's root directory:

$ mvn clean install


Maven Integration
-----------------

To integrate JAXB-Facets into your Maven project, simply add the following repository and dependencies. You need to ensure that there are no other versions of jaxb-api and jaxb-impl on the CLASSPATH.

<project ...>
...
    <dependencies>
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.2.7-facets-1.0.2</version>
        </dependency>
        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-impl</artifactId>
            <version>2.2.6-facets-1.0.6</version>
        </dependency>
        ...
    </dependencies>

    <repositories>
        <repository>
            <id>github-repo-releases</id>
            <url>https://raw.github.com/whummer/mvn/master/releases</url>
        </repository>
    </repositories>
...
</project>
