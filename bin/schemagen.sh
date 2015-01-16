#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CP="$DIR/../jaxb-impl/target/classes:$DIR/../jaxb-api/target/classes:$HOME/.m2/repository/com/sun/xml/ws/jaxws-tools/2.2.6/jaxws-tools-2.2.6.jar:$HOME/.m2/repository/com/sun/xml/bind/jaxb-xjc/2.2.5/jaxb-xjc-2.2.5.jar:$HOME/.m2/repository/com/sun/xml/bind/jaxb-impl/2.2.6/jaxb-impl-2.2.6.jar:$HOME/.m2/repository/com/sun/xml/ws/jaxws-rt/2.2.6/jaxws-rt-2.2.6.jar:$HOME/.m2/repository/com/sun/xml/stream/buffer/streambuffer/1.4/streambuffer-1.4.jar:$HOME/.m2/repository/org/jvnet/staxex/stax-ex/1.7/stax-ex-1.7.jar:$HOME/.m2/repository/com/sun/xml/ws/policy/2.3.1/policy-2.3.1.jar:$HOME/.m2/repository/net/java/loci/jsr308-all/1.1.2/jsr308-all-1.1.2.jar:$HOME/.m2/repository/javax/validation/validation-api/1.1.0.Final/validation-api-1.1.0.Final.jar"
java -cp $CP com.sun.tools.jxc.SchemaGenerator $*
