JAXB Facets
-----------

This is a fork of jaxb-facets from http://www.infosys.tuwien.ac.at/staff/hummer/tools/jaxb-facets.html

It aims to automate the creation of facets specific versions of the jaxb-api and jaxb-impl using maven, to avoid
having to ensure classpath order, etc.

A jaxb jira is outstanding to integrate this stuff directly into JAXB RI.  If and when that happens this project
will be obselete (which I am looking forward to!)

http://java.net/jira/browse/JAXB-917

Maven Repository
----------------

The jaxb-api and jaxb-impl JARs are deployed to a maven repo located here:

http://www.infosys.tuwien.ac.at/mvn/

see:
http://www.infosys.tuwien.ac.at/mvn/javax/xml/bind/jaxb-api/2.2.7-facets-1.0.2/
http://www.infosys.tuwien.ac.at/mvn/com/sun/xml/bind/jaxb-impl/2.2.6-facets-1.0.5/

Maven Endorsed Integration
--------------------------

The easiest way to integrate this into your maven build to override the built in jaxb impl and api is to
use the maven dependency plugin, and the configure the maven compiler and surefire plugins.

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
						<version>2.2.6-facets-1.0.3</version>
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

