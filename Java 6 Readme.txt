Java 6 Maven Endorsed Integration
---------------------------------

In order to make use of the new jaxb facet in your maven projects (at least in java 6), you will need to make use of the endorsed 
strategy with the maven compiler and surefire plugins.

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
