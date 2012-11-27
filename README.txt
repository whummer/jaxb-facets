JAXB Facets
-----------

This is a fork of jaxb-facets from http://www.infosys.tuwien.ac.at/staff/hummer/tools/jaxb-facets.html

It aims to automate the creation of facets specific versions of the jaxb-api and jaxb-impl using maven, to avoid
having to ensure classpath order, etc.

The java 7 code in the original has been excised for the present.  This is designed to be used within CXF
projects.

A jaxb jira is outstanding to integrate this stuff directly into JAXB RI.  If and when that happens this project
will be obselete (which I am looking forward to!)

http://java.net/jira/browse/JAXB-917

Maven Repository
----------------

The 2.2.6 API and 2.2.7 IMPL are deployed to a maven repo located here:

http://pellcorp.github.com/docs/maven2/releases
