#!/bin/bash

JAVA6=/usr/lib/jvm/java-6-sun/
JAVA7=/usr/lib/jvm/java-7-openjdk-i386/

echo "Running tests with Java 7"
export JAVA_HOME=$JAVA7
mvn test

echo "Running tests with Java 6"
export JAVA_HOME=$JAVA6
mvn test
